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
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.businessprocess.OMStopMappingModeBP;
import org.eclipse.jubula.client.ui.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.exception.GDException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;


/**
 * @author BREDEX GmbH
 * @created 27.04.2005
 */
public class OMStopMappingModeAction extends AbstractAction 
    implements IEditorActionDelegate {
    
    /**
     * {@inheritDoc}
     */
    public void runWithEvent(IAction action, Event event) {
        if (action != null && !action.isEnabled()) {
            return;
        }
        if (TestExecution.getInstance().getConnectedAut() == null) {
            String message = I18n.getString("OMStopMappingModeAction.Error1"); //$NON-NLS-1$
            Utils.createMessageDialog(new GDException(message, 
                    MessageIDs.E_UNEXPECTED_EXCEPTION), null, new String[]{
                        message});
        } else {
            TestExecutionContributor.getInstance().
                getClientTest().resetToTesting();
            DataEventDispatcher.getInstance()
                .fireOMStateChanged(OMState.notRunning);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        registerAction(action);
    }
    
    /**
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return OMStopMappingModeBP.getInstance();
    }

}