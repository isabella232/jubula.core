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
 * The message to send information about opening the recordedActionDialog
 * and the checkmode status . <br>
 * 
 * @author BREDEX GmbH
 * @created 05.10.2004
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
public class ServerShowObservConsoleMessage extends Message {

    /** opens the recordedAction dialog */
    public static final int ACT_SHOW_ACTION_SHELL = 1;
    
    /** closes the recordedAction dialog */
    public static final int ACT_CLOSE_ACTION_SHELL = 2;

    /** static version */
    private static final double VERSION = 1.0;

/* DOTNETDECLARE:BEGIN */

    /**
     * transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;
    
    /** 
     * action/dialog that should be executed
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__action") */
    public int m_action = 0;
    
    /** 
     * checkmode on/off
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__check") */
    public boolean m_check = false;
    
    /** true if recorded actions dialog should be open, false otherwise */
    /** @attribute System.Xml.Serialization.XmlElement("m__dialogOpen") */
    public boolean m_dialogOpen;


/* DOTNETDECLARE:END */
    
    /**
     * empty constructor for serialisation
     */
    public ServerShowObservConsoleMessage() {
        // do nothing
    }
    

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.SERVER_SHOW_OBSERV_CONSOLE_COMMAND;
    }
    
    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }
    
    /** if dialog should be opened/closed 
     * @return int
     */
    public int getAction() {
        return m_action;
    }

    /**
     * if dialog should be opened/closed
     * @param action int
     */
    public void setAction(int action) {
        m_action = action;
    }
    
    /** checkmode on/off
     * @return boolean
     */
    public boolean getCheck() {
        return m_check;
    }

    /**
     * checkmode on/off
     * @param check boolean
     */
    public void setCheck(boolean check) {
        m_check = check;
    }
    
    /**
     * @return true if recorded actions dialog should be open, false otherwise
     */
    public boolean getRecordDialogOpen() {
        return m_dialogOpen;
    }

    /**
     * @param dialogOpen set state of recorded actions dialog
     */
    public void setRecordDialogOpen(boolean dialogOpen) {
        m_dialogOpen = dialogOpen;
    }
}
