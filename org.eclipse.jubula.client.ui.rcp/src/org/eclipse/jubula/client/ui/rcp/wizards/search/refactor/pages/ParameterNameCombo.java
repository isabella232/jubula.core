/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;

import java.util.List;

import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Data class for storing the old and new specification Test Cases.
 *
 * @author BREDEX GmbH
 */
public class ParameterNameCombo implements SelectionListener {

    /** The combo box for choosing the parameter name */
    private Combo m_combo;

    /** The warning decoration for this combo box. */
    private ControlDecorator m_warningDecoration;

    /**
     * A new combo box with the given items added to the given parent.
     * If the given items only contains one element, this element is selected
     * automatically. In each case an empty item is added at the
     * beginning of the list.
     * @param parent The parent composite.
     * @param items An list of items.
     * @param oldSpecName The name of the old specification Test Case.
     */
    public ParameterNameCombo(Composite parent, List<String> items,
            String oldSpecName) {
        m_combo = new Combo(parent, SWT.READ_ONLY);
        m_warningDecoration = ControlDecorator.addWarningDecorator(
            m_combo, NLS.bind(Messages
                .ReplaceTCRWizard_matchParameterNames_warningUnmatchedParameters
                , oldSpecName)
        );
        m_combo.addSelectionListener(this);
        items.add(0, ""); //$NON-NLS-1$
        m_combo.setItems(items.toArray(new String[] {}));
        if (items.size() == 2) {
            m_combo.select(1);
        }
        widgetSelected(null);
    }

    /**
     * Show warning decoration next to combo box, if the empty element is selected.
     * {@inheritDoc}
     */
    public void widgetSelected(SelectionEvent e) {
        m_warningDecoration.setVisible(m_combo.getSelectionIndex() == 0);
    }

    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        // do nothing
    }

}
