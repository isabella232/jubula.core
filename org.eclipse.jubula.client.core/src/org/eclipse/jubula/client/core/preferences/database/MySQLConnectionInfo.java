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
package org.eclipse.jubula.client.core.preferences.database;

/**
 * 
 * @author BREDEX GmbH
 * @created 04.02.2011
 */
public class MySQLConnectionInfo extends AbstractHostBasedConnectionInfo {

    /**
     * Constructor
     */
    public MySQLConnectionInfo() {
        super(3306);
    }
    
    @Override
    public String getConnectionUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("jdbc:mysql://").append(getHostname()) //$NON-NLS-1$
            .append(":").append(getPort()) //$NON-NLS-1$
            .append("/").append(getDatabaseName()); //$NON-NLS-1$
        return urlBuilder.toString();
    }

    @Override
    public String getDriverClassName() {
        return "com.mysql.jdbc.Driver"; //$NON-NLS-1$
    }

}
