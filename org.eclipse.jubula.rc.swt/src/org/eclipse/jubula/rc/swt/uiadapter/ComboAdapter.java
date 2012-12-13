/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.uiadapter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
/**
 * Implementation of the Interface <code>IComboBoxAdapter</code> as a
 * adapter for the <code>Combo</code> component.
 * This class is sub classing <code>AbstractComboBoxAdapter</code> because
 * <code>Combo</code> and <code>CCombo</code> have common parts
 * 
 * @author BREDEX GmbH
 *
 */
public class ComboAdapter extends AbstractComboBoxAdapter {

    /**  */
    private Combo m_combobox;

    /**
     * 
     * @param objectToAdapt 
     */
    public ComboAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_combobox = (Combo) objectToAdapt;
    }

    /**
     * Select the whole text of the textfield.
     */
    public void selectAll() {
        click(new Integer(1));
        
        // fix for https://bxapps.bredex.de/bugzilla/show_bug.cgi?id=201
        // The keystroke "command + a" sometimes causes an "a" to be entered
        // into the text field instead of selecting all text (or having no 
        // effect).
        if (EnvironmentUtils.isMacOS()) {
            getEventThreadQueuer().invokeAndWait("combo.selectAll", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        int textLength = StringUtils.length(
                                m_combobox.getText());
                        m_combobox.setSelection(new Point(0, textLength));
                        return null;
                    }
                });
        } else {
            getRobot().keyStroke(getRobot().getSystemModifierSpec() + " A"); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        
        int selectedIndex = ((Integer)getEventThreadQueuer().invokeAndWait(
                ComboAdapter.class.getName()
                + "getSelectedIndex", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        return new Integer(m_combobox.getSelectionIndex());
                    }
                })).intValue();

        return selectedIndex;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        Object o = getEventThreadQueuer().invokeAndWait(
                "getSelectedItem", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return m_combobox.getText();
                    }
                });
        return o != null ? o.toString() : null;        
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected boolean isComboEnabled() {
        
        boolean isEnabled = ((Boolean)getEventThreadQueuer().invokeAndWait(
                ComboAdapter.class.getName()
                + "isComboEnabled", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        return m_combobox.isEnabled() 
                                ? Boolean.TRUE : Boolean.FALSE;
                    }
                })).booleanValue();

        return isEnabled;
    }

    /**
     * {@inheritDoc}
     */
    protected void selectImpl(int index) {
        
        // Press 'Escape' key to close the dropdown list

        getRobot().keyType(m_combobox, SWT.ESC);

        // Currently no method to select elements via mouse clicks 
        selectComboIndex(index);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void openDropdownList() {
        // FIXME zeb: Figure out a way to check the status of the dropdown list
        toggleDropdownList();
    }

    /**
     * Returns the number of items contained in the combo list.
     * @return  the number of items.
     */
    protected int getItemCount() {
        return ((Integer)getEventThreadQueuer().invokeAndWait(
                "getItemCount", //$NON-NLS-1$
                new IRunnable() {
                    
                    public Object run() {
                    
                        return new Integer(m_combobox.getItemCount()); 
                    }
                    
                })).intValue();

    }

    /**
     * Returns the item at the given, zero-relative index in the combo list. 
     * Throws an exception if the index is out of range.
     * @param index the index of the item to return
     * @return  the item at the given index
     */
    protected String getItem(final int index) {
        return (String)getEventThreadQueuer().invokeAndWait(
                "getItem", //$NON-NLS-1$
                new IRunnable() {
                    
                    public Object run() {
                    
                        return m_combobox.getItem(index); 
                    }
                    
                });

    }
        
    /**
     * 
     * @param index the index to select.
     * @see Combo#select(int)
     */
    private void selectComboIndex(final int index) {
        final Combo combo = m_combobox;
        getEventThreadQueuer().invokeAndWait("selectComboIndex", new IRunnable() { //$NON-NLS-1$
            public Object run() throws StepExecutionException {
                combo.select(index);
                Event selectionEvent = new Event();
                selectionEvent.type = SWT.Selection;
                selectionEvent.widget = combo;
                combo.notifyListeners(SWT.Selection, selectionEvent);

                return null;
            }
        });   
    }
    
}
