/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
/**
 * Handler for ChooseLanguageCommand
 * 
 * @author BREDEX GmbH
 *
 */
public class ChooseLanguageHandler extends AbstractHandler implements
        IElementUpdater {
    /** ID of command parameter for the working language */
    public static final String LANGUAGE = 
        "org.eclipse.jubula.client.ui.rcp.commands.ChooseLanguageCommand.parameter.language"; //$NON-NLS-1$
   
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        Map map = event.getParameters();
        String lang = (String) map.get(LANGUAGE);
        Locale loc = Languages.getInstance().getLocale(lang);
        // Get default language if you only clicked the button
        if (loc == null) {
            IProjectPO project = GeneralStorage.getInstance().getProject();
            if (project != null) {
                final Locale defaultLanguage = project.getDefaultLanguage();
                if (defaultLanguage != null) {
                    loc = defaultLanguage;
                }
            }
        }
        // no change needed
        if (loc != null && loc.equals(WorkingLanguageBP.getInstance().
            getWorkingLanguage())) {
            return null;
        }
        
        //changes needed
        if (showUnsavedTestSuiteEditors(loc)) {
            WorkingLanguageBP.getInstance().setCurrentLanguage(loc);
            DataEventDispatcher.getInstance().fireLanguageChanged(loc);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void updateElement(UIElement element, Map parameters) {
        String lang =  (String) parameters.get(LANGUAGE);

        if (lang != null && lang.equals(WorkingLanguageBP.getInstance()
              .getWorkingLanguage().getDisplayName())) {
            element.setChecked(true);
        } else {
            element.setChecked(false);
        }
    }
    
    /**
     * Opens a dialog, to show all unsaved TS-Editors.
     * 
     * @param lang
     *            the language locale on which to look for unsaved test suite
     *            editors
     * @return True, if there are TestSuiteEditors to save.
     */
    private boolean showUnsavedTestSuiteEditors(Locale lang) {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        List<String> editorsToSave = new ArrayList<String>();
        List<String> editorsToClose = new ArrayList<String>();
        
        String preLine = StringConstants.SPACE + StringConstants.MINUS 
                + StringConstants.SPACE;
        for (ITestSuitePO testSuite
            : TestSuiteBP.getListOfTestSuites(project)) {
            
            IEditorPart editor = Utils.getEditorByPO(testSuite);
            if (testSuite.getAut() != null 
                && !WorkingLanguageBP.getInstance().isTestSuiteLanguage(
                    lang, testSuite)
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
                editorNames += preLine + name + StringConstants.NEWLINE;
            }
            ErrorHandlingUtil.createMessageDialog(MessageIDs.I_EDITORS_TO_CLOSE,
                    new Object[] { editorNames }, null);
            for (String name : editorsToClose) {
                IEditorPart editor = Plugin.getEditorByTitle(name);
                Plugin.getActivePage().closeEditor(editor, false);
            }
            return true;
        }
        if (!editorsToSave.isEmpty()) {
            String editorNames = StringConstants.EMPTY;
            for (String name : editorsToSave) {
                editorNames += preLine + name + StringConstants.NEWLINE;
            }
            ErrorHandlingUtil.createMessageDialog(MessageIDs.I_EDITORS_TO_SAVE,
                    new Object[] { editorNames }, null);
            return false;
        }
        return true;
    }
}
