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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public abstract class DatabaseConnectionInfo {

    /** name of <code>connectionUrl</code> property */
    public static final String PROP_NAME_CONN_URL = "connectionUrl"; //$NON-NLS-1$

    /** property change support */
    private PropertyChangeSupport m_propChangeSupport = 
        new PropertyChangeSupport(this);

    /**
     * 
     * @return the connection URL constructed based on the receiver's current
     *         information.
     */
    public abstract String getConnectionUrl();
    
    /**
     * Informs all property change listeners that the 
     * <code>connectionUrl</code> property has changed.
     */
    protected final void fireConnectionUrlChanged() {
        m_propChangeSupport.firePropertyChange(
                PROP_NAME_CONN_URL, null, getConnectionUrl());
    }
    
    /**
     * standard bean support
     * @param l standard bean support
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        m_propChangeSupport.addPropertyChangeListener(l);
    }

    /**
     * standard bean support
     * @param l standard bean support
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        m_propChangeSupport.removePropertyChangeListener(l);
    }
}
