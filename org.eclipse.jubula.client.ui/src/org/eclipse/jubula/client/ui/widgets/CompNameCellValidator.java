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
package org.eclipse.jubula.client.ui.widgets;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ICompNameValidator;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.ui.databinding.validators.ComponentNameValidator;



/**
 * @author BREDEX GmbH
 * @created Feb 20, 2007
 */
public class CompNameCellValidator implements ICellEditorValidator {

    /** The table viewer */
    private CheckboxTableViewer m_tableViewer;

    /** the object responsible for performing the actual validation */
    private ICompNameValidator m_validationDelegate;
    
    /**
     * @param tableViewer The table viewer
     * @param validationDelegate the object that will perform the actual 
     *                           validation
     *                              
     */
    public CompNameCellValidator(
            CheckboxTableViewer tableViewer, 
            ICompNameValidator validationDelegate) {
        
        m_tableViewer = tableViewer;
        m_validationDelegate = validationDelegate;
    }

    /**
     * {@inheritDoc}
     */
    public String isValid(Object value) {
        
        String checkableName = ObjectUtils.toString(value);
        IStatus status = 
            ComponentNameValidator.isValidComponentNameString(checkableName);
        if (!status.isOK()) {
            return status.getMessage();
        }
        if (m_tableViewer.getSelection() == null 
            || m_tableViewer.getSelection().isEmpty()
            || !(m_tableViewer.getSelection() 
                    instanceof IStructuredSelection)) {
            return null;
        }

        
        ICompNamesPairPO compNamesPair = 
            ((ICompNamesPairPO)((IStructuredSelection)
                m_tableViewer.getSelection()).getFirstElement());

        return m_validationDelegate.isValid(
                compNamesPair.getType(), checkableName);
    }

}
