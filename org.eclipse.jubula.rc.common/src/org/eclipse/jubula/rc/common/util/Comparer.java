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
package org.eclipse.jubula.rc.common.util;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * Utilities to compare values.
 * 
 * @author BREDEX GmbH
 * @created 20.08.2009
 */
public class Comparer {

    /** comparison string for less */
    private static final String LESS = "less than"; //$NON-NLS-1$

    /** comparison string for less or equal */
    private static final String LESS_OR_EQUAL = "less or equal than"; //$NON-NLS-1$

    /** comparison string for equal */
    private static final String EQUAL = "equal to"; //$NON-NLS-1$

    /** comparison string for greater or equal */
    private static final String GREATER_OR_EQUAL = "greater or equal than"; //$NON-NLS-1$

    /** comparison string for greater */
    private static final String GREATER = "greater than"; //$NON-NLS-1$

    /**
     * Default constructor.
     */
    private Comparer() {
    // Nothing to be done.
    }

    /**
     * compare two double values
     * 
     * @param value1
     *            value 1
     * @param value2
     *            value 2
     * @param comparisonMethod
     *            comparison method
     */
    public static void compare(String value1, String value2,
            String comparisonMethod) throws StepExecutionException {
        try {
            Double val1 = new Double(value1);
            Double val2 = new Double(value2);
            int comparisonResult = val1.compareTo(val2);
            boolean expectedComparison = false;
            if (comparisonMethod.equals(LESS)) {
                expectedComparison = comparisonResult < 0;
            } else if (comparisonMethod.equals(LESS_OR_EQUAL)) {
                expectedComparison = comparisonResult <= 0;
            } else if (comparisonMethod.equals(EQUAL)) {
                expectedComparison = comparisonResult == 0;
            } else if (comparisonMethod.equals(GREATER_OR_EQUAL)) {
                expectedComparison = comparisonResult >= 0;
            } else if (comparisonMethod.equals(GREATER)) {
                expectedComparison = comparisonResult > 0;
            }
            if (!expectedComparison) {
                throw new StepExecutionException("Comparison failed", //$NON-NLS-1$
                        EventFactory.createVerifyFailed(StringConstants.EMPTY, "\"" + value1 //$NON-NLS-1$
                                + "\" has been expected to be " //$NON-NLS-1$
                                + comparisonMethod + " \"" + value2 //$NON-NLS-1$
                                + "\".")); //$NON-NLS-1$ 
            }
        } catch (NumberFormatException e) {
            throw new StepExecutionException(
                    "No valid Input Data", //$NON-NLS-1$
                    EventFactory.
                        createActionError(TestErrorEvent.INVALID_PARAM_VALUE));
        }
    }
}
