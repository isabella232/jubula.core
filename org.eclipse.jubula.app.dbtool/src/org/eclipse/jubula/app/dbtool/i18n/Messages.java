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
package org.eclipse.jubula.app.dbtool.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 * @created 10.12.2010
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.jubula.app.dbtool.i18n.messages"; //$NON-NLS-1$
    public static String DBToolName;
    public static String DBToolDelete;
    public static String DBToolDeleteAll;
    public static String DBToolDeleteFailed;
    public static String DBToolDeleteKeepSummary;
    public static String DBToolInvalidVersion;
    public static String DBToolMissingProject;
    public static String DBToolExportAll;
    public static String DBToolInvalidExportDirectory;
    public static String DBToolNonEmptyExportDirectory;
    public static String DBToolExport;
    public static String DBToolDir;
    public static String DBToolImport;
    public static String ExecutionControllerInvalidDBDataError;
    
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
