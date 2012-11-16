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

import java.util.List;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.commands.AbstractSendAUTListOfSupportedComponentsCommand;
import org.eclipse.jubula.rc.common.exception.UnsupportedComponentException;
import org.eclipse.jubula.rc.common.implclasses.IComponentFactory;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.listener.ComponentHandler;
import org.eclipse.jubula.tools.xml.businessmodell.ConcreteComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for QueryAUTForComponentsMessage. <br>
 * 
 * execute() registers all components sent by the
 * <code>QueryAUTForComponentsMessage</code> in the AUT server and returns an
 * <code>AUTComponentsMessage</code> containing all components of the AUT
 * which are supported.
 * 
 * timeout() should never be called. <br>
 * 
 * @author BREDEX GmbH
 * @created 04.10.2004
 * 
 */
public class SendAUTListOfSupportedComponentsCommand  
    extends AbstractSendAUTListOfSupportedComponentsCommand {

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
        SendAUTListOfSupportedComponentsCommand.class);
    /** list of comp IDs */
    private List m_componentIds;
    /** concrete component */
    private ConcreteComponent m_concrete;
    
    /**
     * {@inheritDoc}
     */
    protected void addToHierarchy(IComponentFactory factory, 
        ConcreteComponent c, String technicalName) 
        throws UnsupportedComponentException {
        
        ComponentHandler.addToHierarchy(factory, c.getComponentClass(), 
            technicalName);
    }
    
    /**
     * {@inheritDoc}
     */
    protected List addComponentID(List componentIds, 
        ConcreteComponent concrete) {
        
        m_componentIds = componentIds;
        m_concrete = concrete;
        ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay()
            .syncExec(new Runnable() {
                public void run() {
                    try {
                        m_componentIds.add(createIdentifier(m_concrete));
                    } catch (UnsupportedComponentException e) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
            });
        return m_componentIds;
    }
}