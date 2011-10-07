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
package org.eclipse.jubula.client.ui.rcp.constants;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;


/**
 * @author BREDEX GmbH
 * @created 31.07.2006
 */
public class RcpIconConstants {
    
    /** no server image */
    public static final Image NO_SERVER_IMAGE = Plugin.getImage("NoServer.gif"); //$NON-NLS-1$
    /** no connection image */
    public static final Image NO_CONNECTION_IMAGE = Plugin.getImage("NoSC.gif"); //$NON-NLS-1$
    /** camera image */
    public static final Image CAM_IMAGE = Plugin.getImage("cam.gif"); //$NON-NLS-1$
    /** checkcamera image */
    public static final Image CHECK_CAM_IMAGE = Plugin.getImage("checkcam.gif"); //$NON-NLS-1$
    /** map image */
    public static final Image MAP_IMAGE = Plugin.getImage("map.gif"); //$NON-NLS-1$
    /** pause image */
    public static final Image PAUSE_IMAGE = Plugin.getImage("pause.gif"); //$NON-NLS-1$
    /** no aut image */
    public static final Image NO_AUT_IMAGE = Plugin.getImage("NoAUT.gif"); //$NON-NLS-1$

    /**
     * 
     * @param original The original, or base, image.
     * @return the "cut" version of the image. Client should not 
     *         dispose this image.
     */
    public static final Image TC_DISABLED_IMAGE = new Image(
            IconConstants.TC_IMAGE.getDevice(), 
            IconConstants.TC_IMAGE, 
            SWT.IMAGE_GRAY);
    
    /** maps images to their "cut" (grayscale) counterparts */
    private static final Map<Image, Image> CUT_IMAGES = 
        new HashMap<Image, Image>();

    /** to prevent instantiation */
    private RcpIconConstants() {
        // do nothing
    }

    /**
     * 
     * @param original The original, or base, image.
     * @return the "cut" version of the image. Client should not 
     *         dispose this image.
     */
    public static Image getCutImage(Image original) {
        Image cutImage = CUT_IMAGES.get(original);
        if (cutImage == null) {
            cutImage = 
                new Image(original.getDevice(), original, SWT.IMAGE_GRAY);
            CUT_IMAGES.put(original, cutImage);
        }
        
        return cutImage;
    }

}