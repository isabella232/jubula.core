package org.eclipse.jubula.qa.api;

import junit.framework.Assert;

import org.eclipse.jubula.toolkit.base.AbstractComponentFactory;
import org.eclipse.jubula.toolkit.base.widgets.GraphicsComponent;
import org.eclipse.jubula.toolkit.concrete.ConcreteComponentFactory;
import org.eclipse.jubula.toolkit.concrete.widgets.TabComponent;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.junit.Test;

/**
 * Class for testing the factories
 */
public class TestFactories {
    
    /**
     * test method
     */
    @Test
    public void testFactories() {
        
        Object componentIdentifier1 = null;
        GraphicsComponent graphicsComponent =
                AbstractComponentFactory.createGraphicsComponent(
                        componentIdentifier1);
        graphicsComponent.click(1, 1);
        Assert.assertNotNull(graphicsComponent);

        Object componentIdentifier2 = null;
        TabComponent accordion =
                ConcreteComponentFactory.createTabComponent(
                        componentIdentifier2);
        Assert.assertNotNull(accordion);
        accordion.checkExistenceOfTab("Testtab", Operator.matches, true); //$NON-NLS-1$
    }
}
