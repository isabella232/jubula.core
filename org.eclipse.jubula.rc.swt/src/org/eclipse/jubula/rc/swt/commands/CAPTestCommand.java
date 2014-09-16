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
package org.eclipse.jubula.rc.swt.commands;

import org.eclipse.jubula.rc.common.commands.AbstractCapTestCommand;
import org.eclipse.jubula.rc.common.exception.ComponentNotFoundException;
import org.eclipse.jubula.rc.swt.listener.ComponentHandler;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;


/**
 * This class gets an message with ICommand action parameter triples. It invokes
 * the implementation class and executes the method. Then it creates a
 * <code>CAPTestResponseMessage</code> and sends it back to the client. The
 * <code>CAPTestResponseMessage</code> contains an error event only if the
 * test step fails, due to a problem prior to or during the execution of the
 * implementation class action method.
 * 
 * @author BREDEX GmbH
 * @created 02.09.2004
 * 
 */
public class CAPTestCommand extends AbstractCapTestCommand {

    /** the found comp */
    private Object m_comp;

    /**
     * {@inheritDoc}
     */
    protected Object findComponent(final IComponentIdentifier ci, 
        final int timeout) throws ComponentNotFoundException, 
        IllegalArgumentException {

        m_comp = ComponentHandler.findComponent(ci, true, timeout);
        return m_comp;
    }

}