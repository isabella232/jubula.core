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
package org.eclipse.jubula.communication.message;

import org.eclipse.jubula.tools.constants.CommandConstants;



/**
 * The message to send all supported and currently instantiated components of
 * the AUT. <br>
 * 
 * @author BREDEX GmbH
 * @created 05.10.2004
 *
 *
 *
 *
 */

/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 *
 * @attribute System.Serializable()
 */
public class ServerShowDialogResponseMessage extends Message {

    /** static version */
    private static final double VERSION = 1.0;

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;
    
    /** if the dialog is opened or closed */
    /** @attribute System.Xml.Serialization.XmlElement("m__open") */
    public boolean m_open;
    
    /** normal recordlistener or checkmode*/
    /** @attribute System.Xml.Serialization.XmlElement("m__mode") */
    public int m_mode = ChangeAUTModeMessage.CHECK_MODE;
    
    /** true if closing of check dialog is caused by user-action on dialog-buttons
     *  false if closing is caused by guidancer-client*/
    /** @attribute System.Xml.Serialization.XmlElement("m__belongsToDialog") */
    public boolean m_belongsToDialog = false;

/* DOTNETDECLARE:END */
    
    /**
     * Constructor
     */
    public ServerShowDialogResponseMessage() {
        // only for serialisation
    }
    
    /**
     * constructor
     * @param open true if the dialog opens, false otherwise.
     */
    public ServerShowDialogResponseMessage(boolean open) {
        m_open = open;
    }
    
    /**
     * constructor
     * @param open true if the dialog opens, false otherwise.
     * @param mode checkmode if checkmode is on, recordmode otherwise.
     */
    public ServerShowDialogResponseMessage(boolean open, int mode) {
        m_open = open;
        m_mode = mode;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.SERVER_SHOW_DIALOG_RESULT_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

    /**
     * @return the open
     */
    public boolean isOpen() {
        return m_open;
    }
    
    /**
     * @return the mode
     */
    public int getMode() {
        return m_mode;
    }
    
    /**
     * @param mode int
     */
    public void setMode(int mode) {
        m_mode = mode;
    }
    
    /**
     * @return true if closing of check dialog is caused by user-action on dialog-buttons
     *  false if closing is caused by guidancer-client
     */
    public boolean belongsToDialog() {
        return m_belongsToDialog;
    }
    
    /**
     * @param belongsToDialog boolean
     * true if closing of check dialog is caused by user-action on dialog-buttons
     *  false if closing is caused by guidancer-client
     */
    public void setBelongsToDialog(boolean belongsToDialog) {
        m_belongsToDialog = belongsToDialog;
    }
}
