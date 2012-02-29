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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
/**
 * 
 * @author volker
 *
 */
public class QueryAnalyzeResultListLabelProvider extends LabelProvider {
    /**
     * @param element element
     * @return null 
     */
    public Image getImage(Object element) {
        return null;
    }
    /**
     * @param element element
     * @return The Texts
     */
    public String getText(Object element) {
        return element.toString();
        
    }


}
