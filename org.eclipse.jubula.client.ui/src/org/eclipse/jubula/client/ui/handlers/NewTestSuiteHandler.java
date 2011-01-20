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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.persistence.NodePM.AbstractCmdHandleChild;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.GuiNodeBP;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.i18n.I18n;


/**
 * @author BREDEX GmbH
 * @created 03.07.2009
 */
public class NewTestSuiteHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent arg0) {
        newTestSuite();
        return null;
    }

    /**
     * Creates a new TestSuite.
     */
    public void newTestSuite() {
        InputDialog dialog = newTestSuitePopUp();
        if (dialog.getReturnCode() == Window.CANCEL) {
            return;
        }
        IProjectPO project = GeneralStorage.getInstance().getProject();
        try {
            ITestSuitePO testSuite = NodeMaker.createTestSuitePO(dialog
                    .getName());
            setDefaultValuesToTestSuite(testSuite, project);
            AbstractCmdHandleChild cmd = NodePM.getCmdHandleChild(project,
                    testSuite);
            NodePM.addAndPersistChildNode(project, testSuite, null, cmd);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    testSuite, DataState.Added, UpdateState.all);
            GuiNode node = GuiNodeBP.getGuiNodeForNodePO(testSuite);
            InteractionEventDispatcher.getDefault().
                fireProgammableSelectionEvent(
                        new StructuredSelection(node));
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        }
    }

    /**
     * Sets a default AUT and a default AUTConfig to the new TestSuite (when
     * only one AUT / AUTConfig is defined in the Project.
     * 
     * @param testSuite
     *            the new test suite.
     * @param project
     *            the parent of this test suite.
     */
    private void setDefaultValuesToTestSuite(ITestSuitePO testSuite,
            IProjectPO project) {

        // set default AUTMainPO to testSuite
        int autListSize = project.getAutMainList().size();
        if (autListSize == 0 || autListSize > 1) {
            return;
        }
        IAUTMainPO aut = (IAUTMainPO)(project.getAutMainList().toArray())[0];
        testSuite.setAut(aut);

        // set default AUTConfigPO to testSuite
        int autConfigListLength = aut.getAutConfigSet().size();
        if (autConfigListLength == 0 || autConfigListLength > 1) {
            return;
        }
    }

    /**
     * Opens the dialog for creating a new TestSuite.
     * 
     * @return the dialog.
     */
    private InputDialog newTestSuitePopUp() {
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        int testSuiteCount = project.getTestSuiteCont().getTestSuiteList()
                .size();
        String str = StringConstants.EMPTY;
        if (testSuiteCount > 0) {
            str = str + testSuiteCount;
        }
        str = I18n.getString("InputDialog.newTS") + str; //$NON-NLS-1$
        InputDialog dialog = new InputDialog(Plugin.getShell(), I18n
                .getString("NewTestSuiteAction.TSTitle"), //$NON-NLS-1$
                str, I18n.getString("NewTestSuiteAction.TSMessage"), //$NON-NLS-1$
                I18n.getString("NewTestSuiteAction.TSLabel"), //$NON-NLS-1$
                I18n.getString("NewTestSuiteAction.TSError"), //$NON-NLS-1$
                I18n.getString("NewTestSuiteAction.doubleTSName"), //$NON-NLS-1$
                IconConstants.NEW_TS_DIALOG_STRING, I18n
                        .getString("NewTestSuiteAction.TSShell"), //$NON-NLS-1$
                false) {
            protected boolean isInputAllowed() {
                String newName = getInputFieldText();
                if (ProjectPM.doesTestSuiteExists(project.getId(), newName)) {
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
                ContextHelpIds.DIALOG_TS_NEW);
        dialog.open();
        return dialog;
    }
}
