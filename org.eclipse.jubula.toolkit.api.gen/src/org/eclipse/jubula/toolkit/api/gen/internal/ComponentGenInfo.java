package org.eclipse.jubula.toolkit.api.gen.internal;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;

/**
 * Contains all necessary information for API generation of a component
 * @author BREDEX GmbH
 */
public class ComponentGenInfo {
    
    /** the component */
    private Component m_component;
    
    /** the package for the interface */
    private String m_className;

    /** the package for the interface */
    private String m_interfaceDirectoryPath;
    
    /** the package for the interface */
    private String m_interfacePackageName;
    
    /** Whether an interface should be generated */
    private Boolean m_genInterface;
    
    /** Whether an the component has a default mapping */
    private Boolean m_hasDefaultMapping = false;

    /** the most specific visible super type of a component */
    private String m_mostSpecificVisibleSuperTypeName;
    
    /**
     * Contains all necessary information for API generation of a component
     * Supposed to be used for class/interface generation.
     * @param component the component
     * @param generateInterface whether an interface should be generated
     * @param toolkitName the toolkitName
     * @param className the className
     *          (else an impl class)
     */
    public ComponentGenInfo(Component component, boolean generateInterface,
            String toolkitName, String className) {
        m_component = component;
        m_genInterface = generateInterface;
        NameLoader nameLoader = NameLoader.getInstance();
        m_interfacePackageName = nameLoader.getInterfacePackageName(
                toolkitName);
        m_className = className;
        m_interfaceDirectoryPath = m_interfacePackageName
                .replace(StringConstants.DOT, StringConstants.SLASH);
        
        m_interfacePackageName = nameLoader.executeExceptions(
                m_interfacePackageName);
        m_interfaceDirectoryPath = nameLoader.executeExceptions(
                m_interfaceDirectoryPath);
        
        m_mostSpecificVisibleSuperTypeName =
                findMostSpecificVisibleSuperTypeName(component);
        
        if (component instanceof ConcreteComponent) {
            m_hasDefaultMapping = 
                    ((ConcreteComponent)component).hasDefaultMapping();
        }
    }

    /**
     * Returns the component.
     * <code>null</code> if constructor for factories was used.
     * @return the component
     */
    public Component getComponent() {
        return m_component;
    }
    
    /**
     * Returns the interface package name
     * @return the interface package name
     */
    public String getInterfacePackageName() {
        return m_interfacePackageName;
    }
    
    /**
     * Returns the interface package name
     * @return the interface package name
     */
    public String getInterfaceDirectoryPath() {
        return m_interfaceDirectoryPath;
    }
    
    /**
     * Returns true if and only if an interface should be generated
     * and false if and only if a implementation class should be generated
     * @return the toolkit name
     */
    public Boolean generatesInterface() {
        return m_genInterface;
    }
    
    /**
     * Returns the fully qualified interface name
     * @return the fully qualified interface name
     */
    public String getFqInterfaceName() {
        return getInterfacePackageName() + StringConstants.DOT + getClassName();
    }

    /**
     * Returns the class name
     * @return the class name
     */
    private String getClassName() {
        return m_className;
    }

    /**
     * Returns true if and only if the component has a default mapping
     * @return true if and only if the component has a default mapping
     */
    public boolean hasDefaultMapping() {
        return m_hasDefaultMapping;
    }
    
    /**
     * Returns the name of the most specific visible super type of a component
     * @return the name of the most specific visible super type of a component
     */
    public String getMostSpecificVisibleSuperTypeName() {
        return m_mostSpecificVisibleSuperTypeName;
    }
    
    /**
     * Finds the name of the most specific visible super type of a component
     * @param component the component
     * @return the name of the most specific visible super type
     */
    private String findMostSpecificVisibleSuperTypeName(Component component) {
        if (component.isVisible()) {
            return getFqInterfaceName();
        }
        // search for the most specific visible super type of the component
        Component tmp = component;
        while (tmp != null && !tmp.isVisible()) {
            tmp = tmp.getRealized().get(0);
        }
        GenerationInfo visibleSuperType = new GenerationInfo(tmp);
        ComponentGenInfo specificInformation = new ComponentGenInfo(tmp, true,
                visibleSuperType.getToolkitName(),
                visibleSuperType.getClassName());
        return specificInformation.getFqInterfaceName();
    }
}
