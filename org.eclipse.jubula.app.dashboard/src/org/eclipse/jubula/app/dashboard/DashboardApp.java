/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.app.dashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jubula.app.dashboard.i18n.Messages;
import org.eclipse.jubula.client.core.constants.Constants;
import org.eclipse.jubula.client.core.persistence.DatabaseConnectionInfo;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.perspective.ReportPerspective;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author BREDEX GmbH
 * @created Sep 22, 2011
 */
public class DashboardApp implements IApplication {
    /** Name of the properties file */
    private static final String PROPERTIES_FILE_NAME = "dashboardserver.properties"; //$NON-NLS-1$

    /** Jubula home directory */
    private static final String JUBULA_HOME = ".jubula"; //$NON-NLS-1$

    /**
     * @author BREDEX GmbH
     */
    private static class DashboardWorkbenchWindowAdvisor 
        extends WorkbenchWindowAdvisor {

        /**
         * Constructor
         * 
         * @param configurer
         *            an IWorkbenchWindowConfigurer
         */
        public DashboardWorkbenchWindowAdvisor(
                IWorkbenchWindowConfigurer configurer) {
            super(configurer);
        }
        
        @Override
        public void preWindowOpen() {
            IWorkbenchWindowConfigurer config = getWindowConfigurer();
            config.setShowMenuBar(false);
            config.setShowStatusLine(false);
            super.preWindowOpen();
        }
        
        @Override
        public void postWindowOpen() {
            super.postWindowOpen();
        }
    }
    
    /**
     * @author BREDEX GmbH
     */
    private static class DashboardWindowAdvisor extends WorkbenchAdvisor {
        /** the connection info */
        private DatabaseConnectionInfo m_connectionInfo;
        /** the user  name */
        private String m_username;
        /** the password */
        private String m_password;

        /**
         * Constructor
         * 
         * @param connectionInfo
         *            connectionInfo
         * @param username
         *            users name
         * @param password
         *            password
         */
        public DashboardWindowAdvisor(DatabaseConnectionInfo connectionInfo,
            String username, String password) {
            m_connectionInfo = connectionInfo;
            m_username = username;
            m_password = password;
        }

        @Override
        public String getInitialWindowPerspectiveId() {
            return ReportPerspective.PERSPECTIVE_ID;
        }

        @Override
        public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            IWorkbenchWindowConfigurer configurer) {
            return new DashboardWorkbenchWindowAdvisor(configurer);
        }
        
        @Override
        public void postStartup() {
            Persistor.setDbConnectionName(m_connectionInfo);
            Persistor.setUser(m_username);
            Persistor.setPw(m_password);
            
            m_connectionInfo = null;
            m_username = null;
            m_password = null;

            Persistor.init();
            
            Map<String, String[]> parameterMap = RWT
                .getRequest().getParameterMap();
            handleDashboardRequestParameter(parameterMap);
            
            super.postStartup();
        }
        
        /**
         * @param parameterMap
         *            the parameter map to handle
         */
        private void handleDashboardRequestParameter(
            Map<String, String[]> parameterMap) {
            String[] summaryIdParameter = parameterMap
                .get(Constants.DASHBOARD_SUMMARY_PARAM);
            String[] resultNodeParameter = parameterMap
                .get(Constants.DASHBOARD_RESULT_NODE_PARAM);
            if (summaryIdParameter != null && resultNodeParameter != null) {
                try {
                    Long summaryId = Long.valueOf(summaryIdParameter[0]);
                    Long nodeCount = Long.valueOf(resultNodeParameter[0]);
                    openTestResultDetailAndSelectNode(summaryId, nodeCount);
                } catch (NumberFormatException nfe) {
                    // ignore
                }
            }
        }
        
        /**
         * @param summaryId
         *            the summary id
         * @param nodeCount
         *            the index of the node to select
         */
        private void openTestResultDetailAndSelectNode(final Long summaryId,
            final Long nodeCount) {
            new OpenTestResultDetailsJob(NLS.bind(
                Messages.OpeningTestResultDetailsJobName, summaryId),
                summaryId, nodeCount, PlatformUI.getWorkbench().getDisplay())
                .schedule(1000);
        }
    }
    
    /**
     * @author BREDEX GmbH
     */
    private static class OpenTestResultDetailsJob extends Job {
        /**
         * the id of the summary
         */
        private Long m_summaryId;
        
        /**
         * the node to select by index (count)
         */
        private Long m_nodeCount;
        /**
         * the display
         */
        private Display m_display;

        /**
         * Constructor
         * 
         * @param name
         *            the name of the job
         * @param nodeCount
         *            the node to select
         * @param summaryId
         *            the summary id to open the details for
         * @param display
         *            the display to use
         */
        public OpenTestResultDetailsJob(String name, Long summaryId,
            Long nodeCount, Display display) {
            super(name);
            m_summaryId = summaryId;
            m_nodeCount = nodeCount;
            m_display = display;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            UICallBack.runNonUIThreadWithFakeContext(m_display, new Runnable() {
                public void run() {
                    final Command projectPropertiesCommand = CommandHelper
                        .getCommandService().getCommand(
                            CommandIDs.OPEN_TEST_RESULT_DETAIL_COMMAND_ID);
                    final Map<String, String> parameters = 
                        new HashMap<String, String>();
                    parameters.put(CommandIDs.
                        OPEN_TEST_RESULT_DETAIL_COMMAND_PARAMETER_SUMMARY_ID,
                            m_summaryId.toString());
                    parameters.put(CommandIDs.
                        OPEN_TEST_RESULT_DETAIL_COMMAND_PARAMETER_NODE_ID,
                            m_nodeCount.toString());
                    CommandHelper
                        .executeParameterizedCommand(ParameterizedCommand
                            .generateCommand(projectPropertiesCommand,
                                parameters));
                }
            });
            return Status.OK_STATUS;
        }
    }
    
    
    /** Property name: JDBC driver class name */
    private static final String PROP_DRIVER_CLASS_NAME = 
            "org.eclipse.jubula.dashboard.jdbc_driver"; //$NON-NLS-1$

    /** Property name: JDBC connection URL */
    private static final String PROP_JDBC_URL = 
            "org.eclipse.jubula.dashboard.jdbc_url"; //$NON-NLS-1$

    /** Property name: JDBC connection user name */
    private static final String PROP_USERNAME = 
            "org.eclipse.jubula.dashboard.database_username"; //$NON-NLS-1$

    /** Property name: JDBC connection password */
    private static final String PROP_PASSWORD = 
            "org.eclipse.jubula.dashboard.database_password"; //$NON-NLS-1$

    /**
     *  
     * {@inheritDoc} 
     */
    public Object start(IApplicationContext context) throws Exception {
        final String testdriverClassName = System
                .getProperty(PROP_DRIVER_CLASS_NAME);
        if (testdriverClassName == null) {
            FileInputStream configFileInputStream = null;
            Properties configuration = new Properties();
            try {
                String path = System
                        .getenv("TEST_DASHBOARD_PROPERTIES"); //$NON-NLS-1$
                if (StringUtils.isBlank(path)) {
                    String home = System.getProperty(
                            "TEST_DASHBOARD_PROPERTIES");  //$NON-NLS-1$
                }
                if (StringUtils.isBlank(path)) {
                    String home = System.getProperty("user.home");  //$NON-NLS-1$
                    path = home + File.separator + JUBULA_HOME;
                }
                path += File.separator + PROPERTIES_FILE_NAME;
                configFileInputStream = new FileInputStream(path);
                configuration.load(configFileInputStream);
                System.getProperties().putAll(configuration);
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe.getLocalizedMessage());
            } finally {
                if (configFileInputStream != null) {
                    configFileInputStream.close();
                }
            }
        }
        final String connectionUrl = System.getProperty(PROP_JDBC_URL);
        final String username = System.getProperty(PROP_USERNAME);
        final String password = System.getProperty(PROP_PASSWORD);
        final String driverClassName = System
                .getProperty(PROP_DRIVER_CLASS_NAME);

        final DatabaseConnectionInfo connectionInfo = 
            new DatabaseConnectionInfo() {
                @Override
                public String getDriverClassName() {
                    return driverClassName;
                }
    
                @Override
                public String getConnectionUrl() {
                    return connectionUrl;
                }
            };

        return PlatformUI.createAndRunWorkbench(PlatformUI.createDisplay(),
                new DashboardWindowAdvisor(connectionInfo, username, password));
    }
    
    /**
     *  
     * {@inheritDoc} 
     */
    public void stop() {
        // currently empty
    }
}
