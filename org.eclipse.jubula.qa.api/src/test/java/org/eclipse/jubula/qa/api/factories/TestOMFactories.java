/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.qa.api.factories;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.qa.api.om.OM;
import org.eclipse.jubula.toolkit.concrete.ConcreteComponents;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.junit.Test;
/**
 * Class for testing the OM factory
 */
public class TestOMFactories {

    /** the resource url */
    private URL m_resourceURL = TestComponentFactories.class.getClassLoader()
            .getResource("objectMapping_SimpleAdder.properties"); //$NON-NLS-1$
    /** object mapping loader */
    private ObjectMapping m_omLoader;

    /**
     * test method
     */
    @Test
    public void testFactoriesViaPropertiesFile() {
        
        try {
            m_omLoader = MakeR.createObjectMapping(m_resourceURL.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(m_omLoader);
        
        /** The first text field */
        ComponentIdentifier identifierTextField1 =
                m_omLoader.get("bound_SimpleAdder_inputField1_txf"); //$NON-NLS-1$
        Assert.assertNotNull(identifierTextField1);
        
        /** The second text field */
        ComponentIdentifier identifierTextField2 =
                m_omLoader.get("bound_SimpleAdder_inputField2_txf"); //$NON-NLS-1$
        Assert.assertNotNull(identifierTextField2);
        
        /** The equals button */
        ComponentIdentifier identifierEqualsButton =
                m_omLoader.get("bound_SimpleAdder_equals_btn"); //$NON-NLS-1$
        Assert.assertNotNull(identifierEqualsButton);
        
        /** The result text field */
        ComponentIdentifier identifierResultField =
                m_omLoader.get("bound_SimpleAdder_resultField_txf"); //$NON-NLS-1$
        Assert.assertNotNull(identifierResultField);
        
        TextInputComponent textField1 = ConcreteComponents
                .createTextInputComponent(identifierTextField1);
        Assert.assertNotNull(textField1);
        
        TextInputComponent textField2 = ConcreteComponents
                .createTextInputComponent(identifierTextField2);
        Assert.assertNotNull(textField2);
        
        ButtonComponent equalsButton = ConcreteComponents
                .createButtonComponent(identifierEqualsButton);
        Assert.assertNotNull(equalsButton);
        
        TextComponent resultField = ConcreteComponents
                .createTextComponent(identifierResultField);
        Assert.assertNotNull(resultField);
    
        CAP cap1 = textField1.replaceText("17"); //$NON-NLS-1$
        Assert.assertNotNull(cap1);
        CAP cap2 = textField2.replaceText("4"); //$NON-NLS-1$
        Assert.assertNotNull(cap2);
        CAP cap3 = equalsButton.click(1, InteractionMode.primary);
        Assert.assertNotNull(cap3);
        CAP cap4 = resultField.checkText("21", Operator.equals); //$NON-NLS-1$
        Assert.assertNotNull(cap4);
    }

    /**
     * test method
     */
    @Test
    public void testFactoriesViaPropertiesClass() {
        
        /** The first text field */
        ComponentIdentifier identifierTextField1 =
                OM.bound_SimpleAdder_inputField1_txf;
        Assert.assertNotNull(identifierTextField1);
        
        /** The second text field */
        ComponentIdentifier identifierTextField2 =
                OM.bound_SimpleAdder_inputField2_txf;
        Assert.assertNotNull(identifierTextField2);
        
        /** The equals button */
        ComponentIdentifier identifierEqualsButton =
                OM.bound_SimpleAdder_equals_btn;
        Assert.assertNotNull(identifierEqualsButton);
        
        /** The result text field */
        ComponentIdentifier identifierResultField =
                OM.bound_SimpleAdder_resultField_txf;
        Assert.assertNotNull(identifierResultField);
        
        TextInputComponent textField1 = ConcreteComponents
                .createTextInputComponent(identifierTextField1);
        Assert.assertNotNull(textField1);
        
        TextInputComponent textField2 = ConcreteComponents
                .createTextInputComponent(identifierTextField2);
        Assert.assertNotNull(textField2);
        
        ButtonComponent equalsButton = ConcreteComponents
                .createButtonComponent(identifierEqualsButton);
        Assert.assertNotNull(equalsButton);
        
        TextComponent resultField = ConcreteComponents
                .createTextComponent(identifierResultField);
        Assert.assertNotNull(resultField);
    
        CAP cap1 = textField1.replaceText("17"); //$NON-NLS-1$
        Assert.assertNotNull(cap1);
        CAP cap2 = textField2.replaceText("4"); //$NON-NLS-1$
        Assert.assertNotNull(cap2);
        CAP cap3 = equalsButton.click(1, InteractionMode.primary);
        Assert.assertNotNull(cap3);
        CAP cap4 = resultField.checkText("21", Operator.equals); //$NON-NLS-1$
        Assert.assertNotNull(cap4);
    }

}
