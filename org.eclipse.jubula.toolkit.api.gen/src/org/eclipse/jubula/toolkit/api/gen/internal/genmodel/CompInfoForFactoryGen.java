package org.eclipse.jubula.toolkit.api.gen.internal.genmodel;



/**
 * Contains all necessary information of a component for factory generation
 * @author BREDEX GmbH
 * @created 20.10.2014
 */
public class CompInfoForFactoryGen {
    
    /** the class name */
    private String m_fqClassName;

    /** Whether an interface should be generated */
    private Boolean m_hasDefaultMapping;

    /** name of the component */
    private String m_componentName;
    
    /** the most specific visible super type of a component */
    private String m_mostSpecificVisibleSuperTypeName;
    
    /**
     * Contains all necessary information of a component for factory generation
     * @param componentName the component name
     * @param fqClassName the class name
     * @param hasDefaultMapping true if and only if component has default mapping
     * @param mostSpecificVisibleSuperTypeName most specific visible super type of a component
     */
    public CompInfoForFactoryGen(String componentName, String fqClassName,
            boolean hasDefaultMapping,
            String mostSpecificVisibleSuperTypeName) {
        m_componentName = componentName;
        m_fqClassName = fqClassName;
        m_hasDefaultMapping = hasDefaultMapping;
        m_mostSpecificVisibleSuperTypeName = mostSpecificVisibleSuperTypeName;
    }
    
    /**
     * Returns the class name
     * @return the class name
     */
    public String getClassName() {
        return m_fqClassName;
    }
    
    /**
     * Returns the component name
     * @return the component name
     */
    public String getComponentName() {
        return m_componentName;
    }
    
    /**
     * Returns true if and only if component has a default mapping
     * @return the toolkit name
     */
    public Boolean hasDefaultMapping() {
        return m_hasDefaultMapping;
    }

    /** 
     * Returns the most specific visible super type of a component
     * @return the most specific visible super type of a component
     */
    public String getMostSpecificVisibleSuperTypeName() {
        return m_mostSpecificVisibleSuperTypeName;
    }
}
