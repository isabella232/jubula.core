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
package org.eclipse.jubula.client.ui.properties;

import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.tools.exception.JBFatalAbortException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * @author BREDEX GmbH
 * @created 09.11.2005
 *
 */
public abstract class AbstractProjectPropertyPage extends PropertyPage 
        implements IWorkbenchPropertyPage {
    
    /**
     * <code>m_editSupport</code>
     */
    private EditSupport m_editSupport = null;
    
    /** work version for this session */
    private IProjectPO m_workProject = null;
    /**
     * @param es the editSupport
     */
    public AbstractProjectPropertyPage(EditSupport es) {
        m_editSupport = es;
    }
    
    /**
     * This constructor is needed for the extension point.
     * 
     * setEditSupport should be called after that.
     */
    public AbstractProjectPropertyPage() {
        // nothing
    }

    /**
     * @return es The editsupport.
     * @throws PMException if editSupport cannot 
     */
    public static EditSupport createEditSupport() throws PMException {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        EditSupport editSupport = 
            new EditSupport(project.getProjectProperties(), null);
        editSupport.lockWorkVersion();
        ProjectNameBP.getInstance().clearCache();
        return editSupport;
    }

    /**
     * @return shared project
     */
    public IProjectPO getProject() {
        if (m_workProject == null) {
            try {
                m_workProject = getEditSupport().getWorkProject();
            } catch (PMException e) {
                throw new JBFatalAbortException(
                        "Can't load project in edit session", e, //$NON-NLS-1$
                        MessageIDs.E_DATABASE_GENERAL);
            }
        }
        return m_workProject;
    }

    /**
     * @return shared edit support
     */
    protected EditSupport getEditSupport() {
        return m_editSupport;
    }
    
    /**
     * @param es - the new editsupport
     */
    public void setEditSupport(EditSupport es) {
        m_editSupport = es;
    }

    /**
     * {@inheritDoc}
     */
    public boolean performCancel() {
        Plugin.stopLongRunning();
        return super.performCancel();
    }
}