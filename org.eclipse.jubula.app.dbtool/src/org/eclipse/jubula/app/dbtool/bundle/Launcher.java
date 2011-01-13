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
package org.eclipse.jubula.app.dbtool.bundle;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jubula.app.dbtool.core.DBToolClient;
import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;

/**
 * @author BREDEX GmbH
 * @created Mar 11, 2009
 */
public final class Launcher implements IApplication {
    /**
     * {@inheritDoc}
     */
    public Object start(IApplicationContext context) throws Exception {
        String[] args = (String[])context.getArguments().get(
                IApplicationContext.APPLICATION_ARGS);
        if (args == null) {
            args = new String[0];
        }
        // Run Test Suites
        AbstractCmdlineClient client = DBToolClient.getInstance();
        int exitCode = client.run(args);

        // Return a value that indicates test results
        return exitCode;
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        // nothing yet
    }

}
