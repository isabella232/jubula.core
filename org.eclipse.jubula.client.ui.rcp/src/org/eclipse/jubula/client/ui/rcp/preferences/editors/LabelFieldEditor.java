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
package org.eclipse.jubula.client.ui.rcp.preferences.editors;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A field editor for displaying labels not associated with other widgets. <br>
 * 
 * This editor does not a store a value.
 *
 * @author BREDEX GmbH
 * @created 11.10.2004
 */
public class LabelFieldEditor extends FieldEditor {

    /** the used label */
    private Label m_label;

    /**
     * All labels can use the same preference name since they don't
     * store any preference.
     * @param value the m_text to display
     * @param parent the parent of the field editor's control
     */
    public LabelFieldEditor(String value, Composite parent) {
        super("label", value, parent); //$NON-NLS-1$
    }

    
    /**
     * Adjusts the field editor to be displayed correctly for the given
     * number of columns.
     * 
     * @param numColumns
     *            the number of columns of the grid layout
     */
    protected void adjustForNumColumns(int numColumns) {
        ((GridData) m_label.getLayoutData()).horizontalSpan = numColumns;
    }

    /**
     * Fills the field editor's controls into the given parent.
     * 
     * @param parent
     *            the parent of the field editor's control
     * @param numColumns
     *            the number of columns of the grid layout
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        m_label = getLabelControl(parent);
        
        GridData gridData = new GridData();
        gridData.horizontalSpan = numColumns;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessVerticalSpace = false;
        
        m_label.setLayoutData(gridData);
    }

    /**
     * Returns the number of controls in the field editor.
     * {@inheritDoc}
     */
    public int getNumberOfControls() {
        return 1;
    }

    // Labels do not persist any preferences, so these methods are empty.
    /**
     * Labels do not persist any preferences, so this method is empty
     */
    protected void doLoad() {
        // do nothing
    }
    /**
     * Labels do not persist any preferences, so this method is empty
     */
    protected void doLoadDefault() {
        // do nothing
    }
    /**
     * Labels do not persist any preferences, so this method is empty
     */
    protected void doStore() {
        // do nothing
    }
}
