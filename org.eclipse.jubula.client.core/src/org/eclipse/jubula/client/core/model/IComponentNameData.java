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

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;

/**
 * The readable (getter) functionality of a component name. Methods can return
 * objects of this type in order to indicate that the returned object is not
 * editable.
 *
 * @author BREDEX GmbH
 * @created Jan 30, 2009
 */
public interface IComponentNameData {

    /**
     * @return The name.
     */
    public abstract String getName();

    /**
     * @return the Component Type.
     */
    public String getComponentType();

    /**
     * @return the referenced Guid.
     */
    public String getReferencedGuid();

    /**
     * @return The context of creation.
     */
    public CompNameCreationContext getCreationContext();
    
    /**
     * @return the ID of the Project to which this Component Name belongs.
     */
    public Long getParentProjectId();
    
    /**
     * @return the GUID of the component name.
     */
    public String getGuid();
}
