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
package org.eclipse.jubula.toolkit.provider.swing.gui;

import java.util.Map;

import org.eclipse.jubula.client.ui.rcp.widgets.JavaAutConfigComponent;
import org.eclipse.swt.widgets.Composite;


/**
 * @author BREDEX GmbH
 * @created 13.02.2006
 * 
 */
public class SwingAutConfigComponent extends JavaAutConfigComponent {

    /**
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param autConfig data to be displayed/edited
     * @param autName the name of the AUT that will be using this configuration.
     */
    public SwingAutConfigComponent(Composite parent, int style,
        Map<String, String> autConfig, String autName) {
        
        super(parent, style, autConfig, autName);
    }
 
}