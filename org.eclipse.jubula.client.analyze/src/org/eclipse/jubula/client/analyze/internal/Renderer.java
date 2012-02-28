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
 * This class is a model for a Renderer. It is used to save the different
 * attributes of a Renderer, when the Renderer is registered when the plugin
 * starts. *
 * @author volker
 */
public class Renderer {
    /** Renderer-ID */
    private String m_id;
    
    /** Renderer-Class */
    private String m_class;
    
    /** Analyze ResultType */
    private String m_resultType;
    
    /** The Instance of this Renderer */
    private Object m_executableExtension;
   
    /**
     * @param rendererId Renderer-ID
     * @param rendererClass Renderer-Class
     * @param rendererResultType The Analyze ResultType
     * @param obj The Instance of this Renderer
     */
    public Renderer(String rendererId, String rendererClass,
            String rendererResultType, Object obj) {
        setID(rendererId);
        setClass(rendererClass);
        setResultType(rendererResultType);
        setRendererInstance(obj);
    }
    
    /////////////////////////
    // Getters and Setters //
    /////////////////////////
    /**
     * @return The Instance of this Renderer 
     */
    public Object getRendererInstance() {
        return m_executableExtension;
    }
    
    /**
     * @param executableExtension The Instance of this Renderer
     */
    public void setRendererInstance(Object executableExtension) {
        this.m_executableExtension = executableExtension;
    }
    
    /**
     * @return The RendererID
     */
    public String getID() {
        return m_id;
    }
    /**
     * 
     * @param id The Renderer ID
     */
    public void setID(String id) {
        this.m_id = id;
    }
   
    /**
     * @return The RendererClass
     */
    public String getRendererClass() {
        return m_class;
    }
    
    /**
     * @param rendererClass The RendererClass
     */
    public void setClass(String rendererClass) {
        this.m_class = rendererClass;
    }
    
    /**
     * @return String The resultType
     */
    public String getResultType() {
        return m_resultType;
    }
    
    /**
     * @param resultType The resultType
     */
    public void setResultType(String resultType) {
        this.m_resultType = resultType;
    }
}
