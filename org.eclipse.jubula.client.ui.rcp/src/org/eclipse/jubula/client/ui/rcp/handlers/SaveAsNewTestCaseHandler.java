/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;

/**
 * @author Markus Tiede
 * @since 1.2
 */
public class SaveAsNewTestCaseHandler extends AbstractRefactorHandler {
    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        String newTestCaseName = getNewTestCaseName(event);
        return null;
    }
}
