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
package org.eclipse.jubula.client.ui.dialogs;

import java.util.Arrays;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Oct 15, 2007
 */
public class ReusedProjectSelectionDialog extends TitleAreaDialog {

    /** horizontal span = 3 */
    private static final int HORIZONTAL_SPAN = 3;    
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;    
    /** number of columns = 4 */
    private static final int NUM_COLUMNS_4 = 4;    
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;    
    /** margin width = 2 */
    private static final int MARGIN_WIDTH = 2;    
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 2;
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;

    /** Names of projects to be displayed */
    private String [] m_projectNames;
    
    /** The widget used to select the project */
    private Combo m_selectionWidget;
    
    /** Selected project name */
    private String m_selectedName;
    
    /** dialog title */
    private String m_title;

    /** initial dialog message */
    private String m_message;

    /** path to dialog title image */
    private String m_titleImage;
    
    /** title of dialog shell */
    private String m_shellTitle;
        
    /**
     * Constructor
     * 
     * @param parentShell The parent of the dialog.
     * @param projectNames The project names to display. 
     * @param title Title of the dialog.
     * @param message Message of the dialog.
     * @param titleImage Title image of the dialog.
     * @param shellTitle Title of the dialog shell.
     */
    public ReusedProjectSelectionDialog(Shell parentShell, 
        String [] projectNames, String title, String message,
        String titleImage, String shellTitle) {
        super(parentShell);

        m_projectNames = projectNames;
        Arrays.sort(m_projectNames);

        m_title = title;
        m_message = message;
        m_titleImage = titleImage;
        m_shellTitle = shellTitle;
    }

    /**
     * 
     * @return the selected name. If "Cancel" was pressed, the return value of
     *         this method is undefined.
     */
    public String getSelectedName() {
        return m_selectedName;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {

        setTitle(m_title);
        setMessage(m_message);
        setTitleImage(Plugin.getImage(m_titleImage));
        getShell().setText(m_shellTitle);

        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        Plugin.createSeparator(parent);
        Composite area = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = NUM_COLUMNS_4;
        area.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;
        area.setLayoutData(gridData);

        new Label(area, SWT.NONE).setText(
            I18n.getString("MoveTestCaseDialog.label")); //$NON-NLS-1$
        
        GridData selectionGridData = new GridData();
        selectionGridData.grabExcessHorizontalSpace = true;
        selectionGridData.horizontalAlignment = GridData.FILL;
        selectionGridData.horizontalSpan = HORIZONTAL_SPAN;

        m_selectionWidget = new Combo(area, SWT.READ_ONLY);
        m_selectionWidget.setLayoutData(selectionGridData);
        for (String name : m_projectNames) {
            m_selectionWidget.add(name);
        }
        m_selectionWidget.select(0);

        Plugin.createSeparator(parent);

        return area;
    }

    /**
     * {@inheritDoc}
     */
    protected void buttonPressed(int buttonId) {
        m_selectedName = m_selectionWidget.getItem(
            m_selectionWidget.getSelectionIndex());
        super.buttonPressed(buttonId);
    }

}
