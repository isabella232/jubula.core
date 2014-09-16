package org.eclipse.jubula.toolkit.api.gen.internal;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;

/**
 * Contains all necessary information for API generation of a component
 * @author BREDEX GmbH
 */
public class GenerationInfo {
    
    /** the component */
    private Component m_component;
    
    /** the class name */
    private String m_className;
    
    /** the package name */
    private String m_packageName;

    /** the directory path extension */
    private String m_directoryPath;

    /** the toolkit name */
    private String m_toolkitName;

    /** Whether an interface should be generated */
    private Boolean m_genInterface;
    
    /**
     * Contains all necessary information for API generation of a component
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
        m_className = nameLoader.getClassName(m_component.getType());
        m_packageName = nameLoader.getPackageName(
                component, m_toolkitName);
        m_directoryPath = m_packageName;
        m_packageName = nameLoader.executeExceptions(m_packageName);
        m_directoryPath = nameLoader.executeExceptions(m_directoryPath
                .replace(StringConstants.DOT, StringConstants.SLASH));
        m_toolkitName = nameLoader.executeExceptions(m_toolkitName);
    }

    /**
     * Returns the component
     * @return the component
     */
    public Component getComponent() {
        return m_component;
    }
    
    /**
     * Returns the class name
     * @return the class name
     */
    public String getClassName() {
        return m_className;
    }
    
    /**
     * Returns the package name
     * @return the package name
     */
    public String getPackageName() {
        return m_packageName;
    }
    
    /**
     * Returns the directory path extension
     * @return the directory path extension
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
     * Returns the fully qualified name
     * @return the fully qualified name
     */
    public String getFqn() {
        return getPackageName() + StringConstants.DOT + getClassName();
    }

}
