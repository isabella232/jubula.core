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
package org.eclipse.jubula.client.ui.preferences;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jubula.client.ui.constants.Constants;
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
        IEclipsePreferences prefNode = 
                DefaultScope.INSTANCE.getNode(Constants.PLUGIN_ID);

        initializeDefaultPreferencesJubulaBasic(prefNode);
        initializeDefaultPreferencesObjectMapping(prefNode);
        initializeDefaultPreferencesKeyBoardShortCuts(prefNode);
        initializeDefaultPreferencesObservation(prefNode);
        initializeDefaultPreferencesTestResults(prefNode);

        prefNode.putBoolean(Constants.ASKSTOPAUT_KEY,
                Constants.ASKSTOPAUT_KEY_DEFAULT);
        prefNode.put(Constants.USER_KEY, Constants.USER_DEFAULT);
        prefNode.put(Constants.SCHEMA_KEY, Constants.SCHEMA_DEFAULT);
        prefNode.putBoolean(Constants.LINK_WITH_EDITOR_TCVIEW_KEY, 
                Constants.LINK_WITH_EDITOR_TCVIEW_KEY_DEFAULT);
        prefNode.putBoolean(Constants.DATADIR_WS_KEY, 
                Constants.DATADIR_WS_KEY_DEFAULT);
        prefNode.put(Constants.DATADIR_PATH_KEY, 
                Platform.getLocation().toOSString());
        
        StringBuilder serverValuesBuilder = new StringBuilder();
        serverValuesBuilder.append(new String(Base64.encodeBase64(
                AUT_AGENT_DEFAULT_HOST.getBytes())));
        serverValuesBuilder.append(StringConstants.SEMICOLON);
        serverValuesBuilder.append(new String(
            Base64.encodeBase64(String.valueOf(
                ConfigurationConstants.AUT_AGENT_DEFAULT_PORT).getBytes())));
        prefNode.put(
                Constants.SERVER_SETTINGS_KEY, 
                serverValuesBuilder.toString());
    }

    /**
     * initialize the default preferences for a preference page 
     * @param prefNode Preference node.
     */
    private static void initializeDefaultPreferencesJubulaBasic(
            IEclipsePreferences prefNode) {
        prefNode.putBoolean(Constants.TREEAUTOSCROLL_KEY,
                Constants.TREEAUTOSCROLL_KEY_DEFAULT);
        prefNode.putBoolean(Constants.MINIMIZEONSUITESTART_KEY,
                Constants.MINIMIZEONSUITESTART_KEY_DEFAULT);
        prefNode.putBoolean(Constants.SHOWORIGINALNAME_KEY, 
                Constants.SHOWORIGINALNAME_KEY_DEFAULT);
        prefNode.putInt(Constants.PERSP_CHANGE_KEY,
                Constants.PERSP_CHANGE_KEY_DEFAULT);
        prefNode.putBoolean(Constants.NODE_INSERT_KEY,
                Constants.NODE_INSERT_KEY_DEFAULT);
        prefNode.putBoolean(Constants.SHOWCAPINFO_KEY,
                Constants.SHOWCAPINFO_KEY_DEFAULT);
        prefNode.putBoolean(Constants.SHOW_TRANSIENT_CHILDREN_KEY,
                Constants.SHOW_TRANSIENT_CHILDREN_KEY_DEFAULT);
        prefNode.putBoolean(Constants.REMEMBER_KEY,
                Constants.REMEMBER_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param prefNode Preference node.
     */
    private static void initializeDefaultPreferencesTestResults(
            IEclipsePreferences prefNode) {
        prefNode.putBoolean(Constants.GENERATEREPORT_KEY,
                Constants.GENERATEREPORT_KEY_DEFAULT);
        prefNode.put(Constants.REPORTGENERATORSTYLE_KEY,
            Constants.REPORTGENERATORSTYLE_KEY_DEFAULT);
        prefNode.putBoolean(Constants.OPENRESULTVIEW_KEY,
                Constants.OPENRESULTVIEW_KEY_DEFAULT);
        prefNode.putBoolean(Constants.TRACKRESULTS_KEY,
                Constants.TRACKRESULTS_KEY_DEFAULT);
        prefNode.put(Constants.RESULTPATH_KEY,
                Constants.RESULTPATH_KEY_DEFAULT);
        prefNode.putInt(Constants.MAX_NUMBER_OF_DAYS_KEY,
                Constants.MAX_NUMBER_OF_DAYS_KEY_DEFAULT);
        prefNode.putBoolean(Constants.AUTO_SCREENSHOT_KEY, 
                Constants.AUTO_SCREENSHOT_KEY_DEFAULT);
        prefNode.putInt(Constants.TEST_EXEC_RELEVANT,
                Constants.TEST_EXECUTION_RELEVANT_DEFAULT);
        prefNode.putBoolean(Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY,
                Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param prefNode Preference node.
     */
    private static void initializeDefaultPreferencesObjectMapping(
            IEclipsePreferences prefNode) {
        prefNode.putBoolean(Constants.SHOWCHILDCOUNT_KEY,
                Constants.SHOWCHILDCOUNT_KEY_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param prefNode Preference node.
     */
    private static void initializeDefaultPreferencesKeyBoardShortCuts(
            IEclipsePreferences prefNode) {
        prefNode.putInt(Constants.MAPPING_MOD_KEY,
                Constants.MAPPINGMOD1_KEY_DEFAULT);
        prefNode.putInt(Constants.MAPPING_TRIGGER_KEY,
                Constants.MAPPING_TRIGGER_DEFAULT);
        prefNode.putInt(Constants.MAPPING_TRIGGER_TYPE_KEY,
                Constants.MAPPING_TRIGGER_TYPE_DEFAULT);
    }
    
    /**
     * initialize the default preferences for a preference page 
     * @param prefNode Preference node.
     */
    private static void initializeDefaultPreferencesObservation(
            IEclipsePreferences prefNode) {
        prefNode.putInt(Constants.RECORDMOD_COMP_MODS_KEY,
                Constants.RECORDMOD1_KEY_DEFAULT);
        prefNode.putInt(Constants.RECORDMOD_COMP_KEY_KEY,
                Constants.RECORDMOD2_KEY_DEFAULT);
        prefNode.putInt(Constants.RECORDMOD_APPL_MODS_KEY,
                Constants.RECORDMOD_APPL_MODS_DEFAULT);
        prefNode.putInt(Constants.RECORDMOD_APPL_KEY_KEY,
                Constants.RECORDMOD_APPL_KEY_DEFAULT);        
        
        prefNode.putInt(Constants.CHECKMODE_MODS_KEY,
                Constants.CHECKMODE_MODS_KEY_DEFAULT);
        prefNode.putInt(Constants.CHECKMODE_KEY_KEY,
                Constants.CHECKMODE_KEY_KEY_DEFAULT);
        prefNode.putInt(Constants.CHECKCOMP_MODS_KEY,
                Constants.CHECKCOMP_MODS_KEY_DEFAULT);
        prefNode.putInt(Constants.CHECKCOMP_KEY_KEY,
                Constants.CHECKCOMP_KEY_KEY_DEFAULT);
        
        prefNode.putBoolean(Constants.SHOWRECORDDIALOG_KEY,
                Constants.SHOWRECORDDIALOG_KEY_DEFAULT);
        
        prefNode.put(Constants.SINGLELINETRIGGER_KEY,
                Constants.SINGLELINETRIGGER_KEY_DEFAULT);
        prefNode.put(Constants.MULTILINETRIGGER_KEY,
                Constants.MULTILINETRIGGER_KEY_DEFAULT);
        
    }

}
