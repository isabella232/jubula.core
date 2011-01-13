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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;


/**
 * @author BREDEX GmbH
 * @created 30.03.2006
 */
public class ChooseLanguageAction extends AbstractAction implements
    IWorkbenchWindowPulldownDelegate {

    /**
     * {@inheritDoc}
     */
    public void runWithEvent(IAction action, Event event) {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            final Locale defaultLanguage = project.getDefaultLanguage();
            if (defaultLanguage != null) {
                Locale lang = defaultLanguage;
                if (!WorkingLanguageBP.getInstance().getWorkingLanguage().
                    equals(lang)) {
                    WorkingLanguageBP.getInstance().setCurrentLanguage(lang);
                    DataEventDispatcher.getInstance().fireLanguageChanged(lang);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Menu getMenu(Control parent) {
        List <Locale> languages = null;
        MenuManager menuManager = new MenuManager();
        Menu fMenu = menuManager.createContextMenu(parent);
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            languages = project.getLangHelper().getLanguageList();
            List<String> dispList = new ArrayList<String>(); 
            for (Locale locale : languages) {
                dispList.add(locale.getDisplayName());
            }
            String[] dispNames = dispList.toArray(new String[dispList.size()]);
            
            Arrays.sort(dispNames);
            for (String lang : dispNames) {
                LanguageAction action = new LanguageAction(
                    lang, IAction.AS_CHECK_BOX);
                action.setLanguage(Languages.getInstance().getLocale(lang));
                action.setChecked(false);
                menuManager.add(action);
                if (lang.equals(WorkingLanguageBP.getInstance()
                    .getWorkingLanguage().getDisplayName())) {
                    action.setChecked(true);
                } 
            }
        }   
        return fMenu;
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return WorkingLanguageBP.getInstance();
    }

}
