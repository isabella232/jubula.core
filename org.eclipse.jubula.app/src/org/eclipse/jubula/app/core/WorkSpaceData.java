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
package org.eclipse.jubula.app.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jubula.app.i18n.Messages;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * This class stores the information behind the "Launch Workspace" dialog. The
 * class is able to read and write itself to a well known configuration file.
 * @author BREDEX GmbH
 * @created 18.12.2006
 */
public class WorkSpaceData {
    /**
     * The default max length of the recent workspace mru list. The values
     * stored in xml (both the max-length parameter and actual size of the list)
     * will supersede this value.
     */
    private static final int RECENT_MAX_LENGTH = 5;

    /**
     * The directory within the config area that will be used for the receiver's
     * persisted data.
     */
    private static final String PERS_FOLDER = "org.eclipse.jubula.app"; //$NON-NLS-1$

    /**
     * The name of the file within the config area that will be used for the
     * recever's persisted data.
     * 
     * {@inheritDoc}
     */
    private static final String PERS_FILENAME = "recentWorkspaces.xml"; //$NON-NLS-1$
    /***/
    private static final int PERS_ENCODING_VERSION = 1;
    
    /** for log messages */
    private static Log log = LogFactory.getLog(WorkSpaceData.class);
    /** the initial default */
    private String m_initialDefault;
    /** the selected workspace*/
    private String m_selection;
    /** all recent workspaces */
    private String[] m_recentWorkspaces;

    /** xml tags */
    private static interface XML {
        /** the protocol */
        public static final String PROTOCOL = "protocol"; //$NON-NLS-1$
        /** the version */
        public static final String VERSION = "version"; //$NON-NLS-1$
        /** the workspace */
        public static final String WORKSPACE = "workspace"; //$NON-NLS-1$
        /** last used workspace */
        public static final String LAST_USED_WORKSPACE = "lastUsedWorkspace"; //$NON-NLS-1$
        /** all recent workspaces */
        public static final String RECENT_WORKSPACES = "recentWorkspaces"; //$NON-NLS-1$
        /** the max quantity of workspaces to store */
        public static final String MAX_LENGTH = "maxLength"; //$NON-NLS-1$
        /** the path */
        public static final String PATH = "path"; //$NON-NLS-1$
    }

    /**
     * Creates a new instance, loading persistent data if its found.
     * @param initialDefault The inital default.
     */
    public WorkSpaceData(String initialDefault) {
        readPersistedData();
        m_initialDefault = initialDefault;
    }

    /**
     * @return the folder to be used as a default if no other information exists.
     * Does not return null.
     */
    public String getInitialDefault() {
        if (m_initialDefault == null) {
            m_initialDefault = System.getProperty("user.dir") + File.separator //$NON-NLS-1$
                + "jubulaWorkspace"; //$NON-NLS-1$
        }
        return m_initialDefault;
    }

    /**
     * @return The currently selected workspace or null if nothing is selected.
     */
    public String getSelection() {
        return m_selection;
    }

    /**
     * @return An array of recent workspaces sorted with the most recently used at the start.
     */
    public String[] getRecentWorkspaces() {
        return m_recentWorkspaces;
    }

    /**
     * The argument workspace has been selected, update the receiver. Does not persist the new values.
     * @param dir The selected workspace directory.
     */
    public void workspaceSelected(String dir) {
        // this just stores the m_selection, it is not inserted and persisted
        // until the workspace is actually selected
        m_selection = dir;
    }

    /**
     * Update the persistent store. Call this function after the currently
     * selected value has been found to be ok.
     * @param shell The actual shell.
     */
    public void writePersistedData(Shell shell) {
        Location configLoc = Platform.getUserLocation();
        if (configLoc == null || configLoc.isReadOnly()) {
            return;
        }
        URL persUrl = getPersistenceUrl(configLoc.getURL(), true);
        if (persUrl == null) {
            return;
        }
        // move the new m_selection to the front of the list
        if (m_selection != null) {
            String oldEntry = m_recentWorkspaces[0];
            m_recentWorkspaces[0] = m_selection;
            for (int i = 1; i < m_recentWorkspaces.length && oldEntry != null; 
                ++i) {               
                if (m_selection.equals(oldEntry)) {
                    break;
                }
                String tmp = m_recentWorkspaces[i];
                m_recentWorkspaces[i] = oldEntry;
                oldEntry = tmp;
            }
        }
        Writer writer = null;
        try {
            writer = new FileWriter(persUrl.getFile());

            // E.g.,
            //  <launchWorkspaceData>
            //      <protocol version="1"/>
            //      <recentWorkspaces maxLength="5">
            //      <lastUsedWorkspace="C:\eclipse\workspace1"/>
            //          <workspace path="C:\eclipse\workspace0"/>
            //          <workspace path="C:\eclipse\workspace1"/>
            //      </recentWorkspaces>
            //  </launchWorkspaceData>

            XMLMemento memento = XMLMemento
                .createWriteRoot(Messages.LaunchWorkspaceData);

            memento.createChild(XML.PROTOCOL).putInteger(XML.VERSION,
                PERS_ENCODING_VERSION);
            memento.createChild(XML.LAST_USED_WORKSPACE).putString(XML.PATH,
                m_selection);
            IMemento recentMemento = memento.createChild(XML.RECENT_WORKSPACES);
            recentMemento.putInteger(XML.MAX_LENGTH, m_recentWorkspaces.length);
            for (int i = 0; i < m_recentWorkspaces.length; ++i) {
                if (m_recentWorkspaces[i] == null) {
                    break;
                }
                recentMemento.createChild(XML.WORKSPACE).putString(XML.PATH,
                    m_recentWorkspaces[i]);
            }
            memento.save(writer);
        } catch (IOException e) {
            log.error(Messages.UnableToWriteWorkspaceData, e);
            MessageDialog.openError(shell, 
                    Messages.WorkSpaceDataCantWriteDataTitle,
                Messages.WorkSpaceDataCanWriteData);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Look for and read data that might have been persisted from some previous
     * run. Leave the receiver in a default state if no persistent data is
     * found.
     */
    public void readPersistedData() {
        URL persUrl = null;

        Location configLoc = Platform.getUserLocation();
        if (configLoc != null) {
            persUrl = getPersistenceUrl(configLoc.getURL(), true);
        }
        try {
            // inside try to get the safe default creation in the finally
            // clause
            if (persUrl == null) {
                return;
            }
            // E.g.,
            //  <launchWorkspaceData>
            //      <protocol version="1"/>
            //      <lastUsedWorkspace="C:\eclipse\workspace1"/>
            //      <recentWorkspaces maxLength="5">
            //          <workspace path="C:\eclipse\workspace0"/>
            //          <workspace path="C:\eclipse\workspace1"/>
            //      </recentWorkspaces>
            //  </launchWorkspaceData>

            Reader reader = new FileReader(persUrl.getFile());
            XMLMemento memento = XMLMemento.createReadRoot(reader);
            if (memento == null || !compatibleProtocol(memento)) {
                return;
            }
            IMemento recent = memento.getChild(XML.RECENT_WORKSPACES);
            if (recent == null) {
                return;
            }
            Integer maxLength = recent.getInteger(XML.MAX_LENGTH);
            int max = RECENT_MAX_LENGTH;
            if (maxLength != null) {
                max = maxLength.intValue();
            }
            IMemento indices[] = recent.getChildren(XML.WORKSPACE);
            if (indices == null || indices.length <= 0) {
                return;
            }
            // if a user has edited maxLength to be shorter than the listed
            // indices, accept the list (its tougher for them to retype a long
            // list of paths than to update a max number)
            max = Math.max(max, indices.length);

            m_recentWorkspaces = new String[max];
            for (int i = 0; i < indices.length; ++i) {
                String path = indices[i].getString(XML.PATH);
                if (path == null) {
                    break;
                }
                m_recentWorkspaces[i] = path;
            }
            IMemento lastUsed = memento.getChild(XML.LAST_USED_WORKSPACE);
            if (lastUsed == null) {
                m_selection = m_initialDefault;
                return;
            }
            m_selection = lastUsed.getString(XML.PATH);
            return;
        } catch (IOException e) {
            // do nothing -- cannot log because instance area has not been set
        } catch (WorkbenchException e) {
            // do nothing -- cannot log because instance area has not been set
        } finally {
            // create safe default if needed
            if (m_recentWorkspaces == null) {
                m_recentWorkspaces = new String[RECENT_MAX_LENGTH];
            }
        }
    }

    /**
     * @return True if the protocol used to encode the argument memento is
     * compatible with the receiver's implementation and false otherwise.
     * @param memento The memento.
     */
    private static boolean compatibleProtocol(IMemento memento) {
        IMemento protocolMemento = memento.getChild(XML.PROTOCOL);
        if (protocolMemento == null) {
            return false;
        }
        Integer version = protocolMemento.getInteger(XML.VERSION);
        return version != null && version.intValue() == PERS_ENCODING_VERSION;
    }

    /**
     * The workspace data is stored in the well known file pointed to by the
     * result of this method.
     * @param create If the directory and file does not exist this parameter controls whether it will be created.
     * @param baseUrl The base url.
     * @return An url to the file and null if it does not exist or could not be created.
     */
    private static URL getPersistenceUrl(URL baseUrl, boolean create) {
        if (baseUrl == null) {
            return null;
        }
        try {
            // make sure the base directory exists
            URL url = new URL(baseUrl, PERS_FOLDER);
            File baseDir = new File(baseUrl.getFile());
            if (!baseDir.exists() && (!create || !baseDir.mkdir())) {
                return null;
            }
            // make sure the sub directory exists
            File dir = new File(url.getFile());
            if (!dir.exists() && (!create || !dir.mkdir())) {
                return null;
            }
            // make sure the file exists
            url = new URL(dir.toURL(), PERS_FILENAME);
            File persFile = new File(url.getFile());
            if (!persFile.exists() && (!create || !persFile.createNewFile())) {
                return null;
            }
            return persFile.toURL();
        } catch (IOException e) {
            // cannot log because instance area has not been set
            return null;
        }
    }
}
