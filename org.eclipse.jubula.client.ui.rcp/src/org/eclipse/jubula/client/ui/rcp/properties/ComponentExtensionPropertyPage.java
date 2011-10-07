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
package org.eclipse.jubula.client.ui.rcp.properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project Property Page for managing Simple Component Extensions.
 *
 * @author BREDEX GmbH
 * @created Apr 30, 2009
 */
public class ComponentExtensionPropertyPage 
        extends AbstractProjectPropertyPage {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ComponentExtensionPropertyPage.class);
    
    /**
     * Constructor
     * 
     * @param es The edit support for the property page.
     */
    public ComponentExtensionPropertyPage(EditSupport es) {
        super(es);
    }
    
    @Override
    protected Control createContents(Composite parent) {
        noDefaultAndApplyButton();
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL)
            .grab(true, true).applyTo(composite);
        GridLayoutFactory.fillDefaults().applyTo(composite);
        
        final ListViewer viewer = new ListViewer(composite);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(getProject().getProjectProperties()
                .getSimpleExtensionClassNames());
        GridDataFactory.generate(viewer.getControl(), 1, 1);
        
        Button addButton = createAddButton(composite, viewer);
        final Button editButton = createEditButton(composite, viewer);
        final Button removeButton = createRemoveButton(composite, viewer);

        GridDataFactory.generate(addButton, 1, 1);
        GridDataFactory.generate(editButton, 1, 1);
        GridDataFactory.generate(removeButton, 1, 1);

        editButton.setEnabled(false);
        removeButton.setEnabled(false);

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = 
                    (IStructuredSelection)event.getSelection();
                removeButton.setEnabled(!selection.isEmpty());
                editButton.setEnabled(selection.size() == 1);
            }
        });

        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.PROJECT_PROPERTY_PAGE);

        return composite;
    }

    /**
     * Creates and returns the button for adding a Composite Extension.
     * Necessary listeners are added to the button.
     * No layout data is assigned to the button.
     * 
     * @param composite The parent composite.
     * @param viewer The viewer to assign to the button.
     * @return the created button.
     */
    private Button createAddButton(
            Composite composite, final ListViewer viewer) {
        
        Button addButton = new Button(composite, SWT.NONE);
        addButton.setText(Messages.ComponentExtensions_AddButton_Text);
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                InputDialog dialog = new InputDialog(getShell(), 
                        Messages.ComponentExtensionDialog_Title, 
                        Messages.ComponentExtensionDialog_Message, 
                        StringUtils.EMPTY, null);
                if (dialog.open() == Window.OK) {
                    if (getProject().getProjectProperties()
                            .addSimpleExtensionClassName(dialog.getValue())) {
                        viewer.add(dialog.getValue());
                    }
                    viewer.setSelection(
                            new StructuredSelection(dialog.getValue()));
                }
            }
        });
        
        return addButton;
    }

    /**
     * Creates and returns the button for editing a Composite Extension.
     * Necessary listeners are added to the button.
     * No layout data is assigned to the button.
     * 
     * @param composite The parent composite.
     * @param viewer The viewer to assign to the button.
     * @return the created button.
     */
    private Button createEditButton(
            Composite composite, final ListViewer viewer) {

        final Button editButton = new Button(composite, SWT.NONE);
        editButton.setText(Messages.ComponentExtensions_EditButton_Text);
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object selectedObj = 
                    ((IStructuredSelection)viewer.getSelection())
                        .getFirstElement();
                if (!(selectedObj instanceof String)) {
                    LOG.error("Component Extension Property Page: Selected object for editing not of expected type."); //$NON-NLS-1$
                    return;
                }
                String originalName = (String)selectedObj;
                InputDialog dialog = new InputDialog(getShell(), 
                        Messages.ComponentExtensionEditDialog_Title, 
                        Messages.ComponentExtensionEditDialog_Message, 
                        originalName, null);
                if (dialog.open() == Window.OK) {
                    getProject().getProjectProperties()
                        .removeSimpleExtensionClassName(originalName);
                    viewer.remove(originalName);
                    if (getProject().getProjectProperties()
                            .addSimpleExtensionClassName(dialog.getValue())) {
                        viewer.add(dialog.getValue());
                    }
                    viewer.setSelection(
                            new StructuredSelection(dialog.getValue()));
                }
            }
        });

        return editButton;
    }

    /**
     * Creates and returns the button for removing a Composite Extension.
     * Necessary listeners are added to the button.
     * No layout data is assigned to the button.
     * 
     * @param composite The parent composite.
     * @param viewer The viewer to assign to the button.
     * @return the created button.
     */
    private Button createRemoveButton(
            Composite composite, final ListViewer viewer) {

        final Button removeButton = new Button(composite, SWT.NONE);
        removeButton.setText(Messages.ComponentExtensions_RemoveButton_Text);
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] selectionArray = 
                    ((IStructuredSelection)viewer.getSelection()).toArray();
                for (Object selectedObj : selectionArray) {
                    String selectedName = (String)selectedObj;
                    getProject().getProjectProperties()
                            .removeSimpleExtensionClassName(selectedName);
                }
                viewer.remove(selectionArray);
            }
        });

        return removeButton;
    }

    @Override
    public boolean performOk() {
        return super.performOk();
    }
}
