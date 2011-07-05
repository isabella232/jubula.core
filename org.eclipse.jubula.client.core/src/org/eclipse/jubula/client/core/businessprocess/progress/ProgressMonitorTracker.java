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
package org.eclipse.jubula.client.core.businessprocess.progress;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author BREDEX GmbH
 * @created Nov 26, 2010
 */
public class ProgressMonitorTracker {

    /** the single instance */
    private static ProgressMonitorTracker instance = null;
    
    /** The monitor to which the interceptor reports progress */
    private IProgressMonitor m_monitor;

    /** 
     * set of Persistence (JPA / EclipseLink) IDs for elements that have already been reported
     * as flushed
     */
    private Set<Serializable> m_collectionOwnerIds;

    /**
     * Private constructor
     */
    private ProgressMonitorTracker() {
        m_collectionOwnerIds = new HashSet<Serializable>();
    }
    
    /**
     * 
     * @return the single instance.
     */
    public static ProgressMonitorTracker getInstance() {
        if (instance == null) {
            instance = new ProgressMonitorTracker();
        }
        
        return instance;
    }

    /**
     * 
     * @param monitor The new progress monitor for database access. A value of
     *                <code>null</code> clears the monitor, meaning that the
     *                database access will no longer issue progress updates.
     */
    public void setProgressMonitor(IProgressMonitor monitor) {
        m_monitor = monitor;
        if (monitor == null) {
            m_collectionOwnerIds.clear();
        }
    }

    /**
     * 
     * @return the progress monitor currently being used to monitor 
     *         database access. 
     */
    public IProgressMonitor getProgressMonitor() {
        return m_monitor;
    }
}
