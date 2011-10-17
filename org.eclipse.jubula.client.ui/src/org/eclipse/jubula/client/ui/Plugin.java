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
package org.eclipse.jubula.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.client.ui.utils.ImageUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * Base class for plug-ins that integrate with the Eclipse platform UI.
 * 
 * @author BREDEX GmbH
 * @created 06.07.2004
 */
public class Plugin extends AbstractUIPlugin {
    
    /** single instance of plugin */
    private static Plugin plugin;

    /** m_imageCache */
    private static Map < ImageDescriptor, Image > imageCache = 
        new HashMap < ImageDescriptor, Image > ();

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * @return instance of plugin
     */
    public static Plugin getDefault() {
        return plugin;
    }

    /** 
     * @param fileName Object
     * @return Image
     */
    public static Image getImage(String fileName) {
        ImageDescriptor descriptor = null;
        descriptor = getImageDescriptor(fileName);
        //obtain the cached image corresponding to the descriptor
        Image image = imageCache.get(descriptor);
        if (image == null) {
            image = descriptor.createImage();
            imageCache.put(descriptor, image);
        }
        return image;
    }

    /**
     * @param name String
     * @return ImageDescriptor from URL
     */
    public static ImageDescriptor getImageDescriptor(String name) {
        return ImageUtils.getImageDescriptor(getDefault().getBundle(), name);
    }
    
}