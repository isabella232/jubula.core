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
package org.eclipse.jubula.rc.swt.implclasses;

import org.eclipse.jubula.rc.swt.interfaces.ICombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;


/**
 * Implementation class for swt Combo
 *
 * @author BREDEX GmbH
 * @created Oct 30, 2006
 */
public class ComboImplClass extends AbstractComboBoxImplClass 
    implements ICombo {
    /** The ComboBox helper this instance delegates to. */
    private IComboBoxHelper m_comboBoxHelper;

    /** the Combo from the AUT */
    private Combo m_combo;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_combo = (Combo)graphicsComponent;
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_combo;
    }

    /**
     * {@inheritDoc}
     */
    public IComboBoxHelper getComboBoxHelper() {
        if (m_comboBoxHelper == null) {
            m_comboBoxHelper = new ComboBoxHelper(this);
        }
        return m_comboBoxHelper;
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }
    
}