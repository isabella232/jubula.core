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
package org.eclipse.jubula.client;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.eclipse.jubula.client.exceptions.LoadResourceException;
import org.eclipse.jubula.client.internal.utils.SerilizationUtils;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for loading object mapping associations
 * @author BREDEX GmbH
 * @created Oct 09, 2014
 */
public class ObjectMappingLoader {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
            ObjectMappingLoader.class);
    
    /** object mapping associations */
    private Properties m_objectMappingAssociations = new Properties();

    /** object mapping associations */
    private Map<String, IComponentIdentifier> m_map =
            new TreeMap<String, IComponentIdentifier>();
    
    /**
     * Utility class for loading object mapping association
     */
    public ObjectMappingLoader() {
        super();
    }
    
    /**
     * Initializes the object mapping associations
     * @param resourceURL the URL to the resource properties file
     */
    public void init (URL resourceURL) {
        try {
            m_objectMappingAssociations.load(resourceURL.openStream());
            for (Object obj : m_objectMappingAssociations.keySet()) {
                if (obj instanceof String) {
                    String compName = (String) obj;
                    if (m_map.containsKey(compName)) {
                        log.error("There is already a mapping for the component name " //$NON-NLS-1$
                                + compName);
                    } else {
                        try {
                            m_map.put(compName, getIdentifier(compName));
                        } catch (LoadResourceException e) {
                            log.error(e.getLocalizedMessage(), e);
                        }                    
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error while initialising the ObjectMappingLoader", e); //$NON-NLS-1$
        }
    }
    
    /**
     * Returns the component identifier for a component name from the cache
     * 
     * @param compName
     *            the component name
     * @return the component identifier or <code>null</code> if no identifier
     *         was found
     */
    public IComponentIdentifier get(String compName) {
        return m_map.get(compName);
    }
    
    /**
     * Returns the component identifier for a component name
     * 
     * @param compName
     *            the component name
     * @return the component identifier or <code>null</code> if no identifier
     *         was found
     * @throws LoadResourceException 
     */
    private IComponentIdentifier getIdentifier(String compName) throws
                LoadResourceException {
        try {
            String encodedString =
                    m_objectMappingAssociations.getProperty(compName);
            if (encodedString != null) {
                Object decodedObject = SerilizationUtils.decode(encodedString);
                if (decodedObject instanceof IComponentIdentifier) {
                    return (IComponentIdentifier) decodedObject;
                }
                throw new LoadResourceException("The decoded object is " //$NON-NLS-1$
                        + "not of type 'IComponentIdentfier'."); //$NON-NLS-1$
            }
        } catch (IOException e) {
            throw new LoadResourceException("Could load the given component name", e); //$NON-NLS-1$
        } catch (ClassNotFoundException e) {
            throw new LoadResourceException("Problems during deserialization...", e); //$NON-NLS-1$
        }
        return null;
    }
}
