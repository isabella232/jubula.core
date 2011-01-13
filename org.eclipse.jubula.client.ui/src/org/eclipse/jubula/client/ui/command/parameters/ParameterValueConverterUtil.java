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
package org.eclipse.jubula.client.ui.command.parameters;

import org.apache.commons.lang.Validate;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.jubula.client.core.model.IPersistentObject;


/**
 * Utility class containing useful methods for parameter/String conversion.
 *
 * @author BREDEX GmbH
 * @created Apr 29, 2009
 */
public class ParameterValueConverterUtil {

    /**
     * Private constructor for utility class.
     */
    private ParameterValueConverterUtil() {
        // Do nothing
    }

    /**
     * Parses an ID from the given string.
     * 
     * @param idString The string from which to parse the ID.
     * @return the ID contained in the given string. This is guaranteed not to
     *         be <code>null</code>.
     * @throws ParameterValueConversionException 
     *              if the string does not represent a valid ID.
     */
    public static long parseId(String idString) 
        throws ParameterValueConversionException {
        
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException nfe) {
            throw new ParameterValueConversionException(
                    "parameterValue must be a valid ID", nfe); //$NON-NLS-1$
        }
        
    }

    /**
     * Verifies the type of the given value.
     * 
     * @param parameterValue The value of which to check the type.
     * @param type The expected type.
     * @throws ParameterValueConversionException 
     *              if <code>parameterValue</code> is not an instance 
     *              of <code>type</code>.
     */
    public static void checkType(
            Object parameterValue, Class< ? extends Object> type) 
        throws ParameterValueConversionException {

        Validate.notNull(type);
        if (!type.isInstance(parameterValue)) {
            throw new ParameterValueConversionException(
                    "parameterValue must be of type: " + type.getName()); //$NON-NLS-1$
        }
    }

    /**
     * 
     * @param po The Persistent Object.
     * @return the string-representation of the ID of the <code>po</code>. May
     *         be <code>null</code>.
     */
    public static String getIdString(IPersistentObject po) {
        return po.getId() != null ? po.getId().toString() : null;
    }
}
