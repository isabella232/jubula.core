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
package org.eclipse.jubula.client.ui.rcp.preferences.editors;

import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.widgets.Composite;

/**
 * A field editor for adding space to a preference page.
 * 
 * @author BREDEX GmbH
 * @created 11.10.2004
 */
public class SpaceFieldEditor extends LabelFieldEditor {
    /**
     * Implemented as an empty m_label field editor.
     * 
     * @param parent
     *            the parent of the field editor's control
     */
    public SpaceFieldEditor(Composite parent) {
        super(StringConstants.EMPTY, parent);
    }
}

