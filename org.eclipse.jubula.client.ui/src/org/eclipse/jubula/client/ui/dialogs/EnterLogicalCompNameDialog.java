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
package org.eclipse.jubula.client.ui.dialogs;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.databinding.validators.ComponentNameValidator;
import org.eclipse.jubula.client.ui.widgets.GDText;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Dec 11, 2008
 */
public abstract class EnterLogicalCompNameDialog 
    extends AbstractValidatedDialog {

    /** observable (bindable) value for component name */
    private WritableValue m_name;
    
    /** initial value for name, null if none */
    private String m_initialName;

    /** the mapper used for finding and resolving component names */
    private IComponentNameMapper m_compNamesMapper;
    
    /**
     * Constructor
     * 
     * @param compNamesMapper 
     *          The mapper used for finding and resolving component names.
     * @param parentShell
     *            the parent SWT shell
     */
    public EnterLogicalCompNameDialog(
            IComponentNameMapper compNamesMapper, Shell parentShell) {
        
        super(parentShell);
        m_compNamesMapper = compNamesMapper;
    }
    /**
     * Constructor
     * 
     * @param compNamesMapper 
     *          The mapper used for finding and resolving component names.
     * @param parentShell
     *            the parent SWT shell
     * @param initialName if set used to initialize the name field
     */
    public EnterLogicalCompNameDialog(
            IComponentNameMapper compNamesMapper, Shell parentShell, 
            String initialName) {
        this(compNamesMapper, parentShell);
        m_initialName = initialName;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        Composite area = new Composite(parent, SWT.BORDER);
        area.setLayoutData(gridData);
        area.setLayout(new GridLayout(2, false));

        GDText componentNameField = createComponentName(area);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        Layout.addToolTipAndMaxWidth(gridData, componentNameField);
        componentNameField.setLayoutData(gridData);
        Layout.setMaxChar(componentNameField);
        
        IObservableValue nameFieldText = 
            SWTObservables.observeText(componentNameField, SWT.Modify);
        m_name = WritableValue.withValueType(String.class);
        
        getValidationContext().bindValue(
                nameFieldText,
                m_name,
                new UpdateValueStrategy()
                        .setAfterGetValidator(new ComponentNameValidator(
                                m_compNamesMapper, m_initialName)),
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
        
        if (m_initialName != null) {
            setName(m_initialName);
        }
        componentNameField.selectAll();

        return area;
    }

    /**
     * Creates the label and text field for the component name.
     * 
     * @param area The parent for the created widgets.
     * @return the created text field.
     */
    private GDText createComponentName(Composite area) {
        new Label(area, SWT.NONE).setText(I18n.getString(
                "NewCAPDialog.componentNameLabel")); //$NON-NLS-1$

        GDText componentNameField = new GDText(area, SWT.SINGLE | SWT.BORDER);
        return componentNameField;
    }

    /**
     * This method must be called from the GUI thread.
     * 
     * @return the name of the logical component name being created.
     */
    public String getName() {
        return (String)m_name.getValue();
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        m_name.setValue(name);
    }
    
}
