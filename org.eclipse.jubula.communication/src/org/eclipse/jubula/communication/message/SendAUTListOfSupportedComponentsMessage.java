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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.Profile;

/**
 * This message transfers all Jubula components of type
 * {@link org.eclipse.jubula.tools.xml.businessmodell.Component} and subclasses.
 * the components will be registered in the AUT server by executing
 * <code>SendAUTListOfSupportedComponentsCommand</code>.
 * 
 * @author BREDEX GmbH
 * @created 04.10.2004
 */
public abstract class SendAUTListOfSupportedComponentsMessage extends Message {
    /** static version */
    private static final double VERSION = 1.0;

    // the data of this message BEGIN
    /** The list of supported components and their implementation classes. */
    private List m_components = new ArrayList();
    // the data of this message END

    /** fuzzy profile */
    private Profile m_profile;

    /** empty constructor for serialisation */
    public SendAUTListOfSupportedComponentsMessage() {
        super();
    }

    /** {@inheritDoc} */
    public abstract String getCommandClass();

    /** {@inheritDoc} */
    public double getVersion() {
        return VERSION;
    }

    /** @return The list of <code>Component</code> objects. */
    public List getComponents() {
        return m_components;
    }

    /**
     * Adds a component to this message. It will be registered in the AUT
     * server.
     * 
     * @param component
     *            The component.
     */
    public void addComponent(Component component) {
        m_components.add(component);
    }

    /**
     * @param components
     *            The list of components to set. They are of type
     *            {@link Component} or subclasses
     */
    public void setComponents(List components) {
        m_components = components;
    }

    /** @return Returns the profile. */
    public Profile getProfile() {
        return m_profile;
    }

    /**
     * @param p
     *            The profile to set.
     */
    public void setProfile(Profile p) {
        this.m_profile = p;
    }
}