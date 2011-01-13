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
package org.eclipse.jubula.client.ui.widgets;

/**
 * Methods used to query the state of a CheckedText (or subclass)
 * @author BREDEX GmbH
 * @created 14.03.2006
 */
public interface IGDCheckable {
    /**
     * Is the value of the CheckedText valid?
     * @return true if value is acceptable
     */
    public boolean isValid();
}
