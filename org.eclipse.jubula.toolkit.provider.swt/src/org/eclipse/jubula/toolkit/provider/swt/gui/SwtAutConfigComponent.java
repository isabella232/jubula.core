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
package org.eclipse.jubula.toolkit.provider.swt.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.core.utils.LocaleUtil;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.utils.DialogStatusParameter;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.client.ui.widgets.JavaAutConfigComponent;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;


/**
 * @author BREDEX GmbH
 * @created 13.02.2006
 * 
 */
public class SwtAutConfigComponent extends JavaAutConfigComponent {
    /** The keyboard layout of the AUT */
    public static final String KEYBOARD_LAYOUT = "KEYBOARD_LAYOUT"; //$NON-NLS-1$
       
    /**
     * The Combo to choose the keyboard layout.
     */
    private DirectCombo<Locale> m_keyboardLayoutCombo;
    
    /**
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param autConfig data to be displayed/edited
     * @param autName the name of the AUT that will be using this configuration.
     */
    public SwtAutConfigComponent(Composite parent, int style,
        Map<String, String> autConfig, String autName) {
        
        super(parent, style, autConfig, autName);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected void createAdvancedArea(Composite advancedAreaComposite) {
        super.createAdvancedArea(advancedAreaComposite);

        final List<Locale> langList = Languages.getInstance().getSuppLangList();
        final List<String> strLangList = new ArrayList<String>(langList.size());
        for (Locale locale : langList) {
            strLangList.add(locale.getDisplayName(Locale.getDefault()));
        }

        UIComponentHelper.createLabel(advancedAreaComposite, I18n
                .getString("SwtAutConfigComponent.KEYBOARD_LAYOUT")); //$NON-NLS-1$

        m_keyboardLayoutCombo = new DirectCombo<Locale>(
            advancedAreaComposite, SWT.READ_ONLY, langList, 
            strLangList, false, true);
        GridData comboGrid = new GridData(GridData.FILL, GridData.CENTER, 
            true , false, 2, 1);
        Layout.addToolTipAndMaxWidth(comboGrid, m_keyboardLayoutCombo);
        m_keyboardLayoutCombo.setLayoutData(comboGrid);
        ((GridData)m_keyboardLayoutCombo.getLayoutData()).widthHint = 
            COMPOSITE_WIDTH;
        
        // if new aut config, use defaults.
        String keyboardLayout = getConfigValue(KEYBOARD_LAYOUT);
        if (keyboardLayout == null || keyboardLayout.length() == 0) {
            m_keyboardLayoutCombo.setSelectedObject(Locale.getDefault());
        } else {
            m_keyboardLayoutCombo.setSelectedObject(
                LocaleUtil.convertStrToLocale(keyboardLayout));
        }

        m_keyboardLayoutCombo
                .addModifyListener(new KeyboardLayoutComboListener());
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void checkAll(java.util.List<DialogStatusParameter> paramList) {
        super.checkAll(paramList);
        addError(paramList, modifyKeyboardLayout());
    }
    
    /**
     * 
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyKeyboardLayout() {
        final Locale locale = m_keyboardLayoutCombo.getSelectedObject();
        if (locale != null) {
            putConfigValue(KEYBOARD_LAYOUT, locale.toString());
        }
        
        return null;
    }
    
    /**
     * @author BREDEX GmbH
     * @created 25.07.2007
     */
    protected class KeyboardLayoutComboListener implements ModifyListener {

        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            // FIXME : Take the Combo of the event!
            // Do not call m_keyboardLayoutCombo, it is null!
            // Maybe we have ClassLoader conflicts here?
            checkAll();
        }
    }
}