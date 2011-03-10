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

import org.eclipse.jubula.tools.constants.SwtAUTHierarchyConstants;
import org.eclipse.jubula.tools.objects.MappingConstants;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;


/**
 * For Default Mapping of Menu.
 * This class is a dummy Menu component that represents the Menu in an
 * swt-application. It is used by implementation classes which perform
 * menu actions 
 *
 * @author BREDEX GmbH
 * @created 02.04.2007
 */
public class MenuDefaultMapping extends Menu {
    /**
     * Create a new Menu
     */
    public MenuDefaultMapping() {
        super(new Shell());
        setData(SwtAUTHierarchyConstants.WIDGET_NAME, 
                MappingConstants.MENU_DEFAULTMAPPING_TECHNICAL_NAME);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void checkSubclass() {
        // do nothing, therefor allowing subclassing 
    }
    
}
