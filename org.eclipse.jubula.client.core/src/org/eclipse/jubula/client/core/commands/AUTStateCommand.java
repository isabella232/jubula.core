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
package org.eclipse.jubula.client.core.commands;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.IAUTInfoListener;
import org.eclipse.jubula.client.core.MessageFactory;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.AUTStateMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.communication.message.SendCompSystemI18nMessage;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.exception.CommunicationException;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.xml.businessprocess.ProfileBuilder;


/**
 * The command object for the AUTStateMessage, which is send by the AUTServer
 * reporting the result of the AUTSwingStartMessage. <br>
 * 
 * THe execute() - methods notifies the listeners, returns always null.
 * 
 * @author BREDEX GmbH
 * @created 12.08.2004
 * 
 */
public class AUTStateCommand implements ICommand {
    /** the logger */
    private static Log log = LogFactory.getLog(AUTStateCommand.class);
    
    // HERE after exhibition: remove TIMEOUT, it's not used any more
    // 500000 is useful for RCP AUTs
    /** timeout for requesting all components from AUT */
    private static final int TIMEOUT = 500000;

    
    /** the message */
    private AUTStateMessage m_message;
    
    /**
     * Implementation of <code>IAUTInfoListener</code>.
     */
    static class MyAUTInfoListener implements IAUTInfoListener {
        /**
         * {@inheritDoc}
         */
        public void error(int reason) {
            switch (reason) {
                case IAUTInfoListener.ERROR_TIMEOUT:
                    log.error("timeout occured getting " + //$NON-NLS-1$
                            "all components from AUT"); //$NON-NLS-1$
                    break;
                case IAUTInfoListener.ERROR_COMMUNICATION:
                    log.error("could not request " + //$NON-NLS-1$
                            "components from AUT"); //$NON-NLS-1$
                    break;
                default: 
                    log.fatal("unknown error reason " + //$NON-NLS-1$
                        "getting all components from AUT: " //$NON-NLS-1$
                        + String.valueOf(reason));
            }
            
        }
    }
    
    /**
     * default constructor
     */
    public AUTStateCommand() {
        super();
    }

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
        m_message = (AUTStateMessage)message;
    }
    
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        // HERE after exhibition: use execute() above
        // listener processing the component info
        AUTStartedCommand callback = new AUTStartedCommand(
            new MyAUTInfoListener());
        callback.setStateMessage(m_message);
        try {
            SendAUTListOfSupportedComponentsMessage message = 
                MessageFactory.getSendAUTListOfSupportedComponentsMessage();
            // Send the supported components and their implementation classes
            // to the AUT server to get registered.
            CompSystem compSystem = ComponentBuilder.getInstance()
                .getCompSystem();
            
            List components = compSystem.getComponents(TestExecution
                .getInstance().getConnectedAut().getToolkit(), true);
            message.setComponents(components);
            message.setProfile(ProfileBuilder.getActiveProfile());
            AUTConnection.getInstance().request(message, callback, TIMEOUT);
            // Send RecourceBundle to AUT server
            SendCompSystemI18nMessage i18nMessage =
                new SendCompSystemI18nMessage();
            i18nMessage.setResourceBundles(CompSystemI18n.bundlesToString());
            try {
                AUTConnection.getInstance().send(i18nMessage);
            } catch (CommunicationException ce) {
                log.fatal("communication error while setting Resource Bundle", //$NON-NLS-1$
                        ce); 
            }
        } catch (CommunicationException bce) {
            log.fatal("communication error while getting all components from AUT", //$NON-NLS-1$
                bce);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.info("timeout expired"); //$NON-NLS-1$
    }
}
