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

import org.eclipse.jubula.rc.swt.interfaces.ICComboImplClass;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Control;


/**
 * Implementation class for swt ccombo
 *
 * @author BREDEX GmbH
 * @created Oct 30, 2006
 */
public class CComboImplClass extends AbstractComboBoxImplClass
    implements ICComboImplClass {

    /** the CCombo from the AUT */
    private CCombo m_ccombo;
    /** The ComboBox helper this instance delegates to. */
    private IComboBoxHelper m_comboBoxHelper;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_ccombo = (CCombo)graphicsComponent;
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_ccombo;
    }
    
    /**
     * {@inheritDoc}
     */
    public IComboBoxHelper getComboBoxHelper() {
        if (m_comboBoxHelper == null) {
            m_comboBoxHelper = new CComboBoxHelper(this);
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