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
package org.eclipse.jubula.client.core.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecObjContPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to persist and read nodes
 * 
 * @author BREDEX GmbH
 * @created 07.09.2004
 */
public class NodePM extends PersistenceManager {
    /**
     * Command for parent/child adding and removing
     */
    public abstract static class AbstractCmdHandleChild {
        /**
         * Template for this command. If executed add the child to the parent.
         * 
         * @param parent
         *            Node where the child should be added.
         * @param child
         *            Child to be added to parent.
         * @param pos
         *            where to insert the child, value null means insert after
         *            end
         */
        public abstract void add(INodePO parent, INodePO child, Integer pos);

        /**
         * Template for this command. If executed remove the child from the
         * parent. This method assumes that the child is to be deleted.
         * Calls dispose() on child!
         * @param parent
         *            Node where the child should be removed.
         * @param child
         *            Child to be removed from parent.
         */
        public void delete(INodePO parent, INodePO child) {
            remove(parent, child);
        }

        /**
         * Like delete, but the child is not to be deleted.
         * Does <b>not</b> call dispose() on child!
         * {@inheritDoc}
         */
        public abstract void remove(INodePO parent, INodePO child);
        
        /**
         * Sets the parentProjectId for a node inserted into another node.
         * @param child the child node
         * @param parent the parent node
         */
        public void setParentProjectId(INodePO child, INodePO parent) {
            Long parentProjectId = parent.getParentProjectId();
            if (parentProjectId == null) {
                parentProjectId = GeneralStorage.getInstance().getProject()
                        .getId();
            }
            child.setParentProjectId(parentProjectId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public static class CmdHandleChildIntoNodeList extends
        AbstractCmdHandleChild {

        /**
         * {@inheritDoc}
         *      org.eclipse.jubula.client.core.model.INodePO)
         */
        public void add(INodePO parent, INodePO child, Integer pos) {
            parent.addNode(pos == null ? -1 : pos, child);
            setParentProjectId(child, parent);
        }

        /**
         * {@inheritDoc}
         *      org.eclipse.jubula.client.core.model.INodePO)
         * @param parent
         * @param child
         */
        public void remove(INodePO parent, INodePO child) {
            parent.removeNode(child);
        }
    }

    /**
     * {@inheritDoc}
     */
    public static class CmdHandleChildIntoSpecList extends
        AbstractCmdHandleChild {

        /**
         * {@inheritDoc}
         *      org.eclipse.jubula.client.core.model.INodePO)
         */
        public void add(INodePO parent, INodePO child, Integer pos) {
            // pos is not used here
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            proj.getSpecObjCont().addSpecObject((ISpecPersistable)child);
            setParentProjectId(child, parent);
        }

        /**
         * {@inheritDoc}
         *      org.eclipse.jubula.client.core.model.INodePO)
         */
        public void remove(INodePO parent, INodePO child) {
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            proj.getSpecObjCont().removeSpecObject((ISpecPersistable)child);
        }

    }
    
    /**
     * {@inheritDoc}
     */
    public static class CmdHandleChildIntoExecList extends
        AbstractCmdHandleChild {

        /**
         * {@inheritDoc}
         *      org.eclipse.jubula.client.core.model.INodePO)
         */
        public void add(INodePO parent, INodePO child, Integer pos) {
            // pos is not used here
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            proj.getExecObjCont().addExecObject((IExecPersistable)child);
            setParentProjectId(child, parent);
        }

        /**
         * {@inheritDoc}
         *      org.eclipse.jubula.client.core.model.INodePO)
         */
        public void remove(INodePO parent, INodePO child) {
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            proj.getExecObjCont().removeExecObject((IExecPersistable)child);
        }

    }
    
    /**
     * {@inheritDoc}
     */
    public static class CmdHandleEventHandlerIntoMap 
        extends AbstractCmdHandleChild {
        /**
         * {@inheritDoc}
         * @param assocNode specTc which will use the evHandler
         * @param evHandler evHandler to add to assocNode
         * @param pos not required (use null)
         */
        public void add(INodePO assocNode, INodePO evHandler, Integer pos) {
            if (assocNode instanceof ISpecTestCasePO
                && evHandler instanceof IEventExecTestCasePO) {
                ISpecTestCasePO usingSpecTc = (ISpecTestCasePO)assocNode;
                try {
                    usingSpecTc.addEventTestCase(
                        (IEventExecTestCasePO)evHandler);
                    setParentProjectId(usingSpecTc, evHandler);
                } catch (InvalidDataException e) {
                    log.error(Messages.AttemptToAddAnEventhandlerTwice, e);
                }
            } else {
                throw new JBFatalException(
                    Messages.WrongTypeForAdditionOfEventhandler,
                    MessageIDs.E_UNEXPECTED_EXCEPTION);
            }
            
        }
        
        /**
         * {@inheritDoc}
         * @param assocNode specTc which contains the evHandler
         * @param evHandler evHandler to remove from assocNode
         */
        public void remove(INodePO assocNode, INodePO evHandler) {
            if (assocNode instanceof ISpecTestCasePO
                && evHandler instanceof IEventExecTestCasePO) {
                ISpecTestCasePO usingSpecTc = (ISpecTestCasePO)assocNode;
                usingSpecTc.removeNode(evHandler);
            } else {
                throw new JBFatalException(
                    Messages.WrongTypeForRemovalOfEventhandler,
                    MessageIDs.E_UNEXPECTED_EXCEPTION);
            }
            
        }
    }

    /**
     * class variable for Singleton
     */
    private static NodePM nodePersManager = null;

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(NodePM.class);

    /** cache for project IDs */
    private Map<String, Long> m_projectIDCache = null;
    
    /** cache for SpecTCs */
    private Map<String, Object> m_specTCCache = null;

    /** Session used in last request */
    private EntityManager m_lastSession = null;
    
    /** is cache usage enabled */
    private boolean m_useCache = false;
    
    /**
     * getter for Singleton
     * 
     * @return single instance of CapPM problem of database
     */
    public static NodePM getInstance() {
        if (nodePersManager == null) {
            nodePersManager = new NodePM();
        }
        return nodePersManager;
    }

    /**
     * Factory for Commands
     * 
     * @param parent
     *            p
     * @param child
     *            c
     * @return C
     */
    public static AbstractCmdHandleChild getCmdHandleChild(INodePO parent,
            INodePO child) {
        if (parent == ISpecObjContPO.TCB_ROOT_NODE) {
            return new CmdHandleChildIntoSpecList();
        } else if (parent == IExecObjContPO.TSB_ROOT_NODE) {
            return new CmdHandleChildIntoExecList();
        } else if (parent instanceof ICategoryPO) {
            // category/specTc in category
            return new CmdHandleChildIntoNodeList();
        } else if (parent instanceof ITestSuitePO) {
            // execTc in testsuite
            return new CmdHandleChildIntoNodeList();
        } else if (parent instanceof ISpecTestCasePO) {
            if (child instanceof IEventExecTestCasePO) {
                // eventhandler in using specTc
                return new CmdHandleEventHandlerIntoMap();
            }
            // execTc or Cap in SpecTestCase
            return new CmdHandleChildIntoNodeList();
        }
        final String msg = Messages.UnsupportedINodePOSubclass;
        log.error(msg);
        throw new JBFatalException(msg, MessageIDs.E_UNSUPPORTED_NODE);
    }

    /**
     * Insert a child and persist to DB
     * 
     * @param parent
     *            parent of child to insert
     * @param child
     *            child to insert
     * @param pos
     *            where to insert the child. if null insert after end
     * @throws PMSaveException
     *             in case of DB problem or refresh errors
     * @throws PMAlreadyLockedException in case of locked parent
     * @throws PMException in case of rollback failed
     * @throws ProjectDeletedException if the project was deleted in another
     * instance
     */
    public static void addAndPersistChildNode(INodePO parent, INodePO child,
        Integer pos)
        throws PMSaveException, PMAlreadyLockedException, PMException, 
        ProjectDeletedException {
        processAndPersistChildNode(parent, child, pos,
            getCmdHandleChild(parent, child), true);
    }

    /**
     * Insert a child and persist to DB
     * 
     * @param parent
     *            parent of child to insert
     * @param child
     *            child to insert
     * @param pos
     *            where to insert the child. if null insert after end
     * @param handler
     *            Command for adding and removing a child
     * @param doAdd
     *            specifies if an add operation should be performed. If false,
     *            the child node is removed.
     * @throws PMException in case of rollback failed
     * @throws ProjectDeletedException if the project was deleted in another
     * instance
     */
    private static void processAndPersistChildNode(INodePO parent,
        INodePO child, Integer pos, AbstractCmdHandleChild handler,
        boolean doAdd) throws PMException, ProjectDeletedException {
        EntityTransaction tx = null;
        IPersistentObject lockedObj = null;
        GeneralStorage gs = GeneralStorage.getInstance();
        final EntityManager sess = gs.getMasterSession();
        IProjectPO currentProject = gs.getProject();
        final Persistor persistor = Persistor.instance();
        if (parent == ISpecObjContPO.TCB_ROOT_NODE) {
            lockedObj = currentProject.getSpecObjCont();
        } else if (parent == IExecObjContPO.TSB_ROOT_NODE) {
            lockedObj = currentProject.getExecObjCont();
        } else {
            lockedObj = parent;
        }
        try {
            tx = persistor.getTransaction(sess);
            persistor.lockPO(sess, lockedObj);
            if (!doAdd) { // don't lock newly created POs 
                lockedObj = child;
                persistor.lockPO(sess, lockedObj);
            }
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForMasterSession(lockedObj, e);
        }
        if (doAdd) {
            
            handler.add(parent, child, pos);
        } else {
            handler.delete(parent, child);
        }
        try {
            if (!doAdd) {
                sess.remove(child);
            }
            persistor.commitTransaction(sess, tx);
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForMasterSession(null, e);
        }
    }

    /**
     * @param node
     *            the node to be renamed
     * @param newName
     *            the new name
     * @throws PMDirtyVersionException
     *             in case of dirty version
     * @throws PMAlreadyLockedException
     *             in case of locked node
     * @throws PMSaveException
     *             in case of DB save error
     * @throws PMException in case of general db error
     * @throws ProjectDeletedException if the project was deleted in another
     * instance            
     */
    public static void renameNode(INodePO node, String newName)
        throws PMDirtyVersionException, PMAlreadyLockedException,
        PMSaveException, PMException, ProjectDeletedException {

        EntityManager sess = GeneralStorage.getInstance().getMasterSession();
        EntityTransaction tx = null;
        try {
            final Persistor persistor = Persistor.instance();
            tx = persistor.getTransaction(sess);
            persistor.lockPO(sess, node);
            node.setName(newName);
            persistor.commitTransaction(sess, tx);
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForMasterSession(node, e);
        }
    }

    /**
     * @param node
     *            the node to be renamed
     * @param newComment
     *            the new comment
     * @throws PMDirtyVersionException
     *             in case of dirty version
     * @throws PMAlreadyLockedException
     *             in case of locked node
     * @throws PMSaveException
     *             in case of DB save error
     * @throws PMException in case of general db error
     * @throws ProjectDeletedException if the project was deleted in another
     * instance            
     */
    public static void setComment(INodePO node, String newComment)
        throws PMDirtyVersionException, PMAlreadyLockedException,
        PMSaveException, PMException, ProjectDeletedException {

        EntityManager sess = GeneralStorage.getInstance().getMasterSession();
        EntityTransaction tx = null;
        try {
            final Persistor persistor = Persistor.instance();
            tx = persistor.getTransaction(sess);
            persistor.lockPO(sess, node);
            node.setComment(newComment);
            persistor.commitTransaction(sess, tx);
        } catch (PersistenceException e) {
            PersistenceManager.handleDBExceptionForMasterSession(node, e);
        }
    }
    
    /**
     * @param cat category, which is the parent of the testcases to import
     * @param specObjList list with testcases to import
     * @throws PMException in case of a problem while import
     * @throws ProjectDeletedException if the project was deleted in another
     * instance
     */
    public static void addImportedTestCases(ICategoryPO cat, 
            List< ? extends INodePO> specObjList)
        throws PMException, ProjectDeletedException {
        
        final GeneralStorage genStorage = GeneralStorage.getInstance();
        IProjectPO currentProject = genStorage.getProject();
        for (INodePO specObj : specObjList) {
            cat.addNode(specObj);
        }
        addAndPersistChildNode(currentProject, cat, null);
    }

    /**
     * @param type
     *            the type of elements to find
     * @param parentProjectId
     *            ID of the parent project to search in
     * @param s
     *            The session into which the INodePOs will be loaded.
     * @return list of param all INodePOs
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<? extends INodePO> computeListOfNodes(Class type,
            Long parentProjectId, EntityManager s) {
        Assert.isNotNull(type);
        Assert.isNotNull(s);
        CriteriaQuery query = s.getCriteriaBuilder().createQuery();
        Root from = query.from(type);
        query.select(from).where(
            s.getCriteriaBuilder().equal(
                    from.get("hbmParentProjectId"), parentProjectId)); //$NON-NLS-1$
        
        List<INodePO> queryResult = s.createQuery(query).getResultList();
        return queryResult;
    }

    /**
     * Returns test cases that reference the test case given information. 
     * Only returns test cases that are in the same project as the given test 
     * case. These test cases are loaded in the Master Session.
     * Warning: the fetched ExecTestCases have no parent, because the database
     * doesn't know the parent.
     * 
     * @param specTcGuid GUID of the test case being reused.
     * @param parentProjectId ID of the parent project of the test case being
     *                        reused.
     * @return all test cases that reference the test case with the given
     *         information, provided that the cases are also in the same 
     *         project.
     * @see getAllExecTestCases
     * @see getExternalExecTestCases
     */
    public static List<IExecTestCasePO> getInternalExecTestCases(
        String specTcGuid, long parentProjectId) {

        // a SpecTC with guid == null can't be reused
        if (specTcGuid == null) {
            return new ArrayList<IExecTestCasePO>(0);
        }
        
        List<Long> parentProjectIds = new ArrayList<Long>();
        parentProjectIds.add(parentProjectId);

        return getExecTestCasesFor(specTcGuid, parentProjectIds, 
            GeneralStorage.getInstance().getMasterSession());
    }
    
    /**
     * Returns ref test suites that reference the test suites given information.
     * Only returns ref test cases that are in the same project as the given
     * test suite. These ref test suites are loaded in the Master Session.
     * Warning: the fetched ref test suites have no parent, because the database
     * doesn't know the parent.
     * 
     * @param tsGuid
     *            GUID of the test suite being reused.
     * @param parentProjectId
     *            ID of the parent project of the test case being reused.
     * @return all ref test suites that reference the test suite with the given
     *         information, provided that the cases are also in the same
     *         project.
     */
    public static List<IRefTestSuitePO> getInternalRefTestSuites(
            String tsGuid, long parentProjectId) {
        // a test suite with guid == null can't be reused
        if (tsGuid == null) {
            return new ArrayList<IRefTestSuitePO>(0);
        }

        List<Long> parentProjectIds = new ArrayList<Long>();
        parentProjectIds.add(parentProjectId);

        return getRefTestSuitesFor(tsGuid, parentProjectIds, GeneralStorage
                .getInstance().getMasterSession());
    }
    
    /**
     * 
     * @param tsGuid The GUID of the reused test suite.
     * @param parentProjectIds All returned test suites will have one of these as
     *                         their project parent ID.
     * @param s The session into which the test cases will be loaded.
     * @return list of test suites.
     */
    @SuppressWarnings("unchecked")
    private static synchronized List<IRefTestSuitePO> getRefTestSuitesFor(
        String tsGuid, List<Long> parentProjectIds, EntityManager s) {

        StringBuffer queryBuffer = new StringBuffer(
            "select ref from RefTestSuitePO as ref where ref.testSuiteGuid = :tsGuid and ref.hbmParentProjectId in :ids"); //$NON-NLS-1$
        Query q = s.createQuery(queryBuffer.toString());
        q.setParameter("tsGuid", tsGuid); //$NON-NLS-1$
        q.setParameter("ids", parentProjectIds); //$NON-NLS-1$
        
        List<IRefTestSuitePO> refTestSuiteList = q.getResultList();
        return refTestSuiteList;
    }

    /**
     * Returns test cases that reference the test case given information. Only
     * returns test cases that are in the same project as the given test case
     * including test cases from reused projects. These test cases are loaded in
     * the Master Session. Warning: the fetched ExecTestCases have no parent,
     * because the database doesn't know the parent.
     * 
     * @param specTcGuid
     *            GUID of the test case being reused.
     * @param parentProjectIds
     *            IDs of the parent projects of the test case being reused.
     * @return all test cases that reference the test case with the given
     *         information, provided that the cases are also in the same
     *         project or reused projects.
     * @see getAllExecTestCases
     * @see getExternalExecTestCases
     * @see getInternalExecTestCases
     */
    public static List<IExecTestCasePO> getExecTestCases(
        String specTcGuid, List<Long> parentProjectIds) {

        // a SpecTC with guid == null can't be reused
        if (specTcGuid == null) {
            return new ArrayList<IExecTestCasePO>(0);
        }

        return getExecTestCasesFor(specTcGuid, parentProjectIds, 
            GeneralStorage.getInstance().getMasterSession());
    }
    
    /**
     * Returns test cases that reference the test case given information. 
     * Only returns test cases that are <em>NOT</em> in the same project 
     * as the given test case.
     * This method opens a new session to gather the test cases and then closes
     * the session in order to prevent accidental DB commits for test cases
     * external to the current project.
     * Warning: the fetched ExecTestCases have no parent, because the database
     * doesn't know the parent.
     * 
     * @param specTcGuid GUID of the test case being reused.
     * @param parentProjectId ID of the parent project of the test case being
     *                        reused.
     * @return all test cases that reference the test case with the given
     *         information, provided that the cases are <em>NOT</em> in the 
     *         same project.
     * @see getInternalExecTestCases
     * @see getAllExecTestCases
     */
    public static List<IExecTestCasePO> getExternalExecTestCases(
        String specTcGuid, long parentProjectId) throws JBException {
        
        // a SpecTC with guid == null can't be reused
        if (specTcGuid == null) {
            return new ArrayList<IExecTestCasePO>(0);
        }
        
        EntityManager s = Persistor.instance().openSession();
        
        try {
            EntityTransaction tx = 
                Persistor.instance().getTransaction(s);

            IProjectPO parentProject = s.find(NodeMaker.getProjectPOClass(),
                    parentProjectId);

            if (parentProject == null) {
                String error = 
                    Messages.ParentProjectDoesNotExistWithID 
                    + StringConstants.COLON + StringConstants.SPACE
                    + parentProjectId;
                log.error(error);
                throw new JBException(error, MessageIDs.E_DATABASE_GENERAL);
            }

            List<Long> projectsThatReuse = 
                ProjectPM.findIdsThatReuse(parentProject.getGuid(), 
                    parentProject.getMajorProjectVersion(), 
                    parentProject.getMinorProjectVersion());

            List<IExecTestCasePO> tcList = 
                getExecTestCasesFor(specTcGuid, projectsThatReuse, s);

            Persistor.instance().commitTransaction(s, tx);

            return tcList;

        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(s);
        }        
    }

    /**
     * 
     * @param specTcGuid The GUID of the reused test case.
     * @param parentProjectIds All returned test cases will have one of these as
     *                         their project parent ID.
     * @param s The session into which the test cases will be loaded.
     * @return list of test cases.
     */
    @SuppressWarnings("unchecked")
    private static synchronized List<IExecTestCasePO> getExecTestCasesFor(
        String specTcGuid, List<Long> parentProjectIds, EntityManager s) {

        StringBuffer queryBuffer = new StringBuffer(
            "select ref from ExecTestCasePO as ref where ref.specTestCaseGuid = :specTcGuid and ref.hbmParentProjectId in :ids"); //$NON-NLS-1$
    
        Query q = s.createQuery(queryBuffer.toString());
        q.setParameter("specTcGuid", specTcGuid); //$NON-NLS-1$
        q.setParameter("ids", parentProjectIds); //$NON-NLS-1$
        
        List<IExecTestCasePO> execTcList = q.getResultList();

        return execTcList;

    }
    
    /**
     * Returns all test cases that reference the test case given information. 
     * This method opens a new session to gather the test cases and then closes
     * the session in order to prevent accidental DB commits for test cases
     * external to the current project.
     * Warning: the fetched ExecTestCases have no parent, because the database
     * doesn't know the parent.
     * @param specTcGuid GUID of the test case being reused.
     * @param parentProjectId ID of the parent project of the test case being
     *                        reused.
     * @return <em>all</em> test cases that reference the test case with the 
     *         given information, regardless of which project they are in.
     * @see getInternalExecTestCases
     * @see getExternalExecTestCases
     */
    public static List<IExecTestCasePO> getAllExecTestCases(
        String specTcGuid, long parentProjectId) throws JBException {
        // a SpecTC with guid == null can't be reused
        if (specTcGuid == null) {
            return new ArrayList<IExecTestCasePO>(0);
        }
        
        EntityManager s = Persistor.instance().openSession();

        try {
            EntityTransaction tx = 
                Persistor.instance().getTransaction(s);

            IProjectPO parentProject = s.find(
                    NodeMaker.getProjectPOClass(), parentProjectId);

            if (parentProject == null) {
                String error = 
                    Messages.ParentProjectDoesNotExistWithID 
                    + StringConstants.COLON + StringConstants.SPACE
                    + parentProjectId;
                log.error(error);
                throw new JBException(error, MessageIDs.E_DATABASE_GENERAL);
            }

            List<Long> projectsThatReuse = 
                ProjectPM.findIdsThatReuse(parentProject.getGuid(), 
                    parentProject.getMajorProjectVersion(), 
                    parentProject.getMinorProjectVersion());
            // Project technically "reuses" itself
            projectsThatReuse.add(parentProjectId);

            List<IExecTestCasePO> tcList = 
                getExecTestCasesFor(specTcGuid, projectsThatReuse, s);

            Persistor.instance().commitTransaction(
                s, tx);

            return tcList;
        } finally {
            Persistor.instance().dropSessionWithoutLockRelease(s);
        }        
    }

    /**
     * 
     * @param project the project
     * @param reused the reused project
     * @return list of exec test cases in the given project that use 
     *         specTestCases from the given reused project
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<IExecTestCasePO> getUsedTestCaseNames(
            IProjectPO project, IReusedProjectPO reused) {
 
        if (project == null) {
            return new ArrayList<IExecTestCasePO>();
        }

        EntityManager s = GeneralStorage.getInstance().getMasterSession();
        Query q = s.createQuery("select ref from ExecTestCasePO as ref where ref.hbmParentProjectId = :parentProjectId"  //$NON-NLS-1$
                + " and ref.projectGuid = :projectGuid"); //$NON-NLS-1$
        q.setParameter("parentProjectId", project.getId()); //$NON-NLS-1$
        q.setParameter("projectGuid", reused.getProjectGuid()); //$NON-NLS-1$
        
        List<IExecTestCasePO> result = q.getResultList();
        return result;
        
    }

    /**
     * 
     * @param project proj
     * @return read-only list of ISpecPersistable objects
     */
    public static synchronized List<ISpecPersistable> loadSpecObjList(
        IProjectPO project) {
        
        if (project == null) {
            return new ArrayList<ISpecPersistable>();
        }

        EntityManager s = GeneralStorage.getInstance().getMasterSession();
        Query q = s.createQuery("select cont from SpecObjContPO as cont where cont.hbmParentProjectId = :parentProjectId"); //$NON-NLS-1$
        q.setParameter("parentProjectId", project.getId()); //$NON-NLS-1$
        
        try {
            return ((ISpecObjContPO)q.getSingleResult()).getSpecObjList();
        } catch (NoResultException nre) {
            return new ArrayList<ISpecPersistable>();
        }
        
    }
    
    /**
     * Finds a test case within reused projects.
     * @param reusedProjects Set of reused projects that are available.
     * @param projectGuid The GUID of the parent project of the spec testcase
     * @param specTcGuid The GUID of the spec testcase
     * @return the spec testcase with the given guid, or <code>null</code> if 
     *         the testcase cannot be found
     */
    public static synchronized ISpecTestCasePO getSpecTestCase(
        Set<IReusedProjectPO> reusedProjects, String projectGuid, 
        String specTcGuid) {
       
        Integer majorNumber = null;
        Integer minorNumber = null;
        for (IReusedProjectPO reusedProj : reusedProjects) {
            if (reusedProj.getProjectGuid().equals(projectGuid)) {
                majorNumber = reusedProj.getMajorNumber();
                minorNumber = reusedProj.getMinorNumber();
                break;
            }
        }

        if (majorNumber == null) {
            return null;
        }

        EntityManager s = GeneralStorage.getInstance().getMasterSession();
        Long projectId = NodePM.getInstance().findProjectID(s, projectGuid,
                majorNumber, minorNumber);
        if (projectId == null) {
            return null;
        }
        Object result = NodePM.getInstance().findSpecTC(s, specTcGuid, 
                projectId);
        if (result instanceof ISpecTestCasePO) {
            return (ISpecTestCasePO)result;
        }
        
        return null;
    }

    /**
     * find and cache a referenced spec TC
     * @param s Session
     * @param specTcGuid GUID of TC
     * @param projectId ID of project containing TC
     * @return the resulting TC or null if none was found
     */
    private Object findSpecTC(EntityManager s, String specTcGuid,
            Long projectId) {
        validateSession(s);
        
        StringBuilder idBuilder = new StringBuilder(50);
        idBuilder.append(specTcGuid);
        idBuilder.append(':');
        idBuilder.append(projectId);
        String key = idBuilder.toString();
        
        if (m_useCache) {
            Object cached = m_specTCCache.get(key);
            if (cached != null) {
                if (cached instanceof INodePO) { // check for not found
                    return cached;
                } 
                return null;                
            }
        }
        Query specTcQuery =
            s.createQuery("select node from SpecTestCasePO as node where node.guid = :guid" //$NON-NLS-1$
                + " and node.hbmParentProjectId = :projectId"); //$NON-NLS-1$
        specTcQuery.setParameter("guid", specTcGuid); //$NON-NLS-1$
        specTcQuery.setParameter("projectId", projectId); //$NON-NLS-1$

        Object result = null;

        try {
            result = specTcQuery.getSingleResult();
        } catch (NoResultException nre) {
            // No result found. The result remains null.
        }

        if (m_useCache) {
            if (result != null) {
                m_specTCCache.put(key, result);
            } else {
                m_specTCCache.put(key, new Object()); // set a not found marker
            }
        }
        return result;
    }
    
    /**
     * find and cache a reused projects OID
     * @param s Session
     * @param projectGuid GUID of project
     * @param majorNumber version number
     * @param minorNumber version number
     * @return the OID of the project or null if the project cannot be found
     */
    private Long findProjectID(EntityManager s, String projectGuid,
            Integer majorNumber, Integer minorNumber) {
        validateSession(s);

        String key = buildProjectKey(projectGuid, majorNumber, minorNumber);
        
        if (m_useCache) {
            Long id = m_projectIDCache.get(key);
            if (id != null) {
                if (id.longValue() != -1) { // means already lookuped but not
                                            // found
                    return id;
                }
                return null;
            }
        }
        Query projIdQuery = s.createQuery(
                    "select project from ProjectPO as project" //$NON-NLS-1$
                        + " inner join fetch project.properties where project.guid = :guid" //$NON-NLS-1$
                        + " and project.properties.majorNumber = :majorNumber and project.properties.minorNumber = :minorNumber"); //$NON-NLS-1$           
        projIdQuery.setParameter("guid", projectGuid); //$NON-NLS-1$
        projIdQuery.setParameter("majorNumber", majorNumber); //$NON-NLS-1$
        projIdQuery.setParameter("minorNumber", minorNumber); //$NON-NLS-1$
        try {
            final Object uniqueResult = projIdQuery.getSingleResult();
            Long projectId = ((IProjectPO)uniqueResult).getId();
            if (m_useCache) {
                m_projectIDCache.put(key, projectId);
            }
            return projectId;
        } catch (NoResultException nre) {
            if (m_useCache) {
                m_projectIDCache.put(key, new Long(-1));
            }
            return null;
        }
    }

    /**
     * checks if the Session was used before and discards caches if not
     * @param s Session
     */
    private void validateSession(EntityManager s) {
        if (m_useCache && m_lastSession != s) {
            resetCaching();
            m_lastSession = s;
        }        
    }

    /**
     * clears all caches
     */
    private void resetCaching() {
        if (m_projectIDCache != null) {
            m_projectIDCache.clear();
            m_projectIDCache = null;
        }
        if (m_specTCCache != null) {
            m_specTCCache.clear();
            m_specTCCache = null;
        }
        m_lastSession = null;
        if (m_useCache) {
            m_projectIDCache = new HashMap<String, Long>(20);
            m_specTCCache = new HashMap<String, Object>(500);
        }
    }

    /**
     * build a key for the cache
     * 
     * @param projectGuid
     *            part
     * @param majorNumber
     *            part
     * @param minorNumber
     *            part
     * @return a key combined from the parts
     */
    private static String buildProjectKey(String projectGuid,
            Integer majorNumber, Integer minorNumber) {
        StringBuilder idBuilder = new StringBuilder(200);
        idBuilder.append(projectGuid);
        idBuilder.append(StringConstants.COLON);
        idBuilder.append(majorNumber);
        idBuilder.append(StringConstants.COLON);
        idBuilder.append(minorNumber);
        return idBuilder.toString();
    }

    /**
     * Finds a test case within the project with the given ID.
     * @param projectId The ID of the parent project of the spec testcase
     * @param specTcGuid The GUID of the spec testcase
     * @return the spec testcase with the given guid, or <code>null</code> if 
     *         the testcase cannot be found
     */
    public static synchronized ISpecTestCasePO getSpecTestCase(Long projectId, 
        String specTcGuid) {
       
        EntityManager s = GeneralStorage.getInstance().getMasterSession();
        return (ISpecTestCasePO)NodePM.getInstance().findSpecTC(s, specTcGuid,
                projectId);
    }
    
    /**
     * Finds a Test Suite within the currently opened project.
     * 
     * @param testSuiteGuid The GUID of the Test Suite.
     * @return the Test Suite with the given GUID, or <code>null</code> if 
     *         no such Test Suite can be found.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static synchronized ITestSuitePO getTestSuite(
            String testSuiteGuid) {
       
        GeneralStorage gs = GeneralStorage.getInstance();
        IProjectPO currentProject = gs.getProject();
        if (currentProject != null) {
            EntityManager s = gs.getMasterSession();
            
            CriteriaBuilder builder = s.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery();
            Root from = query.from(NodeMaker.getTestSuitePOClass());
            query.select(from).where(
                builder.like(from.get("guid"), testSuiteGuid),  //$NON-NLS-1$
                builder.equal(
                    from.get("hbmParentProjectId"), currentProject.getId())); //$NON-NLS-1$

            try {
                Object result = s.createQuery(query).getSingleResult();
                if (result instanceof ITestSuitePO) {
                    return (ITestSuitePO)result;
                }
            } catch (NoResultException nre) {
                // No result found. Fall through to return null as per javadoc.
            }
        }
        
        return null;
    }
    
    /**
     * Finds a node within the project with the given ID.
     * @param projectId The ID of the parent project of the spec testcase
     * @param nodeGuid The GUID of the node
     * @return the spec testcase with the given guid, or <code>null</code> if 
     *         the testcase cannot be found
     */
    public static synchronized INodePO getNode(Long projectId, 
            String nodeGuid) {
        return getNode(projectId, nodeGuid, 
                GeneralStorage.getInstance().getMasterSession());
    }
    
    /**
     * Finds a node within the project with the given ID.
     * @param projectId The ID of the parent project of the spec testcase
     * @param nodeGuid The GUID of the node
     * @param session may not be null
     * @return the spec testcase with the given guid, or <code>null</code> if 
     *         the testcase cannot be found
     */
    public static synchronized INodePO getNode(Long projectId, String nodeGuid,
            EntityManager session) {
        
        Validate.notNull(session);
        
        Query specTcQuery = session.createQuery("select node from NodePO node where node.guid = :guid" //$NON-NLS-1$
                + " and node.hbmParentProjectId = :projectId"); //$NON-NLS-1$
        specTcQuery.setParameter("guid", nodeGuid); //$NON-NLS-1$
        specTcQuery.setParameter("projectId", projectId); //$NON-NLS-1$
 
        try {
            Object result = specTcQuery.getSingleResult();
            if (result instanceof INodePO) {
                return (INodePO)result;
            }
        } catch (NoResultException nre) {
            // No result found. Fall through to return null.
        }
        return null;
    }
    
    /**
     * Loads a bag of Nodes into the given session and returns the loaded
     * Nodes.
     * 
     * @param projectId The Project in which to search for the Nodes.
     * @param guids The GUIDs for which to load Nodes.
     * @param session The session into which to load the Nodes.
     * @return the loaded Nodes, mapped by GUID. GUIDs for which no node could
     *         be found are mapped to <code>null</code>.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static synchronized Map<String, INodePO> getNodes(Long projectId, 
            Collection<String> guids, EntityManager session) {
        
        CriteriaQuery query = session.getCriteriaBuilder().createQuery();
        Root from = query.from(NodeMaker.getNodePOClass());
        Predicate parentProjectPred = session.getCriteriaBuilder().equal(
                from.get("hbmParentProjectId"), projectId); //$NON-NLS-1$
        Predicate guidDisjunction = PersistenceUtil.getExpressionDisjunction(
                guids, from.get("guid"), session.getCriteriaBuilder()); //$NON-NLS-1$
        query.select(from).where(parentProjectPred, guidDisjunction);
        
        List<INodePO> nodeList = session.createQuery(query).getResultList();

        Map<String, INodePO> guidToNodeMap = new HashMap<String, INodePO>();
        for (INodePO node : nodeList) {
            String guid = node.getGuid();
            if (!guidToNodeMap.containsKey(guid)) {
                guidToNodeMap.put(guid, node);
            }
        }

        return guidToNodeMap;
    }

    /**
     * @param parentProjectId The ID of the project for which to find the number
     *                        of nodes.
     * @param sess The session in which to perform the query.
     * @return The number of nodes that have the project for the given ID as 
     *         an absolute parent.
     */
    public static long getNumNodes(long parentProjectId, EntityManager sess) {
        
        Query specTcQuery =
            sess.createQuery("select count(node) from NodePO as " //$NON-NLS-1$
                    + "node where node.hbmParentProjectId = :parentProjectId"); //$NON-NLS-1$
        specTcQuery.setParameter("parentProjectId", parentProjectId); //$NON-NLS-1$
        
        try {
            return (Long)specTcQuery.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
        
    }

    /**
     * @param parentProjectId The ID of the project for which to find the number
     *                        of TD managers.
     * @param sess The session in which to perform the query.
     * @return The number of TD managers that have the project for the 
     *         given ID as an absolute parent.
     */
    public static long getNumTestDataManagers(
            long parentProjectId, EntityManager sess) {
        
        Query tdManQuery = sess.createQuery("select count(tdMan) from TDManagerPO " //$NON-NLS-1$
                    + "tdMan where tdMan.hbmParentProjectId = :parentProjectId"); //$NON-NLS-1$
        tdManQuery.setParameter("parentProjectId", parentProjectId); //$NON-NLS-1$

        try {
            return (Long)tdManQuery.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
    }

    /**
     * @param parentProjectId The ID of the project for which to find the number
     *                        of execTCs.
     * @param sess The session in which to perform the query.
     * @return The number of execTCs that have the project for the 
     *         given ID as an absolute parent.
     */
    public static long getNumExecTestCases(
            long parentProjectId, EntityManager sess) {
        
        Query execTcQuery =
            sess.createQuery("select count(execTc) from ExecTestCasePO as " //$NON-NLS-1$
                    + "execTc where execTc.hbmParentProjectId = :parentProjectId"); //$NON-NLS-1$
        execTcQuery.setParameter("parentProjectId", parentProjectId); //$NON-NLS-1$

        try {
            return (Long)execTcQuery.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
    }

    /**
     * @param parentProjectId The ID of the project for which to find the number
     *                        of execTCs.
     * @param sess The session in which to perform the query.
     * @return The number of execTCs that have the project for the 
     *         given ID as an absolute parent.
     */
    public static long getNumExecTestCasesWithRefTd(
            long parentProjectId, EntityManager sess) {
        
        Query execTcQuery =
            sess.createQuery("select count(execTc) from ExecTestCasePO as " //$NON-NLS-1$
                    + "execTc where execTc.hbmParentProjectId = :parentProjectId and execTc.hasReferencedTD = :hasReferencedTD"); //$NON-NLS-1$
        execTcQuery.setParameter("parentProjectId", parentProjectId); //$NON-NLS-1$
        execTcQuery.setParameter("hasReferencedTD", true); //$NON-NLS-1$

        try {
            return (Long)execTcQuery.getSingleResult();
        } catch (NoResultException nre) {
            return 0;
        }
    }

    /**
     * @param useCache should the cache be used
     */
    public void setUseCache(boolean useCache) {
        m_useCache = useCache;
        resetCaching();
    }

    /**
     * @return true if the internal cache is in use
     */
    public boolean isUseCache() {
        return m_useCache;
    }
    
    /**
     * Class to collect nodes with tracked changes
     * @author BREDEX GmbH
     * @created 05.11.2013
     */
    private static class CollectNodesWithTrackedChangesOperation 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
        
        /**
         * list of nodes with tracked changes
         */
        private List<INodePO> m_listOfNodesWithTrackedChanges = 
                new ArrayList<INodePO>();
        
        /**
         * project which contains the nodes
         */
        private IProjectPO m_project;
        
        /**
         * the constructor
         * @param project the project which contains the nodes
         */
        public CollectNodesWithTrackedChangesOperation(IProjectPO project) {
            m_project = project;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            Long parentProjectId = node.getParentProjectId();
            if (parentProjectId != null
                    && !m_project.getId().equals(parentProjectId)) {
                return false;
            }
            if (!node.getTrackedChanges().isEmpty()) {
                m_listOfNodesWithTrackedChanges.add(node);
            }
            return true;
        }
        
        /**
         * returns the list of nodes with tracked changes
         * @return the list of nodes with tracked changes
         */
        public List<INodePO> getListOfNodesWithTrackedChanges() {
            return m_listOfNodesWithTrackedChanges;
        }
    }
    
    /**
     * Deletes all tracked changes of a project
     * @param monitor the monitor
     * @param project the project
     * @return the list of nodes for which tracked changes could not be deleted
     * @throws ProjectDeletedException 
     * @throws PMException
     */
    public static List<INodePO> cleanupTrackedChanges(
            IProgressMonitor monitor, final IProjectPO project) 
        throws PMException, ProjectDeletedException {
        
        CollectNodesWithTrackedChangesOperation treeNodeOp = 
                new CollectNodesWithTrackedChangesOperation(project);
        
        TreeTraverser treeTraverser = new TreeTraverser(
                project, treeNodeOp, true, true);
        treeTraverser.traverse();
        
        List<INodePO> listOfNodesWithTrackedChanges = 
                treeNodeOp.getListOfNodesWithTrackedChanges();
        
        List<INodePO> listOfLockedNodes = new ArrayList<INodePO>();
                
        monitor.beginTask(Messages.DeleteTrackedChangesActionDialog, 
                listOfNodesWithTrackedChanges.size());

        GeneralStorage instance = GeneralStorage.getInstance();
        final EntityManager session = instance.getMasterSession();
        final Persistor persistor = Persistor.instance();
        EntityTransaction tx = null;
        
        for (INodePO node: listOfNodesWithTrackedChanges) {
            tx = persistor.getTransaction(session);
            try {
                persistor.lockPO(session, node);
                node.deleteTrackedChanges();
            } catch (PMException e) {
                // can not delete tracked changes of this node
                listOfLockedNodes.add(node);
            }
            monitor.worked(1);
        }
        
        persistor.commitTransaction(session, tx);
        LockManager.instance().unlockPOs(session);
        
        for (INodePO node: listOfNodesWithTrackedChanges) {
            DataEventDispatcher.getInstance().fireDataChangedListener(node,
                    DataState.StructureModified, UpdateState.all);
        }
        
        monitor.done();
        return listOfLockedNodes;
    }
}
