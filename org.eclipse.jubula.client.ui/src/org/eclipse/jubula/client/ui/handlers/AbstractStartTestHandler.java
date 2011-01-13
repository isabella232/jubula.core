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
package org.eclipse.jubula.client.ui.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.exception.GDException;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;


/**
 * @author BREDEX GmbH
 * @created Mar 22, 2010
 */
public abstract class AbstractStartTestHandler extends AbstractHandler {

    /**
     * run before test execution
     * 
     * @return true if preparation has been successfull, false otherwise
     */
    public static boolean prepareTestExecution() {
        if (Plugin.getDefault().getPreferenceStore().getBoolean(
                Constants.GENERATEREPORT_KEY)) {
            URL xslUrl = TestResultBP.getInstance().getXslFileURL();
            
            String htmlDir = null;
            if (xslUrl == null) {
                Plugin.getDefault().handleError(
                        new GDException("File not found: format.xsl", //$NON-NLS-1$
                                MessageIDs.E_FILE_NOT_FOUND));
                return false;
            }
            try {
                String cssFileName = TestResultBP.getInstance()
                        .getCssFileName();
                File cssFile = new File(cssFileName);
                htmlDir = cssFile.getParentFile().getAbsolutePath();
            } catch (IOException e) {
                Plugin.getDefault().handleError(
                                new GDException("Can't resolve URL of 'format.xsl' or 'reportStyle.css'.", //$NON-NLS-1$
                                MessageIDs.E_FILE_IO));
                return false;
            }
            ClientTestFactory.getClientTest().setLogPath(
                    Plugin.getDefault().getPreferenceStore().getString(
                            Constants.RESULTPATH_KEY), xslUrl, htmlDir);
            ClientTestFactory.getClientTest().setLogStyle(
                    Plugin.getDefault().getPreferenceStore().getString(
                            Constants.REPORTGENERATORSTYLE_KEY));
        } else {
            ClientTestFactory.getClientTest().setLogPath(null, null, null);
            ClientTestFactory.getClientTest().setLogStyle(null);
        }
        return true;
    }

    /**
     * init the GUI test execution part
     * 
     * @return whether initialisation has been successfull
     */
    protected boolean initTestExecution() {
        return initTestExecutionRelevantFlag() && initPauseTestExecutionState();
    }

    /**
     * @return true if init has been successfull
     */
    private boolean initPauseTestExecutionState() {
        ICommandService cmdService = (ICommandService)Plugin.getActivePart()
                .getSite().getService(ICommandService.class);
        if (cmdService != null) {
            final Command command = cmdService
                    .getCommand(CommandIDs.PAUSE_TEST_SUITE_COMMAND_ID);
            if (command != null) {
                final Display display = Plugin.getDisplay();
                ITestExecutionEventListener l = new 
                    ITestExecutionEventListener() {
                    public void endTestExecution() {
                        display.syncExec(new Runnable() {
                            public void run() {
                                State state = command
                                        .getState(RegistryToggleState.STATE_ID);
                                state.setValue(false);
                            }
                        });
                        ClientTestFactory.getClientTest()
                                .removeTestExecutionEventListener(this);
                    }

                    public void stateChanged(final TestExecutionEvent tee) {
                        display.syncExec(new Runnable() {
                            public void run() {
                                State state = command
                                        .getState(RegistryToggleState.STATE_ID);
                                boolean newToggleStateValue = tee.getState() 
                                    == TestExecutionEvent.TEST_EXEC_PAUSED;
                                state.setValue(newToggleStateValue);
                            }
                        });
                    }
                };
                ClientTestFactory.getClientTest()
                        .addTestExecutionEventListener(l);
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if flag has been successfully determined
     */
    private boolean initTestExecutionRelevantFlag() {
        int value = Plugin.getDefault().getPreferenceStore().getInt(
                Constants.TEST_EXEC_RELEVANT);

        if (value == Constants.TEST_EXECUTION_RELEVANT_YES) {
            ClientTestFactory.getClientTest().setRelevantFlag(true);
            return true;
        } else if (value == Constants.TEST_EXECUTION_RELEVANT_NO) {
            ClientTestFactory.getClientTest().setRelevantFlag(false);
            return true;
        }

        // if --> value = Constants.TEST_EXECUTION_RELEVANT_PROMPT:
        final int returnCodeYES = 256; // since Eclipse3.2 (not 0)
        final int returnCodeNO = 257; // since Eclipse3.2 (not 1)
        final int returnCodeCANCEL = -1;
        MessageDialogWithToggle dialog = new MessageDialogWithToggle(
                Plugin.getShell(),
                I18n.getString("TestExecRelevantDialog.Title"), //$NON-NLS-1$
                IconConstants.GUIDANCER_SMALLER_IMAGE,
                I18n.getString("TestExecRelevantDialog.Question"), //$NON-NLS-1$
                MessageDialog.QUESTION,
                new String[] {
                        I18n.getString("Utils.Yes"), I18n.getString("Utils.No") }, 0, //$NON-NLS-1$ //$NON-NLS-2$
                I18n.getString("Utils.remember"), false) { //$NON-NLS-1$

            /**
             * {@inheritDoc}
             */
            protected void buttonPressed(int buttonId) {
                super.buttonPressed(buttonId);
                Plugin.getDefault().getPreferenceStore().setValue(
                        Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY,
                        getToggleState());
                int val = Constants.TEST_EXECUTION_RELEVANT_PROMPT;
                if (getToggleState() && getReturnCode() == returnCodeNO) {
                    val = Constants.TEST_EXECUTION_RELEVANT_NO;
                } else if (getToggleState() && getReturnCode() 
                        == returnCodeYES) {
                    val = Constants.TEST_EXECUTION_RELEVANT_YES;
                }
                Plugin.getDefault().getPreferenceStore().setValue(
                        Constants.TEST_EXEC_RELEVANT, val);
            }
        };
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        ClientTestFactory.getClientTest().setRelevantFlag(true);
        if (dialog.getReturnCode() == returnCodeNO) {
            ClientTestFactory.getClientTest().setRelevantFlag(false);
        } else if (dialog.getReturnCode() == returnCodeCANCEL) {
            ClientTestFactory.getClientTest().setRelevantFlag(false);
            return false;
        }
        return true;
    }
}
