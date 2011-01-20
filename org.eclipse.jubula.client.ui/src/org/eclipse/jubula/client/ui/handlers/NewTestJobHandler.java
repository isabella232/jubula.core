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
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.persistence.NodePM.AbstractCmdHandleChild;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.i18n.I18n;


/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 */
public class NewTestJobHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        newTestJob();
        return null;
    }

    /**
     * create a new test job
     */
    private void newTestJob() {
        InputDialog dialog = newTestJobPopUp();
        if (dialog.getReturnCode() == Window.CANCEL) {
            return;
        }
        IProjectPO project = GeneralStorage.getInstance().getProject();
        try {
            ITestJobPO testJob = NodeMaker.createTestJobPO(dialog.getName());
            AbstractCmdHandleChild cmd = NodePM.getCmdHandleChild(project,
                    testJob);
            NodePM.addAndPersistChildNode(project, testJob, null, cmd);
            DataEventDispatcher.getInstance().fireDataChangedListener(testJob,
                    DataState.Added, UpdateState.all);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        }
    }

    /**
     * @return a test job popup dialog
     */
    private InputDialog newTestJobPopUp() {
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        int testJobCount = project.getTestJobCont().getTestJobList().size();
        String str = StringConstants.EMPTY;
        if (testJobCount > 0) {
            str = str + testJobCount;
        }
        str = I18n.getString("InputDialog.newTJ") + str; //$NON-NLS-1$
        InputDialog dialog = new InputDialog(Plugin.getShell(), I18n
                .getString("NewTestJob.TJTitle"), //$NON-NLS-1$
                str, I18n.getString("NewTestJob.TJMessage"), //$NON-NLS-1$
                I18n.getString("NewTestJob.TJLabel"), //$NON-NLS-1$
                I18n.getString("NewTestJob.TJError"), //$NON-NLS-1$
                I18n.getString("NewTestJob.doubleTJName"), //$NON-NLS-1$
                IconConstants.NEW_TJ_DIALOG_STRING, I18n
                        .getString("NewTestJob.TJShell"), //$NON-NLS-1$
                false) {
            protected boolean isInputAllowed() {
                String newName = getInputFieldText();
                if (ProjectPM.doesTestJobExists(project.getId(), newName)) {
                    return false;
                }
                return true;
            }
        };
        // set help link
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        // set help id
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
                ContextHelpIds.DIALOG_TJ_NEW);
        dialog.open();
        return dialog;
    }

}
