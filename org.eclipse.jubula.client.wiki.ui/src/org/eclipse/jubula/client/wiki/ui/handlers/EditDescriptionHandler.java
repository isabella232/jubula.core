/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.wiki.ui.handlers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.wiki.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.wiki.ui.dialogs.DescriptionEditDialog;
import org.eclipse.jubula.client.wiki.ui.utils.DescriptionUtil;
import org.eclipse.jubula.client.wiki.ui.views.DescriptionView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
/**
 * 
 * @author BREDEX GmbH
 */
public class EditDescriptionHandler extends AbstractSelectionBasedHandler {
    
    /** key for the preference store*/
    private static final String OPEN_VIEW_PREF_KEY = "AUTOMATIC_DESCRIPTIONVIEW"; //$NON-NLS-1$

    /** {@inheritDoc} */
    protected Object executeImpl(ExecutionEvent event) {
        final AbstractJBEditor editor = (AbstractJBEditor) HandlerUtil
                .getActiveEditor(event);
        if (editor != null) {
            editor.getEditorHelper().doEditorOperation(new IEditorOperation() {
                public void run(IPersistentObject workingPo) {
                    IStructuredSelection selection = (IStructuredSelection)
                            editor.getSelection();
                    INodePO workNode = (INodePO) selection.getFirstElement();
                    DescriptionEditDialog dialog =
                            new DescriptionEditDialog(getActiveShell(),
                                    workNode);
                    dialog.setHelpAvailable(true);
                    dialog.create();
                    DialogUtils.setWidgetNameForModalDialog(dialog);
                    Plugin.getHelpSystem().setHelp(dialog.getShell(),
                            ContextHelpIds.EDIT_DESCRIPTION);
                    
                    if (dialog.open() == Window.OK) {
                        performChanges(editor, workNode, dialog);
                    }
                    checkAndAskForDescriptionView();
                }


            });
        }
        return null;
    }

    /** checks if the description view is open and ask if the user wants to pen it
     * it uses the preferenceStore to save it, if the user wants to remember it */
    private void checkAndAskForDescriptionView() {
        IWorkbenchPage workbench = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
        final IPreferenceStore preferenceStore = Plugin.getDefault()
                .getPreferenceStore();
        int value = preferenceStore.getInt(OPEN_VIEW_PREF_KEY);
        
        if (workbench.findView(DescriptionView.VIEW_ID) == null) {
            if (value != Constants.OPEN_DESCRIPTION_VIEW_NO
                    && value != Constants.OPEN_DESCRIPTION_VIEW_YES) {
                MessageDialogWithToggle dialog =
                        createQuestionDialog(preferenceStore);
                if (dialog.getReturnCode() == IDialogConstants.YES_ID) {
                    openDescriptionView();
                }
            } else if (value == Constants.OPEN_DESCRIPTION_VIEW_YES) {
                openDescriptionView();
            }
        }
    }

    /**
     * creates the question dialog if you want to open the {@link DescriptionView}
     * @param preferenceStore the preference store in which you want to store 
     *        the settings if it should be remembered
     * @return the message dialog
     */
    private MessageDialogWithToggle createQuestionDialog(
            final IPreferenceStore preferenceStore) {
        MessageDialogWithToggle dialog = new MessageDialogWithToggle(
                getActiveShell(),
                Messages.OpenDescriptionViewTitle, null,
                Messages.OpenDescriptionViewQuestion,
                MessageDialog.QUESTION,
                new String[] {
                    IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL },
                0,
                org.eclipse.jubula.client.ui.rcp.i18n.Messages.UtilsRemember,
                false) {
            /**
             * {@inheritDoc}
             */
            protected void buttonPressed(int buttonId) {
                super.buttonPressed(buttonId);

                int val = Constants.OPEN_DESCRIPTION_VIEW_PROMPT;
                if (getToggleState() 
                        && getReturnCode() == IDialogConstants.NO_ID) {
                    val = Constants.OPEN_DESCRIPTION_VIEW_NO;
                } else if (getToggleState()
                        && getReturnCode() == IDialogConstants.YES_ID) {
                    val = Constants.OPEN_DESCRIPTION_VIEW_YES;
                }
                preferenceStore.setValue(OPEN_VIEW_PREF_KEY, val);
            }
        };
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog;
    }

    /**
     * try's to open the {@link DescriptionView}
     */
    private void openDescriptionView() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().showView(DescriptionView.VIEW_ID);
        } catch (PartInitException e) {
            // silently ignore
        }
    }
    /**
    * Performs the changes done in the {@link DescriptionEditDialog}
    * @param editor an editor like {@link TestCaseEditor} or {@link TestSuiteEditor}
    * @param workNode the {@link INodePO} which should be worked on
    * @param dialog the {@link DescriptionEditDialog}
    */
    private void performChanges(final AbstractJBEditor editor,
            INodePO workNode, DescriptionEditDialog dialog) {
        if (StringUtils.isBlank(workNode.getDescription())
                && StringUtils.isBlank(dialog.getDescription())) {
            return;
        }
        if (StringUtils.equals(workNode.getDescription(),
                dialog.getDescription())
                || StringUtils.equals(dialog.getDescription(), 
                        DescriptionUtil.getReferenceDescription(workNode))) {
            return;
        }
        workNode.setDescription(dialog.getDescription());
        editor.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance().dataModified(workNode);
    }
}
