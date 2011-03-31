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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.businessprocess.ImportFileBP;


/**
 * @author BREDEX GmbH
 * @created 08.11.2004
 */
public class ImportProjectHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        ImportFileBP.getInstance().importFile();
        return null;
    }
}