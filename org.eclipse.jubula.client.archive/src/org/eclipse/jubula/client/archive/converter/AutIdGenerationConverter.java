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
package org.eclipse.jubula.client.archive.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.archive.schema.Aut;
import org.eclipse.jubula.client.archive.schema.AutConfig;
import org.eclipse.jubula.client.archive.schema.MapEntry;
import org.eclipse.jubula.client.archive.schema.Project;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;

/**
 * Generates unique (in the context of the Project) AUT IDs for all 
 * AUT Configurations in the converted Project that do not yet an AUT ID 
 * defined.
 *
 * @author BREDEX GmbH
 * @created Jan 21, 2010
 */
public class AutIdGenerationConverter extends AbstractXmlConverter {

    /**
     * {@inheritDoc}
     */
    protected boolean conversionIsNecessary(Project xml) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void convertImpl(Project xml) {
        for (Aut autXml : xml.getAutList()) {
            for (AutConfig configXml : autXml.getConfigList()) {
                if (!containsAutId(configXml)) {
                    String autIdValue = 
                        createUniqueAutId(xml, autXml.getName());
                    MapEntry autIdEntryXml = configXml.addNewConfAttrMapEntry();
                    autIdEntryXml.setKey(AutConfigConstants.AUT_ID);
                    autIdEntryXml.setValue(autIdValue);
                }
            }
        }
    }

    /**
     * 
     * @param configXml The AUT Configuration to check.
     * @return <code>true</code> if the AUT Configuration contains an AUT ID. 
     *         Otherwise, <code>false</code>.
     */
    private boolean containsAutId(AutConfig configXml) {
        for (MapEntry configEntryXml 
                : configXml.getConfAttrMapEntryList()) {
            if (AutConfigConstants.AUT_ID.equals(configEntryXml.getKey())
                    && StringUtils.isNotEmpty(configEntryXml.getValue())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 
     * @param xml The context within which to check for uniqueness.
     * @param autName The base text to use for the AUT ID.
     * @return an AUT ID based on the given AUT name that is unique within the
     *         context of the given Project.
     */
    private String createUniqueAutId(Project xml, String autName) {
        String autId = autName;
        int counter = 0;
        
        while (!isAutIdUnique(xml, autId)) {
            counter++;
            autId = autName + counter;
        }
        
        return autId;
    }

    /**
     * 
     * @param xml The context within which to check for uniqueness.
     * @param autId The AUT ID to check.
     * @return <code>true</code> if the given AUT ID is unique within the 
     *         context of the given Project. Otherwise, <code>false</code>.
     */
    private boolean isAutIdUnique(Project xml, String autId) {
        for (Aut autXml : xml.getAutList()) {
            for (AutConfig configXml : autXml.getConfigList()) {
                for (MapEntry configEntryXml 
                        : configXml.getConfAttrMapEntryList()) {
                    if (AutConfigConstants.AUT_ID.equals(
                                configEntryXml.getKey())
                            && StringUtils.equals(autId, 
                                    configEntryXml.getValue())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
