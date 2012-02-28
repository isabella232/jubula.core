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
 * This class is a model for a Category. It is used to save the different
 * attributes of a Category, when the Category is registered when the plugin
 * starts.
 * 
 * @author volker
 *
 */
public class Category {
    
    /** The Category ID */
    private String m_id;
    
    /** The Category Name */
    private String m_name;
    
    /** The ParentCategoryID */
    private String m_parentCatID;
       
    
    /**
     * @param id The CategoryID 
     * @param name The Category-Name
     * @param parentCatID The ParentCategoryID
     */
    public Category(String id, String name, String parentCatID) {
        setID(id);
        setName(name);
        setParentCatID(parentCatID);
    }
    
    /////////////////////////
    // Getters and Setters //
    /////////////////////////
    
    /**
     * @return The CategoryID
     */
    public String getID() {
        return m_id;
    }
    
    /**
     * @param id The CategoryID
     */
    public void setID(String id) {
        this.m_id = id;
    }
   
    /**
     * @return The Category-Name
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * @param name The Category-Name
     */
    public void setName(String name) {
        this.m_name = name;
    }
    
    /**
     * @return String ParentCategoryID
     */
    public String getParentCatID() {
        return m_parentCatID;
    }
    
    /**
     * @param parentCatID The ParentCategoryID
     */
    public void setParentCatID(String parentCatID) {
        this.m_parentCatID = parentCatID;
    }
    
    /**
     * Checks if there is a ParentCategory
     * @return returns true if there is a ParentCategory
     */
    public boolean hasTopLevelCat() {
        if (this.getParentCatID() == null
                || this.getParentCatID().length() == 0) {
            return false;
        }
        return true;
    }
}
