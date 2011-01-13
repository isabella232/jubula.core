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


/**
 * @author BREDEX GmbH
 * @created Apr 7, 2008
 */
public interface IComponentNamePO 
        extends IAbstractGUIDNamePO, IComponentNameData {

    /**
     * @param componentType the Component Type to set.
     */
    public void setComponentType(String componentType);

    /**
     * @param referencedGuid the referenced Guid to set.
     */
    public void setReferencedGuid(String referencedGuid);
    
    /**
     * Two ComponentNamePOs are equal if their GUIDs are equal.
     * @param compNamePO a ComponentNamePO to compare.
     * @return true if the GUID of the given ComponentNamePO equals this GUID,
     * false otherwise.
     */
    public boolean isNameEqual(ComponentNamePO compNamePO);
}
