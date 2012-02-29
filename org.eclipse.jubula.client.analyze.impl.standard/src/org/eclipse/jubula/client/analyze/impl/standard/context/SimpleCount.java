/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.impl.standard.context;

import org.eclipse.jubula.client.analyze.definition.IContext;


/**
 * 
 * @author volker
 * 
 */
public class SimpleCount implements IContext {

    /**
     * {@inheritDoc}
     */
    public boolean isActive(Object obj) {
        // returns true, because the SimpleCount metric has to be shown every
        // time
        return true;
    }

}