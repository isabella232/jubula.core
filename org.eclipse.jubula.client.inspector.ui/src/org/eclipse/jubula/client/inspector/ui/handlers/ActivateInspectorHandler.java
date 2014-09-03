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
package org.eclipse.jubula.client.inspector.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.inspector.ui.commands.ActivateInspectorResponseCommand;
import org.eclipse.jubula.client.inspector.ui.i18n.Messages;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.communication.message.ActivateInspectorMessage;
import org.eclipse.jubula.tools.exception.CommunicationException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.registration.AutIdentifier;


/**
 * Handler for activating the AUT Inspector.
 *
 * @author BREDEX GmbH
 * @created Jun 11, 2009
 */
public class ActivateInspectorHandler extends AbstractHandler {

    /** id for the AUT ID parameter */
    public static final String AUT_ID = 
        "org.eclipse.jubula.client.inspector.ui.commands.parameter.activateInspector.autId"; //$NON-NLS-1$

    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final AutIdentifier autId = 
            (AutIdentifier)event.getObjectParameterForExecution(AUT_ID);
        final String jobName = Messages.UIJobActivateInspector;
        Job activateInspectorJob = new Job(jobName) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                ActivateInspectorMessage message = 
                    new ActivateInspectorMessage();
                try {
                    AUTConnection.getInstance().connectToAut(
                            autId, new NullProgressMonitor());
                    AUTConnection.getInstance().request(message, 
                            new ActivateInspectorResponseCommand(), 5000);
                } catch (NotConnectedException nce) {
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.E_NO_AUT_CONNECTION_ERROR);
                } catch (ConnectionException ce) {
                    ErrorHandlingUtil.createMessageDialog(ce, null, null);
                } catch (CommunicationException ce) {
                    ErrorHandlingUtil.createMessageDialog(ce, null, null);
                }
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        activateInspectorJob.setSystem(true);
        JobUtils.executeJob(activateInspectorJob, null);

        return null;
    }

}
