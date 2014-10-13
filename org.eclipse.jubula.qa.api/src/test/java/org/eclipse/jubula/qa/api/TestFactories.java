package org.eclipse.jubula.qa.api;

import java.net.URL;

import junit.framework.Assert;

import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.toolkit.concrete.ConcreteComponentFactory;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.client.ObjectMappingLoader;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.junit.Test;

/**
 * Class for testing the factories
 */
public class TestFactories {
    
    /** object mapping loader */
    private ObjectMappingLoader m_omLoader = new ObjectMappingLoader();
    
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
        IComponentIdentifier identifierTextField1 =
                m_omLoader.get("bound_SimpleAdder_inputField1_txf"); //$NON-NLS-1$
        Assert.assertNotNull(identifierTextField1);
        
        /** The second text field */
        IComponentIdentifier identifierTextField2 =
                m_omLoader.get("bound_SimpleAdder_inputField2_txf"); //$NON-NLS-1$
        Assert.assertNotNull(identifierTextField2);
        
        /** The equals button */
        IComponentIdentifier identifierEqualsButton =
                m_omLoader.get("bound_SimpleAdder_equals_btn"); //$NON-NLS-1$
        Assert.assertNotNull(identifierEqualsButton);
        
        /** The result text field */
        IComponentIdentifier identifierResultField =
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

        MessageCap mc1 = textField1.replaceText("17"); //$NON-NLS-1$
        Assert.assertNotNull(mc1);
        MessageCap mc2 = textField2.replaceText("4"); //$NON-NLS-1$
        Assert.assertNotNull(mc2);
        MessageCap mc3 = equalsButton.click(1, 1);
        Assert.assertNotNull(mc3);
        MessageCap mc4 = resultField.checkText("21", Operator.equals); //$NON-NLS-1$
        Assert.assertNotNull(mc4);
    }
}
