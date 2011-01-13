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
package org.eclipse.jubula.client.core.attributes;

import org.eclipse.jubula.client.core.model.IDocAttributePO;

/**
 * Initializes a documentation attribute.
 *
 * @author BREDEX GmbH
 * @created 22.05.2008
 */
public interface IDocAttributeInitializer {

    /**
     * Initializes the given attribute.
     * 
     * @param attribute The attribute to initialize.
     */
    public void initializeAttribute(IDocAttributePO attribute);
    
}
