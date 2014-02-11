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
package org.eclipse.jubula.rc.javafx.commands;

import org.eclipse.jubula.rc.common.commands.AbstractCapTestCommand;
import org.eclipse.jubula.rc.common.exception.ComponentNotFoundException;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;

/**
 * This class gets an message with ICommand action parameter triples. It invokes
 * the implementation class and executes the method. Then it creates a
 * <code>CAPTestResponseMessage</code> and sends it back to the client. The
 * <code>CAPTestResponseMessage</code> contains an error event only if the test
 * step fails, due to a problem prior to or during the execution of the
 * implementation class action method.
 *
 * @author BREDEX GmbH
 * @created 4.11.2013
 *
 */
public class CAPTestCommand extends AbstractCapTestCommand {

    /**
     * {@inheritDoc}
     */
    protected Object findComponent(IComponentIdentifier ci, int timeout)
        throws ComponentNotFoundException, IllegalArgumentException {

        return ComponentHandler.findComponent(ci, true, timeout);
    }

}