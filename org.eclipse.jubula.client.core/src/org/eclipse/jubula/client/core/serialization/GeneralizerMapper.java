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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * 
 *
 * @author BREDEX GmbH
 * @created 12.08.2005
 *
 */
public class GeneralizerMapper extends MapperWrapper {

    /**
     * @param wrapped constructor
     * 
     */
    public GeneralizerMapper(ClassMapper wrapped) {
        super(wrapped);
    }

    /**
     * return the name of the serialized class
     * {@inheritDoc}
     * @param type the type
     * @return the name
     */
    public String serializedClass(Class type) {
        int pos = type.getName().indexOf("$$"); //$NON-NLS-1$
        
        if (pos > 0) { 
            return type.getName().substring(0, pos);
        } else if (type.getName().indexOf("Persistence (JPA / EclipseLink)") >= 0) { //$NON-NLS-1$
            if (Map.class.isAssignableFrom(type)) {
                return HashMap.class.getName();
            } else if (List.class.isAssignableFrom(type)) {
                return ArrayList.class.getName();
            } else if (SortedSet.class.isAssignableFrom(type)) {
                return TreeSet.class.getName();
            } else if (Set.class.isAssignableFrom(type)) {
                return HashSet.class.getName();
            }
        }
        return super.serializedClass(type);
    }
}
