/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.core.utils;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 */
public final class ALMAccess {
    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(ALMAccess.class);

    /** Constructor */
    private ALMAccess() {
        // hide
    }

    /**
     * @param repoLabel
     *            the label of the repository
     * @return the task repository or <code>null</code> if not found
     */
    private static TaskRepository getRepositoryByLabel(String repoLabel) {
        IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
        List<TaskRepository> allRepositories = repositoryManager
                .getAllRepositories();

        for (TaskRepository repo : allRepositories) {
            if (repo.getRepositoryLabel().equals(repoLabel)) {
                return repo;
            }
        }
        return null;
    }

    /**
     * @param repo
     *            the task repository
     * @param taskId
     *            the taskId
     * @return the tasks data or <code>null</code> if not found
     * @throws CoreException
     *             in case of a problem
     */
    private static TaskData getTaskDataByID(TaskRepository repo, String taskId)
        throws CoreException {
        TaskData taskData = null;
        if (repo != null && !repo.isOffline()) {
            AbstractRepositoryConnector connector = TasksUi
                    .getRepositoryConnector(repo.getConnectorKind());
            taskData = connector.getTaskData(repo, taskId,
                    new NullProgressMonitor());
        }
        return taskData;
    }

    /**
     * @param repoLabel
     *            repoLabel
     * @param taskId
     *            the taskId
     * @param comment
     *            the comment
     * @return true if succeeded; false otherwise
     */
    public static boolean createComment(String repoLabel, String taskId,
            String comment) {
        boolean succeeded = false;
        TaskRepository repo = getRepositoryByLabel(repoLabel);
        try {
            TaskData taskData = getTaskDataByID(repo, taskId);
            if (taskData != null) {
                AbstractRepositoryConnector connector = TasksUi
                        .getRepositoryConnector(repo.getConnectorKind());
                AbstractTaskDataHandler taskDataHandler = connector
                        .getTaskDataHandler();
                TaskAttribute root = taskData.getRoot();
                TaskAttribute newComment = root
                        .createMappedAttribute(TaskAttribute.COMMENT_NEW);
                newComment.setValue(comment);
                RepositoryResponse response = taskDataHandler.postTaskData(
                        repo, taskData, null, new NullProgressMonitor());
                succeeded = RepositoryResponse.ResponseKind.TASK_UPDATED
                        .equals(response.getReposonseKind());
            }
        } catch (CoreException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return succeeded;
    }

    /**
     * @param repoLabel
     *            repoLabel
     * @param taskId
     *            the taskId
     * @param taskAttributeId
     *            the id of the attribute to retrieve
     * @return the value or null if not found
     */
    public static String getTaskAttributeValue(String repoLabel, String taskId,
            String taskAttributeId) {
        String value = null;
        TaskRepository repo = getRepositoryByLabel(repoLabel);
        try {
            TaskData taskData = getTaskDataByID(repo, taskId);
            if (taskData != null) {
                TaskAttribute root = taskData.getRoot();
                TaskAttributeMapper attributeMapper = 
                        taskData.getAttributeMapper();
                TaskAttribute mappedAttribute = root
                        .getMappedAttribute(taskAttributeId);
                if (mappedAttribute != null) {
                    value = attributeMapper.getValue(mappedAttribute);
                }
            }
        } catch (CoreException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return value;
    }
}
