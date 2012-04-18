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
package org.eclipse.jubula.client.analyze.definition;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.analyze.internal.AnalyzeParameter;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;


/**
 * @author volker
 */
public interface IAnalyze {

    /**
     * executes the Analyze
     * @param obj2analyze 
     *            The Object that is going to be analyzed
     * @param resultType
     *            The ResultType of this Analyze
     * @param monitor
     *            The monitor
     * @param param
     *            A Map which includes the Parameters
     *            of this Analyze
     * @param analyzeName 
     *            The name of the Analyze. It is used for the ProgressMonitor
     * @param event
     *            the execution event which triggered this analyze
     * @return the AnalyzeResult
     */
    public AnalyzeResult execute(Object obj2analyze, IProgressMonitor monitor,
            String resultType, List<AnalyzeParameter> param,
            String analyzeName, ExecutionEvent event);
}
