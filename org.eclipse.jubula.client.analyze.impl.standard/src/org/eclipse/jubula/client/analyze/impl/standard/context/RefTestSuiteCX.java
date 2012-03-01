package org.eclipse.jubula.client.analyze.impl.standard.context;

import org.eclipse.jubula.client.analyze.definition.IContext;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;

/**
 * 
 * @author volker
 * 
 */
public class RefTestSuiteCX implements IContext {

    /**
     * {@inheritDoc}
     */
    public boolean isActive(Object obj) {
        // checks if the given Object is a referenced TestSuite
        if (obj instanceof IRefTestSuitePO) {
            return true;
        }
        return false;
    }

}
