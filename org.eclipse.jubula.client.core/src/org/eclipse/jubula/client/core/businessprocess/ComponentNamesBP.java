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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.businessprocess.treeoperations.CheckIfComponentNameIsReusedOp;
import org.eclipse.jubula.client.core.businessprocess.treeoperations.FindNodesForComponentNameOp;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IComponentNameData;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Acts as a Component Name cache for the master session.
 *
 * @author BREDEX GmbH
 * @created Apr 9, 2008
 */
public class ComponentNamesBP 
        extends AbstractNameBP<IComponentNamePO> 
        implements IComponentNameCache {
    
    /** i18n key for the "unknown" component type */
    public static final String UNKNOWN_COMPONENT_TYPE = "guidancer.abstract.Unknown"; //$NON-NLS-1$
    
    /**
     * <code>log</code> logger for class
     */
    private static Logger log = LoggerFactory.getLogger(ComponentNamesBP.class);
    
    /**
     * @author BREDEX GmbH
     * @created Apr 16, 2008
     */
    public enum CompNameCreationContext {
        /** ObjectMapping-Editor context*/
        OBJECT_MAPPING,
        /** Overriden name context */
        OVERRIDDEN_NAME,
        /** Test Step context */
        STEP;
        
        /** for toString and forName */
        private static final String OBJECT_MAPPING_CTX = "OBJECT_MAPPING"; //$NON-NLS-1$
        
        /** for toString and forName */
        private static final String OVERRIDDEN_NAME_CTX = "OVERRIDDEN_NAME"; //$NON-NLS-1$
        
        /** for toString and forName */
        private static final String STEP_CTX = "STEP"; //$NON-NLS-1$
        
        /**
         * 
         * {@inheritDoc}
         */
        public String toString() {
            switch (this) {
                case OBJECT_MAPPING:
                    return OBJECT_MAPPING_CTX;
                case OVERRIDDEN_NAME:
                    return OVERRIDDEN_NAME_CTX;
                case STEP:
                    return STEP_CTX;
                default:
                    // nothing
            }
            Assert.notReached("Missing toString representation for CompNameCreationContext"); //$NON-NLS-1$
            return StringConstants.EMPTY;
        }
        
        /**
         * 
         * @param name a toString representation of a CompNameCreationContext.
         * @return a CompNameCreationContext
         */
        public static CompNameCreationContext forName(String name) {
            if (OBJECT_MAPPING_CTX.equalsIgnoreCase(name)) {
                return OBJECT_MAPPING;
            }
            if (OVERRIDDEN_NAME_CTX.equalsIgnoreCase(name)) {
                return OVERRIDDEN_NAME;
            }
            if (STEP_CTX.equalsIgnoreCase(name)) {
                return STEP;
            }
            Assert.notReached("No CompNameCreationContext for '" //$NON-NLS-1$
                    + String.valueOf(name) + "'"); //$NON-NLS-1$
            return null;
        }
    }
    
    /** The singleton instance */
    private static ComponentNamesBP instance = null;
    
    /**
     * Singleton Constructor.
     */
    private ComponentNamesBP() {
        //
        
    }
    
    /**
     * @return The singleton instance.
     */
    public static final ComponentNamesBP getInstance() {
        if (instance == null) {
            instance = new ComponentNamesBP();
        }
        return instance;
    }

    /**
     * Adds the given NAME_PO to the chache.<br>
     * This method is null-safe!
     * @param compName namePO object
     */
    public final void addComponentNamePO(IComponentNamePO compName) {
        addNamePO(compName);
    }
    
    /**
     * 
     * @param guid a GUID
     */
    public final void removeComponentNamePO(String guid) {
        removeNamePO(guid);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getGuidForName(String name, Long parentProjectId) {
        for (IComponentNamePO compName : getAllNamePOs()) {
            if (compName.getName().equals(name)
                    && parentProjectId.equals(compName.getParentProjectId())) {
                return compName.getGuid();
            }
        }
        return null;
    }
    
    /**
     * 
     * @param guid the GUID of the Component Name.
     * @return the display name of the Component Name with the given GUID of 
     * the current Project.
     */
    public final String getName(String guid) {
        return getName(guid, GeneralStorage.getInstance().getProject().getId());
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getName(String guid, Long rootProjId) {
        // fallback, show uniqueId, if no name for component is available
        String name = guid;
        IComponentNamePO namePO = getNamePO(guid);
        if (namePO == null) {
            // try to get component name from used Projects
            final IProjectPO currProj = GeneralStorage.getInstance()
                .getProject();
            try {
                namePO = getCompNamePoImpl(currProj.getGuid(), guid, null, 
                    currProj.getMajorProjectVersion(), 
                    currProj.getMinorProjectVersion(),
                    currProj.getMicroProjectVersion(),
                    currProj.getProjectVersionQualifier());
                addComponentNamePO(namePO);
            } catch (JBException e) {
                throw new JBFatalException(e, MessageIDs.E_DATABASE_GENERAL);
            }
        }
        if (namePO != null) {
            if (namePO.getReferencedGuid() != null) {
                return getName(namePO.getReferencedGuid(), rootProjId);
            }
            name = namePO.getName();
        } else {
            // This can happen legally if there is an unused overridden 
            // Component Name which was formerly propagated. 
            if (log.isDebugEnabled()) {
                log.debug(Messages.EmptyComponentName + StringConstants.SPACE
                        + StringConstants.EQUALS_SIGN + StringConstants.SPACE
                        + rootProjId + " uniqueId = " + guid); //$NON-NLS-1$
            }
        }
        return name;
    }
   
    
    /**
     * reads all paramNames of the current Project and its reused Projects 
     * from database into names map
     * @throws PMException in case of an (unexpected) DB access problem
     */
    public final void init() throws PMException {
        clearAllNamePOs();
        final IProjectPO currProject = GeneralStorage.getInstance()
            .getProject();
        initCompNamesTransitive(currProject.getId(), new HashSet<Long>());
    }
    
    /**
     * reads all paramNames of the given Project and its reused Projects 
     * from database into names map
     * @param projectID an IProjectPO id
     * @param loadedProjectIds Accumulated IDs of reused projects that have 
     *                         been loaded.
     * @throws PMException in case of an (unexpected) DB access problem
     */
    private void initCompNamesTransitive(Long projectID, 
        Set<Long> loadedProjectIds) throws PMException { 
        
        if (projectID == null) {
            return;
        }
        readCompNamesForProjectID(projectID);
        for (IReusedProjectPO usedProj : ProjectPM
                .getReusedProjectsForProject(projectID)) {
            final String reusedGuid = usedProj.getProjectGuid();
            final Integer reuseMajVers = usedProj.getMajorNumber();
            final Integer reuseMinVers = usedProj.getMinorNumber();
            final Integer reuseMicVers = usedProj.getMicroNumber();
            final String reuseQualVers = usedProj.getVersionQualifier();
            try {
                final Long usedProjPo = ProjectPM
                    .findProjectIDByGuidAndVersion(reusedGuid, reuseMajVers, 
                        reuseMinVers, reuseMicVers, reuseQualVers);
                if (usedProjPo != null 
                        && loadedProjectIds.add(usedProjPo)) {
                    initCompNamesTransitive(usedProjPo,
                            loadedProjectIds);
                }
            } catch (JBException e) {
                // Continue! Maybe the Project is not present in DB.
            }
        }
    }
    
    /**
     * reads all paramNames of the Project with the given ID from database 
     * into names map
     * @param projId the Project ID.
     * @throws PMException PMException in case of any db problem
     */
    private void readCompNamesForProjectID(Long projId) throws PMException {
        List<IComponentNamePO> names = CompNamePM.readAllCompNames(projId);
        for (IComponentNamePO compNamePO : names) {
            addNamePO(compNamePO);
        }
    }
    
    /**
     * reads all paramNames of the Project with the given ID from database 
     * into names map
     * @param projId projId the Project ID.
     * @throws PMException PMException PMException in case of any db problem
     */
    public final void refreshNames(Long projId) throws PMException {
        readCompNamesForProjectID(projId);
    }
    
    /**
     * 
     * @return all IComponentNamePOs of the current project and its reused 
     * Projects.
     */
    public final Collection<IComponentNamePO> getAllComponentNamePOs() {
        return getAllNamePOs();
    }
    
    /**
     * @param projId a Project ID
     * @return all IComponentNamePOs of the project with the given Project ID.
     */
    public final Collection<IComponentNamePO> getAllComponentNamePOs(
            Long projId) throws PMException {
        
        return CompNamePM.readAllCompNamesRO(projId);
    }
    
    /**
     * @param projId a Project ID
     * @return all IComponentNamePOs of the project with the given Project ID.
     */
    public final Collection<IComponentNamePO> getAllNonRefCompNamePOs(
            Long projId) throws PMException {
        
        Collection<IComponentNamePO> allCompNamePOs = 
            getAllComponentNamePOs(projId);

        CollectionUtils.filter(allCompNamePOs, new Predicate() {

            public boolean evaluate(Object object) {
                if (object instanceof IComponentNamePO) {
                    return ((IComponentNamePO)object).getReferencedGuid() 
                        == null;
                }
                return false;
            }
            
        });
        
        return allCompNamePOs;
    }

    /**
     * Creates a new IComponentNamePO and puts it in the list of 
     * IComponentNamePO to persist.
     * @param guid a GUID, can be null. If null, a GUID will be created.
     * @param name the name.
     * @param type the type 
     * @param ctx the CompNameCreationContext.
     * @return a new IComponentNamePO.
     * @see #createComponentNamePO(String, String, CompNameCreationContext)
     */
    public final IComponentNamePO createComponentNamePO(String guid, 
            String name, String type, CompNameCreationContext ctx) {
        
        String nameGuid = guid;
        if (guid == null) {
            nameGuid = PersistenceUtil.generateGuid();
        }
        final IComponentNamePO newComponentNamePO = PoMaker
            .createComponentNamePO(nameGuid, name, type, ctx, 
                    GeneralStorage.getInstance().getProject().getId());
        addComponentNamePO(newComponentNamePO);
        return newComponentNamePO;
    }
    
    
    /**
     * Gets an IComponentNamePO from the database with the help of the given 
     * Parameters.
     * @param compNameGuid The GUID of the IComponentNamePO which is to load.
     * @param compNameParentProjGuid The ParentProjectGUID of the 
     * IComponentNamePO which is to load.
     * @return an IComponentNamePO or null if not found
     * @throws JBException in case of any db problem.
     */
    public final IComponentNamePO getCompNamePo(String compNameGuid, 
            String compNameParentProjGuid) throws JBException {
        
        final IProjectPO currProject = GeneralStorage.getInstance()
            .getProject();
        final String currGuid = currProject.getGuid();
        if (currGuid.equals(compNameParentProjGuid)) {
            return loadCompNamePoImpl(compNameGuid, currGuid, 
                currProject.getMajorProjectVersion(), 
                currProject.getMinorProjectVersion(),
                currProject.getMicroProjectVersion(),
                currProject.getProjectVersionQualifier());
        }
        
        return getCompNamePoImpl(currProject.getGuid(), compNameGuid, 
                compNameParentProjGuid,
                currProject.getMajorProjectVersion(), 
                currProject.getMinorProjectVersion(),
                currProject.getMicroProjectVersion(),
                currProject.getProjectVersionQualifier());
    }
    
    
    /**
     * Gets an IComponentNamePO with the given Parameters.
     * @param projToSearchGuid The GUID of the Project to search for the 
     * IComponentNamePO
     * @param compNameGuid The GUID of the IComponentNamePO which is to load.
     * @param compNameParentProjGuid The ParentProjectGUID of the 
     * IComponentNamePO which is to load.
     * Can be null. If it is null, the search for the IComponentNamePO begins
     * in the used Projects of the Project with the given projToSearchGuid and 
     * projMajVers and projMinVers. Otherwise the search start in the given
     * Project itself.
     * @param projMajVers the Major Version of the ParentProject of the wanted 
     * IComponentNamePO.
     * @param projMinVers the Minor Version of the ParentProject of the wanted 
     * IComponentNamePO.
     * @param projMicVers the Micro Version of the ParentProject of the wanted 
     * IComponentNamePO.
     * @param projVersQual the Version qualifier of the ParentProject of the wanted 
     * IComponentNamePO.
     * @return an IComponentNamePO or null if not found.
     * @throws JBException in case of any db problem.
     */
    private IComponentNamePO getCompNamePoImpl(String projToSearchGuid, 
        String compNameGuid, String compNameParentProjGuid, 
        Integer projMajVers, Integer projMinVers, Integer projMicVers,
        String projVersQual) throws JBException {
        
        if (compNameParentProjGuid != null) {
            IComponentNamePO compNamePo = loadCompNamePoImpl(compNameGuid,
                    compNameParentProjGuid, projMajVers, projMinVers,
                    projMicVers, projVersQual);
            if (compNamePo != null) {
                return compNamePo;
            }
        }
        
        List<IReusedProjectPO> reusedProjList = ProjectPM.loadReusedProjectsRO(
                projToSearchGuid, projMajVers, projMinVers, projMicVers,
                projVersQual);
        for (IReusedProjectPO reusedProj : reusedProjList) {
            final Integer reusedMajVers = reusedProj.getMajorNumber();
            final Integer reusedMinVers = reusedProj.getMinorNumber();
            final Integer reusedMicVers = reusedProj.getMicroNumber();
            final String reusedQualVers = reusedProj.getVersionQualifier();
            final String reusedGuid = reusedProj.getProjectGuid();
            IComponentNamePO compNamePo = null;
            if (StringUtils.equals(compNameParentProjGuid, reusedGuid)) {
                compNamePo = loadCompNamePoImpl(compNameGuid, reusedGuid, 
                    reusedMajVers, reusedMinVers, reusedMicVers,
                    reusedQualVers);
                if (compNamePo != null) {
                    return compNamePo;
                } 
            }
            compNamePo = getCompNamePoImpl(reusedGuid, compNameGuid, 
                reusedGuid, reusedMajVers, reusedMinVers, reusedMicVers,
                reusedQualVers);
            if (compNamePo != null) {
                return compNamePo;
            }
        }
        return null;
    }
    
    /**
     * Loads an IComponentNamePO
     * @param compNameGuid The GUID of the IComponentNamePO which is to load.
     * @param compNameParentProjGuid The ParentProjectGUID of the 
     * IComponentNamePO which is to load.
     * @param projMajVers the Major Version of the ParentProject of the wanted 
     * IComponentNamePO.
     * @param projMinVers the Minor Version of the ParentProject of the wanted 
     * IComponentNamePO.
     * @param projMicVers the Mic Version of the ParentProject of the wanted 
     * IComponentNamePO.
     * @param projVersQual the Version Qualifier of the ParentProject of the wanted 
     * IComponentNamePO.
     * @return an IComponentNamePO or null if not found.
     * @throws JBException in case of any db problem.
     */
    private IComponentNamePO loadCompNamePoImpl(String compNameGuid, 
        String compNameParentProjGuid, Integer projMajVers, Integer projMinVers,
        Integer projMicVers, String projVersQual)
        throws JBException {

        IComponentNamePO compNamePO = null;
        final Long projId = ProjectPM.findProjectId(compNameParentProjGuid, 
                projMajVers, projMinVers, projMicVers, projVersQual);
        if (projId != null) {
            compNamePO = CompNamePM.loadCompName(compNameGuid, 
                    projId);
        }
        return compNamePO;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public final IComponentNamePO getCompNamePo(String guid) {
        return getCompNamePo(guid, true);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public final IComponentNamePO getCompNamePo(
            String guid, boolean resolveRefs) {
        
        IComponentNamePO compNamePO = getNamePO(guid);
        if (compNamePO != null) {
            if (resolveRefs && compNamePO.getReferencedGuid() != null) {
                return getCompNamePo(compNamePO.getReferencedGuid(),
                        resolveRefs);
            }
            return compNamePO;

        }
        return null;
    }

    /**
     * Gets the IComponentNamePO of the given name
     * @param name a name
     * @return an IComponentNamePO or null if not found.
     */
    public final IComponentNamePO getCompNamePoByName(String name) {
        for (IComponentNamePO compNamePO : getAllComponentNamePOs()) {
            if (name.equals(compNamePO.getName())) {
                return compNamePO;
            }
        }
        return null;
    }

    /**
     * Updates the Component Type of the IComponentNamePO with the given guid.
     * @param guid the GUID of the IComponentNamePO to update.
     * @param componentType the Component Type.
     */
    public void updateType(String guid, String componentType) {
        final IComponentNamePO compNamePo = getCompNamePo(guid);
        compNamePo.setComponentType(componentType);
    }
    
    /**
     * @param componentName The name of the Component Name for which the
     *                      the type should be computed. This is used to 
     *                      determine if a default mapping exists and
     *                      should be used.
     * @param types The Component Types to be included in the computation.
     * @return The most concrete visible type of the given types, 
     *         or <code>null</code> if the types are incompatible.
     */
    public String computeComponentType(
            String componentName, Set<String> types) {
        
        if (types.contains(null) || types.contains(StringConstants.EMPTY)) {
            // Used types contains at least one invalid entry
            return UNKNOWN_COMPONENT_TYPE;
        }

        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        Set<Component> components = new HashSet<Component>();
        for (String compType : types) {
            Component comp = compSystem.findComponent(compType);
            comp = getMostConcreteVisibleAncestor(comp, compSystem);
            components.add(comp);
        }
        if (components.isEmpty()) {
            components.add(compSystem.getMostAbstractComponent());
        }
        Component mostConcreteComponent = compSystem.getMostConcrete(
                components.toArray(new Component[components.size()]));
        if (mostConcreteComponent == null) {
            return null;
        }
        
        String compType = mostConcreteComponent.getType();
        
        mostConcreteComponent = getMostConcreteVisibleAncestor(
                mostConcreteComponent, compSystem);

        // Use the most concrete visible type, if one is available
        if (mostConcreteComponent != null) {
            compType = mostConcreteComponent.getType();
        }
        
        return compType;
    }

    /**
     * Find the most concrete visible ancestor of the given component.
     * 
     * @param component The component for which to find the most concrete
     *                  visible ancestor.
     * @param compSystem The component system to use to perform the search.
     * @return the most concrete, visible ancestor of <code>component</code>, 
     *         which may be <code>component</code> itself. 
     *         Returns <code>null</code> if <code>component</code> and
     *         its ancestors are all invisible.
     */
    private Component getMostConcreteVisibleAncestor(Component component,
            CompSystem compSystem) {

        Component comp = component;
        while (comp != null 
                && !comp.isVisible()) {
            Set<Component> realized = comp.getAllRealized();
            comp = compSystem.getMostConcrete(
                    realized.toArray(new Component [realized.size()]));
        }
        return comp;
    }
    
    /**
     * Checks whether the given checkable name is compatible with the 
     * given component type. Returns <code>null</code> if the types are 
     * compatible.
     * @param originalCompType The component type that determines the 
     *                         compatibility.
     * @param checkableName The component name for which to check the 
     *                      compatibility.
     * @param compNameMapper The mapper to use for Component Name information.
     * @param projectId The ID of the Project to be searched. May be 
     *                  <code>null</code>. If this value is not 
     *                  <code>null</code>, only Component Names belonging to 
     *                  the Project with the given ID will be examined. 
     * @param isSimpleMatch if <code>true</code> only the check for checkable
     *                  is compatible with original is done      
     * @return appropriate error message if <code>checkableName</code> is a 
     *         reserved name or if <code>checkableName</code> is found in the 
     *         component names list and the component represented by 
     *         <code>checkableName</code> is incompatible with 
     *         <code>originalCompType</code>. Otherwise, <code>null</code>.
     */
    public String isCompatible(String originalCompType, String checkableName,
            IComponentNameMapper compNameMapper, Long projectId,
            boolean isSimpleMatch) {
         
        IComponentNameCache compNameCache = compNameMapper.getCompNameCache();
        
        Set<IComponentNameData> componentNameDataSet = 
            compNameCache.getComponentNameData();
        
        IComponentNameData compNameData = null;
        for (IComponentNameData cnd : componentNameDataSet) {
            if ((projectId == null
                        || cnd.getParentProjectId() == null
                        || cnd.getParentProjectId().equals(projectId))
                    && cnd.getName().equals(checkableName)) {
                
                compNameData = cnd;
                break;
            }
        }

        if (compNameData == null) {
            return null;
        }

        Set<String> usedTypes = 
            compNameMapper.getUsedTypes(compNameData.getGuid());

        final String computedType = 
            ComponentNamesBP.getInstance().computeComponentType(
                    checkableName, usedTypes);
        Component originalComponent = getComponent(originalCompType);
        Component checkableComponent = getComponent(computedType);
        if (checkableComponent.isCompatibleWith(originalComponent.getType())) {
            return null;
        }
        if (isSimpleMatch) {
            return NLS.bind(Messages.CompNameIncompatibleTypeDetail,
                    new Object[]{checkableName, 
                            StringHelper.getInstance()
                            .get(checkableComponent.getType(), true),
                            StringHelper.getInstance()
                            .get(originalComponent.getType(), true)});
        }
        
        String compNameGuid = compNameCache.getGuidForName(checkableName);
        final IComponentNamePO namePO = 
            compNameCache.getCompNamePo(compNameGuid);
        final boolean currProj = namePO == null ? false 
            : namePO.getParentProjectId() == null 
                || GeneralStorage.getInstance().getProject().getId()
                    .equals(namePO.getParentProjectId());
        
        if (currProj && originalComponent.isCompatibleWith(
                checkableComponent.getType())) {
            
            return null;
        }        
        if (UNKNOWN_COMPONENT_TYPE.equals(computedType)) {
            // Computed component type is "unknown"
            return NLS.bind(Messages.CompNameUnknownTypeDetail,
                    namePO != null ? namePO.getName() : checkableName);
        }
        return NLS.bind(Messages.CompNameIncompatibleTypeDetail,
                new Object[]{namePO != null ? namePO.getName() : checkableName, 
                        StringHelper.getInstance()
                        .get(checkableComponent.getType(), true),
                        StringHelper.getInstance()
                        .get(originalComponent.getType(), true)});
    }
    
    /**
     * Checks whether the given checkable name is compatible with the 
     * given component type. Returns <code>null</code> if the types are 
     * compatible.
     * @param originalCompType The component type that determines the 
     *                         compatibility.
     * @param checkableName The component name for which to check the 
     *                      compatibility.
     * @param compNameMapper The mapper to use for Component Name information.
     * @param projectId The ID of the Project to be searched. May be 
     *                  <code>null</code>. If this value is not 
     *                  <code>null</code>, only Component Names belonging to 
     *                  the Project with the given ID will be examined. 
     * @return appropriate error message if <code>checkableName</code> is a 
     *         reserved name or if <code>checkableName</code> is found in the 
     *         component names list and the component represented by 
     *         <code>checkableName</code> is incompatible with 
     *         <code>originalCompType</code>. Otherwise, <code>null</code>.
     */
    public String isCompatible(String originalCompType, String checkableName,
            IComponentNameMapper compNameMapper, Long projectId) {
        return isCompatible(originalCompType, checkableName, compNameMapper,
                projectId, false);

    }    
    
    /**
     * Gets the Component with the given typeName.
     * @param typeName a type of a Component.
     * @return the Component with the given typeName.
     */
    private Component getComponent(String typeName) {
        final CompSystem compSystem = ComponentBuilder.getInstance()
            .getCompSystem();
        if (typeName == null) {
            return compSystem.getMostAbstractComponent();
        }
        return compSystem.findComponent(typeName);
    }
    
    /**
     * Sets the Component Name for the given Test Step.
     * 
     * @param capPo The Test Step for which to set the Component Name.
     * @param compName The name of the Component Name to use.
     * @param ctx The creation context of the Component Name.
     * @param compMapper The Component Name mapper responsible for the given 
     *                   Test Step.
     * @throws IncompatibleTypeException if the Component Type for the 
     *                                   Component Name to use is incompatible
     *                                   with the Component Type of the given
     *                                   Test Step.
     * @throws PMException if a database error occurs. 
     */
    public void setCompName(ICapPO capPo, String compName, 
            CompNameCreationContext ctx, 
            IWritableComponentNameMapper compMapper) 
        throws IncompatibleTypeException, PMException {

        
        String oldName = capPo.getComponentName();
        String oldGuid = compMapper.getCompNameCache().getGuidForName(oldName);

        if (StringUtils.isBlank(compName)) {
            compMapper.changeReuse(capPo, oldGuid, null);
        } else {
            String guidToSet = 
                compMapper.getCompNameCache().getGuidForName(
                        compName, 
                        GeneralStorage.getInstance().getProject().getId());
            if (guidToSet == null) {
                final IComponentNamePO newComponentNamePO = 
                    compMapper.getCompNameCache().createComponentNamePO(
                            compName, capPo.getComponentType(), ctx);
                if (capPo.getParentProjectId() != null) {
                    newComponentNamePO.setParentProjectId(
                            capPo.getParentProjectId());
                }
                guidToSet = newComponentNamePO.getGuid();
            }


            compMapper.changeReuse(capPo, oldGuid, guidToSet);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        clearAllNamePOs();
    }

    /**
     * {@inheritDoc}
     */
    public Set<IComponentNameData> getComponentNameData() {
        Set<IComponentNameData> retVal = new HashSet<IComponentNameData>();
        retVal.addAll(getAllComponentNamePOs());
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public Set<IComponentNameData> getLocalComponentNameData() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public void updateStandardMapperAndCleanup(Long activeProjectId) {
        // This is the standard mapper, so do nothing.
    }
    
    /**
     * Finds all instances of reuse of a Component Name within the given 
     * objects.
     * 
     * @param specsToSearch The Test Cases and Categories to search 
     *                      (recursively).
     * @param suitesToSearch The Test Suites to search.
     * @param autsToSearch The AUTs for which to search through the 
     *                     Object Mapping.
     * @param compNameGuid The GUID of the Component Name for which to find the
     *                    instances of reuse.
     * @return <code>true</code> if the Component Name with GUID 
     *         <code>compNameGuid</code> is used within 
     *         <code>specsToSearch</code> and/or <code>autsToSearch</code>.
     *         Otherwise, <code>false</code>.
     */
    public boolean isCompNameReused(
            Collection<ISpecPersistable> specsToSearch, 
            Collection<ITestSuitePO> suitesToSearch,
            Collection<IAUTMainPO> autsToSearch, String compNameGuid) {

        return !findAssocsOfReuse(autsToSearch, compNameGuid).isEmpty() 
            || isCompNameReused(specsToSearch, suitesToSearch, compNameGuid);
    }

    /**
     * 
     * @param specsToSearch The Test Cases and Categories to search 
     *                      (recursively).
     * @param suitesToSearch The Test Suites to search.
     * @param compNameGuid The GUID of the Component Name for which to find 
     *                     whether it is reused.
     * @return <code>true</code> if the Component Name with GUID 
     *         <code>compNameGuid</code> is reused within 
     *         <code>specsToSearch</code>.
     */
    public boolean isCompNameReused(
            Collection<ISpecPersistable> specsToSearch, 
            Collection<ITestSuitePO> suitesToSearch, String compNameGuid) {

        for (ISpecPersistable node : specsToSearch) {
            CheckIfComponentNameIsReusedOp op = 
                new CheckIfComponentNameIsReusedOp(compNameGuid);
            TreeTraverser traverser = new TreeTraverser(node, op);
            traverser.traverse(true);
            if (op.hasFoundReuse()) {
                return true;
            }
        }

        for (ITestSuitePO ts : suitesToSearch) {
            CheckIfComponentNameIsReusedOp op = 
                new CheckIfComponentNameIsReusedOp(compNameGuid);
            TreeTraverser traverser = new TreeTraverser(ts, op);
            traverser.traverse(true);
            if (op.hasFoundReuse()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds all instances of reuse of a Component Name within the given 
     * objects.
     * 
     * @param specsToSearch The Test Cases and Categories to search 
     *                      (recursively).
     * @param suitesToSearch The Test Suites to search.
     * @param compNameGuid The GUID of the Component Name for which to find the
     *                    instances of reuse.
     * @param monitor The progress monitor for this operation.
     * @return all instances of reuse of the Component Name with GUID 
     *         <code>compNameGuid</code> within <code>specsToSearch</code> and
     *         <code>autsToSearch</code>.
     */
    public Set<INodePO> findNodesOfReuse(
            Collection<ISpecPersistable> specsToSearch, 
            Collection<ITestSuitePO> suitesToSearch, String compNameGuid, 
            IProgressMonitor monitor) {

        Set<INodePO> reuse = new HashSet<INodePO>();

        monitor.beginTask(
                Messages.ShowWhereUsedSearching,
                specsToSearch.size() + suitesToSearch.size());
        
        for (ISpecPersistable node : specsToSearch) {
            FindNodesForComponentNameOp op = 
                new FindNodesForComponentNameOp(compNameGuid);
            TreeTraverser traverser = new TreeTraverser(node, op);
            traverser.traverse(true);
            reuse.addAll(op.getNodes());
            
            monitor.worked(1);
        }

        for (ITestSuitePO ts : suitesToSearch) {
            FindNodesForComponentNameOp op = 
                new FindNodesForComponentNameOp(compNameGuid);
            TreeTraverser traverser = new TreeTraverser(ts, op);
            traverser.traverse(true);
            reuse.addAll(op.getNodes());
            
            monitor.worked(1);
        }
        
        return reuse;
    }

    /**
     * Finds all instances of reuse of a Component Name within the given 
     * objects.
     * 
     * @param autsToSearch The AUTs for which to search through the 
     *                     Object Mapping.
     * @param compNameGuid The GUID of the Component Name for which to find the
     *                    instances of reuse.
     * @return all instances of reuse of the Component Name with GUID 
     *         <code>compNameGuid</code> within <code>autsToSearch</code>.
     */
    public Set<IObjectMappingAssoziationPO> findAssocsOfReuse(
            Collection<IAUTMainPO> autsToSearch, String compNameGuid) {
        Set<IObjectMappingAssoziationPO> reuse = 
            new HashSet<IObjectMappingAssoziationPO>();
        
        for (IAUTMainPO aut : autsToSearch) {
            for (IObjectMappingAssoziationPO assoc 
                    : aut.getObjMap().getMappings()) {
                
                if (assoc.getTechnicalName() != null 
                        && assoc.getLogicalNames().contains(compNameGuid)) {
                    reuse.add(assoc);
                }
            }
        }
        
        
        return reuse;
    }

    /**
     * Lock the TC if it is reused for the first time. This prevents deletion of
     * the TC while the editor is not saved.
     * 
     * @param sess
     *            DB session for locking purposes
     * @param refCompName
     *            Component Name to be used as a reference.
     * @param isReferencedByThisAction
     *            tells if there was a reference created by the current action,
     *            i.e. there is one references even if no other TC in the db
     *            references this one.
     * @throws PMAlreadyLockedException
     *             if the TC is locked by someone else
     * @throws PMDirtyVersionException
     *             if the TC was modified outside this instance of the
     *             application.
     * @throws PMObjectDeletedException
     *             if the po as deleted by another concurrently working user
     */
    public static void handleFirstReference(EntityManager sess,
        IComponentNamePO refCompName, boolean isReferencedByThisAction)
        throws PMDirtyVersionException, PMObjectDeletedException, 
               PMAlreadyLockedException {

        IComponentNamePO name = refCompName;
        if (name.getId() != null) {
            int minSize = 0;
            if (isReferencedByThisAction) {
                minSize = 1;
            }
            
            if (CompNamePM.getNumReuseInstances(
                    sess, 
                    GeneralStorage.getInstance().getProject().getId(), 
                    name.getGuid()) <= minSize) {
                
                final EntityManager lockSession = sess;
                // make sure there is no old version
                // in the session cache
                try {
                    lockSession.detach(refCompName);
                    name = lockSession.find(
                            name.getClass(), name.getId());
                } catch (PersistenceException he) {
                    // Continue since we are just refreshing the cache
                    log.error(Messages.StrayPersistenceException 
                            + StringConstants.DOT + StringConstants.DOT,
                        he);                
                }
                if (!LockManager.instance()
                        .lockPO(lockSession, name, false)) {
                    throw new PMAlreadyLockedException(name, 
                            Messages.OrginalTestcaseLocked 
                                + StringConstants.DOT,
                            MessageIDs.E_OBJECT_IN_USE);           
                }
            }
        }
    }

}