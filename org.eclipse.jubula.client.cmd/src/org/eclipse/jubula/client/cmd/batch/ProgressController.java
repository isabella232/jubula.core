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
package org.eclipse.jubula.client.cmd.batch;

import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;
import org.eclipse.jubula.client.cmd.i18n.Messages;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.utils.IProgressListener;
import org.eclipse.jubula.client.core.utils.ProgressEvent;


/**
 * @author BREDEX GmbH
 * @created Nov 17, 2006
 */
public class ProgressController implements IProgressListener {
    /**
     * {@inheritDoc}
     */
    public void reactOnProgressEvent(ProgressEvent e) {
        // After first try stop connecting to database
        if (e.getId() == ProgressEvent.OPEN_PROGRESS_BAR) {
            AbstractCmdlineClient.printConsoleLn(
                    Messages.ExecutionControllerDatabase
                    + Messages.ExecutionControllerDatabaseStart, true);
        } else if (e.getId() == ProgressEvent.CLOSE_PROGRESS_BAR) {
            AbstractCmdlineClient.printConsoleLn(
                    Messages.ExecutionControllerDatabase
                + Messages.ExecutionControllerDataBaseEnd, true);
            Hibernator.setUser(null);
        }
    }
}