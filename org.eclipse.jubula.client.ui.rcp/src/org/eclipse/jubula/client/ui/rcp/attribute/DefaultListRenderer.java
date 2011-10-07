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
package org.eclipse.jubula.client.ui.rcp.attribute;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.model.IDocAttributeDescriptionPO;
import org.eclipse.jubula.client.core.model.IDocAttributePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.factory.AttributeRendererFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;



/**
 * Renders a list of elements, which can be added and edited via a popup dialog.
 *
 * @author BREDEX GmbH
 * @created 16.05.2008
 */
public class DefaultListRenderer extends AbstractAttributeRenderer {

    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;   
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2; 
    /** the quantity of lines in a m_text field */
    private static final int LINES = 15;
    
    /** the add button */
    private Button m_addButton = null;
    
    /** the delete button */
    private Button m_removeButton = null;
    
    /** the edit button */
    private Button m_editButton = null;
    
    /** the list of text representing string entries */
    private List m_textList = null;
    
    /** the list of string entry attributes */
    private java.util.List<IDocAttributePO> m_attrList = 
        new ArrayList<IDocAttributePO>();
    
    /** The type for subattributes */
    private IDocAttributeDescriptionPO m_desc;
        
    /** a new selection listener */
    private WidgetSelectionListener m_selectionListener = 
        new WidgetSelectionListener();

    /**
     * {@inheritDoc}
     */
    public void renderAttribute(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        createCompositeLayout(composite, compositeLayout, compositeData);
        composite.setLayoutData(compositeData);
        createCompositeLayout(composite, compositeLayout, compositeData);
        createAUTList(composite);
        createButtons(composite);
        initFields();
        addListeners();
        Plugin.getHelpSystem().setHelp(composite,
            ContextHelpIds.AUT_PROPERTY_PAGE);
    }

    /**
     * Inits all swt field in this page.
     */
    private void initFields() {
        m_textList.removeAll();
        m_attrList.clear();
        // We know that there is only 1 attribute type
        for (IDocAttributeDescriptionPO docAttrDesc 
                : getAttribute().getDocAttributeTypes()) {
            m_desc = docAttrDesc;
            for (IDocAttributePO attr 
                    : getAttribute().getDocAttributeList(docAttrDesc)
                    .getAttributes()) {
                m_attrList.add(attr);
                m_textList.add(StringUtils.defaultString(attr.getValue()));
            }
        }
    }

    /** Adds all listeners. */
    private void addListeners() {
        m_addButton.addSelectionListener(m_selectionListener);
        m_editButton.addSelectionListener(m_selectionListener);
        m_removeButton.addSelectionListener(m_selectionListener);
        m_textList.addSelectionListener(m_selectionListener);
    }

    /**
     * @param composite the composite
     * @param compositeLayout comp. layout
     * @param compositeData comp. data
     */
    private void createCompositeLayout(Composite composite, 
        GridLayout compositeLayout, GridData compositeData) {
        compositeData.grabExcessHorizontalSpace = false;
        compositeLayout.horizontalSpacing = LayoutUtil.SMALL_HORIZONTAL_SPACING;
        compositeLayout.verticalSpacing = LayoutUtil.SMALL_VERTICAL_SPACING;
        compositeLayout.numColumns = NUM_COLUMNS_2;
        compositeLayout.marginHeight = LayoutUtil.SMALL_MARGIN_HEIGHT;
        compositeLayout.marginWidth = LayoutUtil.SMALL_MARGIN_WIDTH;
        composite.setLayout(compositeLayout);
    }
    
    /** Handels the add-button event. */
    void handleAddButtonEvent() {
        IDocAttributePO attr = PoMaker.createDocAttribute(m_desc);
        IAttributeRenderer renderer = 
            AttributeRendererFactory.getRenderer(m_desc);
        renderer.init(m_desc, attr);

        if (openAttributeDialog(renderer) == Window.OK) {
            getAttribute().getDocAttributeList(m_desc).addAttribute(attr);
            initFields();
        }
    }

    /** Handels the edit-button event. */
    void handleEditButtonEvent() {
        int idx = m_textList.getSelectionIndex();
        if (idx == -1) {
            return;
        }
        
        IDocAttributePO attr = m_attrList.get(idx);
        IAttributeRenderer renderer = 
            AttributeRendererFactory.getRenderer(m_desc);
        renderer.init(m_desc, attr);

        // FIXME zeb changes are still made even if the user presses "Cancel"
        //           the user just doesn't know about it because there is 
        //           no refresh
        if (openAttributeDialog(renderer) == Window.OK) {
            initFields();
        }
    }

    /**
     * Opens a dialog to display and edit an attribute using the given
     * renderer.
     * 
     * @param renderer The renderer that will be presented in the dialog.
     * @return the dialog's return code.
     */
    private int openAttributeDialog(IAttributeRenderer renderer) {
        Dialog dialog = new AttributeDialog(m_textList.getShell(), renderer);
        dialog.create();
        dialog.getShell().setText(I18n.getString(m_desc.getLabelKey()));
        return dialog.open();
    }
    
    /** Handels the remove-button event. */
    void handleRemoveButtonEvent() {
        int idx = m_textList.getSelectionIndex();
        if (idx != -1) {
            IDocAttributePO toRemove = m_attrList.get(idx);
            m_textList.remove(idx);
            m_attrList.remove(idx);
            getAttribute().getDocAttributeList(m_desc).removeAttribute(
                    toRemove);
        }
    }

    /** Handels the list event. */
    void handleListEvent() {
        if (m_textList.getItemCount() == 0) {
            m_editButton.setEnabled(false);
            m_removeButton.setEnabled(false);
            return;
        }
        if (m_textList.getSelectionCount() > 0) {
            String[] selection = m_textList.getSelection();
            if (!StringConstants.EMPTY.equals(selection[0])) { 
                m_editButton.setEnabled(true);
                m_removeButton.setEnabled(true);
            }
        }
    }

    /**
     * Creates a m_text field with the AUTs of a project.
     * @param parent The parent composite.
     */
    private void createAUTList(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_1;
        composite.setLayout(compositeLayout);
        GridData data = new GridData ();
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;
        composite.setLayoutData(data);
        newLabel(composite, I18n.getString(getDescription().getLabelKey()));
        m_textList = new List(composite, 
            LayoutUtil.MULTI_TEXT_STYLE | SWT.SINGLE);
        GridData textGridData = new GridData();
        textGridData.horizontalAlignment = GridData.FILL;
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.heightHint = Dialog.convertHeightInCharsToPixels(
            LayoutUtil.getFontMetrics(m_textList), LINES);
        LayoutUtil.addToolTipAndMaxWidth(textGridData, m_textList);
        m_textList.setLayoutData(textGridData);       
    }

    /**
     * Creates a label for this page.
     * 
     * @param text The label text to set.
     * @param parent The composite.
     * @return a new label
     */
    private Label newLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER,
            false, false, 1, 1);
        label.setLayoutData(labelGrid);
        return label;
    }

    /**
     * Creates three buttons.
     * @param parent The parent composite.
     */
    private void createButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_1;
        composite.setLayout(compositeLayout);
        GridData data = new GridData();
        data.verticalAlignment = GridData.BEGINNING;
        data.horizontalAlignment = GridData.END;
        composite.setLayoutData(data);
        new Label(composite, SWT.NONE);
        m_addButton = new Button(composite, SWT.PUSH);
        m_addButton.setText(Messages.AUTPropertyPageAdd);
        m_addButton.setLayoutData(buttonGrid());
        
        m_editButton = new Button(composite, SWT.PUSH);
        m_editButton.setText(Messages.AUTPropertyPageEdit);
        m_editButton.setLayoutData(buttonGrid());
        m_editButton.setEnabled(false);
        
        m_removeButton = new Button(composite, SWT.PUSH);
        m_removeButton.setText(Messages.AUTPropertyPageRemove);
        m_removeButton.setLayoutData(buttonGrid());
        m_removeButton.setEnabled(false);
    }

    /**
     * Creates new gridData for the buttons.
     * 
     * @return The new GridData.
     */
    private GridData buttonGrid() {
        GridData buttonData = new GridData();
        buttonData.horizontalAlignment = GridData.FILL;
        return buttonData;

    }

    /**
     * A dialog that displays an attribute.
     * @author BREDEX GmbH
     * @created 22.05.2008
     */
    private class AttributeDialog extends Dialog {

        /** the renderer to which this dialog delegates */
        private IAttributeRenderer m_renderer;
        
        /**
         * @param parentShell The parent shell of the dialog.
         * @param renderer The renderer to which this dialog delegates.
         */
        protected AttributeDialog(Shell parentShell, 
                IAttributeRenderer renderer) {
            
            super(parentShell);
            m_renderer = renderer;
        }

        /**
         * 
         * {@inheritDoc}
         */
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite)super.createDialogArea(parent);
            m_renderer.renderAttribute(composite);
            return composite;
        }
        
    }
    
    /**
     * This inner class creates a new SelectionListener.
     * @author BREDEX GmbH
     * @created 11.02.2005
     */
    private class WidgetSelectionListener 
        implements SelectionListener {
        /**
         * @param e The selection event.
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o == m_addButton) {
                handleAddButtonEvent();
                return;
            } else if (o == m_editButton) {
                handleEditButtonEvent();
                return;
            } else if (o == m_removeButton) {
                handleRemoveButtonEvent();
                return;
            } else if (o == m_textList) {
                handleListEvent();
                return;
            }
            Assert.notReached(Messages.EventWasCreatedByAnUnknownWidget);
        }

        /**
         * Reacts on double clicks. 
         * @param e The selection event. */
        public void widgetDefaultSelected(SelectionEvent e) { 
            Object o = e.getSource();
            if (o == m_textList) {
                handleEditButtonEvent();
                return;
            }
            Assert.notReached(
                    Messages.DoubleClickEventWasCreatedByAnUnknownWidget);
        }
    }
}
