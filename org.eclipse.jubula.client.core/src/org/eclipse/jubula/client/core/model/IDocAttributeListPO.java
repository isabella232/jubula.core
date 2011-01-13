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

import java.util.List;

/**
 * @author BREDEX GmbH
 * @created 21.04.2008
 */
public interface IDocAttributeListPO {

    /**
     * 
     * @return all attributes contained in this list.
     */
    public List<IDocAttributePO> getAttributes();

    /**
     * 
     * @param toAdd The attribute to add.
     */
    public void addAttribute(IDocAttributePO toAdd);

    /**
     * 
     * @param toRemove The attribute to remove.
     */
    public void removeAttribute(IDocAttributePO toRemove);
}
