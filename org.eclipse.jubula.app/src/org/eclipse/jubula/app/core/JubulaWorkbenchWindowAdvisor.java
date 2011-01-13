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
package org.eclipse.jubula.app.core;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jubula.app.Activator;
import org.eclipse.jubula.app.i18n.Messages;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * @author BREDEX GmbH
 * @created 23.08.2005
 */
public class JubulaWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    /** Key to look up the action builder on the window configurer. */
    private static final String BUILDER_KEY = "builder"; //$NON-NLS-1$
    /***/
    private IWorkbenchWindowConfigurer m_windowConfigurer;
    
    /**
     * @param configurer IWorkbenchWindowConfigurer 
     */
    public JubulaWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
        m_windowConfigurer = configurer;
    }
    
    /**
     * {@inheritDoc}
     */
    public ActionBarAdvisor createActionBarAdvisor(
        IActionBarConfigurer configurer) {
        
        return new JubulaActionBarAdvisor(configurer, m_windowConfigurer);
    }
    
    /**
     * {@inheritDoc}
     */
    public void preWindowOpen() {
        getWindowConfigurer().setTitle(Messages
                .JubulaWorkbenchWindowAdvisorWindowTitle);
        getWindowConfigurer().setShowMenuBar(true);
        getWindowConfigurer().setShowPerspectiveBar(true);
        getWindowConfigurer().setShowCoolBar(true);
        getWindowConfigurer().setShowStatusLine(true);
        getWindowConfigurer().setShowProgressIndicator(true);
        getWindowConfigurer().setShowFastViewBars(false);
    }
    
    /**
     * {@inheritDoc}
     */
    public void postWindowOpen() {
        super.postWindowOpen();
        AbstractUIPlugin plugin = Activator.getDefault();
        ImageRegistry imageRegistry = plugin.getImageRegistry();
        getWindowConfigurer().getWindow().getShell().setImages(
                new Image [] {
                        imageRegistry.get(Activator.IMAGE_GIF_JB_16_16_ID),
                        imageRegistry.get(Activator.IMAGE_GIF_JB_32_32_ID),
                        imageRegistry.get(Activator.IMAGE_GIF_JB_48_48_ID),
                        imageRegistry.get(Activator.IMAGE_GIF_JB_64_64_ID),
                        imageRegistry.get(Activator.IMAGE_GIF_JB_128_128_ID),
                        imageRegistry.get(Activator.IMAGE_PNG_JB_16_16_ID),
                        imageRegistry.get(Activator.IMAGE_PNG_JB_32_32_ID),
                        imageRegistry.get(Activator.IMAGE_PNG_JB_48_48_ID),
                        imageRegistry.get(Activator.IMAGE_PNG_JB_64_64_ID),
                        imageRegistry.get(Activator.IMAGE_PNG_JB_128_128_ID)
                });
                
        Plugin.createStatusLineItems();
        Plugin.showStatusLine((IWorkbenchPart)null);
    }

    /**
     * @throws WorkbenchException 
     * {@inheritDoc}
     */
    public void postWindowRestore() throws WorkbenchException {
        super.postWindowRestore();
        Plugin.showStatusLine((IWorkbenchPart)null);
    }

    /**
     * {@inheritDoc}
     */
    public void postWindowClose() {
        ActionBuilder builder = (ActionBuilder)getWindowConfigurer()
            .getData(BUILDER_KEY);
        if (builder != null) {
            builder.dispose();
        }
    }
}