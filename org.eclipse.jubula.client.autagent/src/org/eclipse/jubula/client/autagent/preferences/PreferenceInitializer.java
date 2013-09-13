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
package org.eclipse.jubula.client.autagent.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jubula.client.autagent.Activator;

/**
 * 
 * @author BREDEX GmbH
 * @created Jun 29, 2011
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /** ID for "Embedded AUT Agent Port" preference */
    public static final String PREF_EMBEDDED_AGENT_PORT = 
        "org.eclipse.jubula.autagent.preference.port"; //$NON-NLS-1$

    /** default value for "Embedded AUT Agent Port" preference */
    public static final int DEFAULT_EMBEDDED_AGENT_PORT = 60000;
    
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences preferenceNode = 
            DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        preferenceNode.putInt(PREF_EMBEDDED_AGENT_PORT, 
                DEFAULT_EMBEDDED_AGENT_PORT);
    }

}
