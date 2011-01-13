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
package org.eclipse.jubula.client.ui.attribute;

import org.eclipse.jubula.client.core.model.IDocAttributeDescriptionPO;
import org.eclipse.jubula.client.core.model.IDocAttributePO;

/**
 * Base class for attribute renderers. Provides methods to access access 
 * attribute properties after renderer initialization.
 *
 * @author BREDEX GmbH
 * @created 16.05.2008
 */
public abstract class AbstractAttributeRenderer implements IAttributeRenderer {

    /** the attribute's type */
    private IDocAttributeDescriptionPO m_attrDesc;
    
    /** the attribute to render/persist */
    private IDocAttributePO m_attr;
    
    /**
     * {@inheritDoc}
     */
    public void init(IDocAttributeDescriptionPO attributeDescription, 
            IDocAttributePO attribute) {
        m_attrDesc = attributeDescription;
        m_attr = attribute;
    }

    /**
     * 
     * @return the attribute being displayed/persisted.
     */
    protected IDocAttributePO getAttribute() {
        return m_attr;
    }
    
    /**
     * 
     * @return the attribute's type
     */
    protected IDocAttributeDescriptionPO getDescription() {
        return m_attrDesc;
    }
    
    /**
     * Attempts to set the value for the managed attribute. If the given value
     * does not exist in the managed attribute description's set of possible
     * values, an <code>IllegalArgumentException</code> will be thrown.
     * 
     * @param value The value to use.
     */
    protected final void setValue(String value) {
        if (getDescription().isValueValid(value)) {
            getAttribute().setValue(value);
        } else {
            StringBuffer sb = new StringBuffer("Attempted to set a value not " //$NON-NLS-1$
                    + "contained in the value set."); //$NON-NLS-1$
            sb.append(" Value: "); //$NON-NLS-1$
            sb.append(value);
            sb.append("; Value set: ["); //$NON-NLS-1$
            for (String possibleValues : getDescription().getValueSetKeys()) {
                sb.append(possibleValues);
                sb.append(", "); //$NON-NLS-1$
            }
            if (getDescription().getValueSetKeys().isEmpty()) {
                sb.append("]"); //$NON-NLS-1$
            } else {
                sb.replace(sb.lastIndexOf(", "), sb.length(), "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            throw new IllegalArgumentException(sb.toString());
        }
    }
}
