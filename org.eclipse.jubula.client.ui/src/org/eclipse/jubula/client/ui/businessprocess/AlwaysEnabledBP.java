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
package org.eclipse.jubula.client.ui.businessprocess;

/**
 * Enablement logic for an action that is always enabled.
 *
 * @author BREDEX GmbH
 * @created Nov 7, 2006
 */
public class AlwaysEnabledBP extends AbstractActionBP {

    /** single instance */
    private static AbstractActionBP instance = null;

    /**
     * private constructor
     */
    private AlwaysEnabledBP() {
        // Nothing to initialize
    }

    /**
     * @return single instance
     */
    public static AbstractActionBP getInstance() {
        if (instance == null) {
            instance = new AlwaysEnabledBP();
        }
        return instance;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return true;
    }

}
