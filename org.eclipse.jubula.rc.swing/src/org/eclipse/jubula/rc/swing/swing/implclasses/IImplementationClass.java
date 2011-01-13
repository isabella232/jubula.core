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
package org.eclipse.jubula.rc.swing.swing.implclasses;

import java.awt.Color;
import java.awt.Component;

import org.eclipse.jubula.rc.common.implclasses.IBaseImplementationClass;



/**
 * The interface to be implemented by all implementation classes.
 *
 * @author BREDEX GmbH
 * @created 26.08.2004
 */
public interface IImplementationClass extends IBaseImplementationClass {

    /**
     * High light the given component, called during object mapping
     * @param component the component to high light
     * @param border the color we want to highlight with
     */
    public void highLight(Component component, Color border);
    
    /**
     * Low light the given component, called during object mapping
     * @param component the component to remove the 'hight light'
     */
    public void lowLight(Component component);
    
}
