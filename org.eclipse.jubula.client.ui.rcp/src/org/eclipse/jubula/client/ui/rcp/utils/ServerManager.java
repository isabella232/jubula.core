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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.constants.PreferenceConstants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.JBException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * Helper-class to manage the server preferences.
 *
 * @author BREDEX GmbH
 * @created 08.12.2005
 */
public class ServerManager {
    /**
     * <code>instance</code>single instance of ServerManager
     */
    private static ServerManager instance = null;    
    
    /**
     * <code>m_servers</code> all server read from preference store
     */
    private SortedSet<Server> m_servers = new TreeSet<Server>();
    
    /**
     * <code>jreCont</code>container to manage associations between server and
     * corresponding JREs (key is server name)
     */
    private Map<String, SortedSet<String>> m_jreCont = 
        new HashMap<String, SortedSet<String>>();    
    
    
    /** last used server object*/
    private Server m_lastUsedServer = null;
    
    /**
     * <code>m_defaultServer</code> default server result from
     * installation information
     */
    private Server m_defaultServer = null;
    
    /**
     * <p>The constructor.</p>
     * <p>Fills the list with all stored server settings.</p>
     * <p>If there are no stored values, the default values will filled in the list</p>
     */
    private ServerManager() {
        try {
            readFromPrefStore(false);
            if (m_servers.isEmpty()) {
                setDefaultValues();
            }
        } catch (StringIndexOutOfBoundsException e) {
            handleError();
        } catch (JBException e) {
            handleError();
        }
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
     * Reads the pref storage to build the serverList.
     * @param restored True, if default-values should be restored.
     * @throws StringIndexOutOfBoundsException if prefs are out-of-date/wrong.
     * @throws JBException if prefs are out-of-date/wrong.
     */
    private void readFromPrefStore(boolean restored) 
        throws StringIndexOutOfBoundsException, JBException {
        
        String storage = StringConstants.EMPTY;
        if (restored) {
            // add default server from installation
            addServer(getDefaultServer());
        } else {
            // set last used server
            String serverPort = Plugin.getDefault().getPreferenceStore()
                .getString(Constants.LAST_USED_SERVER_KEY);
            if (serverPort != null 
                && !serverPort.equals(StringConstants.EMPTY)
                && !serverPort.equals(":-1")) { //$NON-NLS-1$
                m_lastUsedServer = new Server(
                    serverPort.substring(0, serverPort.indexOf(":")), //$NON-NLS-1$
                    (new Integer(
                        serverPort.substring(serverPort.indexOf(":") + 1)))); //$NON-NLS-1$
                m_servers.add(m_lastUsedServer);
                
            } else {
                m_lastUsedServer = null;
            }
            // set server list
            storage = Plugin.getDefault().getPreferenceStore().getString(
                    PreferenceConstants.SERVER_SETTINGS_KEY);
            String defServer = Plugin.getDefault().getPreferenceStore()
                .getDefaultString(PreferenceConstants.SERVER_SETTINGS_KEY);
            if (storage != null && !storage.equals(defServer)) {
                decodeServerPrefs(storage);
            }
            if (m_servers.isEmpty()) {
                addServer(getDefaultServer());
            }
        }
    }

    /**
     * load the server list from the preference store into m_servers
     * @param store string read from preference store
     * @throws JBException in case of problem with preference store
     */
    private void decodeServerPrefs(String store) 
        throws JBException {
        m_servers.clear();
        String storage = store;
        while (storage.length() > 0) {
            String serverName = decodeString(storage, ";"); //$NON-NLS-1$
            storage = storage.substring(storage.indexOf(";") + 1); //$NON-NLS-1$
            String portPart = storage.substring(0, storage.indexOf(";")); //$NON-NLS-1$
            while (portPart.length() > 0) {
                String port = decodeString(portPart, ","); //$NON-NLS-1$
                portPart = portPart.substring(portPart.indexOf(",") + 1); //$NON-NLS-1$
                m_servers.add(new Server(serverName, new Integer(port)));
            }
            storage = storage.substring(storage.indexOf(";") + 1); //$NON-NLS-1$
            String jrePart = storage.substring(0, storage.indexOf(";")); //$NON-NLS-1$
            SortedSet <String> jres = new TreeSet<String>();
            while (jrePart.length() > 0) {
                String jre = decodeString(jrePart, ","); //$NON-NLS-1$
                jrePart = jrePart.substring(jrePart.indexOf(",") + 1); //$NON-NLS-1$
                jres.add(jre);
            }
            storage = storage.substring(storage.indexOf(";") + 1); //$NON-NLS-1$
            m_jreCont.put(serverName, jres);
        }
    }

    
    /**
     * @param encodedString decode a base64 encoded string
     * @param delimiter delimiter to separate string part from index 0 to delimiter
     * @return decoded string part
     * @throws JBException in case of not base64 decoded string
     */
    String decodeString(String encodedString, String delimiter) 
        throws JBException {
        String decodedString = StringConstants.EMPTY;
        checkPreferences(encodedString.substring(0, 
            encodedString.indexOf(delimiter)));
        decodedString = new String(Base64.decodeBase64(
            encodedString.substring(0, 
                encodedString.indexOf(delimiter)).getBytes()));
        return decodedString;
    }

    /**
     * Sets the default values from the store.
     */
    public void setDefaultValues() {
        m_servers.clear();
        try {
            readFromPrefStore(true);
        } catch (JBException e) {
            // correct: do nothing
        }
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
     * adds a jre to the server
     * @param serverName name of server, which get a new JRE
     * @param jre jre to add
     */
    public void addJRE(String serverName, String jre) {
        if (m_jreCont.get(serverName) != null) {
            SortedSet<String> jres = m_jreCont.get(serverName);
            if (!jres.contains(jre)) {
                jres.add(jre);
            }
        } else {
            SortedSet<String> jres = new TreeSet<String>();
            jres.add(jre);
            m_jreCont.put(serverName, jres);
        }
    }
    
    /**
     * remove a jre from a server
     * @param serverName name of server, for which is to remove a JRE
     * @param jre jre to add
     */
    public void removeJRE(String serverName, String jre) {
        if (m_jreCont.get(serverName) != null) {
            SortedSet<String> jres = m_jreCont.get(serverName);
            if (jres.contains(jre)) {
                jres.remove(jre);
                if (jres.isEmpty()) {
                    m_jreCont.remove(serverName);
                }
            }
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
        m_jreCont.remove(server);
    }
    
    
    /**
     * Stores the server list in the preferences.
     */
    public void storeServerList() {
        String storage = StringConstants.EMPTY;
        for (Server server : m_servers) {
            // servername;
            byte[] serverArray = server.getName().getBytes();
            String serverEncoded = new String(Base64.encodeBase64(serverArray));
            storage = storage + serverEncoded + ";"; //$NON-NLS-1$
            // servername:port1,port2,...,;
            for (Integer port : getAllPorts(server.getName())) {
                serverArray = port.toString().getBytes();
                serverEncoded = new String(Base64.encodeBase64(serverArray));
                storage = storage + serverEncoded + ","; //$NON-NLS-1$
            }
            storage = storage + ";"; //$NON-NLS-1$
            // servername:port1,port2,...,;jre1,jre2,...,;
            if (m_jreCont.get(server.getName()) != null) {
                for (String jre : m_jreCont.get(server.getName())) {
                    serverArray = jre.getBytes();
                    serverEncoded = 
                        new String(Base64.encodeBase64(serverArray));
                    storage = storage + serverEncoded + ","; //$NON-NLS-1$
                }
            }
            storage = storage + ";"; //$NON-NLS-1$
        }        
        Plugin.getDefault().getPreferenceStore().setValue(
                PreferenceConstants.SERVER_SETTINGS_KEY, storage);
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
     * @param name name of given server
     * @return all port for given server name
     */
    public SortedSet <Integer> getPorts(String name) {
        Validate.notNull(name);
        SortedSet<Integer> ports = new TreeSet<Integer>();
        for (Server server : m_servers) {
            if (name.equals(server.getName())) {
                ports.add(server.getPort());
            }
        }
        return ports;      
    }
    
    /**
     * @param serverName servername associated with jre list
     * @return jre list for given server, if available or an empty list
     */
    public SortedSet<String> getJREs(String serverName) {
        SortedSet<String> jres = new TreeSet<String>();
        if (m_jreCont.get(serverName) != null) {
            jres = m_jreCont.get(serverName);
        }
        return jres;
    }
    
    /**
     * @param serverName name of server
     * @return all ports associated with the given server name
     */
    public List<Integer> getAllPorts(String serverName) {
        List<Integer> ports = new ArrayList<Integer>();
        for (Server server : m_servers) {
            if (serverName.equals(server.getName())) {
                ports.add(server.getPort());
            }
        }
        return ports;
    }
    
    
    /**
     * @return Returns the last used server, if available in Preference Store
     * or null.
     */
    public Server getLastUsedServer() {
        return m_lastUsedServer;
    }


    
    /**
     * Checks correctness of the stored preferences.
     * @param pref The readed, base64-coded preference.
     * @throws JBException if the prefrence is not base64-coded.
     */
    private void checkPreferences(String pref) throws JBException {
        if (!Base64.isArrayByteBase64(pref.getBytes())) {
            throw new JBException(StringConstants.EMPTY, new Integer(0));
        }
    }
    
    /**
     * Shows an error-dialog/stores the default prefs, if some preferences are wrong
     */
    private void handleError() {
        ErrorHandlingUtil.createMessageDialog(MessageIDs.I_WRONG_SERVER_PREFS);
        setDefaultValues();
        storeServerList();
    }

    /**
     * @return Returns the defaultServer.
     */
    public Server getDefaultServer() {
        if (m_defaultServer == null) {
            String defServer = Plugin.getDefault().getPreferenceStore()
                .getDefaultString(PreferenceConstants.SERVER_SETTINGS_KEY);
            String[] str = defServer.split(StringConstants.COLON);
            m_defaultServer = new Server(str[0], new Integer(str[1]));
        }
        return m_defaultServer;
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
     * @param jreCont The jreCont to set.
     */
    public void setJreCont(Map<String, SortedSet<String>> jreCont) {
        m_jreCont = jreCont;
    }


    /**
     * @param server last used server
     */
    public void setLastUsedServer(Server server) {
        m_lastUsedServer = server;
    }
}