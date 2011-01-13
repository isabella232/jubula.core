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


import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.ObjectMappedMessage;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;


/**
 * The command object for ObjectMappedMessage. <br>
 * 
 * The <code>execute()</code> method just logs the information from the
 * message (info level). No message is returned.
 * 
 * 
 * @author BREDEX GmbH
 * @created 25.08.2004
 * 
 */
public class ObjectMappedCommand implements ICommand {

    /** the logger */
    private static Log log = LogFactory.getLog(ObjectMappedCommand.class);
    
    /** the message */
    private ObjectMappedMessage m_message;
    
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
        m_message = (ObjectMappedMessage)message;
    }

    /**
     * Just create a log entry on info level. <br> 
     * Returns always null.
     *  
     * {@inheritDoc}
     */
    public Message execute() {
        mapObject(m_message.getComponentIdentifier());
        return null;
    }

    /**
     * @param componentIdentifier The identifier to be mapped
     */
    private void mapObject(IComponentIdentifier componentIdentifier) {
        if (log.isInfoEnabled()) {
            try {
                String logMessage = "mapped object '" //$NON-NLS-1$
                    + componentIdentifier.getComponentName()
                    + "' of type '" //$NON-NLS-1$
                    + componentIdentifier.getComponentClassName() 
                    + "' in hierachy: "; //$NON-NLS-1$ 
                for (Iterator iter = componentIdentifier.getHierarchyNames()
                        .iterator(); iter.hasNext();) {
                    String element = (String)iter.next();
                    logMessage = logMessage + element + ","; //$NON-NLS-1$
                }
                log.info(logMessage);
            } catch (ClassCastException cce) {
                log.error("component identifiers does " + //$NON-NLS-1$
                        "not consist of Strings"); //$NON-NLS-1$
            }
        }
        if (TestExecution.getInstance().getConnectedAut() != null) {
            ObjectMappingEventDispatcher.notifyObjectMappedObserver(
                componentIdentifier);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }

}
