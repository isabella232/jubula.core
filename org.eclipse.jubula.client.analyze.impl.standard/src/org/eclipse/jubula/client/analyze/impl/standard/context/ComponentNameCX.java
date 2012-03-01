package org.eclipse.jubula.client.analyze.impl.standard.context;

import org.eclipse.jubula.client.analyze.definition.IContext;
import org.eclipse.jubula.client.core.model.IComponentNamePO;

/**
 * 
 * @author volker
 *
 */
public class ComponentNameCX implements IContext {

    /**   
     *  {@inheritDoc}
     */
    public boolean isActive(Object obj) {
        // checks if the given Object is a ComponentName
        if (obj instanceof IComponentNamePO) {
            return true;
        }
        return false;
    }
}