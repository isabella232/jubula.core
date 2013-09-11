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
package org.eclipse.jubula.dashboard.jettycustomizer;

import java.util.Dictionary;

import org.eclipse.equinox.http.jetty.JettyCustomizer;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.session.SessionHandler;

/**
 * Jetty customizer for dashboard.
 *
 * @author BREDEX GmbH
 * @created Oct 05, 2011
 */
public class DashboardSessionCustomizer extends JettyCustomizer {

    @SuppressWarnings("rawtypes")
    @Override
    public Object customizeContext(Object context, Dictionary settings) {
        Object result = super.customizeContext(context, settings);

        // disables cookies for session management
        // see http://wiki.eclipse.org/RAP/FAQ#Jetty
        if (context instanceof ServletContextHandler) {
            ServletContextHandler jettyContext = (ServletContextHandler)context;
            SessionHandler sessionHandler = jettyContext.getSessionHandler();
            if (sessionHandler != null) {
                SessionManager sessionManager = 
                        sessionHandler.getSessionManager();
                if (sessionManager instanceof AbstractSessionManager) {
                    ((AbstractSessionManager)sessionManager)
                        .setUsingCookies(false);
                }
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Object customizeHttpConnector(
            Object connector, Dictionary settings) {
        
        Object result = super.customizeHttpConnector(connector, settings);

        if (result instanceof AbstractConnector) {
            // disable Reuse Address to prevent starting on occupied port
            // on Windows from failing silently (see 
            // http://docs.codehaus.org/display/JETTY/Socket+reuse+on+Windows)
            ((AbstractConnector)result).setReuseAddress(false);
        }
        
        return result;
    }
}
