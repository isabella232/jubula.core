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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.controllers.dnd.objectmapping.OMEditorDndSupport;
import org.eclipse.jubula.client.ui.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.provider.labelprovider.OMEditorTreeLabelProvider;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 27.04.2005
 */
public class OMSetCategoryToMapInto extends AbstractAction {

    /** The handle to this Action */
    private static IAction handleAction;
    
    /**
     * {@inheritDoc}
     */
    public void init(IAction action) {
        handleAction = action;
        handleAction.setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    public void runWithEvent(IAction action, Event event) {
        ISelection sel = 
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getSelectionService().getSelection();
        if (sel instanceof IStructuredSelection) {
            setCategoryToMapInto((IStructuredSelection)sel);
        }
    }

    /**
     * Sets the category to map into.
     * 
     * @param selection The current workbench selection.
     */
    private void setCategoryToMapInto(IStructuredSelection selection) {
        ObjectMappingMultiPageEditor editor = 
            ((ObjectMappingMultiPageEditor)Plugin.getActivePart());
        if (editor.getAut().equals(TestExecution.getInstance()
                .getConnectedAut())) {
            
            IObjectMappingCategoryPO category = null;
            
            Object node;
            if (selection.size() == 1) {
                node = selection.getFirstElement();
                IObjectMappingCategoryPO unmappedTechNames = 
                    editor.getAut().getObjMap().getUnmappedTechnicalCategory();
                if (node instanceof IObjectMappingCategoryPO) {
                    IObjectMappingCategoryPO catNode = 
                        (IObjectMappingCategoryPO)node;
                    if (unmappedTechNames.equals(
                            OMEditorDndSupport.getSection(catNode))) {

                        category = catNode;
                    }
                } else if (node instanceof IObjectMappingAssoziationPO) {
                    IObjectMappingAssoziationPO assocNode = 
                        (IObjectMappingAssoziationPO)node;
                    if (unmappedTechNames.equals(
                            OMEditorDndSupport.getSection(assocNode))) {
                        category = assocNode.getCategory();
                    }
                }
                
                editor.getOmEditorBP().setCategoryToCreateIn(category);
                
                String strCat;
                IObjectMappingCategoryPO cat = 
                    ObjectMappingEventDispatcher.getCategoryToCreateIn();
                if (cat != null) {
                    strCat = cat.getName();
                    if (OMEditorTreeLabelProvider
                            .getTopLevelCategoryName(strCat) != null) {
                        strCat = OMEditorTreeLabelProvider
                            .getTopLevelCategoryName(strCat);
                    }
                } else {
                    strCat = I18n.getString("TestExecutionContributor.CatUnassigned"); //$NON-NLS-1$
                }
                String message = I18n.getString("TestExecutionContributor.AUTStartedMapping",  //$NON-NLS-1$
                    new Object[] {strCat}); 
                Plugin.showStatusLine(Constants.MAPPING, message);
            }
        }
    }
    
    /**
     * @return Returns the handleAction.
     */
    public static IAction getAction() {
        return handleAction;
    }
    
    /**
     * @param enabled The Action to set enabled.
     */
    public static void setEnabled(boolean enabled) {
        handleAction.setEnabled(enabled);
    }
}