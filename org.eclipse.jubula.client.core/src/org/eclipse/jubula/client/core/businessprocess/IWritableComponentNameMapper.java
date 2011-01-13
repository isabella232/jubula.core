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

import java.util.Map;

import org.eclipse.jubula.client.core.model.IComponentNameReuser;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;


/**
 * Handles mapping Component Names between memory and persistence layer.
 *
 * @author BREDEX GmbH
 * @created Jan 29, 2009
 */
public interface IWritableComponentNameMapper extends IComponentNameMapper {

    /**
     * Handles the existence of Component Names that were to be inserted into
     * the database. This occurs when, for example: an editor with a new
     * Component Name is saved, but another Component Name with the same name
     * already exists in the database. The resolution is usually to identify
     * references to the saved GUID and replace them with references to the 
     * pre-existing GUID.
     * 
     * @param guidToCompNameMap Mapping from GUID of Component Names that were
     *                          supposed to be inserted in the database to the
     *                          GUID of Component Names that already exist in 
     *                          the database.
     */
    public void handleExistingNames(
            Map<String, String> guidToCompNameMap);

    /**
     * Updates the the given object to use the Component Name with the new GUID
     * instead of the one with the old GUID.
     * 
     * @param user The object having its reuse changed.
     * @param oldGuid The GUID of the Component Name that is no longer reused.
     *                May be <code>null</code>, which indicates that no
     *                Component Name was being used.
     * @param newGuid The GUID of the Component Name that is now to be reused.
     *                May be <code>null</code>, which indicates that no
     *                Component Name will be used.
     * @throws PMException if a database error occurs.
     */
    public void changeReuse(
            IComponentNameReuser user, String oldGuid, String newGuid) 
        throws IncompatibleTypeException, PMException;

    /**
     * 
     * @param componentNameCache the new cache to use.
     */
    public void setCompNameCache(
            IWritableComponentNameCache componentNameCache);

    /**
     * 
     * @param context the new context to use.
     */
    public void setContext(Object context);
    
    /**
     * {@inheritDoc}
     */
    public IWritableComponentNameCache getCompNameCache();
}
