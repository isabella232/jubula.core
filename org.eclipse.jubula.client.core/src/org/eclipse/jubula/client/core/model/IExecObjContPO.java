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

import org.eclipse.jubula.client.core.persistence.IExecPersistable;


/**
 * @author BREDEX GmbH
 * @created 14.10.2011
 */
public interface IExecObjContPO extends IPersistentObject {
    /**
     * the pseudo root node for the test suite browser
     */
    public static final ICategoryPO TSB_ROOT_NODE = NodeMaker.createCategoryPO("TSB_ROOT"); //$NON-NLS-1$
    /**
     * @return unmodifiable ExecObjList
     */
    public abstract List<IExecPersistable> getExecObjList();

    /**
     * @param execObj ExecObj to add
     */
    public abstract void addExecObject(IExecPersistable execObj);

    /**
     * @param execObj ExecObj to remove
     */
    public abstract void removeExecObject(IExecPersistable execObj);
}