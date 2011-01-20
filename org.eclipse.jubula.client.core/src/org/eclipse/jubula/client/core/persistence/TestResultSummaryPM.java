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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.eclipse.jubula.client.core.businessprocess.progress.OperationCanceledUtil;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PM to handle all test result summaries related hibernate queries
 * 
 * @author BREDEX GmbH
 * @created Mar 3, 2010
 */
public class TestResultSummaryPM {

    /** standard logging */
    private static Logger log = 
        LoggerFactory.getLogger(TestResultSummaryPM.class);
    
    /** name of Test Result Summary's (internal) "GUID" property */
    private static final String PROPNAME_GUID = "internalGuid"; //$NON-NLS-1$

    /** name of Test Result Summary's (internal) "Project GUID" property */
    private static final String PROPNAME_PROJECT_GUID = "internalProjectGuid"; //$NON-NLS-1$

    /** name of Test Result Summary's "Project Major Version" property */
    private static final String PROPNAME_PROJECT_MAJOR_VERSION = "projectMajorVersion"; //$NON-NLS-1$

    /** name of Test Result Summary's "Project Minor Version" property */
    private static final String PROPNAME_PROJECT_MINOR_VERSION = "projectMinorVersion"; //$NON-NLS-1$

    /** name of Test Result Summary's "AUT Agent Name" property */
    private static final String PROPNAME_AUT_AGENT_NAME = "autAgentName"; //$NON-NLS-1$
    
    /** name of Test Result Summary's "Executed Test Steps" property */
    private static final String PROPNAME_EXECUTED_TESTSTEPS = "testsuiteExecutedTeststeps"; //$NON-NLS-1$
    
    /** name of Test Result Summary's "AUT Hostname" property */
    private static final String PROPNAME_AUT_HOSTNAME = "autHostname"; //$NON-NLS-1$
    
    /** name of Test Result Summary's "Test Suite Date" property */
    private static final String PROPNAME_TESTSUITE_DATE = "testsuiteDate"; //$NON-NLS-1$

    /**
     * hide
     */
    private TestResultSummaryPM() {
    // empty
    }

    /**
     * @param proj
     *            the project to search in
     * @param se
     *            the hibernate session to use for query (optional)
     * @return a list of all test result summaries for the given project for all
     *         available project version
     * @throws PMException
     *             in case of any DB error.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final List<ITestResultSummaryPO> getAllTestResultSummaries(
            IProjectPO proj, EntityManager se) throws PMException {
        List<ITestResultSummaryPO> ltrs = null;
        EntityManager s = null;
        try {
            s = se != null ? se : Hibernator.instance().openSession();
            
            CriteriaBuilder builder = s.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery();
            Root from = query.from(PoMaker.getTestResultSummaryClass());
            query.select(from).where(
                    builder.equal(
                        from.get(PROPNAME_PROJECT_GUID), 
                        proj.getGuid()));
            
            ltrs = s.createQuery(query).getResultList();
        } catch (PersistenceException e) {
            OperationCanceledUtil.checkForOperationCanceled(e);
            PersistenceManager.handleDBExceptionForAnySession(null, e, s);
        } finally {
            Hibernator.instance().dropSession(s);
        }
        return ltrs;
    }
    
    /**
     * store the testresult summary of test run in database
     * @param summary the testresult summary to store
     */
    public static final void storeTestResultSummaryInDB(
        ITestResultSummaryPO summary) {
        final EntityManager sess = Hibernator.instance().openSession();
        try {            
            final EntityTransaction tx = 
                Hibernator.instance().getTransaction(sess);
            sess.persist(summary);
            Hibernator.instance().commitTransaction(sess, tx);
        } catch (PMException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Hibernator.instance().dropSession(sess);
        }
    }
    
    /**
     * saving collected monitoring data into DB. Instead of sess.persist() 
     * sess.merge() is called.
     * @param summary the testresult summary to store
     */
    public static final void mergeTestResultSummaryInDB(
        ITestResultSummaryPO summary) {
        final EntityManager sess = Hibernator.instance().openSession();
        try {            
            final EntityTransaction tx = 
                Hibernator.instance().getTransaction(sess);
            sess.merge(summary);
            Hibernator.instance().commitTransaction(sess, tx);
        } catch (PMException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Hibernator.instance().dropSession(sess);
        }
    }
    

    /**
     * Checks whether the given Test Result Summary already exists in the 
     * currently connected database. This opens a session in order
     * to perform the check, and closes the session immediately thereafter.
     * 
     * @param summary The Test Result Summary to check.
     * @return <code>true</code> if <code>summary</code> already exists in the
     *         currently connected database. Otherwise <code>false</code>.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final boolean doesTestResultSummaryExist(
            ITestResultSummaryPO summary) {

        final EntityManager sess = Hibernator.instance().openSession();
        try {            
            
            CriteriaBuilder builder = sess.getCriteriaBuilder();
            CriteriaQuery guidDuplicateQuery = builder.createQuery();
            Root guidDuplicateFrom = 
                guidDuplicateQuery.from(PoMaker.getTestResultSummaryClass());
            guidDuplicateQuery.select(builder.count(guidDuplicateFrom)).where(
                    builder.equal(
                            guidDuplicateFrom.get(PROPNAME_GUID), 
                            summary.getInternalGuid()));

            Number count = 
                (Number)HibernateUtil
                    .setReadOnlyHint(sess.createQuery(guidDuplicateQuery))
                    .getSingleResult();
            if (count.longValue() > 0) {
                return true;
            }

            CriteriaQuery duplicateQuery = builder.createQuery();
            Root duplicateFrom = 
                duplicateQuery.from(PoMaker.getTestResultSummaryClass());
            duplicateQuery.select(builder.count(duplicateFrom)).where(
                    builder.and(
                        builder.equal(
                            duplicateFrom.get(PROPNAME_TESTSUITE_DATE), 
                            summary.getTestsuiteDate()),
                        builder.equal(
                            duplicateFrom.get(PROPNAME_AUT_HOSTNAME), 
                            summary.getAutHostname()),
                        builder.equal(
                            duplicateFrom.get(PROPNAME_EXECUTED_TESTSTEPS), 
                            summary.getTestsuiteExecutedTeststeps()),
                        builder.equal(
                            duplicateFrom.get(PROPNAME_AUT_AGENT_NAME), 
                            summary.getAutAgentName())));

            count = (Number)HibernateUtil.setReadOnlyHint(
                    sess.createQuery(duplicateQuery)).getSingleResult();
            if (count.longValue() > 0) {
                log.error("Duplicate Test Result Summary (GUID=" 
                        + summary.getInternalGuid() 
                        + ") will not be imported.");
                return true;
            }

            return false;
        } finally {
            Hibernator.instance().dropSessionWithoutLockRelease(sess);
        }
    }

    /**
     * load metadata from database
     * 
     * @param startTime
     *            the test suite start time for summaries to be included
     * @return list of metadata objects
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final List<ITestResultSummaryPO> findAllTestResultSummaries(
        Date startTime)
        throws JBException {
        EntityManager session = null;
        if (Hibernator.instance() == null) {
            return null;
        }
        try {
            session = Hibernator.instance().openSession();
            Query query = session
                    .createQuery("select s from TestResultSummaryPO as s " + //$NON-NLS-1$
                            "where s.testsuiteDate > :startTime"); //$NON-NLS-1$
            query.setParameter("startTime", startTime); //$NON-NLS-1$
            List<ITestResultSummaryPO> metaList = query.getResultList();
            return metaList;
        } catch (PersistenceException e) {
            log.error(Messages.HibernateLoadFailed, e);
            throw new JBException(e.getMessage(),
                    MessageIDs.E_HIBERNATE_LOAD_FAILED);
        } finally {
            Hibernator.instance().dropSessionWithoutLockRelease(session);
        }
    }
    
    /**
     * get set of test result summary ids older than amount of days
     * @param cleanDate Date
     * @param projGUID the project guid
     * @param majorVersion the project major version
     * @param minorVersion the project minor version
     * @return set of test result summary ids
     */
    @SuppressWarnings("unchecked")
    public static final Set<Long> findTestResultSummariesByDate(Date cleanDate,
            String projGUID, int majorVersion, int minorVersion)
        throws JBException {
        EntityManager session = null;
        if (Hibernator.instance() == null) {
            return null;
        }
        try {
            session = Hibernator.instance().openSession();
            Query query = session.createQuery("select r.internalTestResultSummaryID from TestResultPO as r " + //$NON-NLS-1$
                            "where r.internalTestResultSummaryID in " + //$NON-NLS-1$
                            "(select s.id from TestResultSummaryPO as s " + //$NON-NLS-1$
                            "where s.testsuiteDate < :cleanDate " + //$NON-NLS-1$
                            "and s.internalProjectGuid = :projGUID " + //$NON-NLS-1$
                            "and s.projectMajorVersion = :majorVersion " + //$NON-NLS-1$
                            "and s.projectMinorVersion = :minorVersion)"); //$NON-NLS-1$
            query.setParameter("cleanDate", cleanDate); //$NON-NLS-1$
            query.setParameter("projGUID", projGUID); //$NON-NLS-1$
            query.setParameter("majorVersion", majorVersion); //$NON-NLS-1$
            query.setParameter("minorVersion", minorVersion); //$NON-NLS-1$
            List<Long> metaList = query.getResultList();
            Set<Long> idSet = new HashSet<Long>(metaList);
            return idSet;
        } catch (PersistenceException e) {
            log.error(Messages.HibernateLoadFailed, e);
            throw new JBException(e.getMessage(),
                    MessageIDs.E_HIBERNATE_LOAD_FAILED);
        } finally {
            Hibernator.instance().dropSessionWithoutLockRelease(session);
        }
    }
    
    /**
     * delete testruns
     * @param resultIDs array of test result ids 
     */
    public static final void deleteTestruns(Long[] resultIDs) {
        if (Hibernator.instance() == null || resultIDs.length == 0) {
            return;
        }
        
        for (Long resultID : resultIDs) {
            deleteTestrun(resultID);
        }
        
    }
    
    /**
     * delete testrun
     * @param resultID id of test result
     */
    public static final void deleteTestrun(Long resultID) {
        if (Hibernator.instance() == null) {
            return;
        }
        final EntityManager session = Hibernator.instance().openSession();
        try {
            final EntityTransaction tx = Hibernator.instance()
                    .getTransaction(session);
            
            TestResultPM.executeDeleteTestresultOfSummary(session,
                    resultID);

            Query querySummary = session
                .createQuery("select s from TestResultSummaryPO as s where s.id = :id"); //$NON-NLS-1$
            querySummary.setParameter("id", resultID); //$NON-NLS-1$

            try {
                ITestResultSummaryPO meta = 
                    (ITestResultSummaryPO)querySummary.getSingleResult();
                session.remove(meta);
            } catch (NoResultException nre) {
                // No result found. Nothing to delete.
            }
            Hibernator.instance().commitTransaction(session, tx);
        } catch (PMException e) {
            throw new JBFatalException(Messages.DeleteTestrunMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.DeleteTestrunMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Hibernator.instance().dropSession(session);
        }
    }
    
    /**
     * delete testruns by project guid, minor, major version
     * @param guid project guid
     * @param major major project version
     * @param minor minor project version
     * @param deleteOnlyDetails true, if only testrun details will
     *          be deleted, summaries will not be deleted
     */
    @SuppressWarnings("unchecked")
    public static final void deleteTestrunsByProject(String guid,
            int major, int minor, boolean deleteOnlyDetails) {
        if (Hibernator.instance() == null) {
            return;
        }
        final EntityManager session = Hibernator.instance().openSession();
        try {
            final EntityTransaction tx = Hibernator.instance()
                    .getTransaction(session);
            
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select s from TestResultSummaryPO as s where s.") //$NON-NLS-1$
                .append(PROPNAME_PROJECT_GUID).append(" = :guid and s.") //$NON-NLS-1$
                .append(PROPNAME_PROJECT_MAJOR_VERSION).append(" = :major and s.") //$NON-NLS-1$
                .append(PROPNAME_PROJECT_MINOR_VERSION).append(" = :minor"); //$NON-NLS-1$
            
            Query querySummary = session.createQuery(queryBuilder.toString());
            querySummary.setParameter("guid", guid); //$NON-NLS-1$
            querySummary.setParameter("major", major); //$NON-NLS-1$
            querySummary.setParameter("minor", minor); //$NON-NLS-1$
            List <ITestResultSummaryPO> summaryList = 
                querySummary.getResultList();
            
            for (ITestResultSummaryPO summary : summaryList) {
                TestResultPM.executeDeleteTestresultOfSummary(
                        session, summary.getId());
                if (!deleteOnlyDetails) {
                    session.remove(summary);
                }
            }
            Hibernator.instance().commitTransaction(session, tx);
        } catch (PMException e) {
            throw new JBFatalException(Messages.DeleteTestrunFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.DeleteTestrunFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Hibernator.instance().dropSession(session);
        }
    }
    
    /**
     * delete all testresult summaries
     */
    public static final void deleteAllTestresultSummaries() {
        if (Hibernator.instance() == null) {
            return;
        }
        final EntityManager session = Hibernator.instance().openSession();
        try {
            final EntityTransaction tx = Hibernator.instance()
                    .getTransaction(session);
            Query query = session.createQuery("delete from TestResultSummaryPO as s"); //$NON-NLS-1$
            query.executeUpdate();
            Hibernator.instance().commitTransaction(session, tx);
        } catch (PMException e) {
            throw new JBFatalException(Messages.DeleteAllTestrunSummariesFailed,
                    e, MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.DeleteAllTestrunSummariesFailed,
                    e, MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Hibernator.instance().dropSession(session);
        }
    }
}
