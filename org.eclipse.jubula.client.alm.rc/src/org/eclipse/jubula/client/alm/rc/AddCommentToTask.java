/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.rc;

import org.eclipse.jubula.client.alm.core.utils.ALMAccess;
import org.eclipse.jubula.client.core.rc.commands.AbstractPostExecutionCommand;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;

/**
 * @author BREDEX GmbH
 */
public class AddCommentToTask extends AbstractPostExecutionCommand {
    /** comment key */
    private static final String COMP_SYSTEM_ALM_COMMENT_KEY = "CompSystem.alm.comment"; //$NON-NLS-1$ 
    /** task id key */
    private static final String COMP_SYSTEM_ALM_TASK_ID_KEY = "CompSystem.alm.taskID"; //$NON-NLS-1$
    /** repository name key */
    private static final String COMP_SYSTEM_ALM_REPOSITORY_NAME_KEY = "CompSystem.alm.repositoryName"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public TestErrorEvent execute() throws JBException {
        TestErrorEvent event = null;
        String repoName = getValueForParam(COMP_SYSTEM_ALM_REPOSITORY_NAME_KEY);
        String taskID = getValueForParam(COMP_SYSTEM_ALM_TASK_ID_KEY);
        String comment = getValueForParam(COMP_SYSTEM_ALM_COMMENT_KEY);

        boolean success = ALMAccess.createComment(repoName, taskID, comment);
        if (!success) {
            event = EventFactory.createActionError();
        }
        return event;
    }
}
