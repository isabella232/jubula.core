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
package org.eclipse.jubula.rc.common.components;

import org.eclipse.jubula.rc.common.exception.UnsupportedComponentException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * Creates a new graphics component by calling the default constructor of the
 * component class. No further initialization is performed.
 * 
 * @author BREDEX GmbH
 * @created 10.08.2005
 */
public class DefaultComponentFactory implements IComponentFactory {
    /**
     * Creates an error message.
     * 
     * @param componentName
     *            The component name
     * @param e
     *            The exception
     * @return The error message
     */
    private String createMessage(String componentName, Exception e) {
        return "The component '" + componentName //$NON-NLS-1$
            + "' could not be instantiated:" + e.getMessage(); //$NON-NLS-1$
    }
    /**
     * {@inheritDoc}
     */
    public Object createComponent(String componentName)
        throws UnsupportedComponentException {
        try {
            return Class.forName(componentName).newInstance();
        } catch (InstantiationException e) {
            throw new UnsupportedComponentException(createMessage(
                componentName, e), MessageIDs.E_COMPONENT_NOT_INSTANTIATED);
        } catch (IllegalAccessException e) {
            throw new UnsupportedComponentException(createMessage(
                componentName, e), MessageIDs.E_COMPONENT_NOT_INSTANTIATED);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedComponentException(createMessage(
                componentName, e), MessageIDs.E_COMPONENT_NOT_INSTANTIATED);
        }
    }
}
