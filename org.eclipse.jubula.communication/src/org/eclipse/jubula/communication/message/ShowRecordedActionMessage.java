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
 * message to send recorded Action name and information if CAP is recorded.
 *
 * @author BREDEX GmbH
 * @created 27.08.2004
 */

/**
 * The @-attribute comments are configuration attributes for the .NET XML
 * serializer. They are not needed by the native Java classes. They are
 * defined here because the classes are shared on source code level.
 * Due to the way the attributes are set, the property variables need to be
 * public. Since these are pure data carrying properties this is acceptable.
 * 
 * @attribute System.Serializable()
 * */
public class ShowRecordedActionMessage extends Message {
    
    /**
     * Static version
     */
    private static final double VERSION = 1.0;
    
/* DOTNETDECLARE:BEGIN */

    /**
     * Transmitted version of this message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;

    /** if the action is recorded */
    /** @attribute System.Xml.Serialization.XmlElement("m__recorded") */
    public boolean m_recorded;
    
    /** name of recorded action */
    /** @attribute System.Xml.Serialization.XmlElement("m__recAction") */
    public String m_recAction;
    
    /** if additional Message exists */
    /** @attribute System.Xml.Serialization.XmlElement("m__hasExtraMsg") */
    public boolean m_hasExtraMsg;
    
    /** additional Message */
    /** @attribute System.Xml.Serialization.XmlElement("m__extraMsg") */
    public String m_extraMsg;    

/* DOTNETDECLARE:END */
    
    /**
     * Default constructor.
     * Do nothing (required by Betwixt).
     */
    public ShowRecordedActionMessage() {
        // Nothing to be done
    }
    
    /**
     * constructor
     * @param recorded true if action recorded, false otherwise.
     */
    public ShowRecordedActionMessage(boolean recorded) {
        m_recorded = recorded;
    }
    
    /**
     * constructor
     * @param recorded true if action recorded, false otherwise.
     * @param recAction name of recorded Action
     */
    public ShowRecordedActionMessage(boolean recorded, String recAction) {
        m_recorded = recorded;
        m_recAction = recAction;
    }
    
    /**
     * constructor
     * @param recorded true if action recorded, false otherwise.
     * @param recAction name of recorded Action
     * @param extraMsg additional Message / Info
     */
    public ShowRecordedActionMessage(boolean recorded, String recAction,
            String extraMsg) {
        m_recorded = recorded;
        m_recAction = recAction;
        m_extraMsg = extraMsg;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.SHOW_RECORDED_ACTION_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }
    
    /**
     * @return if open or not
     */
    public boolean isRecorded() {
        return m_recorded;
    }
    
    /**
     * @return true if additional Message exists
     */
    public boolean hasExtraMsg() {
        return m_hasExtraMsg;
    }
    
    /**
     * @return name of recorded Action
     */
    public String getRecAction() {
        return m_recAction;
    }
    
    /**
     * @param recAction name of recorded Action
     */
    public void setRecAction(String recAction) {
        m_recAction = recAction;
    }
    
    /**
     * @return additional Message / Info
     */
    public String getExtraMessage() {
        return m_extraMsg;
    }
    
    /**
     * @param extraMsg additional Message / Info
     */
    public void setExtraMessage(String extraMsg) {
        m_extraMsg = extraMsg;
    }
}