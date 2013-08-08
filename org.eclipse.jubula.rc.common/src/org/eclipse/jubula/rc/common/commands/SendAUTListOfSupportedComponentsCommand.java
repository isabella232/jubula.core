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
package org.eclipse.jubula.rc.common.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.AUTStartStateMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.ConcreteComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for QueryAUTForComponentsMessage. <br>
 * execute() registers all components sent by the
 * <code>QueryAUTForComponentsMessage</code> in the AUT server and returns an
 * <code>AUTComponentsMessage</code> containing all components of the AUT
 * which are supported.
 * timeout() should never be called. <br>
 * @author BREDEX GmbH
 * @created 02.01.2007
 * 
 */
public final class SendAUTListOfSupportedComponentsCommand 
    implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
        SendAUTListOfSupportedComponentsCommand.class);
    /** the (empty) message */
    private SendAUTListOfSupportedComponentsMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (SendAUTListOfSupportedComponentsMessage)message;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        log.info("Entering method " + getClass().getName() + ".execute()."); //$NON-NLS-1$ //$NON-NLS-2$
        List componentIds = new ArrayList();
        // Register the supported components and their implementation
        // classes.
        AUTServerConfiguration.getInstance().setProfile(m_message.getProfile());
        for (Iterator it = m_message.getComponents().iterator(); 
            it.hasNext();) {
            
            Component component = (Component)it.next();
            if (!component.isConcrete()) {
                // only handle concrete components on server side
                continue;
            }
            ConcreteComponent concrete = (ConcreteComponent)component;
            
            try {
                String testerClass = concrete.getTesterClass();
                String componentClass = concrete.getComponentClass();
                if (!(StringUtils.isEmpty(testerClass) 
                        && StringUtils.isEmpty(componentClass))) {
                    AUTServerConfiguration.getInstance().registerComponent(
                            concrete);
                }
                
            } catch (IllegalArgumentException e) {
                log.error("An error occurred while registering a component.", e); //$NON-NLS-1$
            }
        }

        log.info("Exiting method " + getClass().getName() + ".execute()."); //$NON-NLS-1$ //$NON-NLS-2$
        return new AUTStartStateMessage(componentIds);
    }

    /** 
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}