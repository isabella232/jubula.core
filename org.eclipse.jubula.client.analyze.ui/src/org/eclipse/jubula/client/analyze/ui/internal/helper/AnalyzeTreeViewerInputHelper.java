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
package org.eclipse.jubula.client.analyze.ui.internal.helper;

import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.analyze.ExtensionRegistry;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.internal.Category;
import org.eclipse.jubula.client.analyze.ui.internal.QueryResult;


/**
 * Handles the conversion of a given QueryResult, which is going to be used as a
 * TreeInput
 * 
 * @author volker
 * 
 */
public class AnalyzeTreeViewerInputHelper {
    
    /** Empty because of HelperClass */
    private AnalyzeTreeViewerInputHelper() {
    }
    /**
     * This method is used to transform a given QueryResult to an Array
     * which can be used as TreeInput
     * 
     * @param object The given Object (a QueryResult)
     * @return returns an Array of the ToplevelCategories 
     */
    public static Object[] getTreeInput(Object object) {
        
        QueryResult qr = (QueryResult) object;
        
        HashSet<Category> categorySet = new HashSet<Category>();
        
        for (Map.Entry<Analyze, AnalyzeResult> a : qr.getResultMap()
                .entrySet()) {
            
            Category c = ExtensionRegistry.getCategory().get(
                    a.getKey().getCategoryID());
            // add the parent category of this category to the categorySet
            categorySet.add(getRoot(c));
        }
        return categorySet.toArray();
    }
    
    /**
     * This method returns the root Category of the given Category by calling
     * itself recursively until the rootnode ist reached
     * 
     * @param category
     *            The given Category
     * @return Parent the parentObject
     */
    public static Category getRoot(Category category) {
        
        Category parent = category;
        if (!StringUtils.isEmpty(category.getParentCatID())) {
            
            for (Map.Entry<String, Category> entry : ExtensionRegistry
                    .getCategory().entrySet()) {
            
                if (category.getParentCatID().equals(entry.getKey())) {
                    parent =  entry.getValue();
                
                    getRoot(parent);
                }
            }
        }

        return parent;        
    }
}
