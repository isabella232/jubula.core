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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.businessprocess.IParamNameMapper;
import org.eclipse.jubula.client.core.persistence.HibernateUtil;


/**
 * @author BREDEX GmbH
 * @created Jul 13, 2010
 * 
 */
@Entity
@Table(name = "PARAM_INTERFACE")
class TestDataCubePO implements ITestDataCubePO {

    /** hibernate OID */
    private transient Long m_id = null;

    /** hibernate version id */
    private transient Integer m_version = null;

    /** the name by which the cube can be referenced */
    private String m_name = null;
    
    /**
     * <code>m_parameters</code>parameters for testcase
     */
    private List<IParamDescriptionPO> m_hbmParameterList = 
        new ArrayList<IParamDescriptionPO>();
    /**
     * <code>m_dataManager</code> dataManager for handling of testdata
     */
    private ITDManagerPO m_dataManager = null;

    /**
     * path to externalDataFile, could be Excel or sth. else
     */
    private String m_dataFile = null;
    
    /**
     * <code>m_completeTdMap</code>map to manage the information about testdata 
     * completeness for each supported language <br>
     * key: supported languages, Type: string (string presentation of Locale)
     * value: flag to label the completeness of testdata
     */
    @SuppressWarnings("unchecked") // because of XDoclet
    private transient Map<String, Boolean> m_completeTdMap = 
        new HashMap();
    
    /** the data cube referenced by this node */
    private IParameterInterfacePO m_referencedDataCube;

    /**
     * 0-arg constructor for Hibernate.
     */
    @SuppressWarnings("unused")
    private TestDataCubePO() {
        // For Hibernate
        // Nothing to initialize
    }

    /**
     * Constructor
     * 
     * @param name The reference name for the newly created object.
     */
    TestDataCubePO(String name) {
        setName(name);
        setDataManager(PoMaker.createTDManagerPO(this));
    }
    
    /**
     * 
     * @return parameters instance
     */
    @OneToMany(targetEntity = ParamDescriptionPO.class, 
               fetch = FetchType.EAGER, 
               cascade = CascadeType.ALL, 
               orphanRemoval = true)
    @JoinColumn(name = "PARAM_NODE")
    @OrderColumn(name = "IDX_PARAM_NODE")
    protected List<IParamDescriptionPO> getHbmParameterList() {
        return m_hbmParameterList;
    }   
    
    /**
     * Add a parameter description to the list of descriptions
     * @param p <code>ParamDescriptionPO</code> to be added
     */
    protected void addParameter(IParamDescriptionPO p) {
        getModifiableParameterList().add(p);
        getHbmDataManager().addUniqueId(p.getUniqueId());
    }
    /**
     * Get the ParameterList, create one if necessary
     * @return the normal List
     */
    @Transient
    private List<IParamDescriptionPO> getModifiableParameterList() {
        if (getHbmParameterList() == null) {
            setHbmParameterList(new ArrayList<IParamDescriptionPO>());
        }
        return getHbmParameterList();
    }

    /**
     * Remove a parameter description from the list of descriptions. This
     * is a method used by derived classes to work with the list.
     * @param p <code>ParamDescriptionPO</code> to be removed
     */
    protected void removeParameter(IParamDescriptionPO p) {
        getModifiableParameterList().remove(p);
        getHbmDataManager().removeUniqueId(p.getUniqueId());
    }
    /**
     * Gets the parameter with the given unique id
     * 
     * @param uniqueId uniqueId (GUID or I18NKey) of parameter
     * @return The parameter or <code>null</code>, if this node doesn't
     *         contain a parameter with the passed unique id
     */
    public IParamDescriptionPO getParameterForUniqueId(String uniqueId) {
        Validate.notNull(uniqueId, "The unique id must not be null"); //$NON-NLS-1$
        for (IParamDescriptionPO desc : getParameterList()) {
            if (uniqueId.equals(desc.getUniqueId())) {
                return desc;
            }
        }
        return null;
    }
    
    /**
     * gets the parameter description for the given parameter
     * @param paramName name of parameter 
     * @return paramDescription for given parameter
     */
    public IParamDescriptionPO getParameterForName(String paramName) {
        Validate.notNull(paramName, "Param name must not be null."); //$NON-NLS-1$
        for (IParamDescriptionPO desc : getParameterList()) {
            if (paramName.equals(desc.getName())) {
                return desc;
            }
        }
        return null;
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.IParamNodePO#getParamNames()
     */
    @Transient
    public List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        for (IParamDescriptionPO desc : getParameterList()) {
            paramNames.add(desc.getName());
        }
        return paramNames;
    }
    

    /**
     * Clears the parameter list.
     */
    protected final void clearParameterList() {
        getModifiableParameterList().clear();
        getDataManager().clearUniqueIds();
    }
    
    /**
     * @return an unmodifiable copy for further use
     */
    @Transient
    public List<IParamDescriptionPO> getParameterList() {
        List<IParamDescriptionPO> hbmParameterList = getHbmParameterList();
        if (hbmParameterList == null) {
            return Collections.emptyList();
        } 
        return Collections.unmodifiableList(hbmParameterList);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public ListIterator<IParamDescriptionPO> getParameterListIter() {
        if (getHbmParameterList() == null) {
            List<IParamDescriptionPO>emptyList = Collections.emptyList();
            return emptyList.listIterator();
        }
        return getHbmParameterList().listIterator();
    }
    /**
     * 
     * @return Size of ParameterList to prevent calls get getParamterList()
     * just to check if there are any parameters
     */
    @Transient
    public int getParameterListSize() {
        if (getHbmParameterList() == null) {
            return 0;
        } 
        return getHbmParameterList().size();        
    }
    
    /**
     * @param parameterList The parameterList to set.
     */
    protected void setHbmParameterList(
            List<IParamDescriptionPO> parameterList) {
        m_hbmParameterList = parameterList;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public ITDManagerPO getDataManager() {
        if (getReferencedDataCube() != null) {
            return getReferencedDataCube().getDataManager();
        }
        return getHbmDataManager();
    }
    
    /**
     * 
     * @return Returns the dataManager.
     */
    @OneToOne(cascade = CascadeType.ALL, 
              fetch = FetchType.EAGER, 
              targetEntity = TDManagerPO.class)
    @JoinColumn(name = "TD_MANAGER")
    protected ITDManagerPO getHbmDataManager() {
        return m_dataManager;
    }
    
    /**
     * Setter for internal data used by hibernate
     * @param dataManager data
     */
    protected void setHbmDataManager(ITDManagerPO dataManager) {
        m_dataManager = dataManager;        
    }
    
    /**
     * @param dataManager The dataManager to set.
     */
    public void setDataManager(ITDManagerPO dataManager) {
        setHbmDataManager(dataManager);
        m_dataManager.setParentProjectId(getParentProjectId());
    }
    
    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        for (IParamDescriptionPO paramDesc : getHbmParameterList()) {
            paramDesc.setParentProjectId(projectId);
        }
        if (getDataManager() != null 
                && getDataManager().getParentProjectId() == null) {
            getDataManager().setParentProjectId(projectId);
        }
    }

    /**
     * 
     * @return Returns the completeTdMap.
     */
    @Transient
    private Map<String, Boolean> getCompleteTdMap() {
        return m_completeTdMap;
    }
    
    /**
     * method to get the CompleteTdFlag for a given Locale
     * @param loc locale, for which to get the completeTdFlag
     * @return the state of completeTdFlag
     */
    public boolean getCompleteTdFlag(Locale loc) {
        Boolean value = getCompleteTdMap().get(loc.toString());
        return (value != null) ? value.booleanValue() : false;
    }
    
    /**
     * FIXME Katrin This method should not be public!
     * <b>Only use this for internal purposes</b>
     * method to set the completeTdFlag for a given Locale
     * @param loc  locale, for which to set the completeTdFlag
     * @param flag the state of completeTdFlag to set
     */
    public void setCompleteTdFlag(Locale loc, boolean flag) {
        getCompleteTdMap().put(loc.toString(), Boolean.valueOf(flag));
    }

    /**
     * FIXME Katrin This method should not be public!
     * <b>Only use this for internal purposes!</b>
     * method to reset complete TD Flags
     */
    @Transient
    public void resetCompleteTdFlag() {
        getCompleteTdMap().clear();
    }

    /**
     * sets the File
     * @param pathToExternalDataFile
     *      path to file
     */
    private void setHbmDataFile(String pathToExternalDataFile) {
        m_dataFile = pathToExternalDataFile;
    }
    
    
    /**
     * gets the value of the m_dataFile property
     * 
     * @return the name of the node
     */
    @Basic
    @Column(name = "DATA_FILE", length = 4000)
    private String getHbmDataFile() {
        return m_dataFile;
    }
    
    
    /**
     * gets the value of the m_dataFile property
     * 
     * @return the name of the node
     */
    @Transient
    public String getDataFile() {
        return getHbmDataFile();
    }

    /**
     * sets the File
     * @param pathToExternalDataFile
     *      path to file
     */
    public void setDataFile(String pathToExternalDataFile) {
        setHbmDataFile(pathToExternalDataFile);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public IParameterInterfacePO getReferencedDataCube() {
        return getHbmReferencedDataCube();
    }

    /**
     * {@inheritDoc}
     */
    public void setReferencedDataCube(IParameterInterfacePO dataCube) {
        setHbmReferencedDataCube(dataCube);
    }

    /**
     * 
     * @param dataCube The Data Cube to reference.
     */
    private void setHbmReferencedDataCube(IParameterInterfacePO dataCube) {
        m_referencedDataCube = dataCube;
    }
    
    
    /**
     *      
     * @return the referenced Data Cube, or <code>null</code> if no Data Cube 
     *         is referenced from this node.
     */
    @ManyToOne(targetEntity = TestDataCubePO.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "REF_DATA_CUBE")
    private IParameterInterfacePO getHbmReferencedDataCube() {
        return m_referencedDataCube;
    }

    /**
     *  
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    /**
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     *          
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "NAME", length = 4000)
    public String getName() {
        return m_name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() throws UnsupportedOperationException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public INodePO getSpecificationUser() {
        return null;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {
        return m_version;
    }
    /**
     * @param version The version to set.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }

    /** {@inheritDoc} */
    public IParamDescriptionPO addParameter(String type, String name,
            IParamNameMapper mapper) {
        return addParameter(type, name, HibernateUtil.generateGuid(), mapper);
    }

    /** {@inheritDoc} */
    public IParamDescriptionPO addParameter(String type, String name,
            String guid, IParamNameMapper mapper) {
        Validate.notEmpty(type, "Missing parameter type for TestDataCube " + //$NON-NLS-1$
                this.getName());
        Validate.notEmpty(name, "Missing name for parameter in " + //$NON-NLS-1$
                "TestDataCube " + this.getName()); //$NON-NLS-1$

        IParamDescriptionPO desc = PoMaker.createTcParamDescriptionPO(type,
                name, guid, mapper);
        addParameter(desc);
        return desc;
    }

    /** {@inheritDoc} */
    public IParamDescriptionPO addParameter(String type, String userDefName,
            boolean always, IParamNameMapper mapper) {
        IParamDescriptionPO desc = null;
        if (always || getParameterForName(userDefName) == null) {
            desc = addParameter(type, userDefName, mapper);
        }
        return desc;
    }

    /** {@inheritDoc} */
    public void moveParameter(String guId, int index) {
        final IParamDescriptionPO parameter = getParameterForUniqueId(guId);
        final List<IParamDescriptionPO> paramList = getHbmParameterList();
        final int currIdx = paramList.indexOf(parameter);
        paramList.remove(currIdx);
        paramList.add(index, parameter);
    }

    /** {@inheritDoc} */
    public void removeParameter(String uniqueId) {
        IParamDescriptionPO desc = getParameterForUniqueId(uniqueId);
        if (desc != null) {
            removeParameter(desc);
            ((TcParamDescriptionPO)desc).getParamNameMapper()
                .removeParamNamePO(desc.getUniqueId());
        }
    }
}
