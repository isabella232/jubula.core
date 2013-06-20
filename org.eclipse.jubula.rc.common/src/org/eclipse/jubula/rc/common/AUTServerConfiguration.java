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
package org.eclipse.jubula.rc.common;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.businessprocess.ReflectionBP;
import org.eclipse.jubula.rc.common.classloader.ImplClassClassLoader;
import org.eclipse.jubula.rc.common.exception.UnsupportedComponentException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.tools.exception.ConfigXmlException;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.objects.MappingConstants;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.ComponentClass;
import org.eclipse.jubula.tools.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.xml.businessmodell.Profile;


/**
 * This class contains the configuration for the AUTServer at runtime. It's a
 * singleton. <br>
 * 
 * It comprises the supported components (with their implementation classes).
 * <br>
 * The implementation classes are cached.
 * 
 * @author BREDEX GmbH
 * @created 26.08.2004
 */
public class AUTServerConfiguration {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        AUTServerConfiguration.class);
    
    /** the singleton instance */
    private static AUTServerConfiguration instance = null;
    
    /** the map for the implementation class names: 
     * key = component class name, value = implementation class name
     */
    private Map m_implClassNames;
    
    /**
     * A cache for implementation class instances
     * key = implementation class name value = implementation class instance
     */
    private Map m_implClassCache;
    
    /**
     * profile for fuzzy logic
     */
    private Profile m_profile;
    
    /**
     * keyboard shortcut for starting Mapping/Records Mode Modifier
     */
    private int m_keyMod;

    /**
     * keyboard shortcut for starting Mapping/Record Mode
     */
    private int m_key;
    
    /**
     * mouse button for collecting a UI element in Mapping Mode
     */
    private int m_mouseButton;
    
    /**
     * keyboard shortcut for starting Mapping/Records Mode Modifier
     */
    private int m_key2Mod;

    /**
     * keyboard shortcut for starting Mapping/Record Mode
     */
    private int m_key2;

    /**
     * keyboard shortcut for starting Check Mode
     */
    private int m_checkModeKeyMod;

    /**
     * keyboard shortcut for starting Check Mode
     */
    private int m_checkModeKey;   
    
    /**
     * keyboard shortcut for check current component
     */
    private int m_checkCompKeyMod;

    /**
     * keyboard shortcut for check current component
     */
    private int m_checkCompKey;
    
    /**
     * singleLineTrigger for Observation Mode
     */
    private SortedSet m_singleLineTrigger;

    /**
     * multiLineTrigger for Observation Mode
     */
    private SortedSet m_multiLineTrigger;
    
    /** which toolkit is selected for this project */
    private String m_projectToolkit;
    
    
    
    /**
     * the complete list of supported components, what actions are supported etc.
     */
    private List m_components;

    /**
     * the set of actually supported component class names
     */
    private Set m_supportedComponentTypes;
    
    /**
     * private constructor (singleton) <br>
     * initializes the cache 
     */
    private AUTServerConfiguration() {
        m_implClassNames = new HashMap();
        m_implClassCache = new HashMap();
        m_components = new ArrayList();
    }
    
    /**
     * @return Returns the instance.
     */
    public static AUTServerConfiguration getInstance() {
        if (instance == null) {
            instance = new AUTServerConfiguration();
        }
     
        return instance;
    }
    
    /**
     * returns true if the type of <code>component</code> is supported
     * 
     * @param graphicsComponent
     *            any instance of the component to check, must not be null
     * @throws IllegalArgumentException
     *             if component is null
     * @return true if the type of <code>component</code> is supported, false
     *         otherwise
     */
    public boolean isSupported(Object graphicsComponent)
        throws IllegalArgumentException {

        Validate.notNull(graphicsComponent,
            "graphics component must not be null"); //$NON-NLS-1$
        final String className = graphicsComponent.getClass().getName();
        if (className.equals(MappingConstants
                .SWING_MENU_DEFAULT_MAPPING_CLASSNAME)) {
            
            return true;
        }
        return m_implClassNames.containsKey(className);
    }
    
    /**
     * Registers an instance of the implementation class for a component.
     * 
     * @param componentClassName
     *            The name of the component, e.g.
     *            <code>javax.swing.JButton</code>
     * @param implClassName
     *            The name of the implementation class
     * @throws IllegalArgumentException
     *             if the <code>componentClassName</code> or the
     *             <code>implClassName</code> is <code>null</code>.
     */
    private void registerImplementationClass(String componentClassName,
        String implClassName) throws IllegalArgumentException {
        
        Validate.notNull(componentClassName);
        Validate.notNull(implClassName);
        
        m_implClassNames.put(componentClassName, implClassName);   
    }
    
    /**
     * Returns an instance of the implementation class for
     * <code>componentClassName</code>.
     * 
     * @param componentClass
     *            the class name of the component, e.g javax.swing.JButton
     * @throws UnsupportedComponentException
     *             If the <code>componentClassName</code> has no registered
     *             implementation class.
     * @throws IllegalArgumentException
     *             if the <code>componentClassName</code> is <code>null</code>.
     * @return An instance of the implementation class.
     */
    public Object getImplementationClass(Class componentClass) 
        throws UnsupportedComponentException, 
        IllegalArgumentException {
        
        Validate.notNull(componentClass);
        Class currentClass = componentClass;
        
        String implClassName = (String)m_implClassNames.get(
            currentClass.getName());
        while (implClassName == null && currentClass.getSuperclass() != null) {
            currentClass = currentClass.getSuperclass();
            implClassName = (String)m_implClassNames.get(
                currentClass.getName());
        }
        if (implClassName != null) {
            Class implClass = null;
            Object implInstance = m_implClassCache.get(implClassName);
            if (implInstance != null) {
                return implInstance;
            }
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Loading ImplementationClass: "  //$NON-NLS-1$
                        + "'" + String.valueOf(implClassName) + "'"  //$NON-NLS-1$ //$NON-NLS-2$
                        + " with ClassLoader: " //$NON-NLS-1$
                        + "'" + String.valueOf(currentClass.getClassLoader()) + "'"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                implClass = loadImplementationClass(implClassName, 
                    currentClass.getClassLoader());
                implInstance = implClass.newInstance();
                if (!m_implClassCache.containsKey(implClassName)) {
                    m_implClassCache.put(implClassName, implInstance);
                }
            } catch (InstantiationException ie) {
                log.error(ie);
                throw new UnsupportedComponentException(
                    "component '" + implClass.getName() //$NON-NLS-1$
                    + "' could not be instantiated", MessageIDs.E_INSTANTIATION);  //$NON-NLS-1$
            } catch (IllegalAccessException iae) {
                log.error(iae);
                throw new UnsupportedComponentException(
                    "component '" + implClass.getName() //$NON-NLS-1$
                    + "' could not be accessed", MessageIDs.E_ILLEGAL_ACCESS);  //$NON-NLS-1$
            } catch (ClassNotFoundException cnfe) {
                log.error(cnfe);
                throw new UnsupportedComponentException(
                    "component '" + componentClass.getName() //$NON-NLS-1$
                    + "' is not supported: implementation class '" //$NON-NLS-1$
                    + implClassName
                    + "' not found", MessageIDs.E_COMPONENT_UNSUPPORTED); //$NON-NLS-1$
            } 
            return implInstance;
        }
        throw new UnsupportedComponentException(
            "component '" + componentClass.getName() //$NON-NLS-1$
            + "' is not supported", MessageIDs.E_COMPONENT_UNSUPPORTED); //$NON-NLS-1$
    }

    /**
     * Loads the ImplementationClass
     * @param implClassName the name of the ImplementationClass
     * @param componentClassLoader the ClassLoader of the Component
     * @return the loaded ImplementationClass
     * @throws ClassNotFoundException in case of class not found
     */
    private Class loadImplementationClass(String implClassName, 
        ClassLoader componentClassLoader) throws ClassNotFoundException {
        
        if (AUTServer.getInstance().isRcpAccessible()) {
            return this.getClass().getClassLoader().loadClass(implClassName);
        }
        URLClassLoader thisCL = (URLClassLoader)this.getClass()
            .getClassLoader();
        ClassLoader implCL = new ImplClassClassLoader(thisCL,
            componentClassLoader);
        return implCL.loadClass(implClassName);
    }

    /**
     * Returns the testable class for a given component class
     * <code>componentClassName</code>.
     * 
     * @param component the component, e.g javax.swing.JButton
     * @throws IllegalArgumentException If the <code>componentClassName</code> has no registered implementation class.
     * @return An instance of the implementation class.
     */
    public Class getTestableClass(
        Class component) throws IllegalArgumentException {
        
        Validate.notNull(component);
        Class componentClass = component;
        while (componentClass.getSuperclass() != null) {    
            String componentClassName = componentClass.getName();
            if (m_implClassNames.containsKey(componentClassName)) {
                return componentClass;
            }
            componentClass = componentClass.getSuperclass();
        }
        throw new IllegalArgumentException(
            "component '" + component.getName() //$NON-NLS-1$
            + "' is not supported"); //$NON-NLS-1$
    }
    
    
    /**
     * Prepares the implementation class, which has been registered for the
     * passed component class name, by setting the passed graphics component
     * instance. After this call, the action method of the returned
     * implementation class can be invoked.
     * @param graphicsComponent The graphics component.
     * @param componentClass the class name of the component, e.g javax.swing.JButton
     * @return The implementation class.
     * @throws UnsupportedComponentException If the component identified by the
     *             <code>componentClassName</code> is not supported, that
     *             means, if there is no mapped implementation class.
     * @throws IllegalArgumentException If the <code>graphicsComponent</code> or
     *             <code>componentClassName</code> is <code>null</code>.
     */
    public Object prepareImplementationClass(Object graphicsComponent,
        Class componentClass) throws UnsupportedComponentException, 
        IllegalArgumentException {
        
        Validate.notNull(graphicsComponent);
        Object implClass = getImplementationClass(componentClass);
        ReflectionBP.invokeMethod("setComponent", implClass, //$NON-NLS-1$
            new Class[] {Object.class}, 
            new Object[] {graphicsComponent});
        return implClass;
    }
    
    /**
     * @return Returns the components.
     */
    public List getComponents() {
        return m_components;
    }
    /**
     * Registers a component. An implementation class instance is created and registered.
     * 
     * @param c The component to register.
     * @throws IllegalArgumentException if the <code>componentClassName</code> or the
     *             <code>implClassName</code> is <code>null</code>.
     */
    public void registerComponent(ConcreteComponent c)
        throws IllegalArgumentException {

        m_components.add(c);
        registerImplementationClass(c.getComponentClass(), c.getTesterClass());
    }
    
    /**
     * Returns the component with the specified typeName.
     * @param typeName Name of the specified component.
     * @return the specified Component.
     */
    public Component findComponent(String typeName) {
        Validate.notNull(typeName);
        List list = getComponents();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Component comp = (Component) it.next();
            if (comp instanceof ConcreteComponent) {
                ConcreteComponent ccomp = (ConcreteComponent)comp;
                if (ccomp.getComponentClass() != null
                        && ccomp.getComponentClass().equals(typeName)) {
                    return ccomp;
                }
            } else {
                if (comp.getType().equals(typeName)) {
                    return comp;
                }
            }
        }
        String message = "Component " + typeName //$NON-NLS-1$
                + " does not exist"; //$NON-NLS-1$
        log.error(message);
        throw new ConfigXmlException(message, MessageIDs.E_NO_COMPONENT);
    }
    
    /**
     * Returns the components with the specified typeName.
     * @param typeName Name of the specified component.
     * @return the specified Components.
     */
    public List findComponents(String typeName) {
        Validate.notNull(typeName);
        List list = getComponents();
        Iterator it = list.iterator();
        List comps = new LinkedList();
        while (it.hasNext()) {
            Component comp = (Component) it.next();
            if (comp instanceof ConcreteComponent) {
                ConcreteComponent ccomp = (ConcreteComponent)comp;
                if (ccomp.getComponentClass() != null
                        && ccomp.getComponentClass().equals(typeName)) {
                    comps.add(ccomp);
                }
            }            
        }
        if (!(comps.isEmpty())) {
            return comps;
        }
        String message = "Component " + typeName //$NON-NLS-1$
                + " does not exist"; //$NON-NLS-1$
        log.error(message);
        throw new ConfigXmlException(message, MessageIDs.E_NO_COMPONENT);
    }
    
    /**
     * gets the jubula component name for a java component
     * @param component java component
     * @return jubula component type
     */
    public String getComponentName (Object component) {
        String gdCompName = null;        
        try {            
            String compType = getTestableClass(component.getClass()).getName();
            Component gdComp = findComponent(compType);
            while (gdComp != null && (!gdComp.isVisible()
                    || !gdComp.isObservable())
                    && !gdComp.getRealized().isEmpty()) {
                List realizedComponents = gdComp.getRealized();
                gdComp = (Component)realizedComponents.get(0);
            }
            if (gdComp.getType() != null) {
                gdCompName = CompSystemI18n.getString(gdComp.getType());
            }
        } catch (IllegalArgumentException iae) {
            log.error("componentClassname has no registrered implementation class", //$NON-NLS-1$
                    iae);
        }
        return gdCompName;
    }
    
    /**
     * @return Returns the profile.
     */
    public Profile getProfile() {
        return m_profile;
    }
    /**
     * @param p The profile to set.
     */
    public void setProfile(Profile p) {
        m_profile = p;
    }
    /**
     * @return Returns the key.
     */
    public int getKey() {
        return m_key;
    }
    /**
     * @param key The key to set.
     */
    public void setKey(int key) {
        m_key = key;
    }
    /**
     * @param mouseButton the mouseButton to set
     */
    public void setMouseButton(int mouseButton) {
        m_mouseButton = mouseButton;
    }

    /**
     * @return the mouseButton
     */
    public int getMouseButton() {
        return m_mouseButton;
    }

    /**
     * @return Returns the keyMod.
     */
    public int getKeyMod() {
        return m_keyMod;
    }
    /**
     * @param keyMod The keyMod to set.
     */
    public void setKeyMod(int keyMod) {
        m_keyMod = keyMod;
    }

    /**
     * @return Returns the key for Application record
     */
    public int getKey2() {
        return m_key2;
    }

    /**
     * @param key2 The key to set.
     */
    public void setKey2(int key2) {
        m_key2 = key2;
    }

    /**
     * @return Returns the keyMod for Application component.
     */
    public int getKey2Mod() {
        return m_key2Mod;
    }

    /**
     * @param mod the keyMod for Application component to set.
     */
    public void setKey2Mod(int mod) {
        m_key2Mod = mod;
    }

    /**
     * @return Returns the key for checkMode
     */
    public int getCheckModeKey() {
        return m_checkModeKey;
    }

    /**
     * @param checkModeKey The checkModeKey to set.
     */
    public void setCheckModeKey(int checkModeKey) {
        m_checkModeKey = checkModeKey;
    }

    /**
     * @return Returns the checkModeKeyMod for checkMode.
     */
    public int getCheckModeKeyMod() {
        return m_checkModeKeyMod;
    }

    /**
     * @param checkModeKeyMod the checkModeKeyMod to set.
     */
    public void setCheckModeKeyMod(int checkModeKeyMod) {
        m_checkModeKeyMod = checkModeKeyMod;
    }

    /**
     * @return the checkCompKeyMod
     */
    public int getCheckCompKeyMod() {
        return m_checkCompKeyMod;
    }

    /**
     * @param checkCompKeyMod the checkCompKeyMod to set
     */
    public void setCheckCompKeyMod(int checkCompKeyMod) {
        m_checkCompKeyMod = checkCompKeyMod;
    }

    /**
     * @return the checkCompKey
     */
    public int getCheckCompKey() {
        return m_checkCompKey;
    }

    /**
     * @param checkCompKey the checkCompKey to set
     */
    public void setCheckCompKey(int checkCompKey) {
        m_checkCompKey = checkCompKey;
    }
    
    /**
     * @return the singleLineTrigger for Observation Mode
     */
    public SortedSet getSingleLineTrigger() {
        return m_singleLineTrigger;
    }

    /**
     * @param singleLineTrigger singleLineTrigger for Observation Mode
     */
    public void setSingleLineTrigger(SortedSet singleLineTrigger) {
        m_singleLineTrigger = singleLineTrigger;
    }

    /**
     * @return the multiLineTrigger for Observation Mode
     */
    public SortedSet getMultiLineTrigger() {
        return m_multiLineTrigger;
    }

    /**
     * @param multiLineTrigger multiLineTrigger for Observation Mode
     */
    public void setMultiLineTrigger(SortedSet multiLineTrigger) {
        m_multiLineTrigger = multiLineTrigger;
    }

    /**
     * @return the projectToolkit
     */
    public String getProjectToolkit() {
        return m_projectToolkit;
    }

    /**
     * @param projectToolkit the projectToolkit to set
     */
    public void setProjectToolkit(String projectToolkit) {
        m_projectToolkit = projectToolkit;
    }
    
    /**
     * @return a set of supported type identifier
     */
    public Set getSupportedTypes() {
        if (m_supportedComponentTypes != null) {
            return m_supportedComponentTypes;
        }
        m_supportedComponentTypes = new HashSet();
        List supportedComponents = AUTServerConfiguration
                .getInstance().getComponents();
        Iterator iterator = supportedComponents.iterator();
        while (iterator.hasNext()) {
            ConcreteComponent c = (ConcreteComponent) iterator.next();
            if (!c.hasDefaultMapping()) {
                List ccl = c.getCompClass();
                Iterator compClassIterator = ccl.iterator();
                while (compClassIterator.hasNext()) {
                    ComponentClass cc = (ComponentClass) 
                            compClassIterator.next();
                    m_supportedComponentTypes.add(cc);
                }
            }
        }
        return m_supportedComponentTypes;
    }
}
