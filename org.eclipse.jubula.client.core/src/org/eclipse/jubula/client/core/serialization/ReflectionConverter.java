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

import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.HibernateUtil;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * 
 *
 * @author BREDEX GmbH
 * @created 11.08.2005
 *
 */
public class ReflectionConverter extends
    com.thoughtworks.xstream.converters.reflection.ReflectionConverter {
    /**
     * @param mp mapper
     * @param prov provider
     */
    public ReflectionConverter(Mapper mp, ReflectionProvider prov) {
        super(mp, prov);
    }
    
    /**
     * {@inheritDoc}
     * @param obj object
     * @param writer writer
     * @param ctxt context
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer, 
        MarshallingContext ctxt) {
        
//        Class c = obj.getClass();
        if (obj instanceof IPersistentObject) {
            HibernateUtil.initialize(obj);
        }
//        Method[] methods = c.getMethods();
//        for (int i = 0; i < methods.length; i++) {
//            Method m = methods[i];
//            if (m.getName().startsWith("get")  //$NON-NLS-1$
//                && (m.getParameterTypes().length == 0)) {
//                try {
//                    m.setAccessible(true);
//                    m.invoke(obj, new Object[] {});
//                } catch (Throwable e) {
//                    final String msg = "Any problem calling getter of PO Objects."; //$NON-NLS-1$
//                    log.error(msg, e);
//                }
//            }
//        }
        super.marshal(obj, writer, ctxt);
    }

}
