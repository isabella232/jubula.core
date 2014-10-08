/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jubula.client.alm.mylyn.ui.propertysource.ITaskPropertySource;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author BREDEX GmbH
 */
public class ITaskAdapterFactory implements IAdapterFactory {
    /** types for which adapters are available */
    private final Class[] m_types = { ITask.class, IPropertySource.class };

    /** {@inheritDoc} */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        
        if (adapterType == IPropertySource.class
            && adaptableObject instanceof ITask) {            
            return new ITaskPropertySource((ITask)adaptableObject);
        }  
        return null;
    }

    /** {@inheritDoc} */
    public Class[] getAdapterList() {
        return m_types;
    }
}