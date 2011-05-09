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
package org.eclipse.jubula.client.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesDecorator;
import org.eclipse.jubula.client.core.businessprocess.INameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.ProjectComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.databinding.validators.AutIdValidator;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.client.ui.wizards.pages.AUTSettingWizardPage;
import org.eclipse.jubula.client.ui.wizards.pages.AutConfigSettingWizardPage;
import org.eclipse.jubula.client.ui.wizards.pages.ProjectInfoWizardPage;
import org.eclipse.jubula.client.ui.wizards.pages.ProjectSettingWizardPage;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.jarutils.IVersion;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.xml.businessmodell.ToolkitPluginDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 18.05.2005
 */
public class ProjectWizard extends Wizard implements INewWizard {
    
    /** the ID for the ProjectSettingWizardPage */
    private static final String PROJECT_SETTING_WP = 
        "org.eclipse.jubula.client.ui.wizards.pages.ProjectSettingWizardPage"; //$NON-NLS-1$
    
    /** the ID for the AUTSettingWizardPage */
    private static final String AUT_SETTING_WP = 
        "org.eclipse.jubula.client.ui.wizards.pages.AUTSettingWizardPage"; //$NON-NLS-1$
    
    /** the ID for the AutConfigSettingWizardPage */
    private static final String AUT_CONFIG_SETTING_WP = 
        "org.eclipse.jubula.client.ui.wizards.pages.AutConfigSettingWizardPage"; //$NON-NLS-1$
    
    /** the ID for the ProjectInfoWizardPage */
    private static final String PROJECT_INFO_WP = 
        "org.eclipse.jubula.client.ui.wizards.pages.ProjectInfoWizardPage"; //$NON-NLS-1$
    
    /**
     * Prefix for unbound modules project names
     */
    private static final String UNBOUND_MODULES_PREFIX = "unbound_modules_"; //$NON-NLS-1$
    
    /** the logger */
    private static Log log = LogFactory.getLog(ProjectWizard.class);
    
    /** the new project to create */
    private IProjectPO m_newProject;
    /** the new autMain of the new project */
    private IAUTMainPO m_autMain;
    /** the new autConfig of the new project */
    private IAUTConfigPO m_autConfig;
    /** dialog to get the new project name from */
    private ProjectSettingWizardPage m_projectSettingWizardPage;
    /** the wizard page for the aut settings */
    private AUTSettingWizardPage m_autSettingWizardPage;
    /** the wizard page for the aut configuration settings */
    private AutConfigSettingWizardPage m_autConfigSettingWizardPage;
    /** the project information wizard page */
    private ProjectInfoWizardPage m_projectInfoWizardPage;

    /**
     * @return the wizard page for the project settings
     */
    public ProjectSettingWizardPage getProjectSettingWizardPage() {
        return m_projectSettingWizardPage;
    }
    
    /**
     * @return the wizard page for the AUT settings
     */
    public AUTSettingWizardPage getAutSettingWizardPage() {
        return m_autSettingWizardPage;
    }

    /**
     * @return the key of the wizard page for the AUT configuration settings
     */
    public String getAutConfigSettingWpID() {
        return AUT_CONFIG_SETTING_WP;
    }
    
    /**
     * {@inheritDoc}
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(Messages.ProjectWizardNewProjectWizard);
        setDefaultPageImageDescriptor(IconConstants
                .PROJECT_WIZARD_IMAGE_DESCRIPTOR);
        setNeedsProgressMonitor(true);    
        setHelpAvailable(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        final String name = m_projectSettingWizardPage.getNewProjectName();
        if (ProjectPM.doesProjectNameExist(name)) {
            Utils.createMessageDialog(MessageIDs.E_PROJECTNAME_ALREADY_EXISTS, 
                new Object[]{name}, null);
            return false;
        }
        try {
            PlatformUI.getWorkbench().getProgressService().run(false, false,
                new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor) 
                        throws InterruptedException {
                        monitor.beginTask(
                            NLS.bind(Messages.ProjectWizardCreatingProject,
                                    new Object[] { name }),
                            IProgressMonitor.UNKNOWN);
                        try {
                            createNewProject(name, monitor);
                            m_newProject = null;
                        } finally {
                            monitor.done();
                        }
                    }
                });
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error(ite.getCause());
        } catch (InterruptedException ie) {
            // Operation was canceled.
            // Do nothing.
        }
        return true;
    }
        
    /**
     * {@inheritDoc}
     */
    public boolean performCancel() {
        m_newProject = null;
        Plugin.stopLongRunning();
        ProjectNameBP.getInstance().clearCache();
        return true;
    }
    /**
     * Adds the pages of this wizard.
     */
    public void addPages() {
        Plugin.startLongRunning();
        final String emptystr = StringConstants.EMPTY;
        m_newProject = NodeMaker.createProjectPO(emptystr, 
            IVersion.JB_CLIENT_METADATA_VERSION); 
        m_autMain = PoMaker.createAUTMainPO(emptystr);
        m_newProject.addAUTMain(m_autMain);
        m_autConfig = PoMaker.createAUTConfigPO();
        m_autMain.addAutConfigToSet(m_autConfig);
        IValidator autIdValidator = 
            new AutIdValidator(m_newProject, null, m_autConfig);
        m_projectSettingWizardPage = new ProjectSettingWizardPage(
                PROJECT_SETTING_WP, m_newProject);
        m_projectSettingWizardPage.setTitle(Messages
                .ProjectWizardProjectSettings);
        m_projectSettingWizardPage.setDescription(Messages
                .ProjectWizardNewProject);
        addPage(m_projectSettingWizardPage);            
        
        m_autSettingWizardPage = new AUTSettingWizardPage(
                AUT_SETTING_WP, m_newProject, m_autMain);
        m_autSettingWizardPage.setTitle(Messages.ProjectWizardAutSettings);
        m_autSettingWizardPage.setDescription(Messages.ProjectWizardNewAUT);
        m_autSettingWizardPage.setPageComplete(true);
        addPage(m_autSettingWizardPage);
        
        m_autConfigSettingWizardPage = 
            new AutConfigSettingWizardPage(AUT_CONFIG_SETTING_WP, 
                    m_autConfig, autIdValidator); 
        m_autConfigSettingWizardPage.setTitle(
                Messages.ProjectWizardAutSettings);
        m_autConfigSettingWizardPage.setDescription(
                Messages.ProjectWizardAUTData);
        m_autConfigSettingWizardPage.setPageComplete(true);
        addPage(m_autConfigSettingWizardPage);  
        
        m_projectInfoWizardPage =
            new ProjectInfoWizardPage(PROJECT_INFO_WP);
        m_projectInfoWizardPage.setTitle(Messages.ProjectWizardProjectSettings);
        m_projectInfoWizardPage.setDescription(
            Messages.ProjectWizardProjectCreated);
        m_projectInfoWizardPage.setPageComplete(true);
        addPage(m_projectInfoWizardPage); 
        Plugin.stopLongRunning();
    }

    /**
     * Creates a new project, stops a started AUT, closes all opened editors.
     * @param newProjectName the name for this project
     * @param monitor The progress monitor for this potentially long-running 
     *                operation.
     * @throws InterruptedException if the operation is canceled.
     */
    private void createNewProject(final String newProjectName, 
        IProgressMonitor monitor) throws InterruptedException {          
        
        Plugin.closeAllOpenedJubulaEditors(false);
        m_newProject.setIsReusable(
            m_projectSettingWizardPage.isProjectReusable());
        m_newProject.setIsProtected(
                m_projectSettingWizardPage.isProjectProtected());
        if (m_autMain.getName() == null
                || StringConstants.EMPTY.equals(m_autMain.getName())) {
            m_newProject.removeAUTMain(m_autMain);
        }
        if (m_autConfig.getName() == null
                || StringConstants.EMPTY.equals(m_autConfig.getName())) {
            
            m_autMain.removeAutConfig(m_autConfig);
        }
        ParamNameBPDecorator paramNameMapper = new ParamNameBPDecorator(
                ParamNameBP.getInstance());
        final IWritableComponentNameMapper compNamesMapper = 
            new ProjectComponentNameMapper(
                new ComponentNamesDecorator(null), m_newProject);
        List<INameMapper> mapperList = new ArrayList<INameMapper>();
        List<IWritableComponentNameMapper> compNameCacheList = 
            new ArrayList<IWritableComponentNameMapper>();
        addUnboundModules(m_newProject);
        mapperList.add(paramNameMapper);
        compNameCacheList.add(compNamesMapper);
        try {
            GeneralStorage.getInstance().reset();
            ProjectPM.attachProjectToROSession(m_newProject, newProjectName, 
                    mapperList, compNameCacheList, monitor);
        } catch (PMSaveException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(
                new PMSaveException(e.getMessage(), 
                    MessageIDs.E_CREATE_NEW_PROJECT_FAILED));
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        } catch (InterruptedException ie) {
            throw ie;
        }
    }

    /**
     * Adds appropriate testcase libraries to the given project's reused 
     * projects set.
     * 
     * @param newProject The project that will reuse the testcase libraries.
     */
    private void addUnboundModules(IProjectPO newProject) {

        // Use toolkit-specific module, and modules for all required toolkits
        ToolkitPluginDescriptor desc = 
            ComponentBuilder.getInstance().getCompSystem()
            .getToolkitPluginDescriptor(newProject.getToolkit());

        while (desc != null) {
            try {
                String moduleName = 
                    UNBOUND_MODULES_PREFIX + desc.getName().toLowerCase();
                IProjectPO ubmProject = 
                    ProjectPM.loadLatestVersionOfProjectByName(moduleName);
                if (ubmProject != null) {
                    newProject.addUsedProject(
                            PoMaker.createReusedProjectPO(ubmProject));
                } else {
                    if (log.isInfoEnabled()) {
                        log.info(Messages.Project + StringConstants.SPACE
                            + StringConstants.APOSTROPHE + moduleName
                            + StringConstants.APOSTROPHE + Messages.DoesNotExist
                            + StringConstants.DOT);
                    }
                }
            } catch (JBException e) {
                log.error(e + StringConstants.COLON + StringConstants.SPACE 
                        + e.getMessage());
            }
            desc = ComponentBuilder.getInstance().getCompSystem()
                .getToolkitPluginDescriptor(desc.getIncludes());
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean canFinish() {
        return getContainer().getCurrentPage().isPageComplete();
    }

    /**
     * @return the IAUTMainPO
     */
    public IAUTMainPO getAutMain() {
        return m_autMain;
    }    

}