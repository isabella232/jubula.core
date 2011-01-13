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
import java.util.Collections;
import java.util.Set;

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.model.IComponentNameData;
import org.eclipse.jubula.client.core.model.IComponentNamePO;


/**
 * Null implementation of a writable Component Name cache.
 *
 * @author BREDEX GmbH
 * @created Feb 16, 2009
 */
public class NullComponentNameCache implements IWritableComponentNameCache {

    /**
     * {@inheritDoc}
     */
    public void addComponentNamePO(IComponentNamePO compNamePo) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void addReuse(String componentNameGuid) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public IComponentNamePO createComponentNamePO(String name, String type,
            CompNameCreationContext creationContext) {
        // Null implmentation
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<IComponentNamePO> getDeletedNames() {
        // Null implmentation
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<IComponentNamePO> getNewNames() {
        // Null implmentation
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<IComponentNamePO> getRenamedNames() {
        // Null implmentation
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getReusedNames() {
        // Null implmentation
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public void removeReuse(String componentNameGuid) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void renameComponentName(String guid, String newName) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public IComponentNamePO getCompNamePo(String guid) {
        // Null implmentation
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public IComponentNamePO getCompNamePo(String guid, boolean resolveRefs) {
        // Null implmentation
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Set<IComponentNameData> getComponentNameData() {
        // Null implmentation
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public String getGuidForName(String name) {
        // Null implmentation
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Set<IComponentNameData> getLocalComponentNameData() {
        // Null implmentation
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public String getName(String guid) {
        // Null implmentation
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void updateStandardMapperAndCleanup(Long activeProjectId) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public String getGuidForName(String name, Long parentProjectId) {
        // Null implmentation
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteComponentName(IComponentNamePO toDelete) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void initCache(Set<String> guids) {
        // Do nothing
    }

}
