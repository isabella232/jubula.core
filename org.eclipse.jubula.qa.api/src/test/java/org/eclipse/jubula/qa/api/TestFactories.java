package org.eclipse.jubula.qa.api;

import java.net.URL;

import junit.framework.Assert;

import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.toolkit.concrete.ConcreteComponentFactory;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.OM;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.junit.Test;

/**
 * Class for testing the factories
 */
public class TestFactories {
    
    /** object mapping loader */
    private OM m_omLoader = MakeR.createOM();
    
    /**
     * test method
     */
    @Test
    public void testFactories() {

        URL resourceURL = TestFactories.class.getClassLoader()
            .getResource("objectMapping_SimpleAdder.properties"); //$NON-NLS-1$
        
        m_omLoader.init(resourceURL);
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
        
        TextInputComponent textField1 = ConcreteComponentFactory
                .createTextInputComponent(identifierTextField1);
        Assert.assertNotNull(textField1);
        
        TextInputComponent textField2 = ConcreteComponentFactory
                .createTextInputComponent(identifierTextField2);
        Assert.assertNotNull(textField2);
        
        ButtonComponent equalsButton = ConcreteComponentFactory
                .createButtonComponent(identifierEqualsButton);
        Assert.assertNotNull(equalsButton);
        
        TextComponent resultField = ConcreteComponentFactory
                .createTextComponent(identifierResultField);
        Assert.assertNotNull(resultField);

        CAP cap1 = textField1.replaceText("17"); //$NON-NLS-1$
        Assert.assertNotNull(cap1);
        CAP cap2 = textField2.replaceText("4"); //$NON-NLS-1$
        Assert.assertNotNull(cap2);
        CAP cap3 = equalsButton.click(1, 1);
        Assert.assertNotNull(cap3);
        CAP cap4 = resultField.checkText("21", Operator.equals); //$NON-NLS-1$
        Assert.assertNotNull(cap4);
    }
}
