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
import org.eclipse.swt.widgets.Composite;


/**
 * Contains methods for displaying and persisting a documentation attribute 
 * value.
 *
 * @author BREDEX GmbH
 * @created 16.05.2008
 */
public interface IAttributeRenderer {

    /**
     * Initialize this renderer with the given values. Clients must call this
     * method before attempting to call any other methods on this object.
     * 
     * @param attributeDescription The type for the attribute.
     * @param attribute The attribute that will be displayed/persisted.
     */
    public void init(IDocAttributeDescriptionPO attributeDescription, 
            IDocAttributePO attribute);

    /**
     * Adds widgets to the given composite that enable to user to view and/or
     * modify the attribute.
     * 
     * @param parent The parent composite. 
     */
    public void renderAttribute(Composite parent);

}
