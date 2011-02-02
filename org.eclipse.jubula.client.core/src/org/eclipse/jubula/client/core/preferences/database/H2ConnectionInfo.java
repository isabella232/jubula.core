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

import org.eclipse.jubula.client.core.persistence.DatabaseConnectionInfo;

/**
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public class H2ConnectionInfo extends DatabaseConnectionInfo {

    /** name of <code>location</code> property */
    public static final String PROP_NAME_LOCATION = "location"; //$NON-NLS-1$
    
    /** the location of the database files (on the filesystem) */
    private String m_location = "~/jubula-db"; //$NON-NLS-1$
    
    /**
     * 
     * @return the location of the database files (on the filesystem).
     */
    public String getLocation() {
        return m_location;
    }

    /**
     * 
     * @param location The location of the database files (on the filesystem).
     */
    public void setLocation(String location) {
        m_location = location;
        fireConnectionUrlChanged();
    }
    
    @Override
    public String getConnectionUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:h2:") //$NON-NLS-1$
            .append(getLocation())
            .append(";MVCC=TRUE;AUTO_SERVER=TRUE"); //$NON-NLS-1$
        return sb.toString();
    }

}
