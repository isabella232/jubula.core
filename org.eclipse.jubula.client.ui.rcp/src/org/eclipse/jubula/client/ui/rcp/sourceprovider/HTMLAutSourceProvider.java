/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMAUTListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.eclipse.ui.ISources;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class HTMLAutSourceProvider extends AbstractJBSourceProvider implements
        IOMAUTListener, IOMStateListener {

    /**
     * the id of this source provider
     */
    public static final String ID = "org.eclipse.jubula.client.ui.rcp.sourceprovider.HTMLAutSourceProvider"; //$NON-NLS-1$

    
    /** 
     * ID of variable that indicates whether the client is currently connected 
     * to an AUT Agent
     */
    public static final String IS_HTML_AUT = 
        "org.eclipse.jubula.client.ui.rcp.variable.isHtmlAut"; //$NON-NLS-1$

    /** 
     * ID of variable that indicates whether the client is currently connecting 
     * to an AUT Agent
     */
    public static final String WINDOW_COUNT = 
        "org.eclipse.jubula.client.ui.rcp.variable.windowCount"; //$NON-NLS-1$
    
    /** is it an HTML aut in OMM */
    private boolean m_isHTMLAut = false;
    
    /**
     * Constructor for adding listeners to the DataEventDispatcher
     */
    public HTMLAutSourceProvider() {
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.addOMAUTListener(this, false);
        dispatch.addOMStateListener(this, true);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.removeOMAUTListener(this);
        dispatch.removeOMStateListener(this);

    }

    /**
     * {@inheritDoc}
     */
    public Map getCurrentState() {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put(IS_HTML_AUT, m_isHTMLAut);

        return values;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String[] { IS_HTML_AUT };
    }

    /**
     * {@inheritDoc}
     */
    public void handleAUTChanged(AutIdentifier identifier) {
        if (identifier != null) {
            IProjectPO project = GeneralStorage.getInstance().getProject();
            IAUTMainPO aut = AutAgentRegistration.getAutForId(identifier,
                    project);
            String toolkit = aut.getToolkit();
            if (toolkit
                    .equalsIgnoreCase(CommandConstants.HTML_TOOLKIT)) {
                m_isHTMLAut = true;
            } else {
                m_isHTMLAut = false;
            }
        } else {
            m_isHTMLAut = false;
        }
        fireModeChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void handleOMStateChanged(OMState state) {
        if (state == OMState.notRunning) {
            m_isHTMLAut = false;
        }
    }

    /**
     * Fires a source changed event for <code>IS_OBJECT_MAPPING_RUNNING</code>.
     */
    private void fireModeChanged() {
        gdFireSourceChanged(ISources.WORKBENCH, IS_HTML_AUT, m_isHTMLAut);
    }

}
