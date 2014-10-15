package org.eclipse.jubula.toolkit.api.gen.internal;

import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;


/**
 * Contains all necessary information of a component for factory generation
 * @author BREDEX GmbH
 */
public class FactoryInfo {
    
    /** the class name */
    private String m_className;
    
    /** the package name */
    private String m_interfaceName;

    /** Whether an interface should be generated */
    private Boolean m_hasDefaultMapping;

    /** name of the component */
    private String m_componentName;

    /** the most specific visible super type of a component */
    private String m_mostSpecificVisibleSuperTypeName;
    
    /** the most specific visible super type of a component */
    private ComponentClass m_componentClass;

    /** the most specific visible super type of a component */
    private String m_testerClass;
    
    /**
     * Contains all necessary information of a component for factory generation
     * @param componentName the component name
     * @param className the class name
     * @param interfaceName the interface name
     * @param hasDefaultMapping true if and only if component has default mapping
     * @param mostSpecificVisibleSuperTypeName most specific visible super type of a component
     * @param componentClass the component class
     * @param testerClass the tester class
     */
    public FactoryInfo(String componentName, String className,
            String interfaceName, boolean hasDefaultMapping,
            String mostSpecificVisibleSuperTypeName,
            ComponentClass componentClass, String testerClass) {
        m_componentName = componentName;
        m_className = className;
        m_interfaceName = interfaceName;
        m_hasDefaultMapping = hasDefaultMapping;
        m_mostSpecificVisibleSuperTypeName = mostSpecificVisibleSuperTypeName;
        m_componentClass = componentClass;
        m_testerClass = testerClass;
    }
    
    /**
     * Returns the class name
     * @return the class name
     */
    public String getClassName() {
        return m_className;
    }
    
    /**
     * Returns the component name
     * @return the component name
     */
    public String getComponentName() {
        return m_componentName;
    }
    
    /**
     * Returns the interface name
     * @return the interface name
     */
    public String getInterfaceName() {
        return m_interfaceName;
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

    /** 
     * Returns the tester class
     * @return the tester class
     */
    public String getTesterClass() {
        return m_testerClass;
    }
    
    /** 
     * Returns the component class
     * @return the component class
     */
    public ComponentClass getComponentClass() {
        return m_componentClass;
    }
}
