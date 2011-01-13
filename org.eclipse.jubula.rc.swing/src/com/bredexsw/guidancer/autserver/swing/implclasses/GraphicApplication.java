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
package com.bredexsw.guidancer.autserver.swing.implclasses;

import java.awt.Component;

/**
 * This class is a dummy graphics component that represents the tested
 * application itself. It is used by implementation classes which perform
 * general, application wide actions without the need of a special graphics
 * component like a button or a textfield. Those actions are key strokes, for
 * example.
 * 
 * @author BREDEX GmbH
 * @created 08.08.2005
 */
public class GraphicApplication extends Component {
    /*
     * IMPORTANT NOTE: This class extends java.awt.Component. This is not
     * required from the implementation class/default mapping mechanismn's
     * point of view. However, the AUTHierarchy implementation assumes
     * AWT components so far.
     */
    
    /**
     * {@inheritDoc}
     */
    public boolean isShowing() {
        // Always returns true because the "application" itself should
        // always be considered visible.
        return true;
    }
}
