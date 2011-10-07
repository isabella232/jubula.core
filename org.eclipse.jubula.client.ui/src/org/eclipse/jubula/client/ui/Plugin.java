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
package org.eclipse.jubula.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.client.core.utils.PrefStoreHelper;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.utils.ImageUtils;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.jarutils.IVersion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * Base class for plug-ins that integrate with the Eclipse platform UI.
 * 
 * @author BREDEX GmbH
 * @created 06.07.2004
 */
public class Plugin extends AbstractUIPlugin {
    
    /** plugin id */
    public static final String PLUGIN_ID = "org.eclipse.jubula.client.ui"; //$NON-NLS-1$

    /** single instance of plugin */
    private static Plugin plugin;

    /** m_imageCache */
    private static Map < ImageDescriptor, Image > imageCache = 
        new HashMap < ImageDescriptor, Image > ();

    /** true, if preference store was initialized */
    private boolean m_isPrefStoreInitialized = false;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * {@inheritDoc}
     */
    public IPreferenceStore getPreferenceStore() {
        if (!m_isPrefStoreInitialized) {
            m_isPrefStoreInitialized = true;
            initializeDefaultPluginPreferences(super.getPreferenceStore());
            initializePrefStoreHelper();
        }
        return super.getPreferenceStore();
    }
    
    /**
     * @return instance of plugin
     */
    public static Plugin getDefault() {
        return plugin;
    }

    /** 
     * @param fileName Object
     * @return Image
     */
    public static Image getImage(String fileName) {
        ImageDescriptor descriptor = null;
        descriptor = getImageDescriptor(fileName);
        //obtain the cached image corresponding to the descriptor
        Image image = imageCache.get(descriptor);
        if (image == null) {
            image = descriptor.createImage();
            imageCache.put(descriptor, image);
        }
        return image;
    }

    /**
     * @param name String
     * @return ImageDescriptor from URL
     */
    public static ImageDescriptor getImageDescriptor(String name) {
        return ImageUtils.getImageDescriptor(getDefault().getBundle(), name);
    }

    /**
     * @param store IPreferenceStore
     */
    private static void initializeDefaultPluginPreferences(
            IPreferenceStore store) {

        initializeDefaultPreferencesJubulaBasic(store);
        initializeDefaultPreferencesObjectMapping(store);
        initializeDefaultPreferencesKeyBoardShortCuts(store);
        initializeDefaultPreferencesObservation(store);
        initializeDefaultPreferencesTestResults(store);
        initializeDefaultPreferencesTestData(store); 
        store.setDefault(Constants.PREF_MINORVERSION_KEY, -1);
        store.setDefault(Constants.PREF_MAJORVERSION_KEY, -1);
        if (store.getInt(Constants.PREF_MINORVERSION_KEY) == -1) {
            store.setValue(Constants.PREF_MINORVERSION_KEY,
                IVersion.JB_PREF_MINOR_VERSION);
            store.setValue(Constants.PREF_MAJORVERSION_KEY,
                IVersion.JB_PREF_MAJOR_VERSION);
        }
        store.setDefault(Constants.ASKSTOPAUT_KEY,
                Constants.ASKSTOPAUT_KEY_DEFAULT);
        store.setDefault(Constants.USER_KEY, Constants.USER_DEFAULT);
        store.setDefault(Constants.SCHEMA_KEY, Constants.SCHEMA_DEFAULT);
        store.setDefault(Constants.LINK_WITH_EDITOR_TCVIEW_KEY, 
                Constants.LINK_WITH_EDITOR_TCVIEW_KEY_DEFAULT);
        store.setDefault(Constants.DATADIR_WS_KEY, 
                Constants.DATADIR_WS_KEY_DEFAULT);
        store.setDefault(Constants.DATADIR_PATH_KEY, 
                Platform.getLocation().toOSString());
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param store IPreferenceStore
     */
    private static void initializeDefaultPreferencesJubulaBasic(
            IPreferenceStore store) {
        store.setDefault(Constants.TREEAUTOSCROLL_KEY,
                Constants.TREEAUTOSCROLL_KEY_DEFAULT);
        store.setDefault(Constants.MINIMIZEONSUITESTART_KEY,
                Constants.MINIMIZEONSUITESTART_KEY_DEFAULT);
        store.setDefault(Constants.SHOWORIGINALNAME_KEY, 
                Constants.SHOWORIGINALNAME_KEY_DEFAULT);
        store.setDefault(Constants.PERSP_CHANGE_KEY,
                Constants.PERSP_CHANGE_KEY_DEFAULT);
        store.setDefault(Constants.NODE_INSERT_KEY,
                Constants.NODE_INSERT_KEY_DEFAULT);
        store.setDefault(Constants.SHOWCAPINFO_KEY,
                Constants.SHOWCAPINFO_KEY_DEFAULT);
        store.setDefault(Constants.SHOW_TRANSIENT_CHILDREN_KEY,
                Constants.SHOW_TRANSIENT_CHILDREN_KEY_DEFAULT);
        store.setDefault(Constants.REMEMBER_KEY,
                Constants.REMEMBER_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param store IPreferenceStore
     */
    private static void initializeDefaultPreferencesTestResults(
            IPreferenceStore store) {
        store.setDefault(Constants.GENERATEREPORT_KEY,
                Constants.GENERATEREPORT_KEY_DEFAULT);
        store.setDefault(Constants.REPORTGENERATORSTYLE_KEY,
            Constants.REPORTGENERATORSTYLE_KEY_DEFAULT);
        store.setDefault(Constants.OPENRESULTVIEW_KEY,
                Constants.OPENRESULTVIEW_KEY_DEFAULT);
        store.setDefault(Constants.TRACKRESULTS_KEY,
                Constants.TRACKRESULTS_KEY_DEFAULT);
        store.setDefault(Constants.RESULTPATH_KEY,
                Constants.RESULTPATH_KEY_DEFAULT);
        store.setDefault(Constants.MAX_NUMBER_OF_DAYS_KEY,
                Constants.MAX_NUMBER_OF_DAYS_KEY_DEFAULT);
        store.setDefault(Constants.AUTO_SCREENSHOT_KEY, 
                Constants.AUTO_SCREENSHOT_KEY_DEFAULT);
        store.setDefault(Constants.TEST_EXEC_RELEVANT,
                Constants.TEST_EXECUTION_RELEVANT_DEFAULT);
        store.setDefault(Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY,
                Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param store IPreferenceStore
     */
    private static void initializeDefaultPreferencesObjectMapping(
            IPreferenceStore store) {
        store.setDefault(Constants.SHOWCHILDCOUNT_KEY,
                Constants.SHOWCHILDCOUNT_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param store IPreferenceStore
     */
    private static void initializeDefaultPreferencesTestData(
            IPreferenceStore store) {
        store.setDefault(Constants.REFERENCE_CHAR_KEY,
                String.valueOf(TestDataConstants.REFERENCE_CHAR_DEFAULT));
        store.setDefault(Constants.ESCAPE_CHAR_KEY,
                String.valueOf(TestDataConstants.ESCAPE_CHAR_DEFAULT));
        store.setDefault(Constants.FUNCTION_CHAR_KEY,
                String.valueOf(TestDataConstants.FUNCTION_CHAR_DEFAULT));
        store.setDefault(Constants.PATH_CHAR_KEY,
                String.valueOf(TestDataConstants.PATH_CHAR_DEFAULT));
        store.setDefault(Constants.VALUE_CHAR_KEY,
                String.valueOf(TestDataConstants.VALUE_CHAR_DEFAULT));
        store.setDefault(Constants.VARIABLE_CHAR_KEY,
                String.valueOf(TestDataConstants.VARIABLE_CHAR_DEFAULT));
    } 
    
    /**
     * initialize the default preferences for a preference page 
     * @param store IPreferenceStore
     */
    private static void initializeDefaultPreferencesKeyBoardShortCuts(
            IPreferenceStore store) {
        store.setDefault(Constants.MAPPING_MOD_KEY,
                Constants.MAPPINGMOD1_KEY_DEFAULT);
        store.setDefault(Constants.MAPPING_TRIGGER_KEY,
                Constants.MAPPING_TRIGGER_DEFAULT);
        store.setDefault(Constants.MAPPING_TRIGGER_TYPE_KEY,
                Constants.MAPPING_TRIGGER_TYPE_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param store IPreferenceStore
     */
    private static void initializeDefaultPreferencesObservation(
            IPreferenceStore store) {
        store.setDefault(Constants.RECORDMOD_COMP_MODS_KEY,
                Constants.RECORDMOD1_KEY_DEFAULT);
        store.setDefault(Constants.RECORDMOD_COMP_KEY_KEY,
                Constants.RECORDMOD2_KEY_DEFAULT);
        store.setDefault(Constants.RECORDMOD_APPL_MODS_KEY,
                Constants.RECORDMOD_APPL_MODS_DEFAULT);
        store.setDefault(Constants.RECORDMOD_APPL_KEY_KEY,
                Constants.RECORDMOD_APPL_KEY_DEFAULT);        
        
        store.setDefault(Constants.CHECKMODE_MODS_KEY,
                Constants.CHECKMODE_MODS_KEY_DEFAULT);
        store.setDefault(Constants.CHECKMODE_KEY_KEY,
                Constants.CHECKMODE_KEY_KEY_DEFAULT);
        store.setDefault(Constants.CHECKCOMP_MODS_KEY,
                Constants.CHECKCOMP_MODS_KEY_DEFAULT);
        store.setDefault(Constants.CHECKCOMP_KEY_KEY,
                Constants.CHECKCOMP_KEY_KEY_DEFAULT);
        
        store.setDefault(Constants.SHOWRECORDDIALOG_KEY,
                Constants.SHOWRECORDDIALOG_KEY_DEFAULT);
        
        store.setDefault(Constants.SINGLELINETRIGGER_KEY,
                Constants.SINGLELINETRIGGER_KEY_DEFAULT);
        store.setDefault(Constants.MULTILINETRIGGER_KEY,
                Constants.MULTILINETRIGGER_KEY_DEFAULT);
        
    }

    /**
     * Initializes the <code>PrefStoreHelper</code> to set the test data preferences
     */
    private void initializePrefStoreHelper() {
        IPreferenceStore store = super.getPreferenceStore();
        PrefStoreHelper helper = PrefStoreHelper.getInstance();
        helper.setEscapeChar(store.getString(Constants.ESCAPE_CHAR_KEY));
        helper.setFunctionChar(store.getString(Constants.FUNCTION_CHAR_KEY));
        helper.setReferenceChar(store.getString(Constants.REFERENCE_CHAR_KEY));
        helper.setValueChar(store.getString(Constants.VALUE_CHAR_KEY));
        helper.setVariableChar(store.getString(Constants.VARIABLE_CHAR_KEY));
    }
    
}