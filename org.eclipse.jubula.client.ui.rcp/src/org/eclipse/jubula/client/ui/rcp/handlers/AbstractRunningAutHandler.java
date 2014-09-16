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

import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.businessprocess.RunningAutBP;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created May 10, 2010
 */
public abstract class AbstractRunningAutHandler extends AbstractHandler {
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractRunningAutHandler.class);

    /**
     * @param event
     *            the execution event this handler has been triggered from
     * @param parameterKey
     *            the key for the running aut command parameter
     * @return the AutIdentifier for the requested running AUT
     */
    protected AutIdentifier getRunningAut(ExecutionEvent event,
            String parameterKey) {
        if (Job.getJobManager().find(this).length > 0) {
            return null;
        }
        Object runningAutObj = null;
        try {
            runningAutObj = event.getObjectParameterForExecution(parameterKey);
        } catch (ExecutionException ee) {
            // ignore --> check for only one running aut
            LOG.info(Messages.MissingRunningAUTParameter);
        }
        if (runningAutObj == null) {
            Collection<AutIdentifier> availableAUTs = 
                RunningAutBP.getListOfDefinedRunningAuts();
            if (availableAUTs.size() == 1) {
                runningAutObj = availableAUTs.iterator().next();
            } else {
                LOG.info(Messages.UsingFallbackFailed);
                return null;
            }
        }
        if (!(runningAutObj instanceof AutIdentifier)) {
            LOG.error(Messages.RunningAUTParameter + StringConstants.SPACE
                    + StringConstants.APOSTROPHE + runningAutObj
                    + StringConstants.APOSTROPHE + StringConstants.SPACE
                    + Messages.NotOfCorrectType + StringConstants.DOT);
            return null;
        }
        return (AutIdentifier)runningAutObj;
    }

}
