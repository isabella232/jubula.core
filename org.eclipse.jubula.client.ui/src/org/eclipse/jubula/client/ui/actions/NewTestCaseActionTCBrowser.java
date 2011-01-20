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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.GuiNodeBP;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.views.TestCaseBrowser;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.i18n.I18n;


/**
 * @author BREDEX GmbH
 * @created 27.06.2006
 */
public class NewTestCaseActionTCBrowser extends AbstractNewTestCaseAction {

    /**
     * {@inheritDoc}
     */
    public void run() {
        InputDialog dialog = new InputDialog(Plugin.getShell(), 
                I18n.getString("NewTestCaseAction.TCTitle"), //$NON-NLS-1$
                I18n.getString("NewTestCaseAction.newTestCase"), //$NON-NLS-1$
                I18n.getString("NewTestCaseAction.TCMessage"),   //$NON-NLS-1$
                I18n.getString("NewTestCaseAction.TCLabel"),  //$NON-NLS-1$
                I18n.getString("RenameAction.TCError"), //$NON-NLS-1$
                I18n.getString("NewTestCaseAction.doubleTCName"), //$NON-NLS-1$
                    IconConstants.NEW_TC_DIALOG_STRING,
                I18n.getString("NewTestCaseAction.TCShell"), false); //$NON-NLS-1$
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        //setup help id and link
        Plugin.getHelpSystem().setHelp(dialog.getShell(), 
            ContextHelpIds.DIALOG_NEW_TESTCASE);
        dialog.open();
        if (Window.OK == dialog.getReturnCode()) {
            String tcName = dialog.getName();
            TestCaseBrowser tstv = (TestCaseBrowser)
                Plugin.getView(Constants.TC_BROWSER_ID);
            INodePO parent = null;
            if (tstv.getTreeViewer().getSelection() 
                    instanceof IStructuredSelection) {
                
                IStructuredSelection sel = 
                    (IStructuredSelection)tstv.getTreeViewer().getSelection();
                parent = ((GuiNode)sel.getFirstElement()).getContent();
                parent = TestCaseBP.getSpecTestCaseContainer(parent);
            } else {
                parent = GeneralStorage.getInstance().getProject();
            }
            try {
                ISpecTestCasePO newSpecTC = TestCaseBP
                    .createNewSpecTestCase(tcName, parent, null);
                DataEventDispatcher.getInstance().fireDataChangedListener(
                    newSpecTC, DataState.Added, UpdateState.all);
                GuiNode node = GuiNodeBP.getGuiNodeForNodePO(newSpecTC);
                InteractionEventDispatcher.getDefault().
                    fireProgammableSelectionEvent(
                            new StructuredSelection(node));
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleGDProjectDeletedException();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Integer getPositionToInsert(ISpecTestCasePO workTC, 
            GuiNode selectedNodeGUI) {
        
        return null;
    }
}