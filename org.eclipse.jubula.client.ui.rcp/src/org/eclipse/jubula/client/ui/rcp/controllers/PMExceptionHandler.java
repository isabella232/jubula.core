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
package org.eclipse.jubula.client.ui.rcp.controllers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMExtProjDeletedException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.ui.rcp.actions.AbstractAction;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.project.RefreshProjectHandler.RefreshProjectOperation;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 19.10.2005
 */
public class PMExceptionHandler {
    
    /**
     * private constructor
     */
    private PMExceptionHandler() {
        // nothing
    }
    
    
    /**
     * handles different subclasses of PMException
     * @param e PMException is wanted
     */
    public static void handlePMExceptionForMasterSession(PMException e) {
        if (e instanceof PMDirtyVersionException
            || e instanceof PMObjectDeletedException
            || e instanceof PMSaveException) {
            ErrorHandlingUtil.createMessageDialog(e.getErrorId());
            try {
                PlatformUI.getWorkbench().getProgressService().run(true, false,
                        new RefreshProjectOperation());
            } catch (InvocationTargetException ite) {
                // Already handled within the operation.
                // Do nothing.
            } catch (InterruptedException ie) {
                // Operation canceled.
                // Do nothing.
            }
        } else if (e instanceof PMExtProjDeletedException) {
            ErrorHandlingUtil.createMessageDialog(e.getErrorId());
        } else if (e instanceof PMAlreadyLockedException) {
            boolean result = false;
            if (((PMAlreadyLockedException)e).getLockedObject() != null) {
                result = AbstractAction.handleLockedObject((
                    (PMAlreadyLockedException)e).getLockedObject());
            }
            if (!result) {
                ErrorHandlingUtil.createMessageDialog(e.getErrorId());
            } 
        } else {
            GeneralStorage.handleFatalError(e);
        }
    }
      
    /**
     * @param exc PMException
     * @param editor editor caused this excpetion
     */
    public static void handlePMExceptionForEditor(PMException exc, 
        IJBEditor editor) {
        
        if (exc instanceof PMDirtyVersionException
            || exc instanceof PMObjectDeletedException) {
            ErrorHandlingUtil.createMessageDialog(exc);            
            try {
                PlatformUI.getWorkbench().getProgressService().run(true, false,
                        new RefreshProjectOperation());
            } catch (InvocationTargetException e) {
                // Already handled within the operation.
                // Do nothing.
            } catch (InterruptedException e) {
                // Operation canceled.
                // Do nothing.
            }
        } else if (exc instanceof PMAlreadyLockedException) {
            handlePMAlreadyLockedException((PMAlreadyLockedException) exc, 
                    null);
        } else if (exc instanceof PMObjectDeletedException) {
            ErrorHandlingUtil.createMessageDialog(MessageIDs.E_DELETED_OBJECT);
        } else {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_DATABASE_GENERAL);
            if (editor != null) {
                editor.getSite().getPage().closeEditor(editor, false);
            }
        }
    }


    /**
     * @param exc the PMAlreadyLockedException
     * @param details optional details; can be null
     */
    public static void handlePMAlreadyLockedException(
            PMAlreadyLockedException exc, String[] details) {
        ErrorHandlingUtil.createMessageDialog(exc, null, details);
    }
    
    /**
     * clear current views and editors after deletion of current project by
     * another user
     */
    public static void handleGDProjectDeletedException() {
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                IPersistentObject oldProj = GeneralStorage.getInstance()
                        .getProject();
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_CURRENT_PROJ_DEL);
                Utils.clearClient();
                GeneralStorage.getInstance().setProject(null);
                if (oldProj != null) {
                    final DataEventDispatcher ded = 
                            DataEventDispatcher.getInstance();
                    ded.fireDataChangedListener(oldProj, 
                            DataState.Deleted, UpdateState.all);
                }
            }
        });
    }

}
