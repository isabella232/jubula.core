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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IComponentNameData;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PersistenceUtil;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PersistenceManager;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.utils.ValueListIterator;


/**
 * @author BREDEX GmbH
 * @created Apr 21, 2008
 */
public class ComponentNamesDecorator implements IWritableComponentNameCache {

    /** the session used for caching and persisting Component Names */
    private EntityManager m_session;
    
    /** 
     * new Component Names created by this mapper:
     * GUID => Component Name
     * 
     */
    private Map<String, IComponentNamePO> m_added = 
        new HashMap<String, IComponentNamePO>();
    
    /** 
     * Mapping between Component Name GUIDs and their net reuse within this 
     * mapper.
     */
    private Map<String, Integer> m_reuseChanged = 
        new HashMap<String, Integer>();
    
    /** Component Names that have been renamed by this mapper */
    private Set<IComponentNamePO> m_renamed = 
        new HashSet<IComponentNamePO>();
    
    /** Component Names that have been removed by this mapper */
    private Set<IComponentNamePO> m_deleted = 
        new HashSet<IComponentNamePO>();

    /** 
     * cached Component Names that have been loaded into the mapper's session 
     */
    private Map<String, IComponentNamePO> m_cached =
        new HashMap<String, IComponentNamePO>();

    /**
     * Constructor
     * 
     * @param s The session associated with this mapper.
     */
    public ComponentNamesDecorator(EntityManager s) {
        
        m_session = s;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addComponentNamePO(IComponentNamePO compNamePo) {
        if (compNamePo != null) {
            m_added.put(compNamePo.getGuid(), compNamePo);
            m_deleted.remove(compNamePo);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Collection<IComponentNamePO> getNewNames() {
        return Collections.unmodifiableCollection(m_added.values());
    }
    
    /**
     * Reinitializes the ComponentNamesBP.
     * @param projectId the Project ID
     */
    private void updateStandardMapper(Long projectId) throws PMException {
        ComponentNamesBP.getInstance().refreshNames(projectId);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void updateStandardMapperAndCleanup(Long projectId) {
        try {
            updateStandardMapper(projectId);
            clear();
        } catch (PMException e) {
            throw new JBFatalException(Messages.ReadingComponentNamesFailed
                    + StringConstants.EXCLAMATION_MARK, e, 
                    MessageIDs.E_DATABASE_GENERAL);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getGuidForName(String name) {
        for (IComponentNamePO compNamePo : m_renamed) {
            if (compNamePo.getName().equals(name)) {
                return compNamePo.getGuid();
            }
        }

        for (IComponentNamePO compNamePo : m_added.values()) {
            if (compNamePo.getName().equals(name)) {
                return compNamePo.getGuid();
            }
        }

        return ComponentNamesBP.getInstance().getGuidForName(name);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public String getGuidForName(String name, Long parentProjectId) {
        Long parentProjectIdToUse = parentProjectId;
        if (parentProjectIdToUse == null) {
            IProjectPO currentProject = 
                GeneralStorage.getInstance().getProject();
            if (currentProject != null) {
                parentProjectIdToUse = currentProject.getId();
            }
        }
        
        if (parentProjectIdToUse == null) {
            return null;
        }
        
        for (IComponentNamePO compNamePo : m_renamed) {
            if (compNamePo.getName().equals(name)
                    && parentProjectIdToUse.equals(
                            compNamePo.getParentProjectId())) {
                return compNamePo.getGuid();
            }
        }

        for (IComponentNamePO compNamePo : m_added.values()) {
            if (compNamePo.getName().equals(name)
                    && parentProjectIdToUse.equals(
                            compNamePo.getParentProjectId())) {
                return compNamePo.getGuid();
            }
        }

        return ComponentNamesBP.getInstance().getGuidForName(
                name, parentProjectIdToUse);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public IComponentNamePO createComponentNamePO(String name, String type, 
            CompNameCreationContext creationContext) {
        
        return createComponentNamePO(null, name, type, creationContext);
    }
    
    /**
     * Creates and returns a new Component Name with the given attributes.
     * 
     * @param guid The GUID for the Component Name.
     * @param name The name for the Component Name.
     * @param type The reuse type for the Component Name.
     * @param creationContext The creation context.
     * @return the newly created Component Name.
     */
    public IComponentNamePO createComponentNamePO(String guid, String name, 
            String type, CompNameCreationContext creationContext) {
        
        String nameGuid = guid;
        if (guid == null) {
            nameGuid = PersistenceUtil.generateGuid();
        }

        final IComponentNamePO newComponentNamePO = 
            PoMaker.createComponentNamePO(nameGuid, name, 
                    type, creationContext, 
                    GeneralStorage.getInstance().getProject().getId());

        addComponentNamePO(newComponentNamePO);

        return newComponentNamePO;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeReuse(String guid) throws PMException {
        Integer timesReused = m_reuseChanged.get(guid);
        if (timesReused == null) {
            m_reuseChanged.put(guid, -1);
        } else {
            m_reuseChanged.put(guid, timesReused - 1);
        }
        
        if (m_reuseChanged.get(guid) < 1) {
            m_added.remove(guid);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addReuse(String guid) throws PMException {
        Integer timesReused = m_reuseChanged.get(guid);
        if (timesReused == null) {
            m_reuseChanged.put(guid, 1);
        } else {
            m_reuseChanged.put(guid, timesReused + 1);
        }

        IComponentNamePO compNamePo = getCompNamePo(guid);
        try {
            try {
                ComponentNamesBP.handleFirstReference(
                        m_session, compNamePo, true);
            } catch (PersistenceException e) {
                PersistenceManager.handleDBExceptionForAnySession(
                        compNamePo, e, m_session);
            }
        } catch (PMDirtyVersionException e) { // NOPMD by al on 3/19/07 1:25 PM
            // ignore, we are not interested in version checking
        } catch (PMException e) {            
            // OK, this may happen, just forward to caller
            throw e;
        }
        
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void renameComponentName(String guid, String newName) {
        IComponentNamePO compNamePo = getCompNamePo(guid);
        if (compNamePo != null) {
            compNamePo.setName(newName);
            m_renamed.add(compNamePo);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        m_added.clear();
        m_reuseChanged.clear();
        m_renamed.clear();
        m_deleted.clear();
        m_cached.clear();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public IComponentNamePO getCompNamePo(String guid) {
        return getCompNamePo(guid, true);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public IComponentNamePO getCompNamePo(String guid, boolean resolveRefs) {
        IComponentNamePO compNamePo = findCompNameLocal(guid);
        if (compNamePo != null) {
            if (resolveRefs && compNamePo.getReferencedGuid() != null) {
                return getCompNamePo(compNamePo.getReferencedGuid(),
                        resolveRefs);
            }
            return compNamePo;
        }

        IComponentNamePO retVal = null;
        IComponentNamePO cnPoFromOtherSession = 
            ComponentNamesBP.getInstance().getCompNamePo(guid, resolveRefs);
        if (cnPoFromOtherSession != null && m_session != null 
                && m_session.isOpen()) {
            retVal = (IComponentNamePO)m_session.find(
                    PersistenceUtil.getClass(cnPoFromOtherSession), 
                    cnPoFromOtherSession.getId());
            if (retVal != null) {
                m_cached.put(retVal.getGuid(), retVal);
            }
        }
        
        if (resolveRefs && retVal != null 
                && retVal.getReferencedGuid() != null) {
            return getCompNamePo(retVal.getReferencedGuid(), resolveRefs);
        }
        
        return retVal;
    }

    /**
     * lookup comp name in local cached data
     * @param guid GUID of the compname 
     * @return the ComponentNamePO fpr the guid or null if not found
     */
    private IComponentNamePO findCompNameLocal(String guid) {
        IComponentNamePO res = m_added.get(guid);
        if (res == null) {
            res = m_cached.get(guid);
        }
        return res;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getName (String guid) {
        String retVal = guid;
        IComponentNamePO compNamePo = getCompNamePo(guid);
        if (compNamePo != null) {
            retVal = compNamePo.getName();
        }
        
        return retVal;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public Set<IComponentNameData> getComponentNameData() {
        return new HashSet<IComponentNameData>(getComponentNames());
    }
    
    /**
     * @return all available Component Names.
     */
    private Collection<IComponentNamePO> getComponentNames() {
        Set<IComponentNamePO> compNames = new HashSet<IComponentNamePO>();
        compNames.addAll(getLocalComponentNames());
        compNames.addAll(
                ComponentNamesBP.getInstance().getAllComponentNamePOs());
        return compNames;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Set<IComponentNameData> getLocalComponentNameData() {
        return new HashSet<IComponentNameData>(getLocalComponentNames());
    }

    /**
     * 
     * @return all Component Names currently being managed by this cache.
     */
    private Set<IComponentNamePO> getLocalComponentNames() {
        Set<IComponentNamePO> sessionNames = new HashSet<IComponentNamePO>();
        sessionNames.addAll(m_added.values());
        sessionNames.addAll(m_cached.values());

        return sessionNames;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Collection<String> getReusedNames() {
        return Collections.unmodifiableCollection(m_reuseChanged.keySet());
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Collection<IComponentNamePO> getRenamedNames() {
        return Collections.unmodifiableSet(m_renamed);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Collection<IComponentNamePO> getDeletedNames() {
        return Collections.unmodifiableCollection(m_deleted);
    }

    /**
     * {@inheritDoc}
     */
    public void addToCache(IComponentNamePO toBeCached) {
        m_cached.put(toBeCached.getGuid(), toBeCached);
    }

    /**
     * {@inheritDoc}
     */
    public void initCache(Set<String> guids) {
        Set ids = new HashSet();
        // Collect ids
        for (String guid : guids) {
            IComponentNamePO cnPo = 
                ComponentNamesBP.getInstance().getCompNamePo(guid, true);
            if (cnPo != null) {
                ids.add(cnPo.getId());
            }
        }
        
        // load batch by id in packs of 1000
        Query q = m_session.createQuery(
                "select compName from ComponentNamePO as compName where compName.id in :ids"); //$NON-NLS-1$
        
        for (ValueListIterator iter = new ValueListIterator(
            new ArrayList(ids)); iter.hasNext();) {
            q.setParameter("ids", iter.nextList()); //$NON-NLS-1$
            List <IComponentNamePO> list = q.getResultList();
            for (IComponentNamePO cn : list) {
                m_cached.put(cn.getGuid(), cn);
            }
            
        }
        
    }
    
}
