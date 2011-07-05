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
package org.eclipse.jubula.client.ui.handlers.delete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handles the deletion of one or more Component Names in a View.
 * 
 * @author BREDEX GmbH
 * @created Mar 6, 2009
 */
public class DeleteComponentNameInViewHandler extends AbstractHandler {

    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection =
                (IStructuredSelection)selection;
            Set<IComponentNamePO> toDelete = new HashSet<IComponentNamePO>();
            for (Object obj : structuredSelection.toArray()) {
                if (obj instanceof IComponentNamePO) {
                    toDelete.add((IComponentNamePO)obj);
                }
            }

            List<String> itemNames = new ArrayList<String>();
            for (IComponentNamePO compName : toDelete) {
                itemNames.add(compName.getName());
            }

            if (DeleteHandlerHelper.confirmDelete(itemNames)) {
                EntityManager s = GeneralStorage.getInstance()
                        .getMasterSession();
                
                try {
                    EntityTransaction tx = Persistor.instance()
                            .getTransaction(s); 
                    Persistor.instance().lockPOSet(s, toDelete);
                    for (IComponentNamePO compName : toDelete) {
                        s.remove(s.merge(compName));
                    }
                    Persistor.instance().commitTransaction(s, tx);
                    for (IComponentNamePO compName : toDelete) {
                        DataEventDispatcher.getInstance()
                                .fireDataChangedListener(compName,
                                        DataState.Deleted, UpdateState.all);
                        ComponentNamesBP.getInstance().removeComponentNamePO(
                                compName.getGuid());
                    }
                } catch (PMException e) {
                    PMExceptionHandler.handlePMExceptionForMasterSession(e);
                } catch (ProjectDeletedException e) {
                    PMExceptionHandler.handleGDProjectDeletedException();
                }
            }
        }
        
        return null;
    }

}
