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
package org.eclipse.jubula.client.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.views.imageview.ImageProvider;
import org.eclipse.jubula.client.ui.views.imageview.TestResultNodeImageProvider;

/**
 * Adapter factory for test result nodes.
 * 
 * @author BREDEX GmbH
 * @created Jul 31, 2008
 */
public class TestResultNodeAdapterFactory implements IAdapterFactory {
    /** types for which adapters are available */
    private final Class[] m_types = { ImageProvider.class };

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == ImageProvider.class) {
            return new TestResultNodeImageProvider(
                    (TestResultNode) adaptableObject);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Class[] getAdapterList() {
        return m_types;
    }
}
