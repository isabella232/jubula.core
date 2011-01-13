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
package org.eclipse.jubula.client.ui.views.problemview;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.views.markers.internal.MarkerMessages;
import org.eclipse.ui.views.markers.internal.Util;


/**
 * @author BREDEX GmbH
 * @created 16.01.2007
 */
public class GDDialogMarkerProperties extends TrayDialog {

    /***/
    private static final String DIALOG_SETTINGS_SECTION = 
        "DialogMarkerPropertiesDialogSettings"; //$NON-NLS-1$
    /** The marker being shown, or <code>null</code> for a new marker */
    private IMarker m_marker = null;
    /** The resource on which to create a new marker */
    private IResource m_resource = null;
    /** The type of marker to be created */
    private String m_type = IMarker.MARKER; 
    /** The initial attributes to use when creating a new marker */
    private Map<String, String> m_initialAttributes = null; 
    /** The text control for the Description field. */
    private Text m_descriptionText;
    /** The control for the Creation Time field. */
    private Label m_creationTime;
    /** The text control for the Location field. */
    private Text m_locationText;
    /** Dirty flag.  True if any changes have been made. */
    private boolean m_dirty;
    /** the severity text label */ 
    private Label m_severityLabel;
    /** the severity image label*/ 
    private Label m_severityImage;
    /** the location text */
    private String m_location;

    /**
     * Creates the dialog.  By default this dialog creates a new marker.
     * To set the resource and initial attributes for the new marker, 
     * use <code>setResource</code> and <code>setInitialAttributes</code>.
     * To show or modify an existing marker, use <code>setMarker</code>.
     * @param parentShell the parent shell
     * @param marker the marker, or <code>null</code> to create a new marker
     * @param location the location text
     */
    GDDialogMarkerProperties(Shell parentShell, IMarker marker, 
        String location) {
        
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        setMarker(marker);
        m_location = location;
    }

    /**
     * Sets the marker to show or modify.
     * 
     * @param marker the marker, or <code>null</code> to create a new marker
     */
    private void setMarker(IMarker marker) {
        m_marker = marker;
        if (marker != null) {
            try {
                m_type = marker.getType();
            } catch (CoreException e) {
                // really do nothing
            }
        }
    }

    /**
     * For a new marker, this returns <code>null</code> until
     * the dialog returns, but is non-null after.
     * @return the marker being created or modified.
     */
    IMarker getMarker() {
        return m_marker;
    }

    /**
     * Sets the resource to use when creating a new marker.
     * If not set, the new marker is created on the workspace root.
     * 
     * @param resource the marker's resource
     */
    public void setResource(IResource resource) {
        m_resource = resource;
    }

    /**
     * If not set, the new marker is created on the workspace root.
     * @return the resource to use when creating a new marker,
     * or <code>null</code> if none has been set.
     */
    IResource getResource() {
        return m_resource;
    }

    /**
     * Sets initial attributes to use when creating a new marker.
     * If not set, the new marker is created with default attributes.
     * @param initialAttributes initial attributes to use when creating a new marker
     */
    void setInitialAttributes(Map<String, String> initialAttributes) {
        m_initialAttributes = initialAttributes;
    }

    /**
     * If not set, the new marker is created with default attributes.
     * @return the initial attributes to use when creating a new marker,
     * or <code>null</code> if not set.
     */
    Map getInitialAttributes() {
        if (m_initialAttributes == null) {
            m_initialAttributes = new HashMap<String, String>();
        }
        return m_initialAttributes;
    }

    /**
     * {@inheritDoc}
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MarkerMessages.propertiesDialog_title);
        newShell.setImage(IconConstants.GUIDANCER_SMALLER_IMAGE);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        //initialize resources/properties
        if (m_marker != null) {
            m_resource = m_marker.getResource();
            try {
                m_initialAttributes = m_marker.getAttributes();
            } catch (CoreException e) {
                // really do nothing
            }
        } else if (m_resource == null) {
            m_resource = ResourcesPlugin.getWorkspace().getRoot();
        }
        Composite comp = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(comp, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        composite.setLayoutData(gridData);
    
        initializeDialogUnits(composite);

        createDescriptionArea(composite);
        if (m_marker != null) {
            createSeperator(composite);
            createCreationTimeArea(composite);
        }
        createAttributesArea(composite);
        if (m_resource != null) {
            createSeperator(composite);
            createResourceArea(composite);
        }

        updateDialogFromMarker();
        updateEnablement();
        
        Dialog.applyDialogFont(composite);
        
        return composite;
    }

    /**
     * Creates a seperator.
     * @param parent the parent omposite
     */
    protected void createSeperator(Composite parent) {
        Label seperator = new Label(parent, SWT.NULL);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        seperator.setLayoutData(gridData);
    }
    
    /**
     * Method createCreationTimeArea.
     * @param parent the parent composite
     */
    private void createCreationTimeArea(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(MarkerMessages
                .propertiesDialog_creationTime_text);

        m_creationTime = new Label(parent, SWT.NONE);
    }

    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Creates the area for the Description field.
     * @param parent the parent composite
     */
    private void createDescriptionArea(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(MarkerMessages.propertiesDialog_description_text);
        m_descriptionText = new Text(parent, (SWT.SINGLE | SWT.BORDER));
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.widthHint =  convertHorizontalDLUsToPixels(400);
        m_descriptionText.setLayoutData(gridData);

        m_descriptionText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                markDirty();
            }
        });
    }

    /**
     * This method is intended to be overridden by subclasses. The attributes area is created between
     * the creation time area and the resource area.
     * 
     * @param parent the parent composite
     */
    protected void createAttributesArea(Composite parent) {
        createSeperator(parent);
        new Label(parent, SWT.NONE)
                .setText(MarkerMessages.propertiesDialog_severityLabel);

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        m_severityImage = new Label(composite, SWT.NONE);
        m_severityLabel = new Label(composite, SWT.NONE);
    }

    /**
     * Creates the area for the Resource field.
     * @param parent the parent composite
     */
    private void createResourceArea(Composite parent) {
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        Label locationLabel = new Label(parent, SWT.NONE);
        locationLabel.setText(MarkerMessages.propertiesDialog_location_text);
        m_locationText = new Text(parent, SWT.SINGLE | SWT.WRAP
                | SWT.READ_ONLY | SWT.BORDER);
        m_locationText.setLayoutData(gridData);
    }

    /**
     * Updates the dialog from the marker state.
     */
    protected void updateDialogFromMarker() {
        if (m_marker == null) {
            updateDialogForNewMarker();
            return;
        }
        m_descriptionText.setText(Util.getProperty(IMarker.MESSAGE, m_marker));
        if (m_creationTime != null) {
            m_creationTime.setText(Util.getCreationTime(m_marker));
        }
        IMarker marker = getMarker();
        if (m_locationText != null) {
            m_locationText.setText(m_location);
        }
        if (marker == null) {
            return;
        }
        m_descriptionText.selectAll();
        int severity = marker.getAttribute(IMarker.SEVERITY, -1);
        Image image = null;
        switch (severity) {
            case IMarker.SEVERITY_INFO:
                image = IconConstants.INFO_IMAGE;
                break;
            case IMarker.SEVERITY_WARNING:
                image = IconConstants.WARNING_IMAGE;
                break;
            case IMarker.SEVERITY_ERROR:
                image = IconConstants.ERROR_IMAGE;
                break;
            default:
                break;
        }
        m_severityImage.setImage(image);
        if (severity == IMarker.SEVERITY_ERROR) {
            m_severityLabel.setText(MarkerMessages.propertiesDialog_errorLabel);
        } else if (severity == IMarker.SEVERITY_WARNING) {
            m_severityLabel.setText(MarkerMessages
                .propertiesDialog_warningLabel);
        } else if (severity == IMarker.SEVERITY_INFO) {
            m_severityLabel.setText(MarkerMessages.propertiesDialog_infoLabel);
        } else {
            m_severityLabel.setText(MarkerMessages
                .propertiesDialog_noseverityLabel);
        }
    }

    /**
     * Updates the dialog from the predefined attributes.
     */
    protected void updateDialogForNewMarker() {
        if (m_initialAttributes != null) {
            Object description = m_initialAttributes.get(IMarker.MESSAGE);
            if (description != null && description instanceof String) {
                m_descriptionText.setText((String) description);
            }
            m_descriptionText.selectAll();
            m_locationText.setText(m_location);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void okPressed() {
        if (m_marker == null || Util.isEditable(m_marker)) {
            saveChanges();
        }
        super.okPressed();
    }

    /**
     * Sets the dialog's dirty flag to <code>true</code>
     */
    protected void markDirty() {
        m_dirty = true;
    }

    /**
     * @return
     * <ul>
     * <li><code>true</code> if the dirty flag has been set to true.</li>
     * <li><code>false</code> otherwise.</li>
     * </ul>
     */
    protected boolean isDirty() {
        return m_dirty;
    }

    /**
     * Saves the changes made in the dialog if needed.
     * Creates a new marker if needed.
     * Updates the existing marker only if there have been changes.
     */
    private void saveChanges() {
        final CoreException[] coreExceptions = new CoreException[1];
        try {
            final Map attrs = getMarkerAttributes();
            PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
                new IRunnableWithProgress() {
                    /**
                     * {@inheritDoc}
                     */
                    public void run(IProgressMonitor monitor) {
                        try {
                            monitor.beginTask(StringConstants.EMPTY, 100);
                            ResourcesPlugin.getWorkspace().run(
                                new IWorkspaceRunnable() {
                                    public void run(IProgressMonitor monitor)
                                        throws CoreException {
                                        
                                        if (m_marker == null) {
                                            createMarker(monitor);
                                        }
                                        if (isDirty()) {
                                            updateMarker(monitor, attrs);
                                        }
                                    }
                                }, monitor);
                            monitor.done();
                        } catch (CoreException e) {
                            coreExceptions[0] = e;
                        }
                    }

                });
        } catch (InvocationTargetException e) {
            IDEWorkbenchPlugin.log(e.getMessage(), StatusUtil.newStatus(
                    IStatus.ERROR, e.getMessage(), e));
            return;
        } catch (InterruptedException e) {
            // rellay do nothing
        }
        if (coreExceptions[0] != null) {
            ErrorDialog.openError(getShell(),
                    MarkerMessages.Error, null, coreExceptions[0].getStatus());
        } 
    }

    /**
     * Creates or updates the marker.  Must be called within a workspace runnable.
     * @param monitor the monitor we report to. 
     * @param attrs the attributes from the dialog
     * @throws CoreException if an error occurs
     */
    private void updateMarker(IProgressMonitor monitor, Map attrs)
        throws CoreException {
        
        // Set the marker attributes from the current dialog field values.
        // Do not use setAttributes(Map) as that overwrites any attributes
        // not covered by the dialog.

        int increment = 50 / attrs.size();
        for (Iterator i = attrs.keySet().iterator(); i.hasNext();) {
            monitor.worked(increment);
            String key = (String) i.next();
            Object val = attrs.get(key);
            m_marker.setAttribute(key, val);
        }
    }

    /**
     * @return the marker attributes to save back to the marker, 
     * based on the current dialog fields.
     */
    protected Map getMarkerAttributes() {
        Map<String, String> attrs;
        if (m_initialAttributes != null) {
            attrs = m_initialAttributes;
        } else {
            attrs = new HashMap<String, String>();
        }
        attrs.put(IMarker.MESSAGE, m_descriptionText.getText());
        return attrs;
    }

    /**
     * Create the marker and report progress
     * to the monitor.
     * @param monitor the current progress monitor
     * @throws CoreException if error occurs
     */
    private void createMarker(IProgressMonitor monitor) throws CoreException {
        if (m_resource == null) {
            return;
        }

        monitor.worked(10);
        m_marker = m_resource.createMarker(m_type);
        monitor.worked(40);
    }

    /**
     * Updates widget enablement for the dialog. Should be overridden by subclasses. 
     */
    protected void updateEnablement() {
        m_descriptionText.setEditable(isEditable());
    }

    /**
     * @return
     * <ul>
     * <li><code>true</code> if the marker is editable or the dialog is creating a new marker.</li>
     * <li><code>false</code> if the marker is not editable.</li>
     * </ul>
     */
    protected boolean isEditable() {
        if (m_marker == null) {
            return true;
        }
        return Util.isEditable(m_marker);
    }

    /**
     * Sets the marker type when creating a new marker.
     * 
     * @param type the marker type
     */
    void setType(String type) {
        m_type = type;
    }
    
    /**
     * {@inheritDoc}
     */
    protected IDialogSettings getDialogBoundsSettings() {
        IDialogSettings settings = IDEWorkbenchPlugin.getDefault()
            .getDialogSettings();
        IDialogSettings section = settings.getSection(DIALOG_SETTINGS_SECTION);
        if (section == null) {
            section = settings.addNewSection(DIALOG_SETTINGS_SECTION);
        } 
        return section;
    }
}