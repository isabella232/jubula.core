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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.utils.JBThread;
import org.eclipse.jubula.client.ui.views.TestresultSummaryView;
import org.eclipse.ui.IWorkbenchPart;


/**
 * handler for refreshing testresults in testresult summary view
 *
 * @author BREDEX GmbH
 * @created Feb 4, 2010
 */
public class RefreshTestresultsHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = Plugin.getActivePart();
        if (activePart instanceof TestresultSummaryView) {
            final TestresultSummaryView summary = 
                (TestresultSummaryView)activePart;
            JBThread t = new JBThread() {
                public void run() {
                    if (!Hibernator.init()) {
                        Plugin.stopLongRunning();
                        return;
                    }
                    summary.refreshView();
                }

                protected void errorOccured() {
                    Plugin.stopLongRunning();
                }
            };
            t.start();
        }
        
        return null;
    }

}
