/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.constants.PreferenceConstants;
import org.eclipse.jubula.client.ui.rcp.widgets.JavaAutConfigComponent;
import org.eclipse.jubula.tools.constants.ConfigurationConstants;
import org.eclipse.jubula.tools.constants.StringConstants;

/**
 * 
 * @author BREDEX GmbH
 * @created 17.10.2011
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * <code>AUT_AGENT_DEFAULT_HOST</code>
     */
    private static final String AUT_AGENT_DEFAULT_HOST = "localhost"; //$NON-NLS-1$

    @Override
    public void initializeDefaultPreferences() {
        // Use the Plugin ID from org.eclipse.jubula.client.ui so that 
        // preferences will continue to be saved to that area. Otherwise,
        // users would lose their preferences after the UI bundle was split
        // into client.ui and client.ui.rcp.
        IEclipsePreferences prefNode = 
                DefaultScope.INSTANCE.getNode(Constants.PLUGIN_ID);
        prefNode.put(
                PreferenceConstants.SERVER_SETTINGS_KEY, AUT_AGENT_DEFAULT_HOST
                + StringConstants.COLON
                + ConfigurationConstants.AUT_AGENT_DEFAULT_PORT);
        prefNode.put(Constants.AUT_CONFIG_DIALOG_MODE,
                JavaAutConfigComponent.AUT_CONFIG_DIALOG_MODE_KEY_DEFAULT);
    }

}
