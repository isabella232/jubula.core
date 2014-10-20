package org.eclipse.jubula.qa.api;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.toolkit.concrete.ConcreteComponents;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.junit.Test;

/**
 * Class for testing the factories
 */
public class TestFactories {
    
    /** the resource url */
    private URL m_resourceURL = TestFactories.class.getClassLoader()
            .getResource("objectMapping_SimpleAdder.properties"); //$NON-NLS-1$
    
    /** object mapping loader */
    private ObjectMapping m_omLoader;
    
    /**
     * test method
     */
    @Test
    public void testFactories() {
        
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
        CAP cap3 = equalsButton.click(1, 1);
        Assert.assertNotNull(cap3);
        CAP cap4 = resultField.checkText("21", Operator.equals); //$NON-NLS-1$
        Assert.assertNotNull(cap4);
    }
}
