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
package org.eclipse.jubula.client.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.ui.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.wizards.pages.ImportXLSTestdataWizardPage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wizard for exporting Test Result details.
 * 
 * @author BREDEX GmbH
 * @created Jun 25, 2010
 */
public class ImportTestDataSetsWizard extends Wizard implements IImportWizard {
    /**
     * <code>WIZARD_ID</code>
     */
    public static final String ID = "org.eclipse.jubula.client.ui.importWizard.ImportTestDataSetsWizard"; //$NON-NLS-1$

    /** ID for the "Import CSV Data Set" page */
    private static final String IMPORT_XLS_DATA_SET_PAGE_ID = "ImportTestDataSetsWizard.ImportXLSPage"; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(ImportTestDataSetsWizard.class);

    /**
     * <code>m_importCSVData</code>
     */
    private ImportXLSTestdataWizardPage m_importCSVData;

    /**
     * <code>m_selection</code>
     */
    private IStructuredSelection m_selection;

    /**
     * <code>m_ctde</code>
     */
    private CentralTestDataEditor m_ctde;

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        m_importCSVData = new ImportXLSTestdataWizardPage(
                IMPORT_XLS_DATA_SET_PAGE_ID);
        addPage(m_importCSVData);
    }

    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        return m_importCSVData.finish(m_selection, m_ctde);
    }

    /**
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setNeedsProgressMonitor(true);
        setWindowTitle(Messages.ImportTestDataSetsWizardWindowTitle);
        IEditorPart activeEditor = workbench.getActiveWorkbenchWindow()
                .getActivePage().getActiveEditor();
        if (activeEditor instanceof CentralTestDataEditor) {
            m_ctde = (CentralTestDataEditor)activeEditor;
            m_ctde.getEditorHelper().requestEditableState();
            // FIXME MT: cancel opening wizard
        }
        m_selection = selection;
    }

}
