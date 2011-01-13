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
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.ui.controllers.propertysources.AutIdentifierPropertySource;
import org.eclipse.jubula.client.ui.controllers.propertysources.OMLogicNameGUIPropertySource;
import org.eclipse.jubula.client.ui.controllers.propertysources.OMTechNameGUIPropertySource;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.eclipse.ui.views.properties.IPropertySource;


/**
 * Provides property sources for GUIdancer model objects.
 *
 * @author BREDEX GmbH
 * @created Feb 20, 2009
 */
public class PropertySourceAdapterFactory implements IAdapterFactory {

    /** types for which adapters are available */
    private final Class[] m_types = {
        IComponentNamePO.class, IObjectMappingAssoziationPO.class,
        AutIdentifier.class
    };

    /**
     * {@inheritDoc}
     */
    public IPropertySource getAdapter(Object adaptableObject, 
            Class adapterType) {
        if (adapterType == IPropertySource.class) {
            if (adaptableObject instanceof IComponentNamePO) {
                return new OMLogicNameGUIPropertySource(
                        (IComponentNamePO)adaptableObject);
            } else if (adaptableObject instanceof IObjectMappingAssoziationPO) {
                return new OMTechNameGUIPropertySource(
                        (IObjectMappingAssoziationPO)adaptableObject);
            } else if (adaptableObject instanceof AutIdentifier) {
                return new AutIdentifierPropertySource(
                        (AutIdentifier)adaptableObject);
            }
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
