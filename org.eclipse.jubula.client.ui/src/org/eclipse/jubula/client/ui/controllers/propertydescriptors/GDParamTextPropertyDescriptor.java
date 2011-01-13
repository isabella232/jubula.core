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
package org.eclipse.jubula.client.ui.controllers.propertydescriptors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.ui.businessprocess.WorkingLanguageBP;
import org.eclipse.jubula.client.ui.controllers.ContentAssistCellEditor;
import org.eclipse.jubula.client.ui.controllers.propertysources.AbstractGuiNodePropertySource.AbstractParamValueController;
import org.eclipse.jubula.client.ui.widgets.CheckedParamText;
import org.eclipse.jubula.client.ui.widgets.ParamProposalProvider;
import org.eclipse.jubula.tools.xml.businessmodell.Param;
import org.eclipse.jubula.tools.xml.businessmodell.ValueSetElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created 23.11.2007
 */
public class GDParamTextPropertyDescriptor extends TextPropertyDescriptor 
    implements IVerifiable {
    
    /** validator for parameter value validation */
    private IParamValueValidator m_dataValidator;

    /**
      * Creates a property descriptor with the given id and display name.
     * 
     * @param id The associated property controller.
     * @param displayName The name to display for the property.
     * @param validator for parameter value validation
     */
    public GDParamTextPropertyDescriptor(AbstractParamValueController id,
        String displayName, IParamValueValidator validator) {
        super(id, displayName);
        m_dataValidator = validator;
    }

    /**
     * @return Returns the validator.
     */
    public IParamValueValidator getDataValidator() {
        return m_dataValidator;
    }

    /**
     * Find value sets for paarameters in reused test cases
     * @param paramNode the Node to to check for values
     * @param paramGUID the GUID of the parameter
     * @return an array of values for this type of parameter which will be
     * empty if no such
     * set exists
     */
    public static String[] getValuesSet(IParamNodePO paramNode, 
            String paramGUID) {
        Set<Param> values = 
            TestCaseParamBP
                .getValuesForParameter(paramNode, paramGUID, WorkingLanguageBP
                        .getInstance().getWorkingLanguage());
        if (values.size() != 1) {
            return new String[0];
        }
        List<String> strValues = new ArrayList<String>();
        Param p = values.iterator().next();
    
        for (Iterator it = p.valueSetIterator(); it.hasNext();) {
            ValueSetElement vs = (ValueSetElement)it.next();
            strValues.add(vs.getValue());            
        }
        return strValues.toArray(new String[strValues.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public CellEditor createPropertyEditor(Composite parent) {
        AbstractParamValueController contr = 
            (AbstractParamValueController)getId();
        return new ContentAssistCellEditor(
                parent, new ParamProposalProvider(
                        getValuesSet(contr.getParamNode(), 
                                contr.getParamDesc().getUniqueId()), 
                        contr.getParamNode(), contr.getParamDesc()),
                new CheckedParamText.StringTextValidator(
                        contr.getParamNode(), contr.getParamDesc(), 
                        getDataValidator()), 
                ContentProposalAdapter.PROPOSAL_INSERT);
    }

}
