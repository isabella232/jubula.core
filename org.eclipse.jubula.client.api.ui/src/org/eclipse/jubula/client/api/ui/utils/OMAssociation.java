package org.eclipse.jubula.client.api.ui.utils;

import java.util.Map;

/**
 * Containing encoded object mapping and information about
 * how it can be generated into a Java class.
 * @author BREDEX GmbH
 * @created 17.10.2014
 */
public class OMAssociation {
    
    /** the encoded object mapping associations */
    private StringBuffer m_encodedAssociations;
    
    /** the map containing mapping from identifier to java qualifier */
    private Map<String, String> m_identifierMap;
    
    /** the name of the class which is the target of the generation */
    private String m_targetClassName;
    
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
     * @return the name of the class which is the target of the generation
     */
    public String getTargetClassName() {
        return m_targetClassName;
    }

    /**
     * @param name the name of the class which is the target of the generation
     */
    public void setTargetClassName(String name) {
        m_targetClassName = name;
    }
    
    /**
     * @return the map containing mapping from identifier to java qualifier
     */
    public Map<String, String> getIdentifierMap() {
        return m_identifierMap;
    }
    
    
}
