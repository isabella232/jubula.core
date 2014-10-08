package org.eclipse.jubula.qa.api;

import junit.framework.Assert;

import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.toolkit.concrete.ConcreteComponentFactory;
import org.eclipse.jubula.toolkit.concrete.components.ButtonComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextComponent;
import org.eclipse.jubula.toolkit.concrete.components.TextInputComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.junit.Test;

/**
 * Class for testing the factories
 */
public class TestFactories {
    /** The first text field */
    private IComponentIdentifier m_identifierTextField1;
    /** The second text field */
    private IComponentIdentifier m_identifierTextfield2;
    /** The equals button */
    private IComponentIdentifier m_identifierEqualsButton;
    /** The result text field */
    private IComponentIdentifier m_identifierResultField;
    
    /**
     * test method
     */
    @Test
    public void testFactories() {
        
        TextInputComponent textField1 = ConcreteComponentFactory
                .createTextInputComponent(m_identifierTextField1);
        Assert.assertNotNull(textField1);
        
        TextInputComponent textField2 = ConcreteComponentFactory
                .createTextInputComponent(m_identifierTextfield2);
        Assert.assertNotNull(textField2);
        
        ButtonComponent equalsButton = ConcreteComponentFactory
                .createButtonComponent(m_identifierEqualsButton);
        Assert.assertNotNull(equalsButton);
        
        TextComponent resultField = ConcreteComponentFactory
                .createTextComponent(m_identifierResultField);
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
