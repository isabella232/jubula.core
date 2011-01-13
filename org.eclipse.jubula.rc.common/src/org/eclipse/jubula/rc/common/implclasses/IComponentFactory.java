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
package org.eclipse.jubula.rc.common.implclasses;

import org.eclipse.jubula.rc.common.exception.GuiDancerUnsupportedComponentException;

/**
 * This interface represents a factory that creates a graphics component
 * instance from the passed component class name. Implementors are specified in
 * the Guidancer configuration file to support the default object mapping.
 * 
 * @author BREDEX GmbH
 * @created 10.08.2005
 */
public interface IComponentFactory {
    /**
     * Creates a new graphics component instance from the passed name. The
     * component name must be equal to the component type specified in the
     * Guidancer configuration file.
     * 
     * @param componentName
     *            The component name
     * @return The new component
     * @throws GuiDancerUnsupportedComponentException
     *             If the component cannot be created.
     */
    public Object createComponent(String componentName)
        throws GuiDancerUnsupportedComponentException;
}
