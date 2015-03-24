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
package org.eclipse.jubula.client.archive.converter;

import org.eclipse.jubula.client.schema.Project;


/**
 * General Interface for XML Converter
 * 
 * @author BREDEX GmbH
 * @created Nov 13, 2009
 */
public abstract class AbstractXmlConverter implements IXmlConverter {

    /**
     * {@inheritDoc}
     */
    public void convert(Project xml) {
        if (conversionIsNecessary(xml)) {
            convertImpl(xml);
        }
    }

    /**
     * @param xml
     *            the project xml to operate on
     * @return true if conversion is necessary
     */
    protected abstract boolean conversionIsNecessary(Project xml);

    /**
     * the main method which performs the conversion
     * 
     * @param xml
     *            the project xml to operate on
     */
    protected abstract void convertImpl(Project xml);
}