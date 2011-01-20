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
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.JBPropertyDialog;
import org.eclipse.jubula.client.ui.extensions.ProjectPropertyExtensionHandler;
import org.eclipse.jubula.client.ui.properties.AUTPropertyPage;
import org.eclipse.jubula.client.ui.properties.AbstractProjectPropertyPage;
import org.eclipse.jubula.client.ui.properties.ProjectGeneralPropertyPage;
import org.eclipse.jubula.client.ui.properties.ProjectGeneralPropertyPage.IOkListener;
import org.eclipse.jubula.client.ui.properties.ProjectLanguagePropertyPage;
import org.eclipse.jubula.client.ui.properties.ProjectUsedPropertyPage;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handler for opening the Project Properties dialog.
 *
 * @author BREDEX GmbH
 * @created Apr 30, 2009
 */
public class ProjectPropertiesHandler extends AbstractHandler {

    /** 
     * ID of command parameter for the section of the Project Properties
     * dialog to activate.
     */
    public static final String SECTION_TO_OPEN = 
        "org.eclipse.jubula.client.ui.commands.PropertiesCommand.parameter.sectionToOpen"; //$NON-NLS-1$
    
    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow activeWindow = 
            HandlerUtil.getActiveWorkbenchWindow(event);
        
        
        Shell shell = activeWindow != null ? activeWindow.getShell() : null;

        PreferenceManager mgr = new PreferenceManager();
        ISelection sel = new StructuredSelection(GeneralStorage.getInstance()
            .getProject());

        // add the 1st property page
        try {
            final EditSupport es = AbstractProjectPropertyPage
                .createEditSupport();

            ProjectGeneralPropertyPage generalPage = 
                new ProjectGeneralPropertyPage(es);
            generalPage.setTitle(I18n.getString("PropertiesAction.page1")); //$NON-NLS-1$
            IPreferenceNode generalNode = new PreferenceNode(
                Constants.PROJECT_PROPERTY_ID, generalPage);
            mgr.addToRoot(generalNode);

            PropertyPage langPage = new ProjectLanguagePropertyPage(es);
            langPage.setTitle(I18n.getString("PropertiesAction.page2")); //$NON-NLS-1$
            IPreferenceNode langNode = new PreferenceNode(
                Constants.PROJECT_PROPERTY_ID, langPage);
            mgr.addToRoot(langNode);

            PropertyPage autPage = new AUTPropertyPage(es);
            autPage.setTitle(I18n.getString("PropertiesAction.page3")); //$NON-NLS-1$
            IPreferenceNode autNode = new PreferenceNode(
                Constants.AUT_PROPERTY_ID, autPage);
            mgr.addToRoot(autNode);

            ProjectUsedPropertyPage usedPage = new ProjectUsedPropertyPage(es);
            usedPage.setTitle(I18n.getString("PropertiesAction.page4")); //$NON-NLS-1$
            IPreferenceNode usedNode = new PreferenceNode(
                Constants.REUSED_PROJECT_PROPERTY_ID, usedPage);
            mgr.addToRoot(usedNode);
            generalPage.addOkListener(usedPage);
            
            // Adds project property pages from extensions
            for (AbstractProjectPropertyPage pg 
                    : ProjectPropertyExtensionHandler.createPages(es, mgr)) {
                if (pg instanceof IOkListener) {
                    generalPage.addOkListener((IOkListener)pg);
                }
            }
            
            JBPropertyDialog dialog = new JBPropertyDialog(shell, mgr, sel);
            String sectionToOpen = 
                event.getParameter(SECTION_TO_OPEN);
            
            if (sectionToOpen != null) {
                dialog.setSelectedNode(sectionToOpen);
            }
            
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            dialog.open();
            es.close();
        } catch (PMObjectDeletedException e) { 
            // this implies that the project was deleted since the properties
            // are always available
            PMExceptionHandler.handleGDProjectDeletedException();            
        } catch (PMException e) {
            Utils.createMessageDialog(e, null, null);
        }

        return null;
    }
     
}
