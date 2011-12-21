/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created Jul 14, 2010
 */
public abstract class AbstractRefactorHandler 
    extends AbstractSelectionBasedHandler {
    /**
     * @param event
     *            the execution event
     * @return the new name for the test case or <code>null</code> to indicate
     *         that this action has been canceled
     */
    protected String getNewTestCaseName(ExecutionEvent event) {
        String newTcName = null;
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof AbstractTestCaseEditor) {
            final AbstractTestCaseEditor editor = (AbstractTestCaseEditor) 
                    activePart;
            if (editor.isDirty()) {
                Dialog editorDirtyDlg = ErrorHandlingUtil
                        .createMessageDialog(MessageIDs.Q_SAVE_AND_EXTRACT);
                if (editorDirtyDlg.getReturnCode() != Window.OK) {
                    return null;
                }
                editor.doSave(new NullProgressMonitor());
            }
            String extractedTCName = getNewName(editor);
            InputDialog dialog = new InputDialog(getActiveShell(),
                    Messages.NewTestCaseActionTCTitle, extractedTCName,
                    Messages.NewTestCaseActionTCMessage,
                    Messages.RenameActionTCLabel, Messages.RenameActionTCError,
                    Messages.NewTestCaseActionDoubleTCName,
                    IconConstants.NEW_TC_DIALOG_STRING,
                    Messages.NewTestCaseActionTCShell, false);
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            Plugin.getHelpSystem().setHelp(dialog.getShell(),
                    ContextHelpIds.DIALOG_TESTCASE_EXTRACT);
            dialog.open();
            int retCode = dialog.getReturnCode();
            dialog.close();
            if (retCode == Window.OK) {
                newTcName = dialog.getName();
            }
        }
        return newTcName;
    }
    
    /**
     * @param editor
     *            the current editor
     * @return the new extracted test case name
     */
    private String getNewName(AbstractTestCaseEditor editor) {
        String newName = InitialValueConstants.DEFAULT_TEST_CASE_NAME;
        final IStructuredSelection cs = (IStructuredSelection) editor
                .getTreeViewer().getSelection();
        if (cs.size() == 1) {
            Object e = cs.getFirstElement();
            if (e instanceof IExecTestCasePO) {
                String execName = ((IExecTestCasePO) e).getName();
                if (!StringUtils.isBlank(execName)) {
                    newName = execName;
                }
            }
        }
        return newName;
    }
    
    /**
     * @param newSpecTc
     *            new created specTestCase (after extraction)
     * @param mapper
     *            mapper to use for resolving of param names in this context
     */
    public static void registerParamNamesToSave(ISpecTestCasePO newSpecTc,
            ParamNameBPDecorator mapper) {
        for (IParamDescriptionPO desc : newSpecTc.getParameterList()) {
            mapper.registerParamDescriptions((ITcParamDescriptionPO) desc);
        }
    }
}
