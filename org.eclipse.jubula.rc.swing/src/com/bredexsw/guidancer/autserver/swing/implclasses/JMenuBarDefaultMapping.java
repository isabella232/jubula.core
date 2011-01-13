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

import javax.swing.JMenuBar;

import org.eclipse.jubula.tools.objects.MappingConstants;


/**
 * For Default Mapping of Menu.
 *
 * @author BREDEX GmbH
 * @created 14.05.2007
 */
public class JMenuBarDefaultMapping extends JMenuBar {

    /**
     * 
     */
    public JMenuBarDefaultMapping() {
        setName(MappingConstants.MENU_DEFAULTMAPPING_TECHNICAL_NAME);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isShowing() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateUI() {
        // This method is overriden because under some conditions it produces
        // NullPointerExceptions. (ClassLoading-Problems?)
        // Because of this is only a Default-Mapping Component and not visible 
        // on the screen, it needs no explicit UI.
    }
}
