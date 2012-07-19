/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.tools.xml.businessmodell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.ToolkitConstants;
import org.eclipse.jubula.tools.exception.GDConfigXmlException;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.xml.businessprocess.ConfigVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a container for all components which can be tested by
 * Jubula.
 * @author BREDEX GmbH
 * @created 18.07.2005
 */
public class CompSystem {
    /** Constant for an empty extension point entry */
    public static final String EMPTY_EXTPOINT_ENTRY = "null"; //$NON-NLS-1$
    
    /** The logger */
    private static Logger log = LoggerFactory.getLogger(CompSystem.class); 
    
    /** The list of all components. */
    private List m_components;
    
    /** fast lookup */
    private Map m_componentsByType;
    
    /** fast lookup */
    private Map m_componentsByTypeLowerCase;
    
    /** The list of abstract components. */
    private List m_abstractComponents;
    
    /** The list of concrete components. */
    private List m_concreteComponents;
    
    /** The List of Event Types. */
    private Map m_eventTypes;
    
    /** The most abstract component in the hierarchy */
    private Component m_mostAbstractComponent;
    
    /**
     * Map of {@link ToolkitPluginDescriptor}
     * Key: id of toolkit, value: ToolkitPluginDescriptor
     */
    private Map m_toolkitDescriptors;
    
    /** Stores wether the component is initialized. */
    private boolean m_initialized = false;
    
    /** <code>m_configVersion</code> version for clientConfig.xml */
    private ConfigVersion m_configVersion = null;

    /** A List of all DataTypes */
    private Set m_dataTypes = null;
    
    /** All Component Names wich have a default mapping (Name => Type) */
    private Map m_defaultMappingNames = null;

    /** Default constructor */
    public CompSystem() {
        init();
    }
    
    /**  */
    private void init() {
        if (m_components == null) {
            m_components = new ArrayList();
        }
        if (m_componentsByType == null) {
            m_componentsByType = new HashMap(1001);
        }
        if (m_componentsByTypeLowerCase == null) {
            m_componentsByTypeLowerCase = new HashMap(1001);
        }
        if (m_abstractComponents == null) {
            m_abstractComponents = new ArrayList();
        }
        if (m_concreteComponents == null) {
            m_concreteComponents = new ArrayList();
        }
        if (m_eventTypes == null) {
            m_eventTypes = new HashMap(4);
        }
        if (m_toolkitDescriptors == null) {
            m_toolkitDescriptors = new HashMap();
        }
        // FIXME Achim only hard coded EventTypes so far
        m_eventTypes.put(TestErrorEvent.ID.IMPL_CLASS_ACTION_ERROR, 
            new Integer(7));
        m_eventTypes.put(TestErrorEvent.ID.COMPONENT_NOT_FOUND_ERROR, 
            new Integer(7));
        m_eventTypes.put(TestErrorEvent.ID.CONFIGURATION_ERROR, new Integer(1));
        m_eventTypes.put(TestErrorEvent.ID.VERIFY_FAILED, new Integer(1));
        m_initialized = true;
    }

    /**
     * Gets the list with all components of all installed Toolkit-Plugins.
     * @return List A <code>List</code> object.
     */
    public List getComponents() {
        return m_components;
    }

    /**
     * @param toolkitId the unique toolkit id <br>
     * This information is available at the Project!
     * @param addReferencedToolkits <code>true</code> if components from toolkits 
     *                            that are "include"ed or "depend"ed on by
     *                            the given toolkitId should
     *                            be included in the list
     * @return a List with toolkit specific components of the given toolkit
     * (and its included Toolkit if <code>addIncludedToolkits</code> is 
     * <code>true</code>).
     */
    public List getComponents(String toolkitId, boolean addReferencedToolkits) {
        final List toolkitComponents = new ArrayList();
        final ToolkitPluginDescriptor currDescriptor = 
            getToolkitPluginDescriptor(toolkitId);
        String includesToolkit = currDescriptor.getIncludes();
        final ToolkitPluginDescriptor includesDescriptor = 
            getToolkitPluginDescriptor(includesToolkit);
        if (includesDescriptor != null) {
            final String includesLevel = includesDescriptor.getLevel();
            if (!ToolkitConstants.LEVEL_TOOLKIT.equals(includesLevel)) {
                includesToolkit = "NoValidIncludeToolkit"; //$NON-NLS-1$
            }
        } else {
            includesToolkit = "NoValidIncludeToolkit"; //$NON-NLS-1$
        }
        final List dependsToolkits = getDependsToolkitIds(toolkitId);
        Iterator compIter = getComponents().iterator();
        while (compIter.hasNext()) {
            Component component = (Component)compIter.next();
            final String compToolkitId = component.getToolkitDesriptor()
                .getToolkitID();
            if (toolkitId.equals(compToolkitId)
                || (includesToolkit.equals(compToolkitId)
                    && addReferencedToolkits)
                || (dependsToolkits.contains(compToolkitId) 
                    && addReferencedToolkits)) {
                
                toolkitComponents.add(component);
            }
        }
        return toolkitComponents;
    }
    
    
    /**
     * @return Returns the abstractComponents.
     */
    public List getAbstractComponents() {
        return m_abstractComponents;
    }
    
    /**
     * @return Returns the concreteComponents.
     */
    public List getConcreteComponents() {
        return m_concreteComponents;
    }
    
    /**
     * Gets a List of all includes toolkits (the whole hierachy) of the given 
     * toolkit and the given toolkit itself.
     * @param toolkitId the id of a toolkit whose include hierachy is wanted. 
     * @param toolkits an empty List.
     * @return the given  toolkits List
     */
    private List getIncludesToolkits(String toolkitId, List toolkits) {
        toolkits.add(toolkitId);
        final ToolkitPluginDescriptor toolkitPluginDescriptor = 
            getToolkitPluginDescriptor(toolkitId);
        if (toolkitPluginDescriptor != null) {
            final String includes = toolkitPluginDescriptor.getIncludes();
            if (!EMPTY_EXTPOINT_ENTRY.equals(includes)) {
                getIncludesToolkits(includes, toolkits);
            }            
        }
        return toolkits;
    }
    
    /**
     * 
     * @param toolkitId a Toolkit id
     * @return a List of toolkit names which depends on the given toolkit id.
     */
    private List getDependsToolkitIds(String toolkitId) {
        final List dependsToolkits = new ArrayList();
        for (Iterator toolkitIdsIt = m_toolkitDescriptors.keySet().iterator();
            toolkitIdsIt.hasNext();) {
            
            final String tkId = (String)toolkitIdsIt.next();
            final ToolkitPluginDescriptor tkDescr = (ToolkitPluginDescriptor)
                m_toolkitDescriptors.get(tkId);
            if (toolkitId.equals(tkDescr.getDepends())) {
                dependsToolkits.add(tkId);
            }
        }
        return dependsToolkits;
    }
    
    
    /**
     * Gets all Component-Types of the given toolkit, its "Includes-Toolkits"
     * and "Depends-Toolkits".
     * @param toolkitId a toolkit id
     * @return A List of Component-Types
     */
    public String[] getComponentTypes(String toolkitId) {
        final List compTypes = new ArrayList();
        List toolkits = new ArrayList();
        toolkits = getIncludesToolkits(toolkitId, toolkits);
        toolkits.addAll(getDependsToolkitIds(toolkitId));
        for (Iterator compIter = getComponents().iterator(); compIter
            .hasNext();) {
            
            final Component comp = (Component)compIter.next();
            final String compToolkitId = comp.getToolkitDesriptor()
                .getToolkitID();
            if (!comp.isExtender() && toolkits.contains(compToolkitId)) {
                compTypes.add(comp.getType());
            }
        }
        return (String[])compTypes.toArray(new String[compTypes.size()]);
    }
    

    
   /**
    * 
    * @return a <code>String</code> Array of Event Types.
    */
    public Map getEventTypes() {
        return m_eventTypes;
    }
    

    /**
     * Checks if there are multiple components with the same type.
     * 
     * @param component
     *            A
     *            <code>org.eclipse.jubula.tools.xml.businessmodell.Component</code>
     *            object
     * 
     */
    private void check(Component component) {
        Iterator it = getComponents().iterator();
        while (it.hasNext()) {
            Component current = (Component)it.next();
            if (current.getType().equals(component.getType())) {
                final String msg = "multiple definition of component type " //$NON-NLS-1$
                        + component.getType();
                log.error(msg);
                throw new GDConfigXmlException(msg, 
                    MessageIDs.E_MULTIPLE_COMPONENT);
            }
        }
    }
    
    /**
     * Adds all passed components to <code>m_components</code>. The method
     * <code>check()</code> is called for each component in the list.
     * 
     * @param components
     *            The components to add
     */
    private void addAll(List components) {
        for (Iterator it = components.iterator(); it.hasNext();) {
            Component component = (Component)it.next();
            check(component);
            getComponents().add(component);
            m_componentsByType.put(component.getType(), component);
            m_componentsByTypeLowerCase.put(component.getType().toLowerCase(),
                    component);
        }
    }
    /**
     * Adds an Event Type to the List.
     * @param eventType a <code>String</code> object which discribes the Event Type.
     * @param reentryProp The reentry property (<code>Integer</code> object).
     * 
     */
    public void addEventType(String eventType, Integer reentryProp) {
        m_eventTypes.put(eventType, reentryProp);
    }
    
    /**
     * 
     * @param toolkitId the id of the toolkit
     * @param descriptor the {@link ToolkitPluginDescriptor}
     */
    public void addToolkitPluginDescriptor(String toolkitId, 
        ToolkitPluginDescriptor descriptor) {
        
        if (m_toolkitDescriptors == null) {
            m_toolkitDescriptors = new HashMap();
        }
        m_toolkitDescriptors.put(toolkitId, descriptor);
    }
    
    /**
     * 
     * @param toolkitId the id of the toolkit
     * @return the {@link ToolkitPluginDescriptor} for the given toolkit ID,
     *         or <code>null</code> if the given toolkit ID does not have a
     *         corresponding active plugin.
     */
    public ToolkitPluginDescriptor getToolkitPluginDescriptor(
        String toolkitId) {
        if (m_toolkitDescriptors == null) {
            m_toolkitDescriptors = new HashMap();
        }
        return (ToolkitPluginDescriptor)m_toolkitDescriptors.get(toolkitId);
    }
    
    /**
     * @return the {@link ToolkitPluginDescriptor}s of all toolkits
     */
    public List getAllToolkitPluginDescriptors() {
        if (m_toolkitDescriptors == null) {
            m_toolkitDescriptors = new HashMap();
        }
        return new ArrayList(m_toolkitDescriptors.values());
    }
   
    /**
     * @param level Only Toolkits with this level will be returned. May be
     *              <code>null</code>, in which case independent toolkits
     *              for all levels will be returned.
     * @return the {@link ToolkitPluginDescriptor}s of all independent 
     *         toolkits with the given level.
     */
    public List getIndependentToolkitPluginDescriptors(String level) {

        final String nullStr = "null"; //$NON-NLS-1$
        final String emptyStr = StringConstants.EMPTY;
        
        List toolkitDesriptors = getAllToolkitPluginDescriptors();
        
        Collections.sort(toolkitDesriptors);

        Iterator descIt = toolkitDesriptors.iterator();

        // Remove all non-independant and invalid toolkits
        while (descIt.hasNext()) {
            ToolkitPluginDescriptor desc = 
                (ToolkitPluginDescriptor)descIt.next();

            final String includes = desc.getIncludes();
            String toolkitID = desc.getToolkitID();

            boolean removeDueToToolkitLevel = 
                level != null && !level.equals(desc.getLevel());
            if (removeDueToToolkitLevel
                    || (!ToolkitConstants.LEVEL_ABSTRACT.equals(level)
                           && (emptyStr.equals(includes) 
                                || nullStr.equals(includes.toLowerCase())
                                || emptyStr.equals(toolkitID) 
                                || nullStr.equals(toolkitID.toLowerCase())))) {

                descIt.remove();
            }

        }
        
        return toolkitDesriptors;
    }
    
    /**
     * @return A List of all DataTypes of the Actions.
     */
    public Set getDataTypes() {
        if (m_dataTypes != null && !m_dataTypes.isEmpty()) {
            return m_dataTypes;
        }
        m_dataTypes = new HashSet();
        final List components = getComponents();
        for (Iterator compIt = components.iterator(); compIt.hasNext();) {
            final Component component = (Component)compIt.next();
            final List actions = component.getActions();
            for (Iterator actIt = actions.iterator(); actIt.hasNext();) {
                final Action action = (Action)actIt.next();
                final List params = action.getParams();
                for (Iterator paramIt = params.iterator(); paramIt.hasNext();) {
                    final Param param = (Param)paramIt.next();
                    final String type = param.getType();
                    m_dataTypes.add(type);
                }
            }
        }
        return m_dataTypes;
    }
    
    
    /**
     * Returns the component with the specified typeName.
     * 
     * @param typeName
     *            Name of the specified component (the I18N key).
     * @return the specified Component.
     */
    public Component findComponent(String typeName) {
        Validate.notNull(typeName);
        
        if (StringConstants.EMPTY.equals(typeName)) {
            if (log.isDebugEnabled()) {
                log.debug("CompSystem.findComponent(...) called with empty String. Returning InvalidComponent."); //$NON-NLS-1$
            }
            return new InvalidComponent();
        }
        
        Component comp = (Component)m_componentsByType.get(typeName);
        if (comp != null) {
            return comp;
        }
        
        if (log.isDebugEnabled()) {
            String translatedName = CompSystemI18n.getString(typeName);
            String message = "Component " + translatedName + " does not exist"; //$NON-NLS-1$ //$NON-NLS-2$
            log.debug(message);
        }
        
        return new InvalidComponent();
    }
    
    /**
     * Returns the components with the specified typeName.
     * 
     * @param typeName
     *            Name of the specified component (the I18N key).
     * @return the specified Components.
     */
    public List findComponents(String typeName) {
        Validate.notNull(typeName);
        List comps = new LinkedList();
        if (StringConstants.EMPTY.equals(typeName)) {
            if (log.isDebugEnabled()) {
                log.debug("CompSystem.findComponent(...) called with empty String. Returning InvalidComponent."); //$NON-NLS-1$
            }
            comps.add(new InvalidComponent());
            return comps;
        }
        
        Iterator it = getComponents().iterator();

        while (it.hasNext()) {
            Component comp = (Component) it.next();
            if (comp instanceof ConcreteComponent) {
                ConcreteComponent ccomp = (ConcreteComponent)comp;
                if (typeName.equals(ccomp.getComponentClass())) {
                    comps.add(ccomp);
                }
            }            
        }
        if (!(comps.isEmpty())) {
            return comps;
        }
        
        if (log.isDebugEnabled()) {
            String translatedName = CompSystemI18n.getString(typeName);
            String message = "Component " + translatedName + " does not exist"; //$NON-NLS-1$ //$NON-NLS-2$
            log.debug(message);
        }
        comps.add(new InvalidComponent());
        return comps;
    }

    /**
     * Returns a string representation of the component system object.
     * 
     * @return String
     */
    public String toString() {
        return new ToStringBuilder(this).append(
                "Abstract comps", m_abstractComponents) //$NON-NLS-1$
                .append("Concrete comps", m_concreteComponents) //$NON-NLS-1$
                .toString();
    }
    
    
    /**
     * Adds all Components of the given CompSystem to this CompSystem
     * @param compSystem the CompSystem to merge
     */
    public void merge(CompSystem compSystem) {
        if (!m_initialized) {
            init();
        }
        if (compSystem.m_abstractComponents != null) {
            m_abstractComponents.addAll(compSystem.m_abstractComponents);
        }
        if (compSystem.m_concreteComponents != null) {
            m_concreteComponents.addAll(compSystem.m_concreteComponents);
        }
        if (compSystem.m_toolkitDescriptors != null) {
            m_toolkitDescriptors.putAll(compSystem.m_toolkitDescriptors);
        }
    }
    
    /**
     * make all Actions available at all realizing (derived) Components; build
     * the lists of components (realizing concrete Components)
     * 
     */
    public void postProcess() {
        if (!m_initialized) {
            init();
        }
        addAll(m_concreteComponents);
        addAll(m_abstractComponents);
        
        for (Iterator it = getComponents().iterator(); it.hasNext();) {
            Component component = (Component)it.next();
            component.completeActions(this);
            handleRealizer(component);
            handleExtender(component);
            handleDepender(component);
        }
        validateComponents();
        collectDefaultMappingNames();
        
        for (Iterator it = getAbstractComponents().iterator(); it.hasNext();) {
            Component component = (Component)it.next();
            if (component.getRealized().isEmpty()) {
                m_mostAbstractComponent = component;
                break;
            }
        }
    }
    
    /**
     * Handles independent Components (no realizer, no extender) which are added
     * via user plugin to an existing plugin.
     * Those Components are added to the depending toolkit.
     * @param component A Component.
     */
    private void handleDepender(Component component) {
        final ToolkitPluginDescriptor toolkitDesriptor = component
            .getToolkitDesriptor();
        final String depends = toolkitDesriptor.getDepends();
        if (!component.isExtender()
            && !component.isRealizer()
            && !EMPTY_EXTPOINT_ENTRY.equals(depends)
            && toolkitDesriptor.isUserToolkit()) {
            
            final ToolkitPluginDescriptor dependsDescr = 
                (ToolkitPluginDescriptor)m_toolkitDescriptors.get(depends);
            if (dependsDescr != null) {
                component.setToolkitDesriptor(dependsDescr);
            }
            
        }
    }
    /**
     * Validates the Components.
     */
    private void validateComponents() {
        for (Iterator it = m_abstractComponents.iterator(); it.hasNext();) {
            AbstractComponent ac = (AbstractComponent)it.next();
            if (ac.getRealizers().isEmpty()) {
                String message = "AbstractComponent " + ac.getType() //$NON-NLS-1$
                    + " has no realizing concreteComponents";  //$NON-NLS-1$
                if (ac.isVisible()) {
                    log.error("visible " + message); //$NON-NLS-1$
                    throw new GDConfigXmlException("visible " + message, //$NON-NLS-1$
                        MessageIDs.E_NO_ABSTRACT_COMPONENT); 
                }
                log.warn(message);
            }
        }
        for (Iterator it = m_concreteComponents.iterator(); it.hasNext();) {
            final ConcreteComponent cc = (ConcreteComponent)it.next();
            if (cc.isExtender() && !StringUtils.isBlank(
                    cc.getComponentClass())) {
                // extender must not have a componentClass!
                final String msg = "Extending ConcreteComponent '" //$NON-NLS-1$
                    + cc.getType() + "' must not have a componentClass!"; //$NON-NLS-1$
                log.error(msg);
                throw new GDConfigXmlException(msg, 
                    MessageIDs.E_GENERAL_COMPONENT_ERROR);
            }
        }
    }
    
    
    /**
     * Resolves the "realized" relation.
     * @param component a Component.
     */
    private void handleRealizer(Component component) {
        final boolean isConcrete = component.isConcrete();
        Set realizedSet = component.getAllRealized();
        for (Iterator realIt = realizedSet.iterator(); realIt.hasNext();) {
            Component realized = (Component)realIt.next();
            if (isConcrete) {
                realized.addRealizer((ConcreteComponent)component);
            }
            realized.addAllRealizer(component);
        }
        if (isConcrete) {
            component.addRealizer((ConcreteComponent)component);
        }
        component.addAllRealizer(component);
    }
    
    /**
     * Handles a Component which extends another Component and adds its Actions
     * and its tester class to the extended Component.
     * @param component the extending Component.
     */
    private void handleExtender(Component component) {
        if (component.isExtender()) {
            final List extenderActions = component.getActions();
            final List extendedTypes = component.getExtendedTypes();
            final boolean isExtenderVisible = component.isVisible();
            for (Iterator extTypesIt = extendedTypes.iterator();
                extTypesIt.hasNext();) {
                
                final String extendedType = (String)extTypesIt.next();
                final Component extendedComponent = findComponent(extendedType);
                if (!extendedComponent.isVisible()) {
                    extendedComponent.setVisible(isExtenderVisible);
                }
                for (Iterator extActionsIt = extenderActions.iterator();
                    extActionsIt.hasNext();) {
                    
                    final Action extenderAction = (Action)extActionsIt.next();
                    extendedComponent.addAction(extenderAction);
                    if (component instanceof ConcreteComponent
                        && extendedComponent instanceof ConcreteComponent) {
                        
                        final ConcreteComponent extender = (ConcreteComponent)
                            component;
                        final ConcreteComponent extended = (ConcreteComponent)
                            extendedComponent;
                        extended.setTesterClass(extender.getTesterClass());
                    }
                }
            }
        }
    }
    
    /**
     * @return Returns the configVersion.
     */
    public ConfigVersion getConfigVersion() {
        return m_configVersion;
    }
    
    /**
     * Collects all default mapping logical names.
     */
    private void collectDefaultMappingNames() {
        m_defaultMappingNames = new HashMap();
        final List concreteComponents = getConcreteComponents();
        for (Iterator it = concreteComponents.iterator(); it.hasNext();) {
            final ConcreteComponent concComp = (ConcreteComponent)it.next();
            if (concComp.hasDefaultMapping()) {
                final String technicalName = concComp.getDefaultMapping()
                    .getTechnicalName();
                final String componentType = concComp.getType();
                m_defaultMappingNames.put(technicalName, componentType);
            }
        }
    }
    
    /**
     * 
     * @return the default mapping logical names.
     */
    public final Map getDefaultMappingNames() {
        return m_defaultMappingNames;
    }
    
    /**
     * 
     * @param type a Component Type.
     * @return The Component of the given Component Type or null if no 
     * Component was found.
     */
    private Component getComponentForType(String type) {
        return (Component)m_componentsByTypeLowerCase.get(type.toLowerCase());
    }
    
    /**
     * 
     * @return The most abstract component in the hierarchy.
     */
    public final Component getMostAbstractComponent() {
        return m_mostAbstractComponent;
    }
    
    /**
     * 
     * @param type1 a Component Type.
     * @param type2 a Component Type.
     * @return the more concrete Component Type or null if the given 
     * Component Types are incompatible.
     */
    public final String getMoreConcreteType(String type1, String type2) {
        final Component comp1 = getComponentForType(type1);
        final Component comp2 = getComponentForType(type2);
        final Component moreConcreteComp = getMoreConcreteComponent(comp1, 
                comp2);
        
        return moreConcreteComp != null ? moreConcreteComp.getType() : null;
    }
    
    /**
     * 
     * @param components The Components to check. May be empty but may not be 
     *                   <code>null</code>.
     * @return the most concrete of the given components, or <code>null</code>
     *         if any of the given components are incompatible or if 
     *         <code>components</code> is empty.
     */
    public final Component getMostConcrete(Component [] components) {
        Component mostConcrete = null;
        if (components.length > 0) {
            mostConcrete = components[0];
        }
        for (int i = 1; i < components.length && mostConcrete != null; i++) {
            mostConcrete = 
                getMoreConcreteComponent(mostConcrete, components[i]);
        }
        
        return mostConcrete;
    }
    
    /**
     * 
     * @param comp1 a Component
     * @param comp2 a Component
     * @return the more conrete Component or null if the given Components are 
     * incompatible.
     */
    private Component getMoreConcreteComponent(Component comp1, 
            Component comp2) {
        
        return getMoreConcreteComponentImpl(comp1, comp2, true);
    }
    
    
    /**
     * 
     * @param comp1 a Component
     * @param comp2 a Component
     * @param isFirstCall caller should set this to true always!
     * @return the more conrete Component or null if the given Components are 
     * incompatible.
     */
    private Component getMoreConcreteComponentImpl(Component comp1, 
            Component comp2, boolean isFirstCall) {
        
        if (comp1 == null || comp2 == null) {
            return null;
        }
        if (comp1.equals(comp2)) {
            return comp1;
        }
        final String comp2Type = comp2.getType();
        for (Iterator realizerIt = comp1.getAllRealizers().iterator(); 
            realizerIt.hasNext();) {

            final Component realizer = (Component)realizerIt.next();
            if (realizer.getType().equals(comp2Type)) {
                return realizer;
            }
        }
        // if comp2 is not more concrete than comp1, try inverted search:
        return isFirstCall ? getMoreConcreteComponentImpl(comp2, comp1, false) 
                : null;
    }
    
    /**
     * Checks if the given realizingType is realizing the give realizedType.
     * @param realizingType the realizingType to check.
     * @param realizedType the realizedType.
     * @return true if the realizingType is realizing the realizedType, false 
     * otherwise.
     */
    public final boolean isRealizing(String realizingType, 
            String realizedType) {
        if (realizingType.equals(realizedType)) {
            return true;
        }
        final Component realizer = findComponent(realizingType);
        return realizer.isRealizing(realizedType);
    }
    
    /**
     * 
     * @param toolkitLevel true if only toolkits with level "toolkit" 
     * are wanted, false if all toolkits are wanted.
     * @return a List of I18N names of the toolkits or a List
     * of toolkit IDs.
     */
    public List getIndependentToolkitPluginDescriptors(boolean toolkitLevel) {
        String level = toolkitLevel ? ToolkitConstants.LEVEL_TOOLKIT : null;
        return getIndependentToolkitPluginDescriptors(level);
    }

    /**
     * 
     * @param componentClassName The FQN (fully qualified name) of the 
     *                           supported class for the Component. 
     * @param availableComponents Components through which to search.
     * @return The Component from the provided collection that supports the
     *         given class name, or <code>null</code> if no such Component
     *         is found.
     */
    public static String getComponentType(
            String componentClassName, Collection availableComponents) {

        Validate.notNull(componentClassName);
        Validate.allElementsOfType(availableComponents, Component.class);
        
        for (Iterator compIter = availableComponents.iterator(); compIter
                .hasNext();) {
            Component currentComp = (Component)compIter.next();
            if (currentComp instanceof ConcreteComponent
                    && componentClassName.equals(
                        ((ConcreteComponent)currentComp).getComponentClass())) {
                
                return currentComp.getType();
            }
        }
        
        return null;
    }
}
