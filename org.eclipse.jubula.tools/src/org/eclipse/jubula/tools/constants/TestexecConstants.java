/*******************************************************************************
 * Copyright (c) 2004, 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.tools.constants;

/**
 * @author BREDEX GmbH
 * @created April 30, 2014
 */
public interface TestexecConstants {
    /** exit code in case of invalid options */
    public static final int INVALID_VALUE = -2;
    
    /** Constants for values of the run-option modes (introductory steps before the test execution) */
    public static enum NoRunSteps {
        /** Constant for a no-run mode to execute up to Connect to the AUT Agent step (inclusive)*/
        CAA("caa", "the connection to the AUT Agent"), //$NON-NLS-1$ //$NON-NLS-2$
        /** Constant for a no-run mode to execute up to Connect to the database step (inclusive)*/
        CDB("cdb", "the connection to the database"), //$NON-NLS-1$ //$NON-NLS-2$
        /** Constant for a no-run mode to execute up to Load the project step (inclusive)*/
        LP("lp", "the project loading"), //$NON-NLS-1$ //$NON-NLS-2$
        /** Constant for a no-run mode to execute up to Check test completeness step (inclusive)*/
        CC("cc", "the completeness check"), //$NON-NLS-1$ //$NON-NLS-2$
        /** Constant for a no-run mode to execute up to Start the AUT step (inclusive)*/
        SA("sa", "the AUT start"), //$NON-NLS-1$ //$NON-NLS-2$
        /** Constant for a no-run mode to execute up to Prepare test execution step (inclusive)*/
        PTE("pte", "the preparation of the test execution:" //$NON-NLS-1$ //$NON-NLS-2$
                + " clear external data (e.g. the caches)"), //$NON-NLS-1$
        /** Constant for a no-run mode to execute up to Connect to the AUT step (inclusive)*/
        CA("ca", "connection to the AUT"), //$NON-NLS-1$ //$NON-NLS-2$
        /** Constant for a no-run mode to execute up to Resolve predefined variables step (inclusive)*/
        RPV("rpv", "resolution of the pre-defined variables"), //$NON-NLS-1$ //$NON-NLS-2$
        /** Constant for a no-run mode to execute up to Build test execution tree step (inclusive)*/
        BT("bt", "the step to build the test execution tree"); //$NON-NLS-1$ //$NON-NLS-2$
        
        /** the value (shortcut) of the test execution step */
        private String m_stepValue;
        
        /** the description (shortcut) of the test execution step */
        private String m_description;
        
        /**
         * the private constructor
         * @param stepValue the value (shortcut) of the test execution step
         * @param description the description (shortcut) of the test execution step
         */
        private NoRunSteps (String stepValue, String description) {
            m_stepValue = stepValue;
            m_description = description;
        }
        
        /**returns the value (shortcut) of the test execution step
         * @return String the value
         */
        public String getStepValue() {
            return m_stepValue;
        }
        
        /**
         * @return String the description of the test execution step
         */
        public String getDescription() {
            return m_description;
        }
        
    }
}
