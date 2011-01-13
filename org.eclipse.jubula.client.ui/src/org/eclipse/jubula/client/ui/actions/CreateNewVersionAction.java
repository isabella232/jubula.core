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
package org.eclipse.jubula.client.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.archive.XmlStorage;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesDecorator;
import org.eclipse.jubula.client.core.businessprocess.INameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.ProjectComponentNameMapper;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.businessprocess.CreateNewVersionBP;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.VersionDialog;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.exception.ConverterException;
import org.eclipse.jubula.tools.exception.GDException;
import org.eclipse.jubula.tools.exception.GDProjectDeletedException;
import org.eclipse.jubula.tools.exception.GDVersionException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.jarutils.IGdVersion;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;


/**
 * @author BREDEX GmbH
 * @created Jun 29, 2007
 */
@SuppressWarnings("synthetic-access")
public class CreateNewVersionAction extends AbstractAction {

    /** standard logging */
    private static Log log = LogFactory.getLog(CreateNewVersionAction.class);
    
    /**
     * Operation for Create New Version action.
     * 
     * @author BREDEX GmbH
     * @created Dec 3, 2007
     */
    private class NewVersionOperation implements IRunnableWithProgress {

        /** the total work for the operation */
        private static final int TOTAL_WORK = 100;

        /** the work for gathering project data from the database*/
        private static final int WORK_GET_PROJECT_FROM_DB = 5;
        
        /** the work for creating the domain objects for the project */
        private static final int WORK_PROJECT_CREATION = 10;
        
        /** the work for saving the project to the database */
        private static final int WORK_PROJECT_SAVE = 
            TOTAL_WORK - WORK_PROJECT_CREATION - WORK_GET_PROJECT_FROM_DB;

        /** The new major version number */
        private Integer m_majorVersionNumber;

        /** The new minor version number */
        private Integer m_minorVersionNumber;

        /**
         * Constructor
         * 
         * @param majorVersionNumber The new major version number
         * @param minorVersionNumber The new minor version number
         */
        public NewVersionOperation(Integer majorVersionNumber, 
            Integer minorVersionNumber) {
            m_majorVersionNumber = majorVersionNumber;
            m_minorVersionNumber = minorVersionNumber;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) throws InterruptedException {
            String pName = GeneralStorage.getInstance().getProject().getName();
            final SubMonitor subMonitor = SubMonitor.convert(monitor,
                    I18n.getString("CreateNewProjectVersionOperation.CreatingNewVersion", //$NON-NLS-1$
                            new Object[]{m_majorVersionNumber,
                                         m_minorVersionNumber,
                                         pName}), 
                    TOTAL_WORK);
            final ParamNameBPDecorator paramNameMapper = 
                new ParamNameBPDecorator(ParamNameBP.getInstance());
            final IWritableComponentNameCache compNameCache = 
                new ComponentNamesDecorator(null);
            try {
                createNewVersion(monitor, subMonitor, paramNameMapper,
                        compNameCache); 
            } catch (final PMReadException e) {
                log.error("Error occurred while creating new project version",  //$NON-NLS-1$
                    e);
            } catch (PMException e) {
                log.error("Error occurred while creating new project version",  //$NON-NLS-1$
                    e);
            } catch (GDProjectDeletedException e) {
                PMExceptionHandler.handleGDProjectDeletedException();
            } catch (GDVersionException e) {
                // should not be occur
                log.error("Toolkit version conflict while creating new project version."); //$NON-NLS-1$
            } catch (ConverterException e) {
                // should not occur
                final String msg = "Exception '" + e + "' should not occur!"; //$NON-NLS-1$//$NON-NLS-2$
                log.error(msg);
                Assert.notReached(msg);  
            } finally {
                NodePM.getInstance().setUseCache(false);
                monitor.done();
                Plugin.stopLongRunning();
            }
        }

        /**
         * Method extraction forced by checkstyle to avoid overly long method.
         * 
         * @param monitor The progress monitor for the entire operation.
         * @param subMonitor The sub-monitor for the operation.
         * @param paramNameMapper The mapper for Parameter Names.
         * @param compNameCache The mapper for Component Names.
         * @throws GDProjectDeletedException 
         * @throws PMException
         * @throws InterruptedException
         * @throws PMReadException
         * @throws GDVersionException
         * @throws ConverterException
         */
        private void createNewVersion(IProgressMonitor monitor,
                final SubMonitor subMonitor,
                final ParamNameBPDecorator paramNameMapper,
                final IWritableComponentNameCache compNameCache)
            throws GDProjectDeletedException, PMException,
                InterruptedException, PMReadException, GDVersionException,
                ConverterException {
            
            NodePM.getInstance().setUseCache(true);
            GeneralStorage.getInstance().validateProjectExists(
                    GeneralStorage.getInstance().getProject());
            String serializedProject = XmlStorage.save(
                GeneralStorage.getInstance().getProject(), null, 
                false, subMonitor.newChild(WORK_GET_PROJECT_FROM_DB));
            if (monitor.isCanceled() || serializedProject == null) {
                throw new InterruptedException();
            }
            final StringBuilder result = new StringBuilder(
                serializedProject.length());
            result.append(serializedProject);
            final String content = new XmlStorage()
                .checkAndReduceXmlHeaderForSaveAs(result);
            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
            final IProjectPO duplicatedProject = XmlStorage.load(
                content, false, m_majorVersionNumber, 
                m_minorVersionNumber, paramNameMapper, compNameCache,
                subMonitor.newChild(WORK_PROJECT_CREATION));
            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
            IWritableComponentNameMapper compNameMapper =
                new ProjectComponentNameMapper(
                        compNameCache, duplicatedProject);
            try {
                duplicatedProject.setClientMetaDataVersion(
                    IGdVersion.GD_CLIENT_METADATA_VERSION);
                attachProjectWithProgress(
                        subMonitor.newChild(WORK_PROJECT_SAVE), 
                        paramNameMapper, compNameMapper, duplicatedProject);
            } catch (PMSaveException e) {
                Plugin.stopLongRunning();
                PMExceptionHandler.handlePMExceptionForMasterSession(
                    new PMSaveException(e.getMessage(), MessageIDs.
                        E_CREATE_NEW_VERSION_FAILED)
                );
            } catch (PMException e) {
                Plugin.stopLongRunning();
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (GDProjectDeletedException e) {
                Plugin.stopLongRunning();
                PMExceptionHandler.handleGDProjectDeletedException();
            }
        }
        
        /**
         * Attaches the given project to the Master Session and database 
         * using the given parameter name mapper. 
         * Reports progress during the operation.
         * 
         * @param monitor The progress monitor for the operation.
         * @param paramNameMapper The parameter name mapper to use when adding the
         *               project to the database.
         * @param compNameMapper The component name mapper to use when adding the
         *               project to the database.
         * @param project The project to add to the database
         * @throws PMException
         *             in case of any db error
         * @throws GDProjectDeletedException if project is already deleted
         * @throws InterruptedException if the operation was canceled.
         */
        private void attachProjectWithProgress(IProgressMonitor monitor,
                final ParamNameBPDecorator paramNameMapper,
                final IWritableComponentNameMapper compNameMapper,
                final IProjectPO project) throws PMException,
                GDProjectDeletedException, InterruptedException {

            // We need to clear the current project data so 
            // we are in a known state if the operation is 
            // canceled.
            IProjectPO clearedProject = 
                GeneralStorage.getInstance().getProject();
            if (clearedProject != null) {
                Utils.clearGuidancer();
                GeneralStorage.getInstance().setProject(null);
                DataEventDispatcher.getInstance()
                    .fireDataChangedListener(clearedProject, DataState.Deleted,
                        UpdateState.all);
            }
            List<INameMapper> mapperList = new ArrayList<INameMapper>();
            List<IWritableComponentNameMapper> compNameCacheList = 
                new ArrayList<IWritableComponentNameMapper>();
            mapperList.add(paramNameMapper);
            compNameCacheList.add(compNameMapper);
            ProjectPM.attachProjectToROSession(project, project.getName(), 
                    mapperList, compNameCacheList, monitor);

            Plugin.stopLongRunning();
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public void runWithEvent(IAction action, Event event) {
        if (action != null && !action.isEnabled()) {
            return;
        }
        Plugin.startLongRunning(I18n.getString("SaveProjectAsAction.waitWhileSaving")); //$NON-NLS-1$
        VersionDialog dialog = openVersionDialog();
        if (dialog != null && dialog.getReturnCode() == Window.OK) {
            final Integer majorVersionNumber = dialog.getMajorVersionNumber();
            final Integer minorVersionNumber = dialog.getMinorVersionNumber();
            IRunnableWithProgress op = createOperation(majorVersionNumber, 
                minorVersionNumber);
            try {
                IProgressService progressService = 
                    PlatformUI.getWorkbench().getProgressService();
                progressService.busyCursorWhile(op);
                fireReady();
                
            } catch (InvocationTargetException e) {
                // Error occurred during operation.
                // Do nothing. The operation has already handled the error.
            } catch (InterruptedException e) {
                // Operation was canceled.
                // We have to clear the GUI because all of 
                // the save work was done in the Master Session, which has been 
                // rolled back.
                Utils.clearGuidancer();
            }
        } else {
            Plugin.stopLongRunning();
        }

    }


    /**
     * call this if the "save as" has ended to update the GUI.
     */
    private void fireReady() {
        DataEventDispatcher dispatcher = 
            DataEventDispatcher.getInstance();
        dispatcher.fireProjectLoadedListener(new NullProgressMonitor());
        IProjectPO project = 
            GeneralStorage.getInstance().getProject();
        if (project != null) { // the load may have failed
            Plugin.setProjectNameInTitlebar(project.getName(), 
                    project.getMajorProjectVersion(),
                    project.getMinorProjectVersion());
        }
        dispatcher.fireProjectOpenedListener();
    }

    /**
     * @param majorVersionNumber major version of new created project
     * @param minorVersionNumber minor version of new created project
     * @return new WorkerThread
     */
    private IRunnableWithProgress createOperation(
        final Integer majorVersionNumber, 
        final Integer minorVersionNumber) {
        
        return new NewVersionOperation(
            majorVersionNumber, minorVersionNumber);
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return CreateNewVersionBP.getInstance();
    }

    /**
     * Opens the dialog to change the project name
     * @return the dialog, or <code>null</code> if an error prevents the dialog 
     *         from opening
     */
    private VersionDialog openVersionDialog() {
        String highestVersionString = "1.0"; //$NON-NLS-1$
        try {
            GeneralStorage.getInstance().validateProjectExists(
                    GeneralStorage.getInstance().getProject());
            highestVersionString = ProjectPM.findHighestVersionNumber(
                GeneralStorage.getInstance().getProject().getGuid());
        } catch (GDProjectDeletedException e) {
            PMExceptionHandler
                .handleGDProjectDeletedException();
            return null;
        } catch (GDException e) {
            Utils.createMessageDialog(e, null, null);
            return null;
        }

        String [] versionNumbers = highestVersionString.split("\\."); //$NON-NLS-1$
        Integer majNum = Integer.parseInt(versionNumbers[0]);
        Integer minNum = Integer.parseInt(versionNumbers[1]);
        VersionDialog dialog = new VersionDialog(
            Plugin.getShell(),
            I18n.getString("CreateNewProjectVersionAction.title"),  //$NON-NLS-1$
            majNum,
            minNum,
            I18n.getString("CreateNewProjectVersionAction.message"), //$NON-NLS-1$
            I18n.getString("CreateNewProjectVersionAction.majorLabel"), //$NON-NLS-1$
            I18n.getString("CreateNewProjectVersionAction.minorLabel"), //$NON-NLS-1$
            I18n.getString("CreateNewProjectVersionAction.invalidVersion"), //$NON-NLS-1$
            I18n.getString("CreateNewProjectVersionAction.doubleVersion"), //$NON-NLS-1$
            IconConstants.BIG_PROJECT_STRING, 
            I18n.getString("CreateNewProjectVersionAction.shellTitle")) { //$NON-NLS-1$ 

            /**
             * {@inheritDoc}
             */
            protected boolean isInputAllowed() {
                return !ProjectPM.doesProjectVersionExist(
                    GeneralStorage.getInstance().getProject().getGuid(),
                    getMajorFieldValue(), 
                    getMinorFieldValue());
            }

            /**
             * {@inheritDoc}
             */
            protected void okPressed() {
                if (ProjectPM.doesProjectVersionExist(
                    GeneralStorage.getInstance().getProject().getGuid(),
                    getMajorFieldValue(), 
                    getMinorFieldValue())) {
                    
                    Utils.createMessageDialog(
                        MessageIDs.E_PROJECTVERSION_ALREADY_EXISTS, 
                        new Object[]{
                            getMajorFieldValue(), 
                            getMinorFieldValue()}, 
                        null);
                    return;
                }
                super.okPressed();
            }
        };
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(), 
            ContextHelpIds.DIALOG_PROJECT_CREATENEWVERSION);
        dialog.open();
        return dialog;
    }

}
