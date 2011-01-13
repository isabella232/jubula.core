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

import org.eclipse.jubula.client.core.attributes.IDocAttributeInitializer;


/**
 * @author BREDEX GmbH
 * @created 21.04.2008
 */
public interface IDocAttributeDescriptionPO {

    /**
     * 
     * @return the attribute types contained within this attribute type.
     */
    public Set<IDocAttributeDescriptionPO> getSubDescriptions();

    /**
     * 
     * @param description The contained type to remove.
     * @return <code>true</code> if this call changed the set. Otherwise, 
     *         <code>false</code>.
     */
    public boolean removeSubDescription(IDocAttributeDescriptionPO description);
    
    /**
     * 
     * @param description The type to add.
     * @return <code>true</code> if this call changed the set. Otherwise, 
     *         <code>false</code>.
     */
    public boolean addSubDescription(IDocAttributeDescriptionPO description);

    /**
     * 
     * @return the i18n key for this type's label. This also serves as a unique
     *         ID for the type. 
     */
    public String getLabelKey();

    /**
     * 
     * @return The name of a class capable of loading and storing attributes of
     *         this type.
     */
    public String getDisplayClassName();

    /**
     * 
     * @param className The display class name to set.
     */
    public void setDisplayClassName(String className);
    
    /**
     * 
     * @return i18n keys for possible values for attributes of this type.
     */
    public Set<String> getValueSetKeys();

    /**
     * 
     * @return the default value for attributes of this type.
     */
    public String getDefaultValue();

    /**
     * 
     * @return <code>true</code> if this type was defined by 
     *         BREDEX Software GmbH. Otherwise, <code>false</code>.
     */
    public boolean isBXAttribute();

    /**
     * 
     * @param value The value to check.
     * @return <code>true</code> if the given value is allowed for attributes of
     *         this type. Otherwise, <code>false</code>.
     */
    public boolean isValueValid(String value);
    
    /**
     * 
     * @return An object that is capable of initializing attributes of this 
     *         type.
     */
    public IDocAttributeInitializer getInitializer();

    /**
     * 
     * @param className The initializer class name to set.
     */
    public void setInitializerClassName(String className);
}
