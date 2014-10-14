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
package org.eclipse.jubula.communication.internal.message;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 09.05.2006
 */
public class CAPTestMessageFactory {

    /** The logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(CAPTestMessageFactory.class);

    /** 
     * mapping from toolkit name (short form) to corresponding CAP Test  
     * Message class name (FQN) 
     */
    private static Map<String, String> toolkitToTestMessageClassName =
        new HashMap<String, String>();

    static {
        toolkitToTestMessageClassName.put(CommandConstants.SWT_TOOLKIT, 
            "org.eclipse.jubula.communication.internal.message.swt.CAPSwtTestMessage"); //$NON-NLS-1$
        toolkitToTestMessageClassName.put(CommandConstants.RCP_TOOLKIT, 
            toolkitToTestMessageClassName.get(CommandConstants.SWT_TOOLKIT));
        toolkitToTestMessageClassName.put(CommandConstants.SWING_TOOLKIT, 
            "org.eclipse.jubula.communication.internal.message.swing.CAPSwingTestMessage"); //$NON-NLS-1$
        toolkitToTestMessageClassName.put(CommandConstants.HTML_TOOLKIT, 
            "org.eclipse.jubula.communication.internal.message.html.CAPHtmlTestMessage"); //$NON-NLS-1$
        toolkitToTestMessageClassName.put(CommandConstants.WIN_TOOLKIT,
            "org.eclipse.jubula.communication.internal.message.win.CAPWinTestMessage"); //$NON-NLS-1$
        toolkitToTestMessageClassName.put(CommandConstants.WIN__APPS_TOOLKIT,
            "org.eclipse.jubula.communication.internal.message.win.CAPWinTestMessage"); //$NON-NLS-1$
        toolkitToTestMessageClassName.put(CommandConstants.IOS_TOOLKIT, 
            "org.eclipse.jubula.communication.internal.message.ios.IOSCAPTestMessage"); //$NON-NLS-1$
        toolkitToTestMessageClassName.put(CommandConstants.JAVAFX_TOOLKIT, 
            "org.eclipse.jubula.communication.internal.message.javafx.CAPJavaFXTestMessage"); //$NON-NLS-1$
    }
    
    /**
     * default utility constructor.
     */
    private CAPTestMessageFactory() {
        // do nothing
    }

    /**
     * @param messageCap
     *            the messageCap to set.
     * @param autToolKit
     *            the toolkit ID of the AUT
     * @throws UnknownMessageException
     *             the exception thrown if the instantiation of message failed.
     * @return the created Message
     */
    public static CAPTestMessage getCAPTestMessage(MessageCap messageCap,
        String autToolKit) 
        throws UnknownMessageException {
        String messageClassName = "null"; //$NON-NLS-1$
        try {
            messageClassName = toolkitToTestMessageClassName.get(autToolKit);
            if (messageClassName != null) {
                Class messageClass = Class.forName(messageClassName, false, 
                        CAPTestMessage.class.getClassLoader());
                if (!CAPTestMessage.class.isAssignableFrom(
                        messageClass)) {
                    
                    throw new UnknownMessageException(messageClass.getName()
                            + "is not assignable to " //$NON-NLS-1$
                            + CAPTestMessage.class.getName(),
                            MessageIDs.E_MESSAGE_NOT_ASSIGNABLE);
                }
                
                // create a sharedInstance and set the message
                CAPTestMessage result = 
                    (CAPTestMessage)messageClass.newInstance();
                result.setMessageCap(messageCap);
                return result;
            }
            throw new UnknownMessageException(
                    "Creating a shared message instance for " //$NON-NLS-1$
                    + messageClassName 
                    + "failed: " //$NON-NLS-1$
                    + autToolKit, MessageIDs.E_MESSAGE_NOT_CREATED);
        } catch (ExceptionInInitializerError eiie) {
            throwUnknownMessageException(messageClassName, eiie);
        } catch (LinkageError le) {
            throwUnknownMessageException(messageClassName, le);
        } catch (ClassNotFoundException cnfe) {
            throwUnknownMessageException(messageClassName, cnfe);
        } catch (InstantiationException ie) {
            throwUnknownMessageException(messageClassName, ie);
        } catch (IllegalAccessException iae) {
            throwUnknownMessageException(messageClassName, iae);
        }
        return null;
    }
    
    /**
     * @param messageClassName
     *            the message class name
     * @param nestedException
     *            the nested exception
     * @throws UnknownMessageException
     *             when called
     */
    private static void throwUnknownMessageException(String messageClassName,
        Throwable nestedException) throws UnknownMessageException {
        LOG.error(nestedException.getLocalizedMessage(), nestedException);
        throw new UnknownMessageException(
            "Creating a shared message instance for " //$NON-NLS-1$
                + messageClassName + "failed: " //$NON-NLS-1$
                + nestedException.getMessage(),
            MessageIDs.E_MESSAGE_NOT_CREATED);
    }
}