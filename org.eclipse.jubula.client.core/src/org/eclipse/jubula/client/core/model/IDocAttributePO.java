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

import java.util.Set;

/**
 * @author BREDEX GmbH
 * @created 16.04.2008
 */
public interface IDocAttributePO {

    /**
     * @return The value of this attribute.
     */
    public String getValue();

    /**
     * @param newValue The new value for this attribute.
     */
    public void setValue(String newValue);
    
    /**
     * @return all attribute types associated with this node.
     */
    public Set<IDocAttributeDescriptionPO> getDocAttributeTypes();
    
    /**
     * @param attributeType The type of the attribute for which to get the 
     *                      value.
     * @return The documentation attribute value for the given name.
     */
    public IDocAttributeListPO getDocAttributeList(
            IDocAttributeDescriptionPO attributeType);

    /**
     * @param attributeType  The type of the attribute for which to set the 
     *                       value.
     * @param attributeList The list to set.
     */
    public void setDocAttributeList(IDocAttributeDescriptionPO attributeType, 
            IDocAttributeListPO attributeList);
}
