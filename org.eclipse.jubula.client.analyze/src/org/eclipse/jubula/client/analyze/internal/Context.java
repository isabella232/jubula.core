/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.internal;
/**
 * This class is a model for a Context. It is used to save the different
 * attributes of a Context, when the Context is registered when the plugin
 * starts. 
 * @author volker
 *
 */
public class Context {
    
    /** The Context ID */
    private String m_id;
  
    /** The Context ID */
    private String m_name;
    
    /** The Instance of this context (Executable Extension) */
    private Object m_contextInstance;
    
    /**
     * @param contextID The given contextType
     * @param name The given contextName
     * @param executableExtension The given Instance of this Context
     */
    public Context(String contextID, String name, Object executableExtension) {
        setID(contextID);
        setName(name);
        setContextInstance(executableExtension);
    }

    /**
     * @return The ContextID
     */
    public String getID() {
        return m_id;
    }

    /**
     * @param id
     *            The given ContextID
     */
    public void setID(String id) {
        this.m_id = id;
    }
    
    /**
     * @return The Context-Name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param name
     *            The Context-Name
     */
    public void setName(String name) {
        this.m_name = name;
    }
    
    /**
     * @return The Instance of this Context (Executable Extension)
     */
    public Object getContextInstance() {
        return m_contextInstance;
    }
    
    /**
     * @param executableExtension The Instance of this Context (Executable Extension)
     */
    public void setContextInstance(Object executableExtension) {
        this.m_contextInstance = executableExtension;
    }
}
