/*******************************************************************************
 * Copyright (c) 2004, 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import org.eclipse.jubula.client.core.businessprocess.TDVariableStore;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * This abstract class allows you to search for property names.
 * @author BREDEX GmbH
 * @created 27.08.2014
 *
 */
public abstract class AbstractPropertyEvaluator 
    extends AbstractFunctionEvaluator {
    
    /**
     * getProperty allows to search for a Property 
     * with the abstract method getPropertyValue.
     * 
     * @param propertyName Name of the Property
     * @return Value of the property
     * @throws InvalidDataException
     */
    public String getProp(String[] propertyName) 
        throws InvalidDataException {
        validateParamCount(propertyName, 1);
        try {
            String key = 
                    TDVariableStore.PREDEF_VAR_PREFIX + propertyName[0];
            String propertyValue = getPropertyValue(key);
            if (propertyValue == null) {
                throw new IllegalArgumentException(Messages.EmptyProperty);
            }
            return propertyValue;
        } catch (SecurityException se) {
            throw new InvalidDataException(se.getLocalizedMessage(),
                    MessageIDs.E_WRONG_PARAMETER_VALUE);
        }
    }
    
    /**
     * getPropertyValue gets the Value of a property name.
     * @param name Name of the property.
     * @return Value of theProperty.
     */
    public abstract String getPropertyValue(String name);
}
