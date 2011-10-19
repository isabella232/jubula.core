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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
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
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;


/**
 * @author BREDEX GmbH
 * @created 04.07.2005
 */
public class NewCategoryHandler extends AbstractNewHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        try {
            createNewCategory(event);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        }
        return null;
    }
    
    /**
     * @param event the execution event
     * @throws PMSaveException
     *             in case of DB storage problem
     * @throws PMAlreadyLockedException
     *             in case of locked catParentPO
     * @throws PMException
     *             in case of rollback failed
     * @throws ProjectDeletedException
     *             if the project was deleted in another instance
     */
    private void createNewCategory(ExecutionEvent event)
        throws PMSaveException, PMAlreadyLockedException, PMException, 
        ProjectDeletedException {

        final INodePO categoryParent = getParentNode(event);
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
                return !existCategory(categoryParent, getInputFieldText());
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
            NodePM.addAndPersistChildNode(categoryParent, category, null, NodePM
                .getCmdHandleChild(categoryParent, category));
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
        Iterator<? extends INodePO> iter = null;
        if (Persistor.isPoSubclass(node, ICategoryPO.class)) {
            iter = node.getNodeListIterator();
        } else {
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            iter = proj.getSpecObjCont().getSpecObjList().iterator();
        }
        while (iter.hasNext()) {
            INodePO iterNode = iter.next();
            if (Persistor.isPoSubclass(iterNode, ICategoryPO.class)
                && iterNode.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}