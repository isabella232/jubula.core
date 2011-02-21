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

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public class OracleConnectionInfo extends AbstractHostBasedConnectionInfo {
    
    /**
     * Constructor
     */
    public OracleConnectionInfo() {
        super(1521);
    }
    
    @Override
    public String getConnectionUrl() {
        StringBuilder sb = new StringBuilder("jdbc:oracle:thin:@"); //$NON-NLS-1$
        sb.append(StringUtils.defaultString(getHostname()))
            .append(":").append(getPort()) //$NON-NLS-1$
            .append(":").append(StringUtils.defaultString(getDatabaseName())); //$NON-NLS-1$
        return sb.toString();
    }

    @Override
    public String getDriverClassName() {
        return "oracle.jdbc.driver.OracleDriver"; //$NON-NLS-1$
    }
}
