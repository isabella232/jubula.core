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
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.param;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author BREDEX GmbH
 * @created May 03, 2013
 */
public class SelectOldAndNewParameterNamesPage extends WizardPage
        implements SelectionListener, KeyListener, ISelectionChangedListener {

    /**
     * Map, which contains for each selectable parameter name
     * a corresponding set of execution Test Cases.
     */
    private final ParameterNames m_paramNames;

    /** The old parameter names tree viewer. */
    private TreeViewer m_paramNamesTreeViewer;

    /** The new parameter name combo box. */
    private Combo m_newParamNameCombo;

    /**
     * @param paramNames The data of the parameter names including a map of
     *                   the Test Cases.
     */
    public SelectOldAndNewParameterNamesPage(ParameterNames paramNames) {
        super(Messages.ChangeParameterUsageSelectPageTitle,
                Messages.ChangeParameterUsageSelectPageTitle, null);
        m_paramNames = paramNames;
        setPageComplete(false);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        Group group = new Group(composite, SWT.NONE);
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        group.setText(Messages.ChangeParameterUsageOldNameLabel);
        m_paramNamesTreeViewer = new TreeViewer(group, SWT.NONE);
        m_paramNamesTreeViewer.getTree().setLayoutData(
                new GridData(GridData.FILL_BOTH));
        m_paramNamesTreeViewer.setContentProvider(
                new ParameterNamesContentProvider());
        m_paramNamesTreeViewer.setLabelProvider(
                new ParameterNamesLabelProvider());
        ColumnViewerToolTipSupport.enableFor(m_paramNamesTreeViewer);
        m_paramNamesTreeViewer.setInput(m_paramNames);
        m_paramNamesTreeViewer.addSelectionChangedListener(this);
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.ChangeParameterUsageNewNameLabel);
        m_newParamNameCombo = new Combo(composite, SWT.READ_ONLY);
        m_newParamNameCombo.setItems(
                m_paramNames.getAllColumnNamesOfCTDS());
        m_newParamNameCombo.addSelectionListener(this);
        m_newParamNameCombo.addKeyListener(this);
        setControl(composite);
    }

    /**
     * Called, when the combo box selection has been changed. Calls {@link #checkPageComplete()}.
     * {@inheritDoc}
     */
    public void widgetSelected(SelectionEvent e) {
        checkPageComplete();
    }

    /**
     * Do nothing.
     * {@inheritDoc}
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        // do nothing
    }

    /**
     * Do nothing.
     * {@inheritDoc}
     */
    public void keyPressed(KeyEvent e) {
        // do nothing
    }

    /**
     * Called, when something is typed into the combo box. Calls only {@link #checkPageComplete()}.
     * {@inheritDoc}
     */
    public void keyReleased(KeyEvent e) {
        checkPageComplete();
    }

    /**
     * Checks, if this page has been completed or not and set the complete state:
     * The new parameter name must not be empty.
     */
    private void checkPageComplete() {
        m_paramNames.setNewParamName(m_newParamNameCombo.getText());
        setPageComplete(m_paramNames.isComplete()
                && m_newParamNameCombo.isEnabled());
    }

    /**
     * Called, when the selection of the old parameter name in the tree view changed.
     * {@inheritDoc}
     */
    public void selectionChanged(SelectionChangedEvent event) {
        IParamDescriptionPO paramDesc =
                getSelectedParamDescription();
        if (paramDesc != null) {
            m_newParamNameCombo.setItems(
                    m_paramNames.setOldParamDescription(paramDesc));
            if (m_newParamNameCombo.getItemCount() == 1) {
                m_newParamNameCombo.select(0);
            }
        }
        m_newParamNameCombo.setEnabled(paramDesc != null);
        checkPageComplete();
    }

    /**
     * @return The selected parameter description of the old parameter names tree,
     *         or null if no parameter description is selected.
     */
    private IParamDescriptionPO getSelectedParamDescription() {
        TreeSelection treeSelection = (TreeSelection)
                m_paramNamesTreeViewer.getSelection();
        Object selection = treeSelection.getFirstElement();
        if (selection instanceof IParamDescriptionPO) {
            return (IParamDescriptionPO) selection;
        }
        return null;
    }

}
