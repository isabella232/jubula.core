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
package org.eclipse.jubula.client.cmd.utils;

import org.eclipse.jubula.client.cmd.i18n.Messages;

/**
 * Util Interface for I18N
 *
 * @author BREDEX GmbH
 * @created Feb 26, 2007
 */
public interface I18nUtils {

    /** Constant for the String "Test Suite" */
    public static final String TESTSUITE = Messages.I18nUtilsTestSuite;
    
    /** Constant for the String "Event Handler" */
    public static final String EVENTHANDLER = Messages.I18nUtilsEventHandler;
    
    /** Constant for the String "Test Case" */
    public static final String TESTCASE = Messages.I18nUtilsTestCase;
    
    /** Constant for the String "Step" */
    public static final String STEP = Messages.I18nUtilsStep;
    
    /** Constant for the String "Retrying Step" */
    public static final String RETRYSTEP = Messages.I18nUtilsRetryStep;

    /** Constant for the Separator  */
    public static final String SEPARATOR = Messages.I18nUtilsSeparator;
    
}
