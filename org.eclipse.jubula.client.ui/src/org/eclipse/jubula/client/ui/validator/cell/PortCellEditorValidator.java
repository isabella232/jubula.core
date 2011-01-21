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
package org.eclipse.jubula.client.ui.validator.cell;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.tools.i18n.I18n;

/**
 * 
 * @author BREDEX GmbH
 * @created 20.01.2011
 */
public class PortCellEditorValidator implements ICellEditorValidator {

    /** 
     * the name of the column for the validated cell for use in presenting 
     * validation errors to the user 
     */
    private String [] m_i18nArguments;
    
    /**
     * Constructor
     * 
     * @param cellName The name of the validated cell for use in presenting
     *                 validation errors to the user. Must not be 
     *                 <code>null</code>.
     */
    public PortCellEditorValidator(String cellName) {
        Validate.notNull(cellName);
        m_i18nArguments = new String [] {cellName};
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public String isValid(Object value) {
        try {
            int portValue = Integer.parseInt(ObjectUtils.toString(value));
            if (portValue < Constants.MIN_PORT_NUMBER 
                    || portValue > Constants.MAX_PORT_NUMBER) {
                return I18n.getString("Validation.Port.error.invalidPortNumber", //$NON-NLS-1$
                        m_i18nArguments);
            }
            return null;
        } catch (NumberFormatException nfe) {
            // Fall through
        }
        
        return I18n.getString("Validation.Port.error.invalidPortNumber", //$NON-NLS-1$
                m_i18nArguments);
    }

}
