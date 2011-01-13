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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MarkerTransfer;
import org.eclipse.ui.views.markers.internal.ActionCopyMarker;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;
import org.eclipse.ui.views.markers.internal.FieldCreationTime;
import org.eclipse.ui.views.markers.internal.FieldLineNumber;
import org.eclipse.ui.views.markers.internal.FieldSeverityAndMessage;
import org.eclipse.ui.views.markers.internal.IField;
import org.eclipse.ui.views.markers.internal.MarkerMessages;
import org.eclipse.ui.views.markers.internal.MarkerNode;
import org.eclipse.ui.views.markers.internal.Util;


/**
 * @author BREDEX GmbH
 * @created 16.01.2007
 */
public class GDActionCopyMarker extends ActionCopyMarker {

    /** the current workbenchpart */
    private IWorkbenchPart m_part;
    /** the clipboard */
    private Clipboard m_clipboard;
    /** the current properties */
    private IField[] m_properties;

    /**
     * Creates the action.
     * @param part the curren tworkbenchpart
     * @param provider the current selection provider
     * @param clipboard the current clipboard
     * @param properties the current properties
     */
    public GDActionCopyMarker(IWorkbenchPart part, 
        ISelectionProvider provider, Clipboard clipboard, IField[] properties) {
        
        super(part, provider);
        m_part = part;
        m_clipboard = clipboard;
        m_properties = properties;
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setEnabled(false);
    }

    /**
     * Copies the selected IMarker objects to the clipboard. If properties have
     * been set, also copies a plain-text report of the selected markers to the
     * clipboard.
     */
    public void run() {
        ConcreteMarker[] markers = getSelectedGDMarkers(
            getStructuredSelection());
        setClipboard(markers, createGDMarkerReport(markers));
    }
    
    /**
     * Return the selected markers for the structured selection.
     * @param structured IStructuredSelection
     * @return ConcreteMarker[]
     */
    private ConcreteMarker[] getSelectedGDMarkers(
        IStructuredSelection structured) {
        
        Object[] selection = structured.toArray();
        List<ConcreteMarker> markers = new ArrayList<ConcreteMarker>();
        for (int i = 0; i < selection.length; i++) {
            Object object = selection[i];
            if (!(object instanceof MarkerNode)) {
                return new ConcreteMarker[0]; //still pending
            }
            MarkerNode marker = (MarkerNode)object;
            if (marker.isConcrete()) {
                markers.add(((ConcreteMarker)object));
            }
        }
        return markers.toArray(new ConcreteMarker[markers.size()]);
    }

    /**
     * Sets the clipboard string
     * @param markers the current selected markers
     * @param markerReport the created marker report
     */
    private void setClipboard(ConcreteMarker[] markers, String markerReport) {
        try {
            IMarker[] imarkers = new IMarker[markers.length];
            for (int i = 0; i < markers.length; i++) {
                imarkers[i] = markers[i].getMarker();
            }
            // Place the markers on the clipboard
            Object[] data;
            Transfer[] transferTypes;
            if (markerReport == null) {
                data = new Object[] { imarkers };
                transferTypes = new Transfer[] { MarkerTransfer.getInstance() };
            } else {
                data = new Object[] { imarkers, markerReport };
                transferTypes = new Transfer[] { MarkerTransfer.getInstance(),
                        TextTransfer.getInstance() };
            }

            m_clipboard.setContents(data, transferTypes);
        } catch (SWTError e) {
            if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
                throw e;
            }
            if (MessageDialog.openQuestion(m_part.getSite().getShell(),
                    MarkerMessages.CopyToClipboardProblemDialog_title,
                    MarkerMessages.CopyToClipboardProblemDialog_message)) {
                setClipboard(markers, markerReport);
            }
        }
    }

    /**
     * Creates a plain-text report of the selected markers based on predefined properties.
     * @param markers the raw markers
     * @return the marker report
     */
    private String createGDMarkerReport(ConcreteMarker[] markers) {
        StringBuffer report = new StringBuffer();
        final String newline = System.getProperty("line.separator"); //$NON-NLS-1$
        final char delimiter = '\t';
        if (m_properties == null) {
            return null;
        }
        // create header
        for (int i = 0; i < m_properties.length; i++) {
            if (!(m_properties[i] instanceof FieldSeverityAndMessage)
                && !(m_properties[i] instanceof FieldLineNumber)
                && !(m_properties[i] instanceof FieldCreationTime)) {
                continue;
            }
            report.append(m_properties[i].getDescription());
            if (i == m_properties.length - 1) {
                report.append(newline);
            } else {
                report.append(delimiter);
            }
        }
        report.append(newline);
        for (int i = 0; i < markers.length; i++) {
            ConcreteMarker marker = markers[i];
            String severity = StringConstants.EMPTY;
            try {
                Object o = marker.getConcreteRepresentative().getMarker()
                    .getAttribute(IMarker.SEVERITY);
                if (o instanceof Integer) {
                    Integer sev = (Integer)o;
                    switch (sev) {
                        case IMarker.SEVERITY_INFO:
                            severity = I18n.getString("Utils.Info1"); //$NON-NLS-1$
                            break;
                        case IMarker.SEVERITY_WARNING:
                            severity = I18n.getString("Utils.Warning1"); //$NON-NLS-1$
                            break;
                        case IMarker.SEVERITY_ERROR:
                            severity = I18n.getString("Utils.Error"); //$NON-NLS-1$
                            break;
                        default:
                            break;
                    }
                }
            } catch (CoreException e) {
                // really do nothing
            } finally {
                report.append(severity + " : " //$NON-NLS-1$
                    + marker.getDescription());
            }
            report.append(delimiter);
            report.append(marker.getConcreteRepresentative().getMarker()
                .getAttribute(IMarker.LOCATION, StringConstants.EMPTY));
            report.append(delimiter);
            report.append(Util.getCreationTime(marker
                .getConcreteRepresentative().getMarker()));
            report.append(newline);
        }
        return report.toString();
    }
}