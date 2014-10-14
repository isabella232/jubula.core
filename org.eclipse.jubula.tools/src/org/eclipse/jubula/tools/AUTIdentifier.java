/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.tools;

/**
 * Exposes information to uniquely identify an AUT.
 *
 * @author BREDEX GmbH
 * @created Oct 13, 2014
 */
public interface AUTIdentifier {
    /**
     * @return the id of the AUT
     */
    String getID();
}