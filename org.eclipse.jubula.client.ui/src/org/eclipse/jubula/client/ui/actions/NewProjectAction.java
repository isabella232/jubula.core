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
package org.eclipse.jubula.client.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.businessprocess.AlwaysEnabledBP;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.dialogs.NagDialog;
import org.eclipse.jubula.client.ui.utils.JBThread;
import org.eclipse.jubula.client.ui.widgets.JavaAutConfigComponent;
import org.eclipse.jubula.client.ui.wizards.ProjectWizard;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 07.02.2005
 */
public class NewProjectAction extends AbstractAction {

    /** was the nag dialog for the executable field already shown? */
    private boolean m_alreadyNagged = false;
    
    /**
     * {@inheritDoc}
     */
    public void runWithEvent(IAction action, Event event) { 
        if (action != null && !action.isEnabled()) {
            return;
        }
        m_alreadyNagged = false;
        Plugin.startLongRunning();
        JBThread t = new JBThread() {
                public void run() {
                    if (!Hibernator.init()) {
                        Plugin.stopLongRunning();
                        return;
                    }  
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            if (GeneralStorage.getInstance().getProject() 
                                    != null
                                && Plugin.getDefault().anyDirtyStar()) {
                                
                                if (!Plugin.getDefault()
                                    .showSaveEditorDialog()) {

                                    return;
                                } 
                            }     
                            openNewProjectWizard();
                        }
                    });
                }
    
                protected void errorOccured() {
                    Plugin.stopLongRunning();
                }                   
            };
        t.start();
    }
  
    /**
     * Opens the "New Project Wizard".
     */
    private void openNewProjectWizard() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                final ProjectWizard projectWizard = new ProjectWizard();
                projectWizard.init(null, null);
                WizardDialog dialog = new WizardDialog(Plugin.getShell(),
                        projectWizard) {
                    
                    /**
                     * {@inheritDoc}
                     */
                    protected void finishPressed() {
                        String selectedToolkit = projectWizard.
                            getAutSettingWizardPage().getToolkitComboBox().
                            getSelectedObject();
                        if (selectedToolkit == null) {
                            selectedToolkit = projectWizard.
                                getProjectSettingWizardPage().
                                getToolkitComboBox().getSelectedObject();
                        }
                        createWizardNagDialog(selectedToolkit);
                        super.finishPressed();
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    protected void nextPressed() {
                        if (projectWizard.getPage(projectWizard.
                                getAutConfigSettingWpID()).equals(
                                        super.getCurrentPage())) {
                            String selectedToolkit = projectWizard.
                                getAutSettingWizardPage().getToolkitComboBox().
                                getSelectedObject();
                            if (selectedToolkit == null) {
                                selectedToolkit = projectWizard.
                                    getProjectSettingWizardPage().
                                    getToolkitComboBox().getSelectedObject();
                            }
                            createWizardNagDialog(selectedToolkit);
                        }
                        super.nextPressed();
                    }
                };
                dialog.open();
                if (dialog.getReturnCode() == Window.OK) {
                    final IProjectPO project = 
                        GeneralStorage.getInstance().getProject(); 
                    IRunnableWithProgress op = 
                        new OpenProjectAction.OpenProjectOperation(project);
                    try {
                        PlatformUI.getWorkbench().getProgressService()
                            .busyCursorWhile(op);
                        Plugin.getDisplay().syncExec(new Runnable() {
                            public void run() {
                                Plugin.setProjectNameInTitlebar(
                                    project.getName(),
                                    project.getMajorProjectVersion(),
                                    project.getMinorProjectVersion());
                            }
                        });
                    } catch (InvocationTargetException e) {
                        // Exception already handled within operation.
                        // Do nothing.
                    } catch (InterruptedException e) {
                        // Operation was canceled.
                        // Do nothing.
                    }
                }
            }
        });
    }
    
    /**
     * creates NagDialog for the AUT Configuration Settings Wizard Page
     * @param selectedToolkit the currently selected toolkit
     */
    private void createWizardNagDialog(String selectedToolkit) {
        if (!m_alreadyNagged && isSwingOrSwtPlugin(selectedToolkit)
                && !JavaAutConfigComponent.isExecFieldEmpty()) {
            NagDialog.runNagDialog(null,
                "InfoNagger.DefineSwingOrSwtExecutable", //$NON-NLS-1$
                ContextHelpIds.AUT_CONFIG_SETTING_WIZARD_PAGE); 
            m_alreadyNagged = true;
        }
    }
    
    /**
     * @param selectedToolkit the currently selected toolkit
     * @return true if swing or swt toolkit is selected
     */
    private boolean isSwingOrSwtPlugin(String selectedToolkit) {
        if (selectedToolkit.equals(CommandConstants.SWING_TOOLKIT)
                || selectedToolkit.equals(
                        CommandConstants.SWT_TOOLKIT)) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return AlwaysEnabledBP.getInstance();
    }
}