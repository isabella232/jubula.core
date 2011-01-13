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
package org.eclipse.jubula.client.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;


/**
 * Read-only text viewer.
 *
 * @author BREDEX GmbH
 * @created Feb 9, 2007
 */
public class LogViewer extends EditorPart {

    /** the text field for this viewer */
    private Text m_text = null;
    
    /**
     * {@inheritDoc}
     */
    public void doSave(IProgressMonitor monitor) {
        doSaveAs();
    }

    /**
     * {@inheritDoc}
     */
    public void doSaveAs() {
        // Not supported, but could be added later
    }

    /**
     * {@inheritDoc}
     */
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        if (input != null) {
            setSite(site);
            setInput(input);
            setPartName(input.getName());
        } else {
            String msg = I18n.getString("EditorInit.CreateError"); //$NON-NLS-1$
            throw new PartInitException(msg);
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed() {
        // Not supported, but could be added later
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        if (getEditorInput() instanceof ISimpleEditorInput) {
            ISimpleEditorInput input = (ISimpleEditorInput)getEditorInput();
            if (input instanceof ClientLogInput) {
                setTitleImage(Plugin.getImage("clientLogView.gif")); //$NON-NLS-1$
            } else if (input instanceof ServerLogInput) {
                setTitleImage(Plugin.getImage("serverLogView.gif")); //$NON-NLS-1$
            }
            try {
                m_text = new Text(
                    parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
                m_text.setText(input.getContent());
            } catch (CoreException ce) {
                Utils.createMessageDialog(MessageIDs.E_CANNOT_OPEN_EDITOR);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        if (m_text != null) {
            m_text.setFocus();
        }
    }

}
