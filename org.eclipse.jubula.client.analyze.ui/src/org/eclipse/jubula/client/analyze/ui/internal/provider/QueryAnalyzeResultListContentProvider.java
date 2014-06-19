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

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.ui.internal.QueryResult;

/**
 * 
 * @author volker
 *
 */
public class QueryAnalyzeResultListContentProvider implements
        IStructuredContentProvider {
    /**
     * The QueryResult Member
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
        this.m_qr = (QueryResult) newInput;
    }
    
/**
 * {@inheritDoc}
 */
    public Object[] getElements(Object inputElement) {
        
        int counter = 0;
        // contains the String ResultValues
        Object[] value = new Object[m_qr.getResultMap().size()];
        
        
        for (Map.Entry<Analyze, AnalyzeResult> e : m_qr.getResultMap()
                .entrySet()) {
            // put the ResultValues in an Array
            value[counter] =  e.getValue().getResult();
            counter++;
        }
        
        return value;
    }

}
