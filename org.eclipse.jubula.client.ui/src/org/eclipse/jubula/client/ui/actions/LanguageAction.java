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
package org.eclipse.jubula.client.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorPart;



/**
 * @author BREDEX GmbH
 * @created 30.03.2006
 */
public class LanguageAction extends Action {

    /**
     * <code>m_language</code>current language
     */
    private Locale m_language;

    /**
     * @param text text
     * @param style style
     */
    public LanguageAction(String text, int style) {
        super(text, style);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        
        // no change needed
        if (m_language.equals(WorkingLanguageBP.getInstance().
            getWorkingLanguage())) {
            return;
        }
        
        // change language
        if (showUnsavedTestSuiteEditors()) {
            WorkingLanguageBP.getInstance().setCurrentLanguage(m_language);
            DataEventDispatcher.getInstance().fireLanguageChanged(m_language);
        }
    }

    /**
     * @param lang language to set
     */
    public void setLanguage(Locale lang) {
        m_language = lang;
    }
    
    /**
     * Opens a dialog, to show all unsaved TS-Editors.
     * @return True, if there are TestSuiteEditors to save.
     */
    private boolean showUnsavedTestSuiteEditors() {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        List<String> editorsToSave = new ArrayList<String>();
        List<String> editorsToClose = new ArrayList<String>();
        for (ITestSuitePO testSuite
            : project.getTestSuiteCont().getTestSuiteList()) {
            
            IEditorPart editor = Utils.getEditorByPO(testSuite);
            if (testSuite.getAut() != null 
                && !WorkingLanguageBP.getInstance().isTestSuiteLanguage(
                    m_language, testSuite)
                && editor != null && !editor.isDirty()) {

                editorsToClose.add(editor.getTitle());
            }
            // is testSuiteEditor saved ??
            if (editor != null && editor.isDirty()) {
                editorsToSave.add(editor.getTitle());
            }
        }
        if (editorsToSave.isEmpty()) {
            if (editorsToClose.isEmpty()) {
                return true;
            }
            String editorNames = StringConstants.EMPTY;
            for (String name : editorsToClose) {
                editorNames = editorNames + "   - " + name + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            Utils.createMessageDialog(MessageIDs.I_EDITORS_TO_CLOSE, 
                    new Object[]{editorNames}, null);
            for (String name : editorsToClose) {
                IEditorPart editor = Plugin.getEditorByTitle(name);
                Plugin.getActivePage().closeEditor(editor, false);
            }
            return true;
        }
        if (!editorsToSave.isEmpty()) {
            String editorNames = StringConstants.EMPTY;
            for (String name : editorsToSave) {
                editorNames = editorNames + "   - " + name + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            Utils.createMessageDialog(MessageIDs.I_EDITORS_TO_SAVE, 
                    new Object[]{editorNames}, null);
            return false;
        }
        return true;
    }
}