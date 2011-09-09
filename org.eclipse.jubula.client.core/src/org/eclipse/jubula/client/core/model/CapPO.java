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
package org.eclipse.jubula.client.core.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.businessprocess.CapBP;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.xml.businessmodell.Action;
import org.eclipse.jubula.tools.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.Param;


/**
 * class to create CAPs in a testcase
 * 
 * @author BREDEX GmbH
 * @created 26.08.2004
 */
@Entity
@DiscriminatorValue(value = "C")
class CapPO extends ParamNodePO implements ICapPO {
    /**
     * guid of component name
     */
    private String m_componentNameGuid;

    /**
     * the type of component (e.g. JButton)
     */
    private String m_componentType;

    /**
     * the name of the associated action
     */
    private String m_hbmActionName;

    /**
     * reference to meta data tree from xml file
     */
    private transient CompSystem m_compSystem = null;

    /**
     * corresponding componentType of xml file
     */
    private transient Component m_metaComponentType = null;
    
    /**
     * <code>m_completeOmMap</code> manages the completeness Flag for OM
     * relating to the associated AUT key: AUTMainPO, value: completeness flag
     * for OM
     */
    private transient Map<IAUTMainPO, Boolean> m_completeOmMap = 
        new HashMap<IAUTMainPO, Boolean>();

    /**
     * corresponding action of xml file
     */
    private transient Action m_metaAction = null;

    /**
     * Used only by Persistence (JPA / EclipseLink)
     * 
     * @deprecated
     */
    CapPO() {
        // nothing
    }

    
    /**
     * Create a CapPO with all required data
     * @param capName Name (display)
     * @param componentNameGuid guid of component name
     * @param componentType type of component, abstract or concrete
     * @param actionName name of action (from XML config)
     * @param isGenerated indicates whether this node has been generated
     */
    CapPO(String capName, String componentNameGuid, String componentType,
            String actionName, boolean isGenerated) {
        
        super(capName, isGenerated);
        validateCAP(capName, componentNameGuid, componentType, actionName);
        init(componentNameGuid, componentType, actionName);
    }

    /**
     * Create a CapPO with all required data
     * @param capName capName Name (display)
     * @param componentNameGuid guid of component name
     * @param componentType type of component, abstract or concrete
     * @param actionName name of action (from XML config)
     * @param project the Project of this CAP
     * @param isGenerated indicates whether this node has been generated
     */
    CapPO(String capName, String componentNameGuid, String componentType,
        String actionName, IProjectPO project, boolean isGenerated) {
        
        this(capName, componentNameGuid, componentType, actionName, 
                isGenerated);
        createDefaultValues(project);
        setParentProjectId(project.getId());
    }
    
    /**
     * Create a CapPO with all required data
     * @param capName capName Name (display)
     * @param componentNameGuid guid of component name
     * @param componentType type of component, abstract or concrete
     * @param actionName name of action (from XML config)
     * @param guid the GUID of this CAP
     * @param isGenerated indicates whether this node has been generated
     */
    CapPO(String capName, String componentNameGuid, String componentType,
        String actionName, String guid, boolean isGenerated) {
        
        super(capName, guid, isGenerated);
        validateCAP(capName, componentNameGuid, componentType, actionName);
        init(componentNameGuid, componentType, actionName);
    }

    /**
     * Create a CapPO with all required data
     * @param capName capName Name (display)
     * @param componentNameGuid guid of component name
     * @param componentType type of component, abstract or concrete
     * @param actionName name of action (from XML config)
     * @param project the Project of this CAP
     * @param guid the GUID of this CAP
     * @param isGenerated indicates whether this node has been generated
     */
    CapPO(String capName, String componentNameGuid, String componentType,
        String actionName, IProjectPO project, String guid, 
        boolean isGenerated) {
        
        this(capName, componentNameGuid, componentType, actionName, guid, 
                isGenerated);
        createDefaultValues(project);
        setParentProjectId(project.getId());
    }

    /**
     * @param componentNameGuid guid of component name
     * @param componentType type of component, e.g. JButton
     * @param actionName name of action
     */
    private void init(String componentNameGuid, String componentType, 
        String actionName) {
        
        setComponentName(componentNameGuid);
        setComponentType(componentType);
        setActionName(actionName);
    }

    /**
     * validates the arguments of CapPO
     * 
     * @param capName
     *            name of CapPO
     * @param componentName
     *            name of component
     * @param componentType
     *            type of component
     * @param actionName
     *            name of action
     */
    private void validateCAP(String capName, String componentName,
            String componentType, String actionName) {
        Validate.notEmpty(capName, Messages.MissingNameForCapPO);
        Validate
                .notEmpty(componentName, 
                    Messages.MissingComponentNameForComponent);
        Validate
                .notEmpty(componentType, 
                        Messages.MissingComponentNameForComponent);
        Validate.notEmpty(actionName, 
                Messages.MissingComponentNameForComponent + componentName);
        getMetaDataFromXmlDescr(componentType, actionName);
    }

    /**
     * @param componentType
     *            type of component (e.g. Button)
     * @param actionName
     *            name for action
     */
    private void getMetaDataFromXmlDescr(String componentType, 
        String actionName) {       
        m_compSystem = ComponentBuilder.getInstance().getCompSystem();
        m_metaComponentType = m_compSystem.findComponent(componentType);
        m_metaAction = m_metaComponentType.findAction(actionName);        
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "COMP_NAME")
    public String getComponentName() {
        return m_componentNameGuid;
    }

    /**
     * {@inheritDoc}
     */
    public void setComponentName(String name) {
        m_componentNameGuid = name;
    }

    /**
     * 
     * @return Returns the type.
     */
    @Basic
    @Column(name = "COMP_TYPE", length = 4000)
    public String getComponentType() {
        return m_componentType;
    }

    /**
     * @param type
     *            The type to set.
     *  
     */
    public void setComponentType(String type) {
        Validate.notEmpty(type);
        m_componentType = type;
        setMetaComponentType(null);
        getMetaComponentType();
    }

    /**
     * @return Returns the metaAction.
     */
    @Transient
    public Action getMetaAction() {
        if (m_metaAction == null) {
            m_metaAction = getMetaComponentType()
                    .findAction(getHbmActionName());
        }
        return m_metaAction;
    }

    
    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isValid() {
        getMetaComponentType();
        return m_metaComponentType.isValid();
    }


    /**
     * @param metaAction
     *            The metaAction to set.
     */
    private void setMetaAction(Action metaAction) {
        m_metaAction = metaAction;
    }

    /**
     * 
     * @return Returns the metaComponent.
     */
    @Transient
    public Component getMetaComponentType() {
        if (m_metaComponentType == null) {            
            CompSystem compSystem = ComponentBuilder.getInstance()
                .getCompSystem();
            m_metaComponentType = compSystem.findComponent(
                getComponentType());
            Validate.notNull(getComponentType(), Messages.ComponentTypeIsNull);
        }
        return m_metaComponentType;
    }

    /**
     * 
     * @return Returns the actionName.
     */
    @Basic
    @Column(name = "COMP_ACTION", length = 4000)
    private String getHbmActionName() {
        return m_hbmActionName;
    }

    /**
     * @param hbmActionName Persistence (JPA / EclipseLink)
     */
    private void setHbmActionName(String hbmActionName) {
        m_hbmActionName = hbmActionName;
    }
    
    /**
     * @return action name
     */
    @Transient
    public String getActionName() {
        return getHbmActionName();
    }
    /**
     * @param actionName
     *            The actionName to set.
     */
    public void setActionName(String actionName) {
        Validate.notEmpty(actionName, Messages.MissingActionForComponent
                + getComponentName());
        setHbmActionName(actionName);
        setMetaAction(null);
        getMetaAction();
        initParameterList();
    }

    /**
     * not to use for CAPs
     * 
     * {@inheritDoc}
     */
    public void addNode(INodePO childNode) {
        childNode.getName(); // only to use the parameter
        Assert.verify(false, Messages.NotAllowedToAddNodeToCapPO);
    }

    
    /** 
     * Create a parameter list depending on the type and action of this CAP.
     */
    private void initParameterList() {
        clearParameterList();
        List paramList = getMetaAction().getParams();
        Iterator it = paramList.iterator();
        while (it.hasNext()) {
            Param par = ((Param)it.next());
            IParamDescriptionPO desc = PoMaker.createCapParamDescriptionPO(
                par.getType(), par.getName());
            addParameter(desc);
        }
    }
    
    /**
     * creates the default values.
     * @param project the project of this CAP
     */
    private void createDefaultValues(IProjectPO project) {
        
        if (project.getLangHelper().getLanguageList().isEmpty()) {
            return;
        }
        Action action = CapBP.getAction(this);
        for (String paramName : action.getParamNames()) {
            Param parameter = action.findParam(paramName);
            String defaultValue = parameter.getDefaultValue();
            ITestDataPO testData = PoMaker.createTestDataPO();
            for (Locale locale : project.getLangHelper().getLanguageList()) {
                testData.setValue(locale, defaultValue, project);
            }
            getDataManager().updateCell(testData, 0, paramName);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void removeParameter(IParamDescriptionPO p) {
        Assert.verify(false, Messages.ItsNotAllowedToRemoveParametersFromCapPO);
    }

    /**
     * @param metaComponentType The metaComponentType to set.
     */
    private void setMetaComponentType(Component metaComponentType) {
        m_metaComponentType = metaComponentType;
    }

    /**
     * @param aut aut, for which the completeOMFlag is valid
     * @return Returns the completeOMFlag for given AUT
     */
    public boolean getCompleteOMFlag(IAUTMainPO aut) {
        if (aut != null) {
            Boolean value = getCompleteOmMap().get(aut);
            return value != null ? value.booleanValue() : false;
        }
        return true;
    }
    /**
     * FIXME Katrin This method should not be public!
     * <b>Only use this for internal purposes!</b>
     * @param aut aut, for which to set the completeOMFlag
     * @param completeOMFlag The completeOMFlag to set.
     */
    public void setCompleteOMFlag(IAUTMainPO aut, boolean completeOMFlag) {
        getCompleteOmMap().put(aut, Boolean.valueOf(completeOMFlag));
    }
    
    /**
     * @return Returns the completeOmMap.
     */
    @Transient
    private Map<IAUTMainPO, Boolean> getCompleteOmMap() {
        // lazy because of xml-serialization
        if (m_completeOmMap == null) {
            m_completeOmMap = new HashMap<IAUTMainPO, Boolean>();
        }
        return m_completeOmMap;
    }


    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.NodePO#isInterfaceLocked()
     */
    @Transient
    public Boolean isReused() {
        return getParentNode().isReused();
    }

    /**
     * {@inheritDoc}
     */
    public void changeCompName(String oldCompNameGuid, String newCompNameGuid) {
        setComponentName(newCompNameGuid);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getComponentType(IWritableComponentNameCache compNameCache, 
            Collection<Component> availableComponents) {

        return getComponentType();
    }
}
