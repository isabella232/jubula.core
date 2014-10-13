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
package org.eclipse.jubula.client.core.communication;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.message.CAPTestMessage;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 09.05.2006
 */
public class MessageFactory {

    /** The logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(MessageFactory.class);

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
    private MessageFactory() {
        // do nothing
    }

    /**
     * @return the toolkit of the AUT
     */
    private static String getAutToolkit() {
        String autToolKit = StringConstants.EMPTY;
        final IAUTMainPO connectedAut = 
            TestExecution.getInstance().getConnectedAut();
        if (connectedAut != null) {
            autToolKit = connectedAut.getToolkit();            
        }
        return autToolKit;
    }
    
    /**
     * @param messageCap the messageCap to set.
     * @throws UnknownMessageException the exception thrown if the instantiation of message failed.
     * @return the created Message
     */
    public static CAPTestMessage getCAPTestMessage(MessageCap messageCap) 
        throws UnknownMessageException {
        final String autToolKit = getAutToolkit();
        try {
            if (StringConstants.EMPTY.equals(autToolKit) 
                    && !AUTConnection.getInstance().isConnected()) {
                throw new UnknownMessageException(
                        Messages.CreatingMessageSharedInstanceFailed,
                        MessageIDs.E_MESSAGE_NOT_CREATED);
            }
        } catch (ConnectionException e) {
            throw new UnknownMessageException(
                    Messages.CreatingMessageSharedInstanceFailed,
                    MessageIDs.E_MESSAGE_NOT_CREATED);
        }
        String messageClassName = "null"; //$NON-NLS-1$
        try {
            messageClassName = toolkitToTestMessageClassName.get(autToolKit);
            if (messageClassName != null) {
                Class messageClass = Class.forName(messageClassName, false, 
                        CAPTestMessage.class.getClassLoader());
                if (!CAPTestMessage.class.isAssignableFrom(
                        messageClass)) {
                    
                    throw new UnknownMessageException(messageClass.getName()
                            + Messages.IsNotAssignableTo + StringConstants.SPACE
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
                    Messages.CreatingAnMessageSharedInstanceFor
                    + StringConstants.SPACE + messageClassName 
                    + Messages.Failed + StringConstants.COLON 
                    + StringConstants.SPACE
                    + Messages.NoAUTActivationMessageClassFoundForToolkit 
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
            Throwable nestedException)
        throws UnknownMessageException {
        LOG.error(nestedException.getLocalizedMessage(), nestedException);
        throw new UnknownMessageException(
                Messages.CreatingAnMessageSharedInstanceFor
                        + StringConstants.SPACE + messageClassName
                        + Messages.Failed + StringConstants.COLON
                        + StringConstants.SPACE + nestedException.getMessage(),
                MessageIDs.E_MESSAGE_NOT_CREATED);
    }
}