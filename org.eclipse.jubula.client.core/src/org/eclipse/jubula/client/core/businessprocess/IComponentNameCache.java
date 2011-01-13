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

import java.util.Set;

import org.eclipse.jubula.client.core.model.IComponentNameData;
import org.eclipse.jubula.client.core.model.IComponentNamePO;


/**
 * @author BREDEX GmbH
 * @created Feb 13, 2009
 */
public interface IComponentNameCache {

    /**
     * Clears all Component Name information from the cache.
     */
    public void clear();

    /**
     * Copies all modification information contained in this cache to the master
     * cache, and then clears this cache.
     * 
     * @param activeProjectId The ID of the currently active project.
     */
    public void updateStandardMapperAndCleanup(Long activeProjectId);

    /**
     * 
     * @param guid The GUID of a Component Name.
     * @return the Component Name with the given GUID, or <code>null</code> if
     *         no such Component Name can be found.
     */
    public IComponentNamePO getCompNamePo(String guid);

    /**
     * 
     * @param guid The GUID of a Component Name.
     * @param resolveRefs <code>true</code> if the references to other 
     *                    Component Names should be followed. 
     *                    Otherwise <code>false</code>.
     * @return the Component Name with the given GUID, or <code>null</code> if
     *         no such Component Name can be found.
     */
    public IComponentNamePO getCompNamePo(String guid, boolean resolveRefs);

    /**
     * 
     * @param guid The GUID of a Component Name.
     * @return the name of the Component Name with the given GUID, or 
     *         <code>guid</code> if no such Component Name can be found.
     */
    public String getName(String guid);

    /**
     * Returns the GUID of the Component Name with the given <code>name</code>, 
     * if the Component Name exists. First, the local context is searched. If 
     * no Component Name is found locally, a more global search is used. If the
     * Component Name still cannot be found, <code>null</code> will be returned.
     * 
     * @param name The name to search for.
     * @return the GUID of the Component Name with name equal to 
     *         <code>name</code>, or <code>null</code> if no such
     *         Component Name exists.
     */
    public String getGuidForName(String name);

    /**
     * Returns the GUID of the Component Name with the given <code>name</code>
     * within the Project with ID <code>parentProjectId</code>, if the 
     * Component Name exists. This search ignores Component Names from 
     * Reused Projects.
     * 
     * @param name The name to search for.
     * @param parentProjectId The ID of the Project in which to search. If
     *                        <code>null</code>, the ID of the currently open
     *                        Project will be used, if a Project is currently
     *                        open.
     * @return the GUID of the Component Name with name equal to 
     *         <code>name</code> within the Project with ID 
     *         <code>parentProjectId</code>, or <code>null</code> if no such
     *         Component Name exists.
     */
    public String getGuidForName(String name, Long parentProjectId);
    
    /**
     * 
     * @return data for all Component Names reachable from this cache.
     */
    public Set<IComponentNameData> getComponentNameData();

    /**
     * 
     * @return data for all Component Names local to this cache.
     */
    public Set<IComponentNameData> getLocalComponentNameData();

}