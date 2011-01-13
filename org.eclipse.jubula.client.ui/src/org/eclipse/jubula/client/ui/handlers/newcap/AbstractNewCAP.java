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
package org.eclipse.jubula.client.ui.handlers.newcap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.CapBP;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.NewCAPDialog;
import org.eclipse.jubula.client.ui.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.editors.GDEditorHelper;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author BREDEX GmbH
 * @created 18.02.2009
 */
public abstract class AbstractNewCAP extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = Plugin.getActivePart();
        if (activePart instanceof AbstractTestCaseEditor) {
            AbstractTestCaseEditor tse = (AbstractTestCaseEditor)activePart;
            if (tse.getEditorHelper().requestEditableState() 
                    == GDEditorHelper.EditableState.OK) {
                ISpecTestCasePO workTC = (ISpecTestCasePO)tse.getEditorHelper()
                        .getEditSupport().getWorkVersion();
                if (!(tse.getTreeViewer().getSelection() 
                        instanceof IStructuredSelection)) {
                    return null;
                }
                IStructuredSelection selection = (IStructuredSelection)tse
                        .getTreeViewer().getSelection();
                GuiNode selectedNodeGUI = (GuiNode)selection.getFirstElement();
                if (selectedNodeGUI != null) { // using the CTRL modifier, you
                    // may get a click without a selection
                    SpecTestCaseGUI specTcGUI = null;
                    int posistionToAdd = getPositionToInsert(workTC,
                            selectedNodeGUI);
                    while (!(selectedNodeGUI instanceof SpecTestCaseGUI)) {
                        selectedNodeGUI = selectedNodeGUI.getParentNode();
                    }
                    specTcGUI = (SpecTestCaseGUI)selectedNodeGUI;
                    addCap(specTcGUI, workTC, posistionToAdd, tse);
                }
            }
        }
        return null;
    }
    
    /**
     * @param selectedNodeGUI
     *            the currently selected guiNode
     * @param workTC
     *            the workversion of the current specTC
     * @return the position to add
     */
    protected abstract Integer getPositionToInsert(ISpecTestCasePO workTC,
            GuiNode selectedNodeGUI);
    
    /**
     * Adds a new CAP to the given workVersion SpecTestCase at the given
     * position and, if successful, sets the given editor dirty.
     * 
     * @param specTcGUI
     *            the GUI SpecTestCase for the NewCapDialog
     * @param workTC
     *            the workversion of the SpecTestCase
     * @param position
     *            the position to add
     * @param tse
     *            the editor.
     */
    private void addCap(SpecTestCaseGUI specTcGUI, ISpecTestCasePO workTC, 
        Integer position, AbstractTestCaseEditor tse) {

        final NewCAPDialog dialog = 
            new NewCAPDialog(
                Plugin.getShell(), 
                specTcGUI, 
                tse.getEditorHelper().getEditSupport().getCompMapper());
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        if (dialog.getReturnCode() != Window.OK) {
            return;
        }
        final String componentType = dialog.getComponentType();
        final String capName = dialog.getCapName();
        final String action = dialog.getActionName();
        final String componentName = dialog.getComponentName(); 
        final ICapPO cap = CapBP.createCapWithDefaultParams(
            capName, componentName, componentType, action);
        final IWritableComponentNameMapper compMapper = 
            tse.getEditorHelper().getEditSupport().getCompMapper(); //###BLA
        try {
            // Set the Component Name to null so that the Component Name 
            // mapper doesn't remove the instance of reuse 
            // for <code>componentName</code>.
            cap.setComponentName(null);
            ComponentNamesBP.getInstance().setCompName(cap, componentName, 
                    CompNameCreationContext.STEP, compMapper);
            workTC.addNode(position, cap);
            tse.getEditorHelper().setDirty(true);
            DataEventDispatcher.getInstance().fireDataChangedListener(cap, 
                    DataState.Added, UpdateState.onlyInEditor);
        } catch (IncompatibleTypeException e) {
            // Shouldn't happen, but just in case it does, show an error dialog.
            Utils.createMessageDialog(e, e.getErrorMessageParams(), null);
        } catch (PMException pme) {
            PMExceptionHandler.handlePMExceptionForEditor(pme, tse);
        }
    }
}
