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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.widgets.TestCaseTreeComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;


/**
 * @author BREDEX GmbH
 * @since 12.10.2004
 */
public class TestCaseTreeDialog extends TitleAreaDialog {
    /** Add constant. */
    public static final int ADD = 9999;
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;    
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;    
    /** margin width = 2 */
    private static final int MARGIN_WIDTH = 2;    
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 2;
      
    /** List of ISelectionListener */
    private List < ISelectionListener > m_selectionListenerList = 
        new ArrayList < ISelectionListener > ();
    
    /** the title */
    private String m_title = Messages.TestCaseTableDialogTitle;
    
    /** the message */
    private String m_message = Messages.TestCaseTableDialogMessage;
    
    /** the shell title */
    private String m_shellTitle = Messages.TestCaseTableDialogShellTitle;

    /** the add button text */
    private String m_addButtonText = Messages.TestCaseTableDialogAdd;
    
    /** the TestCase which should be parent of the shown TestCases */
    private ISpecTestCasePO m_parentTestCase;
    
    /** the style of the tree */
    private int m_treeStyle = SWT.SINGLE;
    /** the add button */
    private Button m_addButton;
    /** the image of the title area */
    private Image m_image = IconConstants.ADD_TC_DIALOG_IMAGE; 
    
    /**
     * <code>testcaseTreeComposite</code>
     */
    private TestCaseTreeComposite m_testcaseTreeComposite;
    
    /**
     * Constructor.
     * @param shell The parent of the dialog.
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     */  
    public TestCaseTreeDialog(Shell shell, ISpecTestCasePO parentTestCase,
            int treeStyle) {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        m_parentTestCase = parentTestCase;
        m_treeStyle = treeStyle;
    }
    
    /**
     * Constructor.
     * @param shell the parent shell.
     * @param title the title
     * @param message the message
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     * @param shellTitle the shell title
     * @param image The title image.
     */
    public TestCaseTreeDialog(Shell shell,
        String title, String message, ISpecTestCasePO parentTestCase, 
        String shellTitle, int treeStyle, Image image) {
        
        this(shell, parentTestCase, treeStyle);
        m_title = title;
        m_message = message;
        m_shellTitle = shellTitle;
        m_image = image;
    }
    
    /**
     * Constructor.
     * @param shell the parent shell.
     * @param title the title
     * @param message the message
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     * @param shellTitle the shell title
     * @param image The title image.
     * @param addButtonText the text for the add / ok button
     */
    public TestCaseTreeDialog(Shell shell, String title, String message,
            ISpecTestCasePO parentTestCase, String shellTitle, int treeStyle,
            Image image, String addButtonText) {
        this(shell, title, message, parentTestCase, shellTitle, treeStyle,
                image);
        m_addButtonText = addButtonText;
    }
    
    
    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(m_title);
        setMessage(m_message);
        getShell().setText(m_shellTitle);
        setTitleImage(m_image); 
        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        
        Plugin.createSeparator(parent);

        m_testcaseTreeComposite = new TestCaseTreeComposite(parent, 
                m_treeStyle, m_parentTestCase);

        Plugin.createSeparator(parent);
        return m_testcaseTreeComposite;
    }
    
    /**
     * {@inheritDoc}
     *      createButtonsForButtonBar(org.eclipse.swt.widgets.Composite) 
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // Add-Button
        m_addButton = createButton(parent, ADD , m_addButtonText , true);
        m_addButton.setEnabled(false);
        m_addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                notifyListener();
                setReturnCode(ADD);
                close();
            }
        });
        m_testcaseTreeComposite.getTreeViewer().addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent e) {
                        if (e.getSelection() != null) {
                            m_addButton.setEnabled(true);
                        }
                    }
                });
        
        m_testcaseTreeComposite.getTreeViewer().addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        m_addButton.setEnabled(m_testcaseTreeComposite
                                .hasValidSelection());
                    }
                });
        m_testcaseTreeComposite.getTreeViewer().addDoubleClickListener(
            new IDoubleClickListener() {
                public void doubleClick(DoubleClickEvent event) {
                    if (!m_addButton.getEnabled()) {
                        return;
                    }
                    notifyListener();
                    setReturnCode(ADD);
                    close();
                }
            });

        // Cancel-Button
        Button cancelButton = createButton(parent, CANCEL,
                Messages.TestCaseTableDialogCancel, false);
        cancelButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(CANCEL);
                close();
            }
        });
    }       
    
    /**
     * Adds the given ISelectionListener to this dialog
     * @param listener the listener to set.
     */
    public void addSelectionListener(ISelectionListener listener) {
        if (!m_selectionListenerList.contains(listener)) {
            m_selectionListenerList.add(listener);
        }
    }
    
    /**
     * Removes the given IselectionListener from this dialog.
     * @param listener the listener to be removed.
     */
    public void removeSelectionListener(ISelectionListener listener) {
        m_selectionListenerList.remove(listener);
    }
    
    /**
     * Notifies the listeners about the selected TestCases when the Add-button
     * is pressed. <br>
     * Note: The IWorkbenchPart-Parameter of the listener is set to null!
     */
    void notifyListener() {
        for (ISelectionListener listener : m_selectionListenerList) {
            listener.selectionChanged(null, m_testcaseTreeComposite
                    .getTreeViewer().getSelection());
        }
    }
}