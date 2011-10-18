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
package org.eclipse.jubula.client.ui.rcp.utils;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.JBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helper-class to manage the server preferences.
 *
 * @author BREDEX GmbH
 * @created 08.12.2005
 */
public class ServerManager {
    
    /** the logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(ServerManager.class);
    
    /**
     * <code>instance</code>single instance of ServerManager
     */
    private static ServerManager instance = null;    
    
    /**
     * <code>m_servers</code> all server read from preference store
     */
    private SortedSet<Server> m_servers = new TreeSet<Server>();
    
    /** last used server object*/
    private Server m_lastUsedServer = null;
    
    /**
     * <p>The constructor.</p>
     * <p>Fills the list with all stored server settings.</p>
     * <p>If there are no stored values, the default values will filled in the list</p>
     */
    private ServerManager() {
        readFromPrefStore();
    }
    
    
    /**
     * @return single instance of ServerManager
     */
    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }
    
    

    /**
     * 
     * Reads the pref storage to build the serverList.
     */
    private void readFromPrefStore() {

        IPreferenceStore prefStore = Plugin.getDefault().getPreferenceStore();
        String serversValue = 
                prefStore.getString(Constants.SERVER_SETTINGS_KEY);
        String lastUsedServerValue = 
                prefStore.getString(Constants.LAST_USED_SERVER_KEY);
        
        try {
            decodeServerPrefs(serversValue);
        } catch (JBException jbe) {
            LOG.error("Error occurred while loading AUT Agent preferences. Resetting to default values.", jbe); //$NON-NLS-1$
            prefStore.setToDefault(
                    Constants.SERVER_SETTINGS_KEY);
            try {
                decodeServerPrefs(serversValue);
            } catch (JBException e) {
                LOG.error("Error occurred while reading AUT Agent preferences default values.", jbe); //$NON-NLS-1$
            }
        }
        
        // set last used server
        if (!StringUtils.isEmpty(lastUsedServerValue)) {
            m_lastUsedServer = new Server(
                lastUsedServerValue.substring(0, lastUsedServerValue.indexOf(":")), //$NON-NLS-1$
                (new Integer(
                    lastUsedServerValue.substring(lastUsedServerValue.indexOf(":") + 1)))); //$NON-NLS-1$
        } else {
            m_lastUsedServer = null;
        }

    }

    /**
     * load the server list from the preference store into m_servers
     * @param store string read from preference store
     * @throws JBException in case of problem with preference store
     */
    private void decodeServerPrefs(String store) throws JBException {
        m_servers.clear();
        String[] serverStrings = StringUtils.split(store, ';');
        // We expect the length to be divisible by 2 (hostname;ports;)
        if (serverStrings.length % 2 == 0) {
            for (int i = 0; i < serverStrings.length; i += 2) {
                String hostname = decodeString(serverStrings[i]);

                // May be multiple ports. If so, then we create a server for each port.
                String[] encodedPorts = 
                        StringUtils.split(serverStrings[i + 1], ',');
                for (String encodedPort : encodedPorts) {
                    String port = decodeString(encodedPort);
                    m_servers.add(new Server(hostname, Integer.valueOf(port)));
                }
            }
            
        } else {
            throw new JBException("Number of entries in server list must be even.", Integer.valueOf(0)); //$NON-NLS-1$
        }

    }

    
    /**
     * @param encodedString A base64 encoded string.
     * @return the decoded string.
     * @throws JBException in case of not base64 encoded string
     */
    String decodeString(String encodedString) throws JBException {
        if (!Base64.isArrayByteBase64(encodedString.getBytes())) {
            throw new JBException(StringConstants.EMPTY, new Integer(0));
        }
        return new String(Base64.decodeBase64(encodedString.getBytes()));
    }

    /**
     * Adds a server to the list.
     * @param server The server to add.
     */
    public void addServer(Server server) {
        Validate.notNull(server, Messages.ServerObjectMustNotBeNull 
                + StringConstants.DOT);
        if (!server.getName().equals(StringConstants.EMPTY)
            && !m_servers.contains(server)) {
            m_servers.add(server);
        }
    }
    
    /**
     * Removes a server from the list.
     * @param server The server to remove.
     */
    public void removeServer(Server server) {
        if (m_lastUsedServer != null && m_lastUsedServer.equals(server)) {
            m_lastUsedServer = null;
        }
        m_servers.remove(server);
    }
    
    
    /**
     * Stores the server list in the preferences.
     * Old format (base64-encoded):
     *  hostname1;port1,port2,...;hostname2;port1,port2,...;
     *  
     * Current Format (base64-encoded):
     *  hostname1;port;hostname2;port;
     */
    public void storeServerList() {
        StringBuilder storage = new StringBuilder();
        for (Server server : m_servers) {
            // servername;port;
            byte[] serverArray = server.getName().getBytes();
            String serverEncoded = new String(Base64.encodeBase64(serverArray));
            storage.append(serverEncoded).append(";"); //$NON-NLS-1$
            storage.append(new String(Base64.encodeBase64(
                    server.getPort().toString().getBytes())));
            storage.append(";"); //$NON-NLS-1$
        }
        Plugin.getDefault().getPreferenceStore().setValue(
                Constants.SERVER_SETTINGS_KEY, storage.toString());
        if (m_lastUsedServer != null) {
            if (m_servers.contains(m_lastUsedServer)) {                
                Plugin.getDefault().getPreferenceStore().setValue(
                    Constants.LAST_USED_SERVER_KEY, 
                    buildLastUsedServerPortString(m_lastUsedServer));
            } else {
                m_lastUsedServer = null;
            }
        }
        DataEventDispatcher.getInstance().fireServerPreferencesChanged();
    }

    /**
     * @param lastUsedServer last used server as server object
     * @return last used server as string (servername:port)
     */
    private String buildLastUsedServerPortString(Server lastUsedServer) {
        if (lastUsedServer != null) {
            return lastUsedServer.getName() + ":"  //$NON-NLS-1$
                + lastUsedServer.getPort();
        } 
        Integer port = new Integer(-1);
        return StringConstants.EMPTY + ":" + port; //$NON-NLS-1$
    }
    
    
    
    
    /**
     * @param serverName The name of the wanted server.
     * @param port port of wanted server
     * @return The server object for the given server name.
     */
    public Server getServer(String serverName, Integer port) {
        Server serv = null;
        for (Server server : m_servers) {
            if (serverName.equals(server.getName())
                && server.getPort().equals(port)) {
                serv = server;
            }
        }
        return serv;
    }
    
    /**
     * validates, if a server name exists in server preferences
     * @param serverName name of server to validate
     * @return if server name exists in server preferences
     */
    public boolean containsServer(String serverName) {
        Validate.notNull(serverName);
        for (Server server : m_servers) {
            if (serverName.equals(server.getName())) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * @return all server names
     */
    public SortedSet <String> getServerNames() {
        SortedSet<String> serverNames = new TreeSet<String>();
        for (Server server : m_servers) {
            if (!StringConstants.EMPTY.equals(server.getName())) {
                serverNames.add(server.getName());
            }
        }
        return serverNames;
    }
    
    /**
     * @return Returns the last used server, if available in Preference Store
     * or null.
     */
    public Server getLastUsedServer() {
        return m_lastUsedServer;
    }

    /**
     * @return Returns the servers.
     */
    public SortedSet<Server> getServers() {
        return m_servers;
    }
    
    /**
     * @author BREDEX GmbH
     * @created 19.04.2006
     */
    public static class Server implements Comparable {
        /**
         * <code>m_name</code>server name
         */
        private String m_name;
        /**
         * <code>m_port</code>port number
         */
        private Integer m_port = new Integer(-1);
        
        /**
         * @param name server name
         * @param port associated port
         */
        public Server(String name, Integer port) {
            m_name = name;
            m_port = port;
        }

        /**
         * @return Returns the name.
         */
        public String getName() {
            return m_name;
        }

        /**
         * @return Returns the port.
         */
        public Integer getPort() {
            return m_port;
        }
        
        /**
         * @param name The name to set.
         */
        public void setName(String name) {
            m_name = name;
        }

        /**
         * @param port The port to set.
         */
        public void setPort(Integer port) {
            m_port = port;
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(Object o) {
            Server server = (Server)o;
            if (this.getName().compareTo(server.getName()) == 0) {
                return this.getPort().compareTo(server.getPort());
            } 
            return this.getName().compareTo(server.getName());           
        }

    }

    /**
     * @param servers The servers to set.
     */
    public void setServers(SortedSet<Server> servers) {
        m_servers = servers;
    }

    /**
     * @param server last used server
     */
    public void setLastUsedServer(Server server) {
        m_lastUsedServer = server;
    }
}