/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.ui.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jubula.client.analyze.ExtensionRegistry;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeParameter;
import org.eclipse.jubula.client.analyze.internal.AnalyzeRun;
import org.eclipse.jubula.client.analyze.ui.components.AnalyzePreferenceDialog;
import org.eclipse.jubula.client.analyze.ui.internal.Query;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


/**
 * 
 * @author volker
 * 
 */
public class RunSelectionHandler extends AbstractHandler {
    /**
     * Parameter given from the Command
     */
    private String m_idParam;

    /**
     * @param event
     *            event
     * @return null
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        m_idParam = event.getParameter("org.eclipse.jubula.client.analyze.ui.RunSelection.IDParam");  //$NON-NLS-1$
        
        Analyze ana = ExtensionRegistry.getAnalyze().get(m_idParam);
        
        // This List is used to save the AnalyzeParameters
        
        ArrayList<AnalyzeParameter> parameterList = 
                (ArrayList<AnalyzeParameter>) ana.getAnalyzeParameter();

        // create a new AnalyzePreferenceDialog
        AnalyzePreferenceDialog apd = new AnalyzePreferenceDialog(Display
                .getDefault().getActiveShell(), parameterList);   

        if (!apd.getCancelStatus()) {

            // fill the ArrayList with the Analyzes that are going to be
            // executed
            // and create the AnalyzeRun
            
            ArrayList<Analyze> analyzeList = new ArrayList<Analyze>();
            analyzeList.add(ana);
            AnalyzeRun ar = new AnalyzeRun();
            ar.createAnalyzeRun(analyzeList);

            // create an run a new Query
            runQuery(ar, event);
        }
        return null;
    }
    
    /**
     * 
     * @param run
     *            The given AnalyzeRun
     * @param event
     *            The given Event
     */
    public void runQuery(AnalyzeRun run, ExecutionEvent event) {
        Query query = new Query(run);
        
        NewSearchUI.runQueryInForeground(
                PlatformUI.getWorkbench().getProgressService(), query);
    }
}
