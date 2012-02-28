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
package org.eclipse.jubula.client.analyze.ui.internal.provider;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.analyze.ExtensionRegistry;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.internal.Category;
import org.eclipse.jubula.client.analyze.ui.internal.QueryResult;
import org.eclipse.jubula.client.analyze.ui.internal.helper.AnalyzeTreeViewerInputHelper;

/**
 * 
 * @author volker
 *
 */
public class QueryTreeContentProvider implements ITreeContentProvider {

    /**
     * 
     */
    private QueryResult m_qr;
    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }
 
    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        this.m_qr = (QueryResult) inputElement;
        
        return AnalyzeTreeViewerInputHelper.getTreeInput(m_qr);
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        
        // contains the given category or categories by parentElement
        Category[] category;
        
        // check if parentElement is instance of Category. If it is not element of 
        // Category it can only be an Category[]
        if (!(parentElement instanceof Category)) {
        
            Object[] parent = (Object[]) parentElement;
            category = new Category[parent.length];

            for (int i = 0; i < parent.length; i++) {
                category[i] = (Category) parent[i];
            }
        } else {
            category = new Category[1];
            category[0] = (Category) parentElement;
        }
        // contains the ChildCategories
        HashSet<Category> childCat = new HashSet<Category>();
        // contains the Analyzes that were executed by the query
        Analyze[] anaArray = new Analyze[m_qr.getResultMap().size()];
        // run over the category array
        for (int i = 0; i < category.length; i++) { 
            // get the registered categories from the ExtensionRegistry and that Category in
            // the childCat Set, that is a child of the given Category
            for (Map.Entry<String, Category> c : ExtensionRegistry.getCategory()
                    .entrySet()) {
                Category ca = c.getValue();
                if (ca.getParentCatID() != null
                        && ca.getParentCatID().equals(category[i].getID())) {
                    childCat.add(ca);
                } 
            }
            // if there are no ChildCategories get the Analyzes of the parentElement Category
            if (childCat.size() == 0) {
                
                int counter = 0;
                for (Map.Entry<Analyze, AnalyzeResult> ana : m_qr.getResultMap()
                        .entrySet()) {
                    // check if the given category is the parent category of this analyze
                    // an put the analyze in the analyze array if its true
                    if (ana.getKey().getCategoryID()
                            .equals(category[i].getID())) {
                        anaArray[counter] = ana.getKey();
                        counter++;
                    }
                }
            }
        } 
        if (childCat.size() != 0) {
            return childCat.toArray();
        } else {
            return anaArray;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getParent(Object element) {
        Category parentCategory = null;
        if (element instanceof Analyze) {
            Analyze analyze = (Analyze) element;
            for (Map.Entry<String, Category> c : ExtensionRegistry
                    .getCategory().entrySet()) {
                if (c.getKey().equals(analyze.getCategoryID())) {
                    parentCategory = c.getValue();
                }
            }
        } else {
            Category cat = (Category) element;
            for (Map.Entry<String, Category> c : ExtensionRegistry
                    .getCategory().entrySet()) {
                if (c.getKey().equals(cat.getParentCatID())) {
                    parentCategory = c.getValue();
                }
            }
            
        }
        return parentCategory;
    }
    /**
     * {@inheritDoc}
     */
    public boolean hasChildren(Object element) {
        return element instanceof Category;
    }
}
