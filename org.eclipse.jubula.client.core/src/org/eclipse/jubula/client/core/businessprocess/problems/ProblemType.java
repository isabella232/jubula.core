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
package org.eclipse.jubula.client.core.businessprocess.problems;

/**
 * @author BREDEX GmbH
 * @created 30.05.2006
 */
public enum ProblemType {
    
    /** for a CompNamesPairPo in ExecTC exists no CompType - repair : OPEN TESTCASEEDITOR*/
    REASON_NO_COMPTYPE,
    
    /** Deprecated Action used - repair : OPEN TESTCASEEDITOR */
    REASON_DEPRECATED_ACTION,
    
    /** Deprecated Component used - repair : OPEN TESTCASEEDITOR */
    REASON_DEPRECATED_COMP,
    
    /** Component used that does not exist in config.xml - repair : nothing */
    REASON_COMP_DOES_NOT_EXIST,

    /** Action used that does not exist in config.xml - repair : nothing */
    REASON_ACTION_DOES_NOT_EXIST,

    /** Parameter used that does not exist in config.xml - repair : nothing */
    REASON_PARAM_DOES_NOT_EXIST,

    /** Project used that does not exist in DB - repair : OPEN REUSED PROJECTS SETTINGS PAGE */
    REASON_PROJECT_DOES_NOT_EXIST,

    /** No Server Connection - repair : CALL CONNECT ACTION */
    REASON_CONNECTED_TO_NO_SERVER,

    /** Protected Project is currently loaded - repair : nothing */
    REASON_PROTECTED_PROJECT,
    
    /** No Project - repair : CALL CREATE PROJECT ACTION */
    REASON_NO_PROJECT,

    /** No TS in Project - repair : CALL CREATE TS ACTION */
    REASON_NO_TESTSUITE,

    /** no AUT exists - repair : OPEN PROJECT PROPERTIES EDITOR */
    REASON_NO_AUT_FOR_PROJECT_EXISTS,

    /** TS has no Aut selected - repair : OPEN TESTSUITE EDITOR */
    REASON_NO_AUT_FOR_TESTSUITE_SELECTED,

    /** AUT has no AutConfig for current server - repair : OPEN AUTEDITOR */
    REASON_NO_AUTCONFIG_FOR_SERVER_EXIST,

    /** AutConfig is incomplete - repair : OPEN AUTCONFIG EDITOR */
    REASON_NOJAR_FOR_AUTCONFIG,

    /**  test suite has no TCs - repair : nothing or help window, what all could be wrong */
    REASON_EMPTY_TESTSUITE,

    /** test suite has incomplete TD - repair : nothing or help window, what all could be wrong */
    REASON_TD_INCOMPLETE,

    /** test suite has incomplete OM - repair : nothing or help window, what all could be wrong */
    REASON_OM_INCOMPLETE,

    /** No Server defined in Workspace Preferences - repair : Open Server Preferences */
    REASON_NO_SERVER_DEFINED,

    /** No Server defined in AUT config - repair : Open AUT config */
    REASON_NOSERVER_FOR_AUTCONFIG,

    /**
     * test suite has reference to missing SpecTestCase - repair : Open TC editor
     * if parent is a TC (not a TS). Otherwise, nothing.
     */
    REASON_MISSING_SPEC_TC,
    
    /** Reused project is missing a language used in the current project - repair : nothing*/
    REASON_REUSED_PROJECT_MISSING_LANG,
    
    /** An ExecTestase has unused Test Data  */
    REASON_UNUSED_TESTDATA,
    
    /** external reasons for failing */ 
    EXTERNAL
}