/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.rc.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 * @created 16.10.2013
 */
public class PropertyUtil {
    
    /** Constructor */
    private PropertyUtil() {
        //empty
    }
    
    /**
     * Returns a sorted map consisting of the bean properties of a component
     * 
     * @param currComp 
     *              the component
     * @return the sorted map of properties
     */
    public static Map<String, String> getMapOfComponentProperties(
        final Object currComp) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils
                .getPropertyDescriptors(currComp);
        Map<String, String> componentProperties = new TreeMap<String, String>();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor pd = propertyDescriptors[i];
            try {
                Method readMethod = pd.getReadMethod();
                if (readMethod != null) {
                    Object obj = 
                            readMethod.invoke(currComp, new Object[]{});
                    String value = String.valueOf(obj);
                    if (value.length() > 200) {
                        value = StringUtils.substring(
                                value, 0, 200);
                    }
                    if (obj instanceof Character) {
                        Character c = (Character)obj;
                        if (c.charValue() == 0) {
                            value = StringConstants.EMPTY;
                        }
                    }
                    componentProperties.put(pd.getName(), value);
                } else {
                    componentProperties.put(pd.getName(),
                            "This property is not readable"); //$NON-NLS-1$
                }
            } catch (IllegalArgumentException e) {
                componentProperties.put(pd.getName(),
                        "Error"); //$NON-NLS-1$
            } catch (IllegalAccessException e) {
                componentProperties.put(pd.getName(),
                        "Error accessing this property"); //$NON-NLS-1$
            } catch (InvocationTargetException e) {
                componentProperties.put(pd.getName(),
                        "Error reading this property"); //$NON-NLS-1$
            }
        }
        return componentProperties;
    }
}