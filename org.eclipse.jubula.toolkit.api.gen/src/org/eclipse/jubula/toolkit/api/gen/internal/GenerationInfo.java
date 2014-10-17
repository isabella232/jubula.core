package org.eclipse.jubula.toolkit.api.gen.internal;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;

/**
 * Contains all necessary information for API generation of a component
 * @author BREDEX GmbH
 */
public class GenerationInfo {
    
    /** the class name */
    private String m_className;
    
    /** the package for the class */
    private String m_classPackageName;

    /** the directory path (either for class or interface) */
    private String m_classDirectoryPath;

    /** the toolkit name */
    private String m_toolkitName;

    /** the toolkit id */
    private String m_toolkitID;
    
    /** generation dependent information */
    private Object m_specificInformation;
    
    /**
     * Contains all necessary information for API generation of a component
     * Supposed to be used for class/interface generation.
     * @param component the component
     */
    public GenerationInfo(Component component) {
        NameLoader nameLoader = NameLoader.getInstance();
        ToolkitDescriptor toolkitDesriptor = component.getToolkitDesriptor();
        m_toolkitName = nameLoader.getToolkitName(toolkitDesriptor);
        m_className = nameLoader.getClassName(component.getType());
        m_classPackageName = nameLoader.getClassPackageName(m_toolkitName);
        m_toolkitID = toolkitDesriptor.getToolkitID();
        
        // Use package name as directory path name, replace "." by "/"
        m_classDirectoryPath = m_classPackageName
                .replace(StringConstants.DOT, StringConstants.SLASH);
        
        // Check for exceptions in naming
        m_classPackageName = nameLoader.executeExceptions(m_classPackageName);
        m_classDirectoryPath = nameLoader.executeExceptions(
                m_classDirectoryPath);
        m_toolkitName = nameLoader.executeExceptions(m_toolkitName);
    }
    
    /**
     * Contains all necessary information for API generation of a component
     * Supposed to be used for toolkit info or factory generation
     * @param tkDescriptor the toolkit descriptor
     * @param genFactory whether generation info is for creating factories
     */
    public GenerationInfo(ToolkitDescriptor tkDescriptor,
            boolean genFactory) {
        NameLoader nameLoader = NameLoader.getInstance();
        m_toolkitName = nameLoader.getToolkitName(tkDescriptor);
        if (genFactory) {
            m_className = nameLoader.getFactoryName(m_toolkitName);
        } else {
            m_className = nameLoader.getToolkitComponentClassName(
                    m_toolkitName);
        }
        m_classPackageName = nameLoader.getToolkitPackageName(m_toolkitName);
        m_toolkitID = tkDescriptor.getToolkitID();
        
        m_classDirectoryPath = m_classPackageName
                .replace(StringConstants.DOT, StringConstants.SLASH);
        
        // Check for exceptions in naming
        m_classPackageName = nameLoader.executeExceptions(m_classPackageName);
        m_classDirectoryPath = nameLoader.executeExceptions(
                m_classDirectoryPath);
        m_toolkitName = nameLoader.executeExceptions(m_toolkitName);
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
     * Returns the directory path
     * @return the directory path
     */
    public String getClassDirectoryPath() {
        return m_classDirectoryPath;
    }

    /**
     * Returns the toolkit name
     * @return the toolkit name
     */
    public String getToolkitName() {
        return m_toolkitName;
    }
    
    /**
     * Returns the toolkit id
     * @return the toolkit id
     */
    public String getToolkitID() {
        return m_toolkitID;
    }
    
    /**
     * Returns the fully qualified class name
     * @return the fully qualified class name
     */
    public String getFqClassName() {
        return getClassPackageName() + StringConstants.DOT + getClassName();
    }
    
    /**
     * Returns the generation dependent information 
     * @return the generation dependent information 
     */
    public Object getSpecificInformation() {
        return m_specificInformation;
    }
    
    /**
     * Sets the generation dependent information 
     * @param specificInformation generation dependent information
     */
    public void setSpecificInformation(Object specificInformation) {
        m_specificInformation = specificInformation;
    }
}
