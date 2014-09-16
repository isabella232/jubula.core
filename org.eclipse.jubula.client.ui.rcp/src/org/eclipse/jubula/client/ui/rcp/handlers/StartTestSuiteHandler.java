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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ChooseTestSuiteBP;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;


/**
 * @author BREDEX GmbH
 * @created Feb 1, 2010
 */
public class StartTestSuiteHandler extends AbstractStartTestHandler 
        implements IElementUpdater {

    /** ID of command parameter for Test Suite to start */
    public static final String TEST_SUITE_TO_START = 
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestSuiteCommand.parameter.testSuiteToStart"; //$NON-NLS-1$
    
    /** ID of command parameter for Running AUT to test */
    public static final String RUNNING_AUT = 
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestSuiteCommand.parameter.runningAut"; //$NON-NLS-1$

    /** ID of command state for most recently started Test Suite */
    public static final String LAST_STARTED_TEST_SUITE =
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestSuiteCommand.state.lastStartedSuite"; //$NON-NLS-1$

    /** ID of command state for most recently tested Running AUT */
    public static final String LAST_TESTED_RUNNING_AUT =
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestSuiteCommand.state.lastRunningAut"; //$NON-NLS-1$
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(final ExecutionEvent event) {
        Object testSuiteToStartObj = null;
        Object runningAutObj = null;
        ITestSuitePO testSuiteToStart = null;
        AutIdentifier runningAut = null;
        try {
            testSuiteToStartObj = 
                event.getObjectParameterForExecution(TEST_SUITE_TO_START);
            runningAutObj = 
                event.getObjectParameterForExecution(RUNNING_AUT);
        } catch (ExecutionException ee) {
            // Parameters could not be found or parsed.
            // Not a problem. We'll try later to use the current command
            // state to find out which Test Suite to start.
        }
        
        State lastStartedTestSuiteState = 
            event.getCommand().getState(LAST_STARTED_TEST_SUITE);
        State lastTestedRunningAutState = 
            event.getCommand().getState(LAST_TESTED_RUNNING_AUT);
        
        if (testSuiteToStartObj instanceof ITestSuitePO
                && runningAutObj instanceof AutIdentifier) {
            testSuiteToStart = (ITestSuitePO)testSuiteToStartObj;
            runningAut = (AutIdentifier)runningAutObj;
            
        } else {
            if (lastStartedTestSuiteState != null
                    && lastTestedRunningAutState != null) {

                Object testSuiteStateValue = 
                    lastStartedTestSuiteState.getValue();
                Object runningAutStateValue = 
                    lastTestedRunningAutState.getValue();
                if (testSuiteStateValue instanceof String
                        && runningAutStateValue instanceof AutIdentifier) {
                    String testSuiteGUIDtoStart = (String) testSuiteStateValue;
                    List<ITestSuitePO> listOfTS = TestSuiteBP
                            .getListOfTestSuites();
                    for (ITestSuitePO ts : listOfTS) {
                        if (testSuiteGUIDtoStart.equals(ts.getGuid())) {
                            testSuiteToStart = ts;
                            break;
                        }
                    }
                    runningAut = (AutIdentifier) runningAutStateValue;
                }
            }
        }

        if (testSuiteToStart != null && runningAut != null
                && initTestExecution(event)) {
            final boolean autoScreenshots = Plugin.getDefault()
                    .getPreferenceStore().getBoolean(
                            Constants.AUTO_SCREENSHOT_KEY);
            ChooseTestSuiteBP.getInstance().runTestSuite(testSuiteToStart,
                    runningAut, autoScreenshots);

            // Update command state
            if (lastStartedTestSuiteState != null 
                    && lastTestedRunningAutState != null) {
                lastStartedTestSuiteState.setValue(testSuiteToStart.getGuid());
                lastTestedRunningAutState.setValue(runningAut);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void updateElement(UIElement element, Map parameters) {
        boolean check = false;
        Object testSuiteToStart = parameters.get(TEST_SUITE_TO_START);
        Object autToUse = parameters.get(RUNNING_AUT);
        ChooseTestSuiteBP ctsBP = ChooseTestSuiteBP.getInstance();

        ITestSuitePO lastUsedTestSuite = ctsBP.getLastUsedTestSuite();
        if (lastUsedTestSuite != null
                && lastUsedTestSuite.getId().toString()
                        .equals(testSuiteToStart)) {
            AutIdentifier lastUsedAUT = ctsBP.getLastUsedAUT();
            if (lastUsedAUT != null
                    && lastUsedAUT.getExecutableName().equals(autToUse)) {
                check = true;
            }
        }
        element.setChecked(check);
    }
}
