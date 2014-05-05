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
package org.eclipse.jubula.tools.utils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.constants.TestexecConstants;

/**
 * @author BREDEX GmbH
 * @created Mai 5, 2014
 */
public class TestexecUtils {
    
    /** private constructor */
    private TestexecUtils() {
        // utility class
    }
    
    /**
     * @param noRunMode String noRun option mode
     * @param step current step of noRun execution
     * @return true is no run execution must be finished
     * return false if test run without no-run option
     * or the last step of no run execution is not jet reached
     */
    public static boolean isExecutionFinished(String noRunMode,
            TestexecConstants.NoRunSteps step) {
        if (StringUtils.isEmpty(noRunMode)) {
            return false;
        }
        return noRunMode.equals(step.getStepValue());
    }
    
}
