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
package org.eclipse.jubula.client.analyze.ui.internal;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jubula.client.analyze.definition.IAnalyze;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.internal.AnalyzeRun;
import org.eclipse.jubula.client.analyze.ui.i18n.Messages;
import org.eclipse.jubula.client.analyze.ui.internal.helper.ContextHelper;
import org.eclipse.jubula.client.ui.rcp.search.query.AbstractSearchQuery;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

/**
 * 
 * @author volker
 *
 */
public class Query extends AbstractSearchQuery implements ISearchQuery {
    
    /** The QueryResult */
    private QueryResult m_queryResult; 
    
    /** The AnalyzeRun*/
    private AnalyzeRun m_analyzeRun;
    
    /**
     * @param analyzeRun The Analyze
     */
    public Query(AnalyzeRun analyzeRun) {
        setAnalyzeRun(analyzeRun);
    }

    /**
     * @param queryResult the queryResult to set
     */
    protected void setQueryResult(QueryResult queryResult) {
        m_queryResult = queryResult;
    }
    /**
     * {@inheritDoc}
     */
    public IStatus run(IProgressMonitor monitor)
        throws OperationCanceledException {
        
        try {
            calculate(monitor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
    }
    /**
     * executes the given AnalyseRun, creates and sets a QueryResult
     * @param monitor monitor
     * @throws InterruptedException 
     */
    public void calculate(IProgressMonitor monitor) 
        throws InterruptedException {
        
        Object node = ContextHelper.getSelection();
        // New QueryResult
        QueryResult qr = new QueryResult(this);

        // set the workAmount which is used from the ProgressMonitor
        int workAmount = this.getAnalyzeRun().getAnalyzeRunList().size();
        
        monitor.beginTask(Messages.RunningAnalyzes, workAmount);

        ArrayList<Analyze> anaList = new ArrayList<Analyze>(this
                .getAnalyzeRun().getAnalyzeRunList());

        for (int i = 0; i < anaList.size(); i++) {

            IAnalyze executableExtension = (IAnalyze) anaList.get(i)
                    .getExecutableExtension();
            monitor.subTask(Messages.Running + anaList.get(i).getName());

            AnalyzeResult res = executableExtension.execute(node,
                    new SubProgressMonitor(monitor, 1), anaList.get(i)
                            .getResultType(), anaList.get(i)
                            .getAnalyzeParameter(), anaList.get(i).getName());
            qr.getResultMap().put(anaList.get(i), res);
        }
        // Set the Query Result
        setQueryResult(qr);
        monitor.done();
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        if (getAnalyzeRun().getAnalyzeRunList().size() == 1) {
            // Add the actual TimeStamp to the AnalyzeName
            return  getTimestamp() + ": " 
                + getAnalyzeRun().getAnalyzeRunList().get(0).getName();
        }
        return "";
    }
    /**
     * {@inheritDoc}
     */
    public boolean canRerun() {
        return true;
    }
    /**
     * {@inheritDoc}
     */
    public boolean canRunInBackground() {
        return false;
    }
    /**
     * {@inheritDoc}
     */
    public ISearchResult getSearchResult() {
              
        return m_queryResult;
    }
    /**
     * 
     * @return The AnalyzeRun
     */
    public AnalyzeRun getAnalyzeRun() {
        return m_analyzeRun;
    }
    /**
     * 
     * @param analyzeRun The AnalyzeRun
     */
    public void setAnalyzeRun(AnalyzeRun analyzeRun) {
        this.m_analyzeRun = analyzeRun;
    }
}
