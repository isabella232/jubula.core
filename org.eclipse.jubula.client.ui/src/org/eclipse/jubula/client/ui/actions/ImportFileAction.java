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
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.businessprocess.AlwaysEnabledBP;
import org.eclipse.jubula.client.ui.businessprocess.ImportFileBP;
import org.eclipse.swt.widgets.Event;


/**
 * @author BREDEX GmbH
 * @created 08.11.2004
 */
public class ImportFileAction extends AbstractAction {

    /**
     * {@inheritDoc}
     *      org.eclipse.swt.widgets.Event)
     */
    public void runWithEvent(IAction action, Event event) {
        if (action != null && !action.isEnabled()) {
            return;
        }
        ImportFileBP.getInstance().importFile();
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return AlwaysEnabledBP.getInstance();
    }
}