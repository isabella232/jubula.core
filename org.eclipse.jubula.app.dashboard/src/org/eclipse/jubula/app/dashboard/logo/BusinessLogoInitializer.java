/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.app.dashboard.logo;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

/**
 * @author BREDEX GmbH
 */
public class BusinessLogoInitializer implements ILayoutSetInitializer {
    /** The set id */
    public static final String SET_ID = "org.eclipse.rap.design.example.business.layoutset.logo";  //$NON-NLS-1$

    /**
     * the logo path property
     */

    public static final String LOGO = "header.logo"; //$NON-NLS-1$
    /**
     * the logo position property
     */
    
    public static final String LOGO_POSITION = "header.logo.position"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public void initializeLayoutSet(final LayoutSet layoutSet) {
        layoutSet.addImagePath(LOGO, "resources/logo.png"); //$NON-NLS-1$

        // positions
        FormData fdLogo = new FormData();
        fdLogo.right = new FormAttachment(100, -75);
        fdLogo.top = new FormAttachment(0, 7);
        layoutSet.addPosition(LOGO_POSITION, fdLogo);
    }
}
