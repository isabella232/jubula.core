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
package org.eclipse.jubula.client.ui.factory;

import org.eclipse.jubula.client.core.model.IDocAttributeDescriptionPO;
import org.eclipse.jubula.client.ui.attribute.EmptyAttributeRenderer;
import org.eclipse.jubula.client.ui.attribute.ErrorRenderer;
import org.eclipse.jubula.client.ui.attribute.IAttributeRenderer;


/**
 * @author BREDEX GmbH
 * @created 22.05.2008
 */
public class AttributeRendererFactory {

    /**
     * Private constructor for utility class.
     */
    private AttributeRendererFactory() {
        // Nothing to initialize
    }
    
    /**
     * Creates and returns a renderer appropriate for the given attribute
     * description.
     * 
     * @param attributeDescription The description for which to find a renderer.
     * @return an appropriate renderer for the given attribute description.
     */
    public static IAttributeRenderer getRenderer(
            IDocAttributeDescriptionPO attributeDescription) {
        
        try {
            Class displayClass = 
                Class.forName(attributeDescription.getDisplayClassName());
            Object instance = displayClass.newInstance();
            if (instance instanceof IAttributeRenderer) {
                return (IAttributeRenderer)instance; 
            }

            return new EmptyAttributeRenderer(); 
        } catch (ClassNotFoundException e) {
            return new ErrorRenderer(e); 
        } catch (InstantiationException e) {
            return new ErrorRenderer(e); 
        } catch (IllegalAccessException e) {
            return new ErrorRenderer(e); 
        }

    }
    
}
