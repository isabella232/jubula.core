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
package org.eclipse.jubula.app.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 * @created 10.12.2010
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.jubula.app.i18n.messages"; //$NON-NLS-1$

    public static String GuiDancerLauncherAlreadyInUse;
    public static String GuiDancerLauncherAlreadyInUseTitle;
    public static String GuiDancerLauncherCannotBeSet;
    public static String GuiDancerLauncherCannotBeSetTitle;
    public static String GuiDancerLauncherCouldNotRead;
    public static String GuiDancerLauncherCouldNotReadTitle;
    public static String GuiDancerLauncherCouldNotWrite;
    public static String GuiDancerLauncherCouldNotWriteTitle;
    public static String GuiDancerLauncherDifferentVersionMessage;
    public static String GuiDancerLauncherDifferentVersionTitle;
    public static String GuiDancerLauncherNotValid;
    public static String GuiDancerLauncherNotValidTitle;
    public static String GuiDancerLauncherValidWorkspace;
    public static String GuiDancerLauncherValidWorkspaceTitle;
    public static String ActionBuilderCutItem;
    public static String ActionBuilderCutToolTip;
    public static String ActionBuilderEdit;
    public static String ActionBuilderExitItem;
    public static String ActionBuilderExportAll;
    public static String ActionBuilderHelpContentItem;
    public static String ActionBuilderHelpContentToolTip;
    public static String ActionBuilderMyFileEntry;
    public static String ActionBuilderMyHelpEntry;
    public static String ActionBuilderNavigateEntry;
    public static String ActionBuilderopenPerspective;
    public static String ActionBuilderPasteItem;
    public static String ActionBuilderPasteToolTip;
    public static String ActionBuilderPreferencesItem;
    public static String ActionBuilderrefreshItem;
    public static String ActionBuilderresetPerspective;
    public static String ActionBuilderRun;
    public static String ActionBuilderSaveAllItem;
    public static String ActionBuilderSaveAllToolTip;
    public static String ActionBuilderSaveAsPoint;
    public static String ActionBuilderSaveAs;
    public static String ActionBuilderSaveItem;
    public static String ActionBuilderSaveToolTip;
    public static String ActionBuildershowView;
    public static String ActionBuilderWindowEntry;
    public static String CannotOpenThePerspective;
    public static String UnhandledRuntimeException;
    public static String JubulaWorkbenchWindowAdvisorWindowTitle;
    public static String LaunchWorkspaceData;
    public static String UnableToWriteWorkspaceData;
    public static String WorkSpaceDataCantWriteDataTitle;
    public static String WorkSpaceDataCanWriteData;
    public static String ChooseWorkspaceDialogBrowse;
    public static String ChooseWorkspaceDialogDialogMessage;
    public static String ChooseWorkspaceDialogDialogText;
    public static String ChooseWorkspaceDialogMessaageDir;
    public static String ChooseWorkspaceDialogMessageArea;
    public static String ChooseWorkspaceDialogSelectDir;
    public static String ChooseWorkspaceDialogWorkspace;
    public static String CouldNotReadVersionFile;
    public static String CouldNotWriteVersionFile;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
