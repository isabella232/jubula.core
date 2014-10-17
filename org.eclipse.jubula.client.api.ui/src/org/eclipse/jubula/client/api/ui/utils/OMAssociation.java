package org.eclipse.jubula.client.api.ui.utils;

import java.util.Map;

/**
 * @author BREDEX GmbH
 * @created 17.10.2014
 */
public class OMAssociation {
    
    /** the encoded object mapping associations */
    private StringBuffer m_encodedAssociations;
    
    /** the map containing mapping from identifier to java qualifier */
    private Map<String, String> m_identifierMap;
    
    /**
     * @param encodedAssociations the encoded object mapping associations
     * @param identifierMap the map containing mapping from identifier to java qualifier
     */
    public OMAssociation(StringBuffer encodedAssociations,
            Map<String, String> identifierMap) {
        m_encodedAssociations = encodedAssociations;
        m_identifierMap = identifierMap;
    }
    
    /**
     * @return the encoded object mapping associations
     */
    public StringBuffer getEncodedAssociations() {
        return m_encodedAssociations;
    }
    
    /**
     * @return the map containing mapping from identifier to java qualifier
     */
    public Map<String, String> getIdentifierMap() {
        return m_identifierMap;
    }
}
