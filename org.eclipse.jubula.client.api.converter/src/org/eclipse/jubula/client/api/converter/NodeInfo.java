/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.INodePO;

/**
 *  Information for Creating a Java Class corresponding to a Node
 *  @created 28.10.2014
 */
public class NodeInfo {
    
    /** The class name of the test case */
    private String m_className;

    /** The base path of the package */
    private String m_packageBasePath;
    
    /** The node */
    private INodePO m_node;
    
    /**
     * @param className the class name
     * @param node the node
     * @param packageBasePath the base path of the package
     */
    public NodeInfo (String className, INodePO node,
            String packageBasePath) {
        m_className = StringUtils.substringBeforeLast(className, ".java"); //$NON-NLS-1$
        m_node = node;
        m_packageBasePath = packageBasePath;
    }
    
    /**
     * @return The class name of the test case
     */
    public String getClassName() {
        return m_className;
    }
    
    /**
     * @return The base path of the package
     */
    public String getPackageBasePath() {
        return m_packageBasePath;
    }
    
    /**
     * @return The node
     */
    public INodePO getNode() {
        return m_node;
    }
}
