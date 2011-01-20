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
package org.eclipse.jubula.client.core.businessprocess;

import javax.persistence.EntityManager;

import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;


/**
 * @author BREDEX GmbH
 * @created May 7, 2008
 */
public interface INameMapper {
    
    /**
     * Writes in database, but does not perform a commit. This method may 
     * acquire locks on objects or even entire tables. It is the 
     * responsibility of the caller to ensure that the transaction is committed
     * or rolled back in a timely manner in order to avoid unneccessarily long 
     * locking times.
     * 
     * @param s session to use
     * @param projectId id of rootProject       
     * @throws PMException in case of any db problem
     * @throws ProjectDeletedException if current project is already deleted
     * @throws IncompatibleTypeException if the type of any persisted name is
     *                                   incompatible with the type of the 
     *                                   already existing name in the database
     */
    public void persist(EntityManager s, Long projectId) 
        throws PMException, ProjectDeletedException, 
               IncompatibleTypeException;
    
    /**
     * updates in Mastersession
     * @param projectId id of rootProject   
     */
    public void updateStandardMapperAndCleanup(Long projectId);
    
    /**
     * Clears the caches of names which are to edit in the database.
     */
    public void clearAllNames();
}
