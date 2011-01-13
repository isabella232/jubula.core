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
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.RecordModeState;
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.businessprocess.StopObservationModeBP;
import org.eclipse.jubula.client.ui.controllers.TestExecutionContributor;
import org.eclipse.swt.widgets.Event;


/**
 * @author BREDEX GmbH
 * @created 11.05.2005
 */
public class StopObservationModeAction extends AbstractAction {
    
    /**
     * {@inheritDoc}
     */
    public void runWithEvent(IAction action, Event event) {
        if (action != null && !action.isEnabled()) {
            return;
        }
        TestExecutionContributor.getInstance().
            getClientTest().resetToTesting();
        DataEventDispatcher.getInstance()
            .fireRecordModeStateChanged(RecordModeState.notRunning);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return StopObservationModeBP.getInstance();
    }
    
}
