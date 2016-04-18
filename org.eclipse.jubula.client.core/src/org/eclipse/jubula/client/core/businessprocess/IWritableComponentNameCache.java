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

import java.util.Collection;
import java.util.Set;

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.persistence.PMException;


/**
 * Caching mechanism for Component Names.
 *
 * @author BREDEX GmbH
 * @created Feb 5, 2009
 */
public interface IWritableComponentNameCache extends IComponentNameCache {

    /**
     * @return all Component Names that were created within the context of 
     *         this cache.
     */
    Collection<IComponentNamePO> getNewNames();

    /**
     * @return all Component Names whose reuse has been changed within the
     *         context of this cache.
     */
    Collection<String> getReusedNames();

    /**
     * @return a mapping from Component Name GUID to new name for all 
     *         Component Names that were renamed within the context of this 
     *         cache.
     */
    Collection<IComponentNamePO> getRenamedNames();

    /**
     * @return all Component Names marked for deletion within the context of
     *         this cache.
     */
    Collection<IComponentNamePO> getDeletedNames();

    /**
     * Removes an instance of reuse for the Component Name with the given GUID
     * and updates the type for that Component Name accordingly.
     * 
     * @param componentNameGuid The GUID of the Component Name whose reuse
     *                          has changed.
     * @throws PMException if a database error occurs.
     */
    public void removeReuse(String componentNameGuid) throws PMException;

    /**
     * Adds an instance of reuse for the Component Name with the given GUID
     * and updates the type for that Component Name accordingly.
     * 
     * @param componentNameGuid The GUID of the Component Name whose reuse
     *                          has changed.
     * @throws PMException if a database error occurs.
     */
    public void addReuse(String componentNameGuid) throws PMException;

    /**
     * Adds the given Component Name to this cache.
     * 
     * @param compNamePo The new Component Name to add.
     */
    public void addComponentNamePO(IComponentNamePO compNamePo);

    /**
     * Creates and returns a new Component Name with the given attributes.
     * 
     * @param name The name for the Component Name.
     * @param type The reuse type for the Component Name.
     * @param creationContext The creation context.
     * @return the newly created Component Name.
     */
    public IComponentNamePO createComponentNamePO(String name, String type, 
            CompNameCreationContext creationContext);

    /**
     * Marks the Component name with the given GUID as renamed to 
     * <code>newName</code>.
     * 
     * @param guid The GUID of the Component Name to rename.
     * @param newName The new name for the Component Name.
     */
    public void renameComponentName(String guid, String newName);

    /**
     * For performance reasons we want to preload cache
     * @param guids IComponentNamePO
     * @param projectId the parent project id
     */
    public void initCache(Set<String> guids, Long projectId);
    
}
