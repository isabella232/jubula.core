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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.editors.GDEditorHelper;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * @author BREDEX GmbH
 * @created 23.11.2004
 */
public abstract class AbstractAction implements IWorkbenchWindowActionDelegate,
    IActionDelegate2 {

    /** list with all registered actions */
    private Set<IAction> m_actPool = 
        new HashSet<IAction>();

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (getActionBP() != null) {
            getActionBP().unregisterActionDelegate(this);
        }
    }

    /**
     * @return the ActionBP object associated with this Action
     */
    // FIXME zeb make this method abstract when refactoring is complete
    protected AbstractActionBP getActionBP() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void init(IWorkbenchWindow window) {
        //      do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void run(IAction action) {
        //      do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void selectionChanged(IAction action, ISelection selection) {
        //      do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void init(IAction action) {
        if (getActionBP() != null) {
            getActionBP().registerActionDelegate(this);
        }
        registerAction(action);        
    }

    /**
     * {@inheritDoc}
     */
    public abstract void runWithEvent(IAction action, Event event);
    
    /**
     * @param po locked persistent object
     * @return flag to signal, if given object is locked by an own editor
     */
    public static boolean handleLockedObject(IPersistentObject po) {
        boolean result = false;
        IEditorPart editor = GDEditorHelper.findEditor2LockedObj(po);
        if (editor != null) {
            Utils.createMessageDialog(MessageIDs.I_LOCK_OBJ_1, 
                    new Object[] {po.getName(), editor.getTitle() }, null);
            result = true;
        }
        return result;
    }


    
    /**
     * Sets the enabled status of all actions represented by this delegate.
     * @param enabled The value to use for enablement status.
     */
    public void setEnabledStatus(boolean enabled) {
        for (IAction action : m_actPool) {
            action.setEnabled(enabled);
        }
    }

    /**
     * Registers the given action to be handled by this delegate. If this
     * action is already handled by the delegate, this method has no effect.
     * @param proxy The action to be handled.
     */
    protected void registerAction(IAction proxy) {
        m_actPool.add(proxy);
        if (getActionBP() != null) {
            getActionBP().setEnabledStatus();            
        }
    }

}
