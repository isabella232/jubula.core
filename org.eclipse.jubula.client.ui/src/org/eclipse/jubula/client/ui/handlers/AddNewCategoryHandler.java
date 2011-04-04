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

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 04.07.2005
 */
public class AddNewCategoryHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart != null 
                && Constants.TC_BROWSER_ID.equals(
                        activePart.getSite().getId())) {
            IStructuredSelection selection = Plugin.getTreeViewSelection(
                    Constants.TC_BROWSER_ID);            
            try {
                createNewCategory(selection);
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleGDProjectDeletedException();
            }
        }
        return null;
    }
    
    /**
     * @param selection
     *            the parent of the new category.
     * @throws PMSaveException
     *             in case of DB storage problem
     * @throws PMAlreadyLockedException
     *             in case of locked catParentPO
     * @throws PMException
     *             in case of rollback failed
     * @throws ProjectDeletedException
     *             if the project was deleted in another instance
     */
    private void createNewCategory(IStructuredSelection selection)
        throws PMSaveException, PMAlreadyLockedException, PMException, 
        ProjectDeletedException {
        INodePO catParentPO;
        if (!selection.isEmpty()) {
            catParentPO = (INodePO)selection.getFirstElement();
            while (!(catParentPO instanceof ICategoryPO) 
                    && !(catParentPO instanceof IProjectPO)) {
                catParentPO = catParentPO.getParentNode();
            }
        } else {
            catParentPO = GeneralStorage.getInstance().getProject();
        }
        final INodePO finalCatParent = catParentPO;
        InputDialog dialog = new InputDialog(
            Plugin.getShell(), 
            Messages.CreateNewCategoryActionCatTitle,
            InitialValueConstants.DEFAULT_CATEGORY_NAME,
            Messages.CreateNewCategoryActionCatMessage,
            Messages.CreateNewCategoryActionCatLabel,
            Messages.CreateNewCategoryActionCatError,
            Messages.CreateNewCategoryActionDoubleCatName,
            IconConstants.NEW_CAT_DIALOG_STRING,
            Messages.CreateNewCategoryActionNewCategory, false) {
            
            /**
             * @return False, if the input name already exists.
             */
            protected boolean isInputAllowed() {
                return !existCategory(finalCatParent, getInputFieldText());
            }

        };
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
            ContextHelpIds.DIALOG_NEW_CATEGORY);
        dialog.open();
        if (Window.OK == dialog.getReturnCode()) {
            String categoryName = dialog.getName();
            ICategoryPO category = NodeMaker.createCategoryPO(categoryName);
            NodePM.addAndPersistChildNode(catParentPO, category, null, NodePM
                .getCmdHandleChild(catParentPO, category));
            DataEventDispatcher.getInstance().fireDataChangedListener(category, 
                DataState.Added, UpdateState.all);
        }
        dialog.close();
    }

    /**
     * checks if a category exists in childnodes of given parent
     * @param node INodePO
     * @param name String
     * @return boolean
     */
    boolean existCategory(INodePO node, String name) {
        Iterator iter = null;
        if (Hibernator.isPoSubclass(node, ICategoryPO.class)) {
            iter = node.getNodeListIterator();
        } else {
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            iter = proj.getSpecObjCont().getSpecObjList().iterator();
        }
        while (iter.hasNext()) {
            INodePO iterNode = (INodePO)iter.next();
            if (Hibernator.isPoSubclass(iterNode, ICategoryPO.class)
                && iterNode.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}