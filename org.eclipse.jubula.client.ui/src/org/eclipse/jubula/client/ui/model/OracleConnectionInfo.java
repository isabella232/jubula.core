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
package org.eclipse.jubula.client.ui.model;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public class OracleConnectionInfo extends DatabaseConnectionInfo {

    /** name of <code>hostname</code> property */
    public static final String PROP_NAME_HOSTNAME = "hostname"; //$NON-NLS-1$

    /** name of <code>port</code> property */
    public static final String PROP_NAME_PORT = "port"; //$NON-NLS-1$

    /** name of <code>schema</code> property */
    public static final String PROP_NAME_SCHEMA = "schema"; //$NON-NLS-1$

    /** hostname of the computer on which the database is running */
    private String m_hostname = "localhost"; //$NON-NLS-1$
    
    /** port on which the database is running */
    private int m_port = 1521;
    
    /** the name of the database instance */
    private String m_schema = "jubula"; //$NON-NLS-1$
    
    @Override
    public String getConnectionUrl() {
        StringBuilder sb = new StringBuilder("jdbc:oracle:thin@"); //$NON-NLS-1$
        sb.append(StringUtils.defaultString(getHostname()))
            .append(":").append(getPort()) //$NON-NLS-1$
            .append(":").append(StringUtils.defaultString(getSchema())); //$NON-NLS-1$
        return sb.toString();
    }

    /**
     * 
     * @return the hostname of the computer on which the database is running.
     */
    public String getHostname() {
        return m_hostname;
    }

    /**
     * 
     * @param hostname The hostname of the computer on which the 
     *                 database is running.
     */
    public void setHostname(String hostname) {
        m_hostname = hostname;
        fireConnectionUrlChanged();
    }

    /**
     * 
     * @return the port on which the database is running.
     */
    public int getPort() {
        return m_port;
    }

    /**
     * 
     * @param port The port on which the database is running.
     */
    public void setPort(int port) {
        m_port = port;
        fireConnectionUrlChanged();
    }

    /**
     * 
     * @return the name of the database instance.
     */
    public String getSchema() {
        return m_schema;
    }

    /**
     * 
     * @param schema The name of the database instance.
     */
    public void setSchema(String schema) {
        m_schema = schema;
        fireConnectionUrlChanged();
    }
}
