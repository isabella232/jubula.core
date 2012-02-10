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
package org.eclipse.jubula.app.autrun;

import java.io.InputStream;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * 
 * @author BREDEX GmbH
 * @created Sept 07, 2011
 */
public class Activator implements BundleActivator {
    /**
     * logback configuration file
     */
    private static final String LOGBACK_CONFIG_XML = "logback.xml";  //$NON-NLS-1$
    /** the bundle context */
    private static BundleContext context;

    /**
     * 
     * @return the bundle context.
     */
    static BundleContext getContext() {
        return context;
    }

    /**
     *
     * {@inheritDoc}
     */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        // initialize the logging facility
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext) {
            LoggerContext lc = (LoggerContext)loggerFactory;
            try {
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(lc);
                // the context was probably already configured by default
                // configuration rules
                lc.reset();
                InputStream is = 
                        context.getBundle().getResource(LOGBACK_CONFIG_XML)
                            .openStream();
                configurator.doConfigure(is);
            } catch (JoranException je) {
                // no logging if logger fails :-(
            }
        }

    }

    /**
     * 
     * {@inheritDoc}
     */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }

}
