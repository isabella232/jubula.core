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

import org.eclipse.jubula.client.core.persistence.ISpecPersistable;


/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface ISpecObjContPO extends IPersistentObject {
    /**
     * @return unmodifiable SpecObjList
     */
    public abstract List<ISpecPersistable> getSpecObjList();

    /**
     * @param specObj specObj to add
     */
    public abstract void addSpecObject(ISpecPersistable specObj);

    /**
     * @param specObj specObj to remove
     */
    public abstract void removeSpecObject(ISpecPersistable specObj);

}