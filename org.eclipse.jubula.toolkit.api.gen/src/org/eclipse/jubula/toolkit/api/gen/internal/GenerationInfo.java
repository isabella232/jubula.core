package org.eclipse.jubula.toolkit.api.gen.internal;

import java.util.List;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;

/**
 * Contains all necessary information for API generation of a component
 * @author BREDEX GmbH
 */
public class GenerationInfo {
    
    /** the component */
    private Component m_component;
    
    /** the class name */
    private String m_className;
    
    /** the package for the class */
    private String m_classPackageName;
    
    /** the package for the interface */
    private String m_interfacePackageName;

    /** the directory path (either for class or interface) */
    private String m_directoryPath;

    /** the toolkit name */
    private String m_toolkitName;

    /** Whether an interface should be generated */
    private Boolean m_genInterface;
    
    /** Whether an the component has a default mapping */
    private Boolean m_hasDefaultMapping = false;

    /** only used for generating factories 
     *  list of components which have to be generated in factory */
    private List<FactoryInfo> m_componentList;
    
    /** only used for generating factories 
      * name of factory in the toolkit above in toolkit hierarchy */
    private String m_superFactoryName;
    
    /**
     * Contains all necessary information for API generation of a component
     * Supposed to be used for class/interface generation.
     * @param component the component
     * @param generateInterface whether an interface should be generated
     *          (else an impl class)
     */
    public GenerationInfo(Component component, boolean generateInterface) {
        m_component = component;
        m_genInterface = generateInterface;
        NameLoader nameLoader = NameLoader.getInstance();
        m_toolkitName = nameLoader.getToolkitName(
                component.getToolkitDesriptor());
        m_className = nameLoader.getClassName(component.getType());
        m_classPackageName = nameLoader.getClassPackageName(m_toolkitName);
        m_interfacePackageName = nameLoader.getInterfacePackageName(
                m_toolkitName);
        
        // Use package name as directory path name, replace "." by "/" later
        m_directoryPath = generateInterface
                ? m_interfacePackageName : m_classPackageName;
        
        // Check for exceptions in naming
        m_classPackageName = nameLoader.executeExceptions(m_classPackageName);
        m_interfacePackageName = nameLoader.executeExceptions(
                m_interfacePackageName);
        m_directoryPath = nameLoader.executeExceptions(m_directoryPath
                .replace(StringConstants.DOT, StringConstants.SLASH));
        m_toolkitName = nameLoader.executeExceptions(m_toolkitName);
        
        if (component instanceof ConcreteComponent) {
            m_hasDefaultMapping = 
                    ((ConcreteComponent)component).hasDefaultMapping();
        }
    }
    
    /**
     * Contains all necessary information for API generation for a toolkit.
     * Supposed to be used for factory generation.
     * @param tkDescriptor the toolkit descriptor
     * @param componentList the list of components to generate in factory
     * @param compsystem the comp system
     */
    public GenerationInfo(ToolkitDescriptor tkDescriptor,
            List<FactoryInfo> componentList, CompSystem compsystem) {
        m_component = null;
        m_genInterface = false;
        NameLoader nameLoader = NameLoader.getInstance();
        m_toolkitName = nameLoader.getToolkitName(tkDescriptor);
        m_className = nameLoader.getFactoryName(m_toolkitName);
        m_classPackageName = nameLoader.getToolkitPackageName(m_toolkitName);
        m_componentList = componentList;
        // Use package name as directory path name, replace "." by "/" later
        m_directoryPath = m_classPackageName;
        
        // Check for exceptions in naming
        m_classPackageName = nameLoader.executeExceptions(m_classPackageName);
        m_directoryPath = nameLoader.executeExceptions(m_directoryPath
                .replace(StringConstants.DOT, StringConstants.SLASH));
        m_toolkitName = nameLoader.executeExceptions(m_toolkitName);
        
        // For getting factories into a hierarchy
        m_superFactoryName = nameLoader.getSuperFactoryName(tkDescriptor,
                compsystem);
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
     * Returns the class name of the interface/implementation class to generate
     * or the name of the factory if constructor for factories was used
     * @return the class name
     */
    public String getClassName() {
        return m_className;
    }
    
    /**
     * Returns the class package name
     * @return the class package name
     */
    public String getClassPackageName() {
        return m_classPackageName;
    }
    
    /**
     * Returns the interface package name
     * @return the interface package name
     */
    public String getInterfacePackageName() {
        return m_interfacePackageName;
    }
    
    /**
     * Returns the directory path
     * @return the directory path
     */
    public String getDirectoryPath() {
        return m_directoryPath;
    }

    /**
     * Returns the toolkit name
     * @return the toolkit name
     */
    public String getToolkitName() {
        return m_toolkitName;
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
     * Returns the fully qualified class name
     * @return the fully qualified class name
     */
    public String getFqClassName() {
        return getClassPackageName() + StringConstants.DOT + getClassName();
    }
    
    /**
     * Returns the fully qualified interface name
     * @return the fully qualified interface name
     */
    public String getFqInterfaceName() {
        return getInterfacePackageName() + StringConstants.DOT + getClassName();
    }

    /**
     * Returns true if and only if the component has a default mapping
     * @return true if and only if the component has a default mapping
     */
    public boolean hasDefaultMapping() {
        return m_hasDefaultMapping;
    }

    /** Returns the list of components to generate in factory.
     * <code>null</code> if constructor for classes/interfaces was used.
     * @return the component list
     */
    public List<FactoryInfo> getComponentList() {
        return m_componentList;
    }
    

    /**
     * Returns the name of factory of the toolkit above in toolkit hierarchy.
     * <code>null</code> if there is no toolkit above in the hierarchy 
     * or if constructor for classes/interfaces was used.
     * @return the name of factory of the toolkit above in toolkit hierarchy
     */
    public String getSuperFactoryName() {
        return m_superFactoryName;
    }

}
