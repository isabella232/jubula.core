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
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;

/**
 * The response message for CAPTestMessage. This message is sent by
 * <code>CAPTestCommand</code>. <br>
 * 
 * The message contains an error event only if the test step fails.
 * 
 * {@inheritDoc}
 * 
 * @author BREDEX GmbH
 * @created 01.09.2004
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
public class CAPTestResponseMessage extends Message {
    /** If test ok. */
    public static final int TEST_OK = 0;
    
    /** general test failure */
    public static final int TEST_FAILED = 1;
    
    /** test failed: security exception */
    public static final int FAILURE_SECURITY = 2;
    
    /** test failed: accessibility exception */
    public static final int FAILURE_ACCESSIBILITY = 3;
    
    /** test failed: no implementation class found */
    public static final int FAILURE_INVALID_IMPLEMENTATION_CLASS = 10;
    
    /** test failed: method was not found */
    public static final int FAILURE_METHOD_NOT_FOUND = 11;
    
    /** test failed: invalid arguments (parameter) */
    public static final int FAILURE_INVALID_PARAMETER = 12;
    
    /** test failed: method throws an exception */
    public static final int FAILURE_STEP_EXECUTION = 13;
    
    /** test failed: component not supported */
    public static final int FAILURE_UNSUPPORTED_COMPONENT = 20;

    /** test failed: component not found in the AUT*/
    public static final int FAILURE_COMPONENT_NOT_FOUND = 21;

    /** constant to signal to pause the execution */
    public static final int PAUSE_EXECUTION = 31;

    /**
     * The static version
     */
    private static final double VERSION = 1.0;
    
/* DOTNETDECLARE:BEGIN */

    /**
     * Transmitted version of the message.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__version") */
    public double m_version = VERSION;
    
    /**
     * The state of test
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__state") */
    public int m_state = TEST_OK;
    /**
     * The error event.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__testErrorEvent") */
    public TestErrorEvent m_testErrorEvent;
    
    /**
     * The CAP message data.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__messageCap") */
    public MessageCap m_messageCap;
    /**
     * The type of the returnValue that returns to the clientTest. 
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__returnType") */
    public String m_returnType = null;

    /**
     * The value that returns to the clientTest.
     */
    /** @attribute System.Xml.Serialization.XmlElement("m__returnValue") */
    public String m_returnValue = null;

/* DOTNETDECLARE:END */
    
    /**
     * Default constructor.
     */
    public CAPTestResponseMessage() {
        super();
    }
    /**
     * @return Returns the returnValue or an empty String if no return value
     * is available.
     */
    public String getReturnValue() {
        return m_returnValue != null ? m_returnValue : StringConstants.EMPTY;
    }
    /**
     * @param returnValue The returnValue to set.
     */
    public void setReturnValue(String returnValue) {
        m_returnValue = returnValue;
    }
    /**
     * @deprecated
     * @return Returns the returnType.
     */
    public String getReturnType() {
        return m_returnType;
    }
    /**
     * @deprecated
     * @param returnType The returnType to set.
     */
    public void setReturnType(String returnType) {
        m_returnType = returnType;
    }
    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.CAP_TEST_RESPONSE_COMMAND;
    }

    /**
     * {@inheritDoc}
     */
    public double getVersion() {
        return m_version;
    }

   
    /**
     * @deprecated Evaluate the test error event instead.
     * 
     * @return Returns the state.
     */
    public int getState() {
        return m_state;
    }
    /**
     * @deprecated
     * @param state The state to set.
     */
    public void setState(int state) {
        m_state = state;
    }
    /**
     * @return <code>true</code> if the message contains an error event,
     *         <code>false</code> otherwise.
     */
    public boolean hasTestErrorEvent() {
        return m_testErrorEvent != null;
    }
    /**
     * @return The error event (maybe <code>null</code>).
     */
    public TestErrorEvent getTestErrorEvent() {
        return m_testErrorEvent;
    }
    /**
     * Sets the error event.
     * 
     * @param testErrorEvent
     *            The error event.
     */
    public void setTestErrorEvent(TestErrorEvent testErrorEvent) {
        m_testErrorEvent = testErrorEvent;
        m_state = TEST_FAILED;
    }
    
    /**
     * Gets the CAP message data.
     * 
     * @return The message data.
     */
    public MessageCap getMessageCap() {
        return m_messageCap;
    }
    /**
     * Sets the CAP message data (required by Betwixt).
     * 
     * @param messageCap
     *            The message data
     */
    public void setMessageCap(MessageCap messageCap) {
        m_messageCap = messageCap;
    }

}
