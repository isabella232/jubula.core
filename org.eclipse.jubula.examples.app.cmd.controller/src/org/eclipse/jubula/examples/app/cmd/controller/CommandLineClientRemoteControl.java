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
package org.eclipse.jubula.examples.app.cmd.controller;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jubula.client.cmd.controller.ClcServerFactory;
import org.eclipse.jubula.client.cmd.controller.IClcServer;

/**
 * This snippet shows the remote control ability of the commandline client.
 * Before running this code you should start the GUIdancerCMD with the option
 * "-startserver <port_number>   Wait on a port for receiving test execution data"
 * . The same <port_numer> must be used in for the
 * COMMANDLINE_CLIENT_SERVER_LISTENING_PORT
 * 
 * @author BREDEX GmbH
 */
public class CommandLineClientRemoteControl {
    /**
     * This is the port for communication with the GUIdancer commandline client
     */
    private static final int COMMANDLINE_CLIENT_SERVER_LISTENING_PORT = -1;

    /**
     * Constructor
     */
    private CommandLineClientRemoteControl() {
        // hide
    }
    
    /**
     * @param args
     *            the main args
     */
    @SuppressWarnings("nls")
    public static void main(String[] args) {
        // This code must run on the same machine as the commandline client
        // which is controlling the test execution (this does not necessarily
        // need to be the machine on which the AUT-Agent is running)
        IClcServer service = ClcServerFactory.getServer(
                COMMANDLINE_CLIENT_SERVER_LISTENING_PORT);
        // the maximum allowed time for the test suite in seconds
        int timeout = 100;
        // This is an optional map of variables which may be used during test execution
        final HashMap<String, String> testVariables = 
            new HashMap<String, String>();
        // This is the list of Testsuites to execute
        List<String> testSuiteNames = new ArrayList<String>();
        testSuiteNames.add("1.1_SIMPLE_ADDER_TEST_WITH_TEST_STEPS");
        testSuiteNames.add("1.2_SIMPLE_ADDER_TEST_USING_LIBRARY");
        
        try {
            for (String testsuite : testSuiteNames) {
                // this tells the connected commandline client to execute the given testsuite
                int result = service
                    .runTestSuite(testsuite, timeout, testVariables);
                // the result code of the test execution, 0 if everything is fine.
                System.out.println("Testuite \"" + testsuite
                        + "\" executed with result: " + result);
                
                Thread.sleep(2500);
            }
            // shuts the CLC server down
            service.shutdown();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
