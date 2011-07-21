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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jubula.communication.Communicator;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.ServerShowDialogResponseMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.tools.exception.CommunicationException;


/**
 * The command object for ChangeAUTModeMessage. <br>
 * The execute() method enables the <code>mode</code> and returns a
 * AUTModeChangedMessage.
 * 
 * @author BREDEX GmbH
 * @created 23.08.2004
 * 
 */
public class ShowDialogResultCommand implements ICommand {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ShowDialogResultCommand.class);

    /** the message */
    private ServerShowDialogResponseMessage m_message;
    
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
        m_message = (ServerShowDialogResponseMessage) message;

    }

    /**
     * Changes the mode aof the AUTServer to the mode taken from the message.
     * Returns an AUTModeChangedMessage with the new mode.
     * 
     * {@inheritDoc}
     */
    public Message execute() {
        AUTServer.getInstance().setObservingDialogOpen(m_message.isOpen());
        if (m_message.belongsToDialog()) {
            changeCheckModeState(m_message.getMode());
        } else {
            changeCheckModeState(AUTServer.getInstance().getMode());
        }        
        
        return null;
    }
    
    /**
     * change CheckModeState
     * @param mode int
     */
    private void changeCheckModeState(int mode) {
        ChangeAUTModeMessage msg = new ChangeAUTModeMessage();
        msg.setMode(mode);
        msg.setKey(AUTServerConfiguration.getInstance().getKey());
        msg.setKeyModifier(
                AUTServerConfiguration.getInstance().getKeyMod());
        msg.setKey2(AUTServerConfiguration.getInstance().getKey2());
        msg.setKey2Modifier(
                AUTServerConfiguration.getInstance().getKey2Mod());
        msg.setCheckModeKey(AUTServerConfiguration.getInstance()
                .getCheckModeKey());
        msg.setCheckModeKeyModifier(
                AUTServerConfiguration.getInstance().getCheckModeKeyMod());
        msg.setCheckCompKey(AUTServerConfiguration.getInstance()
                .getCheckCompKey());
        msg.setCheckCompKeyModifier(
                AUTServerConfiguration.getInstance().getCheckCompKeyMod());
        
        msg.setSingleLineTrigger(
                AUTServerConfiguration.getInstance().getSingleLineTrigger());
        msg.setMultiLineTrigger(
                AUTServerConfiguration.getInstance().getMultiLineTrigger());
        
        ChangeAUTModeCommand cmd = new ChangeAUTModeCommand();
        cmd.setMessage(msg);
        try {
            Communicator clientCommunicator =
                AUTServer.getInstance().getCommunicator();
            if (clientCommunicator != null 
                    && clientCommunicator.getConnection() != null) {
                AUTServer.getInstance().getCommunicator().send(
                        cmd.execute());
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }

    /** 
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}
