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
package org.eclipse.jubula.toolkit.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.SerilizationUtils;

/**
 * Utility class for loading object mapping associations
 * @author BREDEX GmbH
 * @created Oct 09, 2014
 */
public class ObjectMappingLoader {
    
    /** object mapping associations */
    private Properties m_objectMappingAssociations = new Properties();
    
    /**
     * Utility class for loading object mapping associations
     * @param resourcePath the path to the resource properties file
     */
    public ObjectMappingLoader(String resourcePath) {
        try {
            URL resourceURL = ObjectMappingLoader.class.getClassLoader()
                .getResource(resourcePath);
            m_objectMappingAssociations.load(resourceURL.openStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Returns the component identifier for a component name
     * @param compName the component name
     * @return the component identifier
     *         or <code>null</code> if no identifier was found
     */
    public IComponentIdentifier get(String compName) {
        try {
            String encodedString =
                    m_objectMappingAssociations.getProperty(compName);
            if (encodedString != null) {
                Object decodedObject = SerilizationUtils.decode(encodedString);
                if (decodedObject instanceof IComponentIdentifier) {
                    return (IComponentIdentifier) decodedObject;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
