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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jubula.client.ui.rcp.actions.AbstractAction;


/**
 * @author BREDEX GmbH
 * @created 18.07.2006
 */
public abstract class AbstractActionBP {
    
    /** list with all registered action delegates */
    private Set<AbstractAction> m_actionDelegatePool = 
        new HashSet<AbstractAction>();
    
    
    /**
     * @param proxy associated proxy action to action
     */
    public void registerActionDelegate(AbstractAction proxy) {
        m_actionDelegatePool.add(proxy);
    }
    
    /**
     * @param action action to remove
     */
    public void unregisterActionDelegate(AbstractAction action) {
        if (action != null) {
            m_actionDelegatePool.remove(action);
        }
    }
    
    
    /**
     * @return if the registered actions are enabled
     * hook method (overwrite this method, if required)
     */
    public abstract boolean isEnabled();
    
    
    /**
     * set the EnabledState for all registered action delegates
     */
    public void setEnabledStatus() {
        for (AbstractAction proxy : m_actionDelegatePool) {
            proxy.setEnabledStatus(isEnabled());            
        }
    }
}
