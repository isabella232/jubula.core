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
package com.bredexsw.guidancer.autswtserver.implclasses;

import org.eclipse.swt.widgets.Shell;

/**
 * This class is a dummy graphics component that represents the tested
 * application itself. It is used by implementation classes which perform
 * general, application wide actions without the need of a special graphics
 * component like a button or a textfield. Those actions are key strokes, for
 * example.
 * 
 * {@inheritDoc}
 * 
 * @author BREDEX GmbH
 * @created 20.04.2006
 */
public class GraphicApplication extends Shell {
    // Really empty!
    /*
     * IMPORTANT NOTE: This class extends org.eclipse.swt.widgets.Shell. 
     * This is not required from the implementation class/default mapping 
     * mechanismn's point of view. However, the AUTSWTHierarchy implementation 
     * assumes SWT components so far.
     */
    
    /**
     * {@inheritDoc}
     */
    protected void checkSubclass() {
        // do nothing, therefor allowing subclassing 
    }
}