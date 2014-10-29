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
package org.eclipse.jubula.client.alm.mylyn.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.alm.mylyn.core.Activator;
import org.eclipse.jubula.client.alm.mylyn.core.bp.CommentReporter;
import org.eclipse.jubula.client.alm.mylyn.core.exceptions.InvalidALMAttributeException;
import org.eclipse.jubula.client.alm.mylyn.core.i18n.Messages;
import org.eclipse.jubula.client.alm.mylyn.core.model.ALMChange;
import org.eclipse.jubula.client.alm.mylyn.core.model.CommentEntry;
import org.eclipse.jubula.client.alm.mylyn.core.model.FieldUpdate;
import org.eclipse.jubula.client.core.utils.SimpleStringConverter;
import org.eclipse.jubula.client.core.utils.IParamValueToken;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.core.utils.VariableToken;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 */
public final class ALMAccess {
    /**
     * @author BREDEX GmbH
     */
    public enum CONNECTOR {
        /** default handling type */
        DEFAULT, 
        /** custom handling type */
        HP_ALM
    }
    
    /**
     * @author BREDEX GmbH
     */
    public static class ALMDefaultTaskMapping extends TaskMapping {
        /** m_values */
        private Map<String, String> m_values = new HashMap<String, String>();

        @Override
        public String getDescription() {
            return m_values.get(TaskAttribute.DESCRIPTION);
        }

        @Override
        public String getSummary() {
            return m_values.get(TaskAttribute.SUMMARY);
        }

        @Override
        public String getProduct() {
            return m_values.get(TaskAttribute.PRODUCT);
        }
        
        @Override
        public String getTaskKind() {
            return m_values.get(TaskAttribute.TASK_KIND);
        }
    }
    /**
     * Exception for Problems with resolving a variable
     * @author BREDEX GmbH
     */
    public static class CouldNotResolveException extends Exception {
        /** Could not not resolve a variable or similar */
        public CouldNotResolveException() {
            super();
        }
        /** Could not not resolve a variable or similar
         * @param message the message or the value
         */
        public CouldNotResolveException(String message) {
            super(message);
        }
    }
    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(ALMAccess.class);

    /** the postfix for a variable to generate the date in a specific format*/
    private static final String VARIABLE_DATE_POSTFIX = "date"; //$NON-NLS-1$
    /** the postfix for a variable to generate the url*/
    private static final String VARIABLE_URL_POSTFIX = "url"; //$NON-NLS-1$
    /** the prefix for the variable when to use the summary */
    private static final String VARIABLE_SUMMARY_PREFIX = "summary"; //$NON-NLS-1$
    /** the prefix for the variable when to use the node */
    private static final String VARIABLE_NODE_PREFIX = "node"; //$NON-NLS-1$
    
    /** Constructor */
    private ALMAccess() {
        // hide
    }

    /**
     * @param repoLabel
     *            the label of the repository
     * @return the task repository or <code>null</code> if not found
     */
    public static TaskRepository getRepositoryByLabel(String repoLabel) {
        List<TaskRepository> allRepositories = getAllRepositories();

        for (TaskRepository repo : allRepositories) {
            if (repo.getRepositoryLabel().equals(repoLabel)) {
                return repo;
            }
        }
        return null;
    }

    /**
     * @return a list of all available task repositories
     */
    public static List<TaskRepository> getAllRepositories() {
        IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
        return repositoryManager.getAllRepositories();
    }

    /**
     * @param repo
     *            the task repository
     * @param taskId
     *            the taskId
     * @param monitor
     *            the monitor to use
     * @return the task or <code>null</code> if not found
     * @throws CoreException
     *             in case of a problem
     */
    private static ITask getTaskByID(TaskRepository repo, String taskId,
        IProgressMonitor monitor) throws CoreException {
        ITask task = null;
        if (validRepository(repo)) {
            IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
            task = repositoryModel.getTask(repo, taskId);
            if (task == null) {
                task = repositoryModel.createTask(repo, taskId);
            }
        }
        return task;
    }

    /**
     * @param repo
     *            the repository to check
     * @return if the repository is valid
     */
    private static boolean validRepository(TaskRepository repo) {
        return repo != null && !repo.isOffline();
    }

    /**
     * Writes comments to ALM system
     * @param repoLabel
     *            repoLabel
     * @param taskId
     *            the taskId
     * @param comments
     *            the comment entries
     * @param monitor
     *            the monitor to use
     * @return true if succeeded; false otherwise
     */
    public static boolean createComment(String repoLabel, String taskId,
            List<CommentEntry> comments, IProgressMonitor monitor) {
        boolean succeeded = false;
        TaskRepository repo = getRepositoryByLabel(repoLabel);
        try {
            TaskData taskData = getTaskDataByID(repo, taskId, monitor);
            if (taskData == null) {
                return succeeded;
            }
            
            ITask task = getTaskByID(repo, taskData.getTaskId(), monitor);
            if (task != null) {
                ITaskDataManager taskDataManager = TasksUi.getTaskDataManager();
                ITaskDataWorkingCopy taskWorkingCopy = taskDataManager
                    .createWorkingCopy(task, taskData);
                TaskDataModel taskModel = new TaskDataModel(repo, task,
                    taskWorkingCopy);
                
                String connectorKind = repo.getConnectorKind();
                AbstractRepositoryConnector connector = TasksUi
                    .getRepositoryConnector(connectorKind);
                AbstractTaskDataHandler taskDataHandler = connector
                    .getTaskDataHandler();
                TaskAttribute rootData = taskModel.getTaskData()
                    .getRoot();
                CONNECTOR handle = determineConnectorHandling(connectorKind);
                TaskAttribute change = null;
                switch (handle) {
                    case HP_ALM:
                        change = hpAlmCommentHandling(comments, rootData);
                        break;
                    case DEFAULT:
                    default:
                        change = defaultCommentHandling(comments, rootData);
                        break;
                }
                if (change == null) {
                    return succeeded;
                }

                taskModel.attributeChanged(change);
                
                RepositoryResponse response = taskDataHandler.postTaskData(
                    taskModel.getTaskRepository(), taskModel.getTaskData(),
                    taskModel.getChangedOldAttributes(), monitor);
                
                succeeded = RepositoryResponse.ResponseKind.TASK_UPDATED
                        .equals(response.getReposonseKind());
            }
        } catch (CoreException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return succeeded;
    }

    /**
     * Updates fields in ALM system
     * @param repoLabel name of repo
     * @param taskId task id
     * @param fieldUpdates list of field updates
     * @param monitor monitor
     * @return OK if succeeded; WARNING when problems; ERROR otherwise
     */
    public static IStatus updateFields(String repoLabel, String taskId,
            List<FieldUpdate> fieldUpdates, IProgressMonitor monitor) {
        IStatus status = new Status(IStatus.ERROR, Activator.ID,
                "Unknown error."); //$NON-NLS-1$
        TaskRepository repo = getRepositoryByLabel(repoLabel);
        try {
            TaskData taskData = getTaskDataByID(repo, taskId, monitor);
            if (taskData == null) {
                return status;
            }
            
            ITask task = getTaskByID(repo, taskData.getTaskId(), monitor);
            if (task != null) {
                ITaskDataManager taskDataManager = TasksUi.getTaskDataManager();
                ITaskDataWorkingCopy taskWorkingCopy = taskDataManager
                    .createWorkingCopy(task, taskData);
                TaskDataModel taskModel = new TaskDataModel(repo, task,
                    taskWorkingCopy);
                
                String connectorKind = repo.getConnectorKind();
                AbstractRepositoryConnector connector = TasksUi
                    .getRepositoryConnector(connectorKind);
                AbstractTaskDataHandler taskDataHandler = connector
                    .getTaskDataHandler();
                TaskAttribute rootData = taskModel.getTaskData()
                    .getRoot();
                CONNECTOR handle = determineConnectorHandling(connectorKind);
                List<TaskAttribute> changes = null;
                switch (handle) {
                    case HP_ALM:
                    case DEFAULT:
                    default:
                        changes = defaultFieldUpdateHandling(
                                fieldUpdates, rootData);
                        break;
                }
                if (changes.isEmpty()) {
                    return status;
                }
                for (TaskAttribute change : changes) {
                    taskModel.attributeChanged(change);
                }
                
                RepositoryResponse response = taskDataHandler.postTaskData(
                    taskModel.getTaskRepository(), taskModel.getTaskData(),
                    taskModel.getChangedOldAttributes(), monitor);
                
                if (RepositoryResponse.ResponseKind.TASK_UPDATED
                        .equals(response.getReposonseKind())) {
                    status = new Status(IStatus.OK, Activator.ID,
                            "Task has been updated."); //$NON-NLS-1$
                } else {
                    status = new Status(IStatus.WARNING, Activator.ID,
                            "Task has not been updated successfully."); //$NON-NLS-1$
                }
            }
        } catch (InvalidALMAttributeException e) {
            status = new Status(IStatus.ERROR, Activator.ID,
                    e.getMessage());
        } catch (CouldNotResolveException e) {
            status = new Status(IStatus.CANCEL, Activator.ID,
                    "Could not resolve variable"); //$NON-NLS-1$
        } catch (CoreException e) {
            status = new Status(IStatus.ERROR, Activator.ID,
                    e.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            LOG.error("IllegalArgumentException occured", e); //$NON-NLS-1$
            // This is necessary due to an IllegalArgumentException which might be
            // thrown in the TaskDataHandler
            status = new Status(IStatus.ERROR, Activator.ID,
                    e.getLocalizedMessage(), e);
        }
        return status;
    }
    
    /**
     * Creates list of task attributes to change for default repository type
     * @param fieldUpdates the field updates
     * @param rootAttr root attribute
     * @return list of task attributes to change
     * @throws InvalidALMAttributeException 
     * @throws CouldNotResolveException
     */
    private static List<TaskAttribute> defaultFieldUpdateHandling(
            List<FieldUpdate> fieldUpdates, TaskAttribute rootAttr)
        throws InvalidALMAttributeException, CouldNotResolveException {
        List<TaskAttribute> changes = new ArrayList<TaskAttribute>();

        boolean failed = false;
        for (FieldUpdate u : fieldUpdates) {
            Map<String, Object> attributesToChange = u.getAttributesToChange();
            for (String key : attributesToChange.keySet()) {
                TaskAttribute fieldUpdate = rootAttr.getAttribute(key);
                if (fieldUpdate == null) {
                    throw new InvalidALMAttributeException(NLS.bind(
                            Messages.InvalidAttributeID, key));
                }
                if (fieldUpdate.getMetaData().isReadOnly()) {
                    throw new InvalidALMAttributeException(NLS.bind(
                            Messages.ReadOnlyAttributeID, key));
                }
                Object value = attributesToChange.get(key);
                if (value instanceof String) {
                    try {
                        value = getVariableValues((String)value, u);
                    } catch (CouldNotResolveException ce) {
                        // First validating all attributes and values before ending
                        failed = true;
                    }
                    Map<String, String> options = fieldUpdate.getOptions();
                    if (options != null && !options.isEmpty()) {
                        if (!options.containsKey(value)) {
                            throw new InvalidALMAttributeException(NLS.bind(
                                    Messages.InvalidValue, value, key));
                        }
                    }
                    fieldUpdate.setValue((String)value);
                } else {
                    fieldUpdate.setValue(value.toString());
                }

                changes.add(fieldUpdate);
            }
        }
        if (failed) {
            throw new CouldNotResolveException();
        }
        return changes;
    }

    /**
     * 
     * @param value the string value which variables of it should be resolved (using our parser)
     * @param fieldUpdate the {@link FieldUpdate} of the corresponding value
     * @return a string whith all variables resolved
     * @throws CouldNotResolveException 
     */
    private static String getVariableValues(String value,
            FieldUpdate fieldUpdate) throws CouldNotResolveException {
        boolean isFailing = false;
        if (StringUtils.isNotBlank(value)) {
            ParamValueConverter converter = new SimpleStringConverter(value);
            if (converter.containsErrors()) {
                CommentReporter.getInstance().getConsole()
                    .writeErrorLine(NLS.bind(
                            Messages.ParsingReportingRuledFailed, value));
                throw new CouldNotResolveException(NLS.bind(
                        Messages.ParsingReportingRuledFailed, value));
            }
            List<IParamValueToken> liste = converter.getTokens();
            String result = StringConstants.EMPTY;
            for (Iterator iterator = liste.iterator(); iterator.hasNext();) {
                IParamValueToken iParamValueToken = (IParamValueToken) iterator
                        .next();
                if (iParamValueToken instanceof VariableToken) {
                    try {
                        result += getBeanString(fieldUpdate,
                                (VariableToken) iParamValueToken); 
                    } catch (CouldNotResolveException ce) {
                        isFailing = true;
                    }
                } else {
                    result += iParamValueToken.getGuiString();
                }
            }
            if (isFailing) {
                throw new CouldNotResolveException();
            }
            return result;
        }
        return value;
    }

    /**
     * @param repo
     *            the task repository
     * @param taskId
     *            the taskId
     * @param monitor
     *            the monitor to use
     * @return the tasks data or <code>null</code> if not found
     * @throws CoreException
     *             in case of a problem
     */
    private static TaskData getTaskDataByID(TaskRepository repo, String taskId,
            IProgressMonitor monitor) throws CoreException {
        TaskData taskData = null;
        if (validRepository(repo)) {
            AbstractRepositoryConnector connector = TasksUi
                    .getRepositoryConnector(repo.getConnectorKind());
            taskData = connector.getTaskData(repo, taskId, monitor);
        }
        return taskData;
    }
    
    /**
     * @param comments
     *            the commentEntries to add
     * @param attr
     *            the attribute to modify
     * @return a flag indicating the success of attribute handling
     */
    private static TaskAttribute hpAlmCommentHandling(
        List<CommentEntry> comments, TaskAttribute attr) {
        Properties almProps = Activator.getDefault().getAlmAccessProperties();
        
        String hpTaskKindKeyPrefix = CONNECTOR.HP_ALM.toString()
                + StringConstants.DOT + TaskAttribute.TASK_KIND;
        String req = hpTaskKindKeyPrefix + ".REQUIREMENT"; //$NON-NLS-1$
        String hpTaskKindReq = almProps.getProperty(req);
        String def = hpTaskKindKeyPrefix + ".DEFECT"; //$NON-NLS-1$
        String hpTaskKindDefect = almProps.getProperty(def);

        String taskKindValue = attr.getMappedAttribute(
                TaskAttribute.TASK_KIND).getValue();
        String attrName = null;
        if (hpTaskKindReq.equals(taskKindValue)) {
            attrName = almProps.getProperty(req + ".comment"); //$NON-NLS-1$
        } else if (hpTaskKindDefect.equals(taskKindValue)) {
            attrName = almProps.getProperty(def + ".comment"); //$NON-NLS-1$
        }

        if (attrName != null) {
            TaskAttribute commentAttribute = attr.getMappedAttribute(attrName);
            String oldComment = commentAttribute.getValue();
            String newComment = StringConstants.EMPTY;
            for (ALMChange c : comments) {
                newComment = c.toString() + "<br>" //$NON-NLS-1$
                    + c.getDashboardURL() + "<br>" //$NON-NLS-1$
                    + newComment;
            }
            
            commentAttribute.setValue(newComment + oldComment);
            return commentAttribute;
        }
        return null;
    }

    /**
     * @param comments
     *            the commentEntries to add
     * @param attr
     *            the attribute to modify
     * @return a flag indicating the success of attribute handling
     */
    private static TaskAttribute defaultCommentHandling(
        List<CommentEntry> comments, TaskAttribute attr) {
        TaskAttribute newComment = attr
            .createMappedAttribute(TaskAttribute.COMMENT_NEW);
        String comment = StringConstants.EMPTY;

        for (CommentEntry c : comments) {
            comment = comment + StringConstants.NEWLINE + c.toString()
                + StringConstants.NEWLINE + c.getDashboardURL()
                + StringConstants.NEWLINE + StringConstants.NEWLINE;
        }

        newComment.setValue(comment);
        return newComment;
    }

    /**
     * @param connectorKind
     *            the connector kind
     * @return the connector handling type
     */
    private static CONNECTOR determineConnectorHandling(
            String connectorKind) {
        String hpAlmConnectorKind = Activator.getDefault()
                .getAlmAccessProperties()
                .getProperty(CONNECTOR.HP_ALM.toString());
        if (hpAlmConnectorKind.equals(connectorKind)) {
            return CONNECTOR.HP_ALM;
        }
        return CONNECTOR.DEFAULT;
    }

    /**
     * @param repoLabel
     *            the repository to test the connection for
     * @return a status reflecting the current connection state
     */
    public static IStatus testConnection(String repoLabel) {
        TaskRepository repository = getRepositoryByLabel(repoLabel);
        if (repository == null) {
            return new Status(IStatus.ERROR, Activator.ID, NLS.bind(
                    Messages.TaskRepositoryNotFound, repoLabel));
        }
        if (repository.isOffline()) {
            return new Status(IStatus.ERROR, Activator.ID, NLS.bind(
                    Messages.TaskRepositoryOffline, repoLabel));
        }
        
        boolean savePassword = repository
                .getSavePassword(AuthenticationType.REPOSITORY);
        if (!savePassword) {
            return new Status(IStatus.ERROR, Activator.ID, NLS.bind(
                    Messages.TaskRepositoryNoCredentialsStored, repoLabel));
        }
        
        AbstractRepositoryConnector connector = TasksUi
                .getRepositoryConnector(repository.getConnectorKind());
        if (connector == null) {
            return new Status(IStatus.ERROR, Activator.ID, NLS.bind(
                    Messages.TaskRepositoryNoConnectorFound, repoLabel));
        }
        
        try {
            connector.updateRepositoryConfiguration(repository,
                    new NullProgressMonitor());
        } catch (CoreException e) {
            return new Status(IStatus.ERROR, Activator.ID,
                    e.getLocalizedMessage().replace("\n\n", " ")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        IStatus repoStatus = repository.getStatus();
        if (repoStatus != null) {
            return repoStatus;
        }
        return Status.OK_STATUS;
    }
    
    
    /**
     * gets the variable out of the {@link TestResultNode} or {@link ITestResultSummaryPO}
     * @param fieldUpdate the FieldUpdate which has all necessary information
     * @param variable the variable which should be parsed. 
     *      <code>node.</code> and <code>summary.</code> are allowed with all public 
     *      getters given. Also <code>node.url</code> and <code>summary.date</code> 
     *      could be used.
     * @return the String representation the resolved variable
     */
    private static String getBeanString(FieldUpdate fieldUpdate,
            VariableToken variable) throws CouldNotResolveException {
        String returnValue = variable.getVariableString();
        if (StringUtils.contains(returnValue, '_')) {
            String[] strings = StringUtils.split(returnValue, "_", 2); //$NON-NLS-1$
            try {
                if (strings[0].equalsIgnoreCase(VARIABLE_NODE_PREFIX)) {
                    if (strings[1].equalsIgnoreCase(VARIABLE_URL_POSTFIX)) {
                        returnValue = fieldUpdate.getDashboardURL();
                    } else if (strings[1].equalsIgnoreCase(
                            VARIABLE_DATE_POSTFIX)) {
                        returnValue = formatDate(fieldUpdate.getNode()
                                .getTimeStamp());
                    } else {
                       
                        returnValue = BeanUtils.getProperty(
                                fieldUpdate.getNode(), strings[1]);
                    }
                }
                if (strings[0].equalsIgnoreCase(VARIABLE_SUMMARY_PREFIX)) {
                    if (strings[1].equalsIgnoreCase(VARIABLE_DATE_POSTFIX)) {
                        returnValue = formatDate(fieldUpdate.getSummary()
                                .getTestsuiteDate());
                    } else {
                        returnValue = BeanUtils.getProperty(
                                fieldUpdate.getSummary(), strings[1]);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException 
                    | NoSuchMethodException e) {
                CommentReporter.getInstance().getConsole()
                    .writeErrorLine(NLS.bind(Messages.UnresolvableVariable,
                            variable.getGuiString()));
                throw new CouldNotResolveException(variable.getGuiString());
            }
        } else {
            returnValue = variable.getGuiString();
            CommentReporter.getInstance().getConsole()
            .writeErrorLine(NLS.bind(Messages.UnresolvableVariable,
                    variable.getGuiString()));
            throw new CouldNotResolveException(variable.getGuiString());
        }
        return returnValue;
    }
    /**
     * 
     * @param date the date do Format
     * @return <code>dd.MM.yyyy</code> representation of a {@link Date}
     */
    private static String formatDate(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy") //$NON-NLS-1$
                .format(date);
    }
}
