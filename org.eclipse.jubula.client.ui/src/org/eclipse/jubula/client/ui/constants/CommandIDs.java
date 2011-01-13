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
package org.eclipse.jubula.client.ui.constants;

/**
 * Constants for all used CommandIDs
 *
 * @author BREDEX GmbH
 * @created Jul 30, 2010
 */
public interface CommandIDs {
    /**
     * <code>EXPORT_WIZARD_PARAM_ID</code>
     */
    public static final String EXPORT_WIZARD_PARAM_ID = "exportWizardId"; //$NON-NLS-1$
    
    /**
     * <code>IMPORT_WIZARD_PARAM_ID</code>
     */
    public static final String IMPORT_WIZARD_PARAM_ID = "importWizardId"; //$NON-NLS-1$
    
    /** the ID of the "add event handler" command */
    public static final String ADD_EVENT_HANDLER_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.AddEventHandler"; //$NON-NLS-1$
    
    /** the ID of the "add comment" command */
    public static final String ADD_COMMENT_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.AddComment"; //$NON-NLS-1$
    
    /** the ID of the "delete" command */
    public static final String DELETE_COMMAND_ID = "org.eclipse.ui.edit.delete"; //$NON-NLS-1$
    
    /** the ID of the "edit parameters" command */
    public static final String EDIT_PARAMETERS_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.EditParameters"; //$NON-NLS-1$
    
    /** the ID of the "expand tree item" command */
    public static final String EXPAND_TREE_ITEM_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ExpandTreeItem"; //$NON-NLS-1$
    
    /** the ID of the "extract test case" command */
    public static final String EXTRACT_TESTCASE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ExtractTestCase"; //$NON-NLS-1$
    
    /** the ID of the "new cap" command */
    public static final String NEW_CAP_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.newCap"; //$NON-NLS-1$
    
    /** the ID of the "new category" command */
    public static final String NEW_CATEGORY_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.CreateNewCategoryCommand"; //$NON-NLS-1$

    /** the ID of the "add new test data manager" command */
    public static final String NEW_TESTDATACUBE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.AddNewTestDataManager"; //$NON-NLS-1$
    
    /** the ID of the "new test job" command */
    public static final String NEW_TESTJOB_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.NewTestJobCommand"; //$NON-NLS-1$
    
    /** the ID of the "new test suite" command */
    public static final String NEW_TESTSUITE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.NewTestSuiteCommand"; //$NON-NLS-1$

    /** the ID of the "open central test data editor" command */
    public static final String OPEN_CENTRAL_TESTDATA_EDITOR_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenCentralTestDataEditor"; //$NON-NLS-1$
    
    /** the ID of the "open object mapping editor" command */
    public static final String OPEN_OBJECTMAPPING_EDITOR_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenObjectMappingEditor"; //$NON-NLS-1$

    /** the ID of the "open specification" command */
    public static final String OPEN_SPECIFICATION_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenSpecification"; //$NON-NLS-1$

    /** the ID of the "open test case editor" command */
    public static final String OPEN_TESTCASE_EDITOR_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenTestcaseEditor"; //$NON-NLS-1$
    
    /** the ID of the "open test case" command */
    public static final String OPEN_TESTCASE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenTestCase"; //$NON-NLS-1$
    
    /** the ID of the "open test job editor" command */
    public static final String OPEN_TESTJOB_EDITOR_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenTestJobEditor"; //$NON-NLS-1$
    
    /** the ID of the "open test suite editor" command */
    public static final String OPEN_TESTSUITE_EDITOR_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenTestsuiteEditor"; //$NON-NLS-1$
    
    /** ID of the "Open Test Result Viewer" command  */
    public static final String OPEN_TEST_RESULT_VIEWER_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenTestResultViewer"; //$NON-NLS-1$
    
    /** the ID of the "Pause Test Suite" command */
    public static final String PAUSE_TEST_SUITE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.PauseTestSuiteCommand"; //$NON-NLS-1$
    
    /** the ID of the "open project properties" command */
    public static final String PROJECT_PROPERTIES_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.PropertiesCommand"; //$NON-NLS-1$
    
    /** the ID of the "reference test case" command */
    public static final String REFERENCE_TC_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.newTestCaseReference"; //$NON-NLS-1$
    
    /** the ID of the "refresh" command */
    public static final String REFRESH_COMMAND_ID = "org.eclipse.ui.file.refresh"; //$NON-NLS-1$
    
    /** the ID of the "rename" command */
    public static final String RENAME_COMMAND_ID = "org.eclipse.ui.edit.rename"; //$NON-NLS-1$

    /** the ID of the "revert changes" command */
    public static final String REVERT_CHANGES_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.RevertChanges"; //$NON-NLS-1$
    
    /** the ID of the "show specification" command */
    public static final String SHOW_SPECIFICATION_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ShowSpecification"; //$NON-NLS-1$
    
    /** the ID of the "show responsible node" command */
    public static final String SHOW_RESPONSIBLE_NODE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ShowResponsibleNodes"; //$NON-NLS-1$
    
    /** the ID of the "show where used" command */
    public static final String SHOW_WHERE_USED_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ShowWhereUsed"; //$NON-NLS-1$
    
    /** the ID of the "choose/start AUT" command */
    public static final String START_AUT_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ChooseAutCommand"; //$NON-NLS-1$
    
    /** the ID of the "Start Object Mapping Mode" command */
    public static final String START_OBJECT_MAPPING_MODE_COMMAND_ID =  "org.eclipse.jubula.client.ui.commands.OMStartMappingModeCommand"; //$NON-NLS-1$
    
    /** the ID of the "Start Observation Mode" command */
    public static final String START_OBSERVATION_MODE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.StartObservationModeCommand"; //$NON-NLS-1$
    
    /** the ID of the "choose/start Test Job" command */
    public static final String START_TEST_JOB_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.StartTestJobCommand"; //$NON-NLS-1$
    
    /** the ID of the "choose/start Test Suite" command */
    public static final String START_TEST_SUITE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.StartTestSuiteCommand"; //$NON-NLS-1$
    
    /** the ID of the "Stop Test Suite" command */
    public static final String STOP_TEST_SUITE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.StopSuiteCommand"; //$NON-NLS-1$
    
    /** the ID of the "toggle active state" command */
    public static final String TOGGLE_ACTIVE_STATE_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.ToggleActiveStatus"; //$NON-NLS-1$
    
    /** the ID of the "Pause on Error" command */
    public static final String TOGGLE_PAUSE_ON_ERROR_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.togglePauseOnErrorCommand"; //$NON-NLS-1$

    /** the ID of the "Open test result detail" command */
    public static final String OPEN_TEST_RESULT_DETAIL_COMMAND_ID = "org.eclipse.jubula.client.ui.commands.OpenTestResultViewer"; //$NON-NLS-1$

    /** the ID of the "file export" command */
    public static final String ECLIPSE_RCP_FILE_EXPORT_COMMAND_ID = "org.eclipse.ui.file.export"; //$NON-NLS-1$
    
    /** the ID of the "file import" command */
    public static final String ECLIPSE_RCP_FILE_IMPORT_COMMAND_ID = "org.eclipse.ui.file.import"; //$NON-NLS-1$
}
