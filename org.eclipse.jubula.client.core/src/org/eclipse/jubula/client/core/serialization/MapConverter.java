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

import java.util.Map;

import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author BREDEX GmbH
 * @created 11.08.2005
 */
public class MapConverter extends
    com.thoughtworks.xstream.converters.collections.MapConverter {


    /**
     * @param mapp mapper
     */
    public MapConverter(Mapper mapp) {
        super(mapp);
    }
    
    /**
     * {@inheritDoc}
     * @param type type
     * @return flag
     */
    public boolean canConvert(Class type) {
        
        return super.canConvert(type)
            || Map.class.isAssignableFrom(type);
    }

}
