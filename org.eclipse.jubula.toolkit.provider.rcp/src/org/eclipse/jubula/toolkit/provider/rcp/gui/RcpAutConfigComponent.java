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
package org.eclipse.jubula.toolkit.provider.rcp.gui;

import java.util.Map;

import org.eclipse.jubula.toolkit.provider.swt.gui.SwtAutConfigComponent;
import org.eclipse.swt.widgets.Composite;


/**
 * @author BREDEX GmbH
 * @created Nov 23, 2007
 */
public class RcpAutConfigComponent extends SwtAutConfigComponent {

    /**
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param autConfig data to be displayed/edited
     * @param autName the name of the AUT that will be using this configuration.
     */
    public RcpAutConfigComponent(Composite parent, int style, 
        Map<String, String> autConfig, String autName) {
        
        super(parent, style, autConfig, autName);
    }

}
