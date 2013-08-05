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

import org.eclipse.jubula.client.core.rc.commands.AbstractPostExecutionCommand;

/** @author BREDEX GmbH */
public abstract class AbstractALMExecutionCommand 
    extends AbstractPostExecutionCommand {
    /** comment key */
    public static final String ALM_COMMENT_KEY = "CompSystem.alm.comment"; //$NON-NLS-1$
    /** task id key */
    public static final String ALM_TASK_ID_KEY = "CompSystem.alm.taskID"; //$NON-NLS-1$
    /** repository name key */
    public static final String ALM_REPOSITORY_NAME_KEY = "CompSystem.alm.repositoryName"; //$NON-NLS-1$
    /** product id key */
    public static final String ALM_TASK_PRODUCT_KEY = "CompSystem.alm.taskProduct"; //$NON-NLS-1$
    /** summary id key */
    public static final String ALM_TASK_SUMMARY_KEY = "CompSystem.alm.taskSummary"; //$NON-NLS-1$
    /** description id key */
    public static final String ALM_TASK_DESCRIPTION_KEY = "CompSystem.alm.taskDescription"; //$NON-NLS-1$
}