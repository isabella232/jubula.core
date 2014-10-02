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
package org.eclipse.jubula.toolkit.winapps.provider;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.utils.ToolkitUtils;
import org.eclipse.jubula.toolkit.win.provider.ToolKitProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * @author BREDEX GmbH
 */
public class WinAppsToolkitProvider extends ToolKitProvider {
    
    /** <code>I18N_PROPERTIES</code> */
    private static final String I18N_PROPERTIES = 
        "org.eclipse.jubula.toolkit.winapps.provider.I18nStrings"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public Composite getAutConfigDialog(Composite parent, int style,
            Map<String, String> autConfig, String autName)
        throws ToolkitPluginException {
        return ToolkitUtils.createAutConfigComponent(
                        "org.eclipse.jubula.client.ui.rcp.widgets.autconfig.WinAppsAutConfigComponent", //$NON-NLS-1$
                        this.getClass().getClassLoader(), parent, style,
                        autConfig, autName);
    }
    
    /** {@inheritDoc} */
    public URL getComponentConfigurationFileURL() {
        return ToolkitUtils.getURL(Activator.getDefault(), COMP_CONFIG_PATH);
    }

    /** {@inheritDoc} */
    public ResourceBundle getI18nResourceBundle() {
        return ResourceBundle.getBundle(I18N_PROPERTIES);
    }
}
