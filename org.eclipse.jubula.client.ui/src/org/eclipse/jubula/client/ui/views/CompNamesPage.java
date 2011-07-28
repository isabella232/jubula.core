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
package org.eclipse.jubula.client.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.editors.IJBEditor;
import org.eclipse.jubula.client.ui.widgets.ComponentNamesTableComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;


/**
 * @author BREDEX GmbH
 * @created Sep 10, 2008
 */
public class CompNamesPage extends Page implements ISelectionListener {
    /**
     * The currently selected test execution node, may be <code>null</code>,
     * if no test execution node ist selected.
     */
    private IExecTestCasePO m_oldSelectedExecNode;
    
    /**
     * The owner of the currently selected test execution node, may be
     * <code>null</code>, if no test execution node ist selected or if the
     * part has been closed.
     */
    private IWorkbenchPart m_oldSelectedExecNodeOwner;
    
    /** the primary control for this page */
    private ComponentNamesTableComposite m_control;
    
    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        getControl().setFocus();
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleEditorSaved(IWorkbenchPart part, ISelection selection) {
        m_oldSelectedExecNode = null;
        selectionChanged(part, selection);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        m_control = new ComponentNamesTableComposite(parent, SWT.NONE);
    }

    /**
     * {@inheritDoc}
     */
    public Control getControl() {
        return m_control;
    }

    /**
     * {@inheritDoc}
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!(selection instanceof StructuredSelection)) { 
            // e.g. in Jubula plugin-version you can open an java editor, 
            // that reacts on org.eclipse.jface.text.TextSelection, which
            // is not a StructuredSelection
            return;
        }
        IStructuredSelection sel = (IStructuredSelection)selection;
        IExecTestCasePO selectedExecNode = null;
        IWorkbenchPart selectedExecNodeOwner = null;
        if (sel.getFirstElement() instanceof IExecTestCasePO) {
            selectedExecNode = (IExecTestCasePO)sel.getFirstElement();
            selectedExecNodeOwner = part;
        }
        if ((selectedExecNode == null) || (selectedExecNode != null 
                && !(selectedExecNode.equals(m_oldSelectedExecNode)))
                || (!(selectedExecNodeOwner.equals(
                        m_oldSelectedExecNodeOwner)))) {
            
            if (part instanceof IJBEditor) {
                m_control.getCellEdit().setComponentNameMapper(
                    ((IJBEditor)part).getEditorHelper().getEditSupport()
                        .getCompMapper());
            }
            m_control.setSelectedExecNodeOwner(selectedExecNodeOwner);
            m_control.setSelectedExecNode(selectedExecNode);
            if (part instanceof AbstractTestCaseEditor) {
                AbstractTestCaseEditor editor = (AbstractTestCaseEditor)part;
                if (editor.getEditorInput() instanceof ITestSuitePO) {
                    m_control.disablePropagation();
                }
            }
        }
        m_oldSelectedExecNode = selectedExecNode;
        m_oldSelectedExecNodeOwner = selectedExecNodeOwner;
    }
}
