/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.common.adapter;

import javafx.scene.control.Dialog;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.javafx.tester.adapter.IContainerAdapter;
import org.eclipse.jubula.rc.javafx.u40.tester.adapter.DialogContainerAdapter;

/**
 * Adapter Factory for new adapters required for classes available in Java 8
 * update 40 and higher
 * 
 * @author BREDEX GmbH
 */
public class JavaFXU40ContainerAdapterFactory implements IAdapterFactory {
    /** {@inheritDoc} */
    public Class[] getSupportedClasses() {
        return new Class[] { Dialog.class };
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class targetedClass, Object objectToAdapt) {
        if (targetedClass.isAssignableFrom(IContainerAdapter.class)) {
            if (objectToAdapt instanceof Dialog) {
                return new DialogContainerAdapter((Dialog<?>) objectToAdapt);
            }
        }
        return null;
    }
}
