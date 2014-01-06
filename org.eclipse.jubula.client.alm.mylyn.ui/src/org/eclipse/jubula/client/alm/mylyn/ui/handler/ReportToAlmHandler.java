/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.handler;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO.AlmReportStatus;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;

/**
 * @author BREDEX GmbH
 */
public class ReportToAlmHandler extends AbstractSelectionBasedHandler {
    /** {@inheritDoc} */
    protected Object executeImpl(ExecutionEvent event)
        throws ExecutionException {
        Iterator<Object> iterator = getSelection().iterator();

        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (o instanceof ITestResultSummaryPO) {
                ITestResultSummaryPO summary = (ITestResultSummaryPO) o;
                if (AlmReportStatus.NOT_YET_REPORTED.equals(summary
                        .getAlmReportStatus())) {
                    reportResults(summary);
                }
            }
        }

        return null;
    }

    /**
     * @param summary
     *            the summary to report the results for
     */
    private void reportResults(ITestResultSummaryPO summary) {

    }
}
