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
package org.eclipse.jubula.client.core.businessprocess;

/**
 * @author BREDEX GmbH
 * @created 07.10.2004
 */
public class TestExecutionEvent {
    /**
     * Signals that TestExecution is started
     */
    public static final int TEST_EXEC_START = 1;
    
    /**
     * Signals that TestExecution is stopped
     */
    public static final int TEST_EXEC_STOP = 2;
    
    /**
     * Signals that TestExecution fails
     */
    public static final int TEST_EXEC_FAILED = 3;
    
    /**
     * Signals that the cap has an error
     */
    public static final int TEST_EXEC_ERROR = 4;
    
    /**
     * Signals that the cap is ok
     */
    public static final int TEST_EXEC_OK = 5;
    /**
     * The result tree is ready for showing
     */
    public static final int TEST_EXEC_RESULT_TREE_READY = 6;
    /**
     * Test execution ready
     */
    public static final int TEST_EXEC_FINISHED = 7;
    /**
     * Signals that TestExecution fails, when the component name is wrong.
     */
    public static final int TEST_EXEC_COMPONENT_FAILED = 8;

    /**
     * Signals that TestExecution should be paused.
     */
    public static final int TEST_EXEC_PAUSED = 9;

    /**
     * Signals that TestExecution resumed.
     */
    public static final int TEST_EXEC_RESUMED = 10;

    /**
     * Signals that TestExecution updated.
     */
    public static final int TEST_EXEC_UPDATE = 11;
    
    /**
     * Signals that TestExecution updated.
     */
    public static final int TEST_EXEC_RESTART = 14;
    
//    /** Signals that a test runned with incomplete data failed */
//    public static final int TEST_RUN_INCOMPLETE_FAILED = 12;
    
    /** Signals that a test runned with incomplete data failed  */
    public static final int TEST_RUN_INCOMPLETE_TESTDATA_ERROR = 12;

    /** Signals that a test runned with incomplete object mapping failed */
    public static final int TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR = 13;
    
    /**
     * The state of TestExecution
     */
    private int m_state;
    
    /**
     * occured Exception
     */
    private Exception m_exception;
    
    /**
     * Constructor that sets the state
     * @param state The state of TestExecution
     */
    public TestExecutionEvent(int state) {
        m_state = state;
    }

    /**
     * Constructor that sets the state
     * @param state The state of TestExecution
     * @param e Exception that occured
     */
    public TestExecutionEvent(int state, Exception e) {
        m_state = state;
        m_exception = e;
    }

    
    /**
     * Gets the state
     * @return the state
     */
    public int getState() {
        return m_state;
    }

    /**
     * @return occured Exception
     */
    public Exception getException() {
        return m_exception;
    }
}
