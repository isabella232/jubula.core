/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jubula.client.ui.editors.TestResultViewer;
import org.eclipse.jubula.client.ui.rcp.views.JBPropertiesView;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 *
 * @author BREDEX GmbH
 * @created Oct 06, 2011
 */
public class PropertySheetAdapterFactory implements IAdapterFactory {

    /** types for which adapters are available */
    private final Class[] m_types = {TestResultViewer.class};

    /**
     * 
     * {@inheritDoc}
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IPropertySheetPage.class) {
            if (adaptableObject instanceof TestResultViewer) {
                return new JBPropertiesView(false, null);
            }
        }
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Class[] getAdapterList() {
        return m_types;
    }

}
