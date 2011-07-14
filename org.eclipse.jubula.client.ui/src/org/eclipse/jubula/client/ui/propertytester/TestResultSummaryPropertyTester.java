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
package org.eclipse.jubula.client.ui.propertytester;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.osgi.util.NLS;

/**
 * PropertyTester for TestResultSummary.
 * 
 * @author BREDEX GmbH
 * @created Mar 5, 2009
 */
public class TestResultSummaryPropertyTester extends PropertyTester {

    /** the id of the "hasMonitoringData" property */
    public static final String HAS_MONITORING_DATA_PROP = "hasMonitoringData"; //$NON-NLS-1$

    /** the logger */
    private static final Log LOG = LogFactory
            .getLog(TestResultSummaryPropertyTester.class);

    /**
     * {@inheritDoc}
     */
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {

        if (receiver instanceof ITestResultSummaryPO) {
            ITestResultSummaryPO summary = (ITestResultSummaryPO)receiver;
            if (property.equals(HAS_MONITORING_DATA_PROP)) {
                boolean isBeingUsed = testHasMonitoringData(summary);
                boolean expectedBoolean = 
                    expectedValue instanceof Boolean ? ((Boolean)expectedValue)
                        .booleanValue() : true;
                return isBeingUsed == expectedBoolean;
            }
            LOG.warn(NLS.bind(Messages.PropertyTesterPropertyNotSupported,
                    property));
            return false;
        }
        String receiverClass = receiver != null ? receiver.getClass().getName()
                : "null"; //$NON-NLS-1$
        LOG.warn(NLS.bind(Messages.PropertyTesterTypeNotSupported,
                receiverClass));
        return false;
    }

    /**
     * 
     * @param summary
     *            the summary to check
     * @return <code>true</code> if the summary has monitoring data. Otherwise
     *         <code>false</code>.
     */
    private boolean testHasMonitoringData(ITestResultSummaryPO summary) {
        return summary.isReportWritten();
    }

}
