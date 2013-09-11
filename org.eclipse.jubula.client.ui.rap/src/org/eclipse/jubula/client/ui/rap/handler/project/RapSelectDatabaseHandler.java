/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rap.handler.project;

import org.eclipse.jubula.client.ui.handlers.project.AbstractSelectDatabaseHandler;

/**
 * @author BREDEX GmbH
 * @created Oct 05, 2011
 */
public class RapSelectDatabaseHandler extends AbstractSelectDatabaseHandler {

    @Override
    protected void clearClient() {
        // no-op
    }

    @Override
    protected void writeLineToConsole(String line) {
        // no-op
    }

}
