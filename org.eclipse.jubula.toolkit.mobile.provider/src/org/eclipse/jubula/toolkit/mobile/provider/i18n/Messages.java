/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.mobile.provider.i18n;

import org.eclipse.osgi.util.NLS;
/**
 * @author BREDEX GmbH
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.jubula.toolkit.mobile.provider.i18n.messages"; //$NON-NLS-1$

    public static String autHostnameLabel;
    public static String autPortLabel;
    public static String emptyHostname;
    public static String emptyPort;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
    /**
     * Constructor
     */
    private Messages() {
        // hide
    }
}
