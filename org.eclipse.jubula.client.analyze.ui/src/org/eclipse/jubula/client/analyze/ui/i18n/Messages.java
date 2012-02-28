/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.client.analyze.ui.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * i18n string internationalization
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.jubula.client.analyze.ui.i18n.messages"; //$NON-NLS-1$

    public static String CreateProfilContributionItem;
    public static String RunningAnalyzes;
    public static String NodeType;
    public static String Amount;
    public static String Running;
    public static String AnalyzePreferenceDialog;
    public static String AnalyzePreferenceDialogParameters;
    public static String AnalyzePreferenceDialogAdjustNote;
    public static String AnalyzePreferenceDialogName;
    public static String AnalyzePreferenceDialogDefaultValue;
    public static String AnalyzePreferenceDialogSeparator;
    public static String AnalyzePreferenceDialogNoParameters;
    public static String OKButton;
    public static String DefaultsButton;
    public static String Testsuite;
    public static String Testcase;
    public static String Category;
    public static String ReferencedTestCase;
    public static String ReferencedTestSuite;
    public static String TestJob;
    public static String TestStep;
    public static String Project;
    public static String ComponentName;

    
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
