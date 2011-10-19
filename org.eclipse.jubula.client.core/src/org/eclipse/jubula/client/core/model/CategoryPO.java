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
package org.eclipse.jubula.client.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * class to manage nodes in Specification Tree, which support grouping of nodes
 * 
 * @author BREDEX GmbH
 * @created 08.06.2005
 */
@Entity
@DiscriminatorValue(value = "Y")
class CategoryPO extends NodePO implements ICategoryPO {
    
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger(CategoryPO.class);

    /**
     * @param name name
     * @param isGenerated indicates whether this node has been generated
     */
    CategoryPO(String name, boolean isGenerated) {
        super(name, isGenerated);
    }

    /**
     * @param name name
     * @param guid guid
     * @param isGenerated indicates whether this node has been generated
     */
    CategoryPO(String name, String guid, boolean isGenerated) {
        super(name, guid, isGenerated);
    }

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    CategoryPO() {
        // nothing
    }

    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.NodePO#isInterfaceLocked()
     */
    @Transient
    public Boolean isReused() {
        return true;
    }
}
