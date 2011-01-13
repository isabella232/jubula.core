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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.AUTStartStateMessage;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.SendAUTListOfSupportedComponentsMessage;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.exception.GuiDancerUnsupportedComponentException;
import org.eclipse.jubula.rc.common.implclasses.IComponentFactory;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.xml.businessmodell.DefaultMapping;


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
public abstract class AbstractSendAUTListOfSupportedComponentsCommand 
    implements ICommand {

    /** the logger */
    private static Log log = LogFactory.getLog(
        AbstractSendAUTListOfSupportedComponentsCommand.class);
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
     * Creates a new component identifier and calls
     * {@link ComponentHandler#addToHierarchy(IComponentFactory, String, String)}
     * The identifier contains information about a default mapped component.
     * @param c The component with a default mapping
     * @return The identifier
     * @throws GuiDancerUnsupportedComponentException If the registration of the default mapping fails
     */
    protected IComponentIdentifier createIdentifier(ConcreteComponent c)
        throws GuiDancerUnsupportedComponentException {

        DefaultMapping defaultMapping = c.getDefaultMapping();
        String typeFactoryName = defaultMapping.getTypeFactory();
        String technicalName = defaultMapping.getTechnicalName();
        try {
            Class typeFactoryClass = Class.forName(typeFactoryName);
            IComponentFactory factory = (IComponentFactory)typeFactoryClass
                .newInstance();
            addToHierarchy(factory, c, technicalName);
            IComponentIdentifier id = new ComponentIdentifier();
            id.setComponentClassName(c.getType());
            id.setSupportedClassName(c.getComponentClass());
            List list = new ArrayList();
            list.add(technicalName);
            id.setHierarchyNames(list);
            return id;
        } catch (ClassNotFoundException e) {
            throw new GuiDancerUnsupportedComponentException(e.getMessage(),
                MessageIDs.E_CLASS_NOT_FOUND);
        } catch (InstantiationException e) {
            throw new GuiDancerUnsupportedComponentException(e.getMessage(),
                MessageIDs.E_INSTANTIATION);
        } catch (IllegalAccessException e) {
            throw new GuiDancerUnsupportedComponentException(e.getMessage(),
                MessageIDs.E_ILLEGAL_ACCESS);
        }
    }
    
    /**
     * @param factory factory
     * @param c The component with a default mapping
     * @param technicalName technicalName
     * @throws GuiDancerUnsupportedComponentException .
     */
    protected abstract void addToHierarchy(IComponentFactory factory, 
        ConcreteComponent c, String technicalName)
        throws GuiDancerUnsupportedComponentException;

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
                AUTServerConfiguration.getInstance().registerComponent(
                    concrete);
                
                // Create an identifier for components with a default mapping
                // and store in the message (see below). These identifiers
                // will be used on the client side to create an object mapping.
                if (concrete.hasDefaultMapping()) {
                    componentIds = addComponentID(componentIds, concrete);
                }
            } catch (GuiDancerUnsupportedComponentException e) {
                log.error("An error occurred while registering a component.", e); //$NON-NLS-1$
            } catch (IllegalArgumentException e) {
                log.error("An error occurred while registering a component.", e); //$NON-NLS-1$
            }
        }

        log.info("Exiting method " + getClass().getName() + ".execute()."); //$NON-NLS-1$ //$NON-NLS-2$
        return new AUTStartStateMessage(componentIds);
    }

    /**
     * @param componentIds the list of compIDs to add
     * @param concrete the concrete component
     * @throws GuiDancerUnsupportedComponentException .
     * @return the list of the supported component ids
     */
    protected abstract List addComponentID(List componentIds, 
        ConcreteComponent concrete) 
        throws GuiDancerUnsupportedComponentException;

    /** 
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}