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
package org.eclipse.jubula.tools.xml.businessmodell;


/**
 * @author BREDEX GmbH
 * @created 24.05.2007
 * 
 */
public class ToolkitPluginDescriptor implements Comparable {

    /* DOTNETDECLARE:BEGIN */

    
    /***/
    public String m_toolkitID;
    
    /***/
    public int m_order;
    
    /***/
    public String m_name;
    
    /***/
    public String m_includes;
    
    /***/
    public String m_depends;
    
    /***/
    public int m_majorVersion;
    
    /***/
    public int m_minorVersion;
    
    /***/
    public boolean m_isUserToolkit;
    
    /***/
    public String m_level;

    /* DOTNETDECLARE:END */

    
    /**
     * Only for deserialisation!
     */
    public ToolkitPluginDescriptor() {
        super();
    }
    
    /**
     * Constructor
     * @param toolkitID the unique id of the toolkit 
     *                  (e.g. com.bredexsw.guidancer.SwingToolkitPlugin)
     * @param name the displayable name of the toolkit
     *                 (e.g. Swing)
     * @param includes id of the extended-toolkit or
     *                 empty String if the toolkit is independent.
     * @param depends id of the toolkit on which this toolkit depends.
     * @param level the level of abstraction (abstract, concrete or toolkit)
     * @param order the order of read into the CompSystem
     * @param isUserToolkit whether the toolkit is user defined or not
     * @param majorVersion the major version
     * @param minorVersion the minor version
     */
    public ToolkitPluginDescriptor(String toolkitID, String name, 
        String includes, String depends, String level, int order, 
        boolean isUserToolkit, int majorVersion, int minorVersion) {

        m_toolkitID = toolkitID;
        m_name = name;
        m_includes = includes;
        m_level = level;
        m_order = order;
        m_isUserToolkit = isUserToolkit;
        m_majorVersion = majorVersion;
        m_minorVersion = minorVersion;
        m_depends = depends;
    }


    /**
     * @return id of the extended-toolkit or
     *         empty String if the toolkit is independent.
     */
    public String getIncludes() {
        return m_includes;
    }

    /**
     * @return the dependency to another toolkit. The id of the base
     * toolkit.
     */
    public String getDepends() {
        return m_depends;
    }

    /**
     * @return whether the toolkit is user defined or not
     */
    public boolean isUserToolkit() {
        return m_isUserToolkit;
    }


    /**
     * @return the level
     */
    public String getLevel() {
        return m_level;
    }


    /**
     * @return the majorVerision
     */
    public int getMajorVersion() {
        return m_majorVersion;
    }


    /**
     * @return the minorVersion
     */
    public int getMinorVersion() {
        return m_minorVersion;
    }


    /**
     * @return the displayable name of the toolkit
     */
    public String getName() {
        return m_name;
    }


    /**
     * @return the order of read into the CompSystem
     */
    public int getOrder() {
        return m_order;
    }


    /**
     * @return the id of the toolkit 
     *         (e.g. org.eclipse.jubula.toolkit.swing)
     */
    public String getToolkitID() {
        return m_toolkitID;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        if (!(o instanceof ToolkitPluginDescriptor)) {
            return 0;            
        }
        ToolkitPluginDescriptor descr = (ToolkitPluginDescriptor)o;
        return getToolkitID().compareTo(descr.getToolkitID());
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getToolkitID();
    }
}
