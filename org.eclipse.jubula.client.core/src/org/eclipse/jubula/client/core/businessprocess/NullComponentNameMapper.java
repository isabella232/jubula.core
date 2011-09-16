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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IComponentNameReuser;


/**
 * Null implementation of a Component Name mapper.
 *
 * @author BREDEX GmbH
 * @created Feb 13, 2009
 */
public class NullComponentNameMapper implements IWritableComponentNameMapper {

    /** the "cache" for this mapper */
    private NullComponentNameCache m_cache = new NullComponentNameCache();
    
    /**
     * {@inheritDoc}
     */
    public void changeReuse(IComponentNameReuser user, String oldGuid,
            String newGuid) {
        // Null implementation
    }

    /**
     * {@inheritDoc}
     */
    public void changeReuse(
            @SuppressWarnings("unused") IComponentNameReuser oldUser,
            @SuppressWarnings("unused") IComponentNameReuser newUser,
            @SuppressWarnings("unused") String compNameGuid) {
        // Null implementation
    }

    /**
     * {@inheritDoc}
     */
    public IWritableComponentNameCache getCompNameCache() {
        // Null implementation
        return m_cache;
    }

    /**
     * {@inheritDoc}
     */
    public void handleExistingNames(Map<String, String> guidToCompNameMap) {
        // Null implementation
    }

    /**
     * {@inheritDoc}
     */
    public void setCompNameCache(
            IWritableComponentNameCache componentNameCache) {
        // Null implementation
    }

    /**
     * {@inheritDoc}
     */
    public void setContext(Object context) {
        // Null implementation
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getUsedTypes(String compNameGuid) {
        // Null implementation
        return Collections.emptySet();
    }

}
