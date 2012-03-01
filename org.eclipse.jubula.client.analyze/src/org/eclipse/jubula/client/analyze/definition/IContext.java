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
package org.eclipse.jubula.client.analyze.definition;


/**
 * @author volker
 */
public interface IContext {

    /**
     * checks if the Context is active or not
     * @return true if the Context is active or not
     * @param obj The Object that has to be checked
     */
    boolean isActive(Object obj);
}
