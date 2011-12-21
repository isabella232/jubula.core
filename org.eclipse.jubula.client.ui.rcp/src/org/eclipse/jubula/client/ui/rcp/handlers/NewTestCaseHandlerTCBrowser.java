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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;

/**
 * @author BREDEX GmbH
 * @created 27.06.2006
 */
public class NewTestCaseHandlerTCBrowser extends AbstractNewHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        INodePO parent = getParentNode(event);
        InputDialog dialog = new InputDialog(getActiveShell(),
                Messages.NewTestCaseActionTCTitle,
                InitialValueConstants.DEFAULT_TEST_CASE_NAME,
                Messages.NewTestCaseActionTCMessage,
                Messages.NewTestCaseActionTCLabel,
                Messages.RenameActionTCError,
                Messages.NewTestCaseActionDoubleTCName,
                IconConstants.NEW_TC_DIALOG_STRING,
                Messages.NewTestCaseActionTCShell, false);
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        // setup help id and link
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
                ContextHelpIds.DIALOG_NEW_TESTCASE);
        dialog.open();
        if (Window.OK == dialog.getReturnCode()) {
            String tcName = dialog.getName();
            try {
                ISpecTestCasePO newSpecTC = TestCaseBP.createNewSpecTestCase(
                        tcName, parent, null);
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        newSpecTC, DataState.Added, UpdateState.all);
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleGDProjectDeletedException();
            }
        }
        return null;
    }

}