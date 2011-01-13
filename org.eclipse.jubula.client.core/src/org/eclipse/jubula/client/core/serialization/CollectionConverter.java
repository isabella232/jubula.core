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
package org.eclipse.jubula.client.core.serialization;

import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.mapper.Mapper;

/**
 * 
 *
 * @author BREDEX GmbH
 * @created 11.08.2005
 *
 */
public class CollectionConverter extends
    com.thoughtworks.xstream.converters.collections.CollectionConverter {


    /**
     * @param arg0 arg
     */
    public CollectionConverter(Mapper arg0) {
        super(arg0);
    }

    /**
     * {@inheritDoc}
     * @param arg0
     * @return
     */
    public boolean canConvert(Class type) {
        return super.canConvert(type) 
            || List.class.isAssignableFrom(type)
            || Set.class.isAssignableFrom(type);       
    }

}
