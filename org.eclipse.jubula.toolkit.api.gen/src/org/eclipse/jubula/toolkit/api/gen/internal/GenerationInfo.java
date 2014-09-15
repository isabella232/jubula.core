package org.eclipse.jubula.toolkit.api.gen.internal;

import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.xml.businessmodell.Component;

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
    
    /**
     * Contains all necessary information for API generation of a component
     * @param component the component
     */
    public GenerationInfo(Component component) {
        m_component = component;
        m_toolkitName = component.getToolkitDesriptor().getName().toLowerCase();
        NameLoader nameLoader = NameLoader.getInstance();
        m_className = nameLoader.getClassName(m_component.getType());
        m_packageName = nameLoader.getPackageName(
                component, m_toolkitName);
        m_directoryPath = m_packageName;
        m_packageName = executeExceptions(m_packageName);
        m_directoryPath = executeExceptions(m_directoryPath
                .replace(StringConstants.DOT, StringConstants.SLASH));
        m_toolkitName = executeExceptions(m_toolkitName);
    }
    
    /**
     * modifies a string such that it fits into api naming patterns
     * @param string the string
     * @return the adjusted string
     */
    private String executeExceptions(String string) {
        return string.replace("abstract", "base").replace("gef", "rcp.gef"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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

}
