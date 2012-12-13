/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.uiadapter.factory;

import org.eclipse.jubula.rc.common.uiadapter.interfaces.IComponentAdapter;

/**
 * This is the interface for the factory which is used by the 
 * <code>AbstractUI</code> to get the specific Object which implements the
 * corresponding interface for the tester class.
 * 
 * @author BREDEX GmbH
 */
public interface IUIAdapterFactory {
    /**
     * @return all classes that will be supported by this adapter factory
     */
    Class[] getSupportedClasses();

    /**
     * Adapts object to adapt to a new object of type targetAdapterClass
     * 
     * @param objectToAdapt
     *            object that should be adapted
     * @return the adapter for the object to adapt of type targetAdapterClass
     */
    IComponentAdapter getAdapter(Object objectToAdapt);
}
