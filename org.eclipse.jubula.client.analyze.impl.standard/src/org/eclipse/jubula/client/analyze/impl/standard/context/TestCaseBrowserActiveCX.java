package org.eclipse.jubula.client.analyze.impl.standard.context;

import org.eclipse.jubula.client.analyze.definition.IContext;
/**
 * 
 * @author volker
 *
 */
public class TestCaseBrowserActiveCX implements IContext {

    /**
     * 
     */
    public TestCaseBrowserActiveCX() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive(Object obj) {
            
        return false;
    }

}
