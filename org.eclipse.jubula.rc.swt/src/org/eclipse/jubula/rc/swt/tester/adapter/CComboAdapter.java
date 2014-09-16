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
package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swt.tester.CAPUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
/**
 * Implementation of the Interface <code>IComboBoxAdapter</code> as a
 * adapter for the <code>CCombo</code> component.
 * This class is sub classing <code>AbstractComboBoxAdapter</code> because
 * <code>Combo</code> and <code>CCombo</code> have common parts
 * @author BREDEX GmbH
 *
 */
public class CComboAdapter extends AbstractComboBoxAdapter {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        CComboAdapter.class);
    
    /** */
    private CCombo m_combobox;
    
    /**
     * 
     * @param objectToAdapt 
     */
    public CComboAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_combobox = (CCombo) objectToAdapt;
    }

    /**
     * {@inheritDoc}
     */
    public void selectAll() {
        
        // Get focus
        selectNone();

        // FIXME zeb: Find a platform-independent way to select all text
        //            without calling CCombo methods directly.
        //            The current problem with clicking twice in the text area
        //            is that if there is any white space, only part of the
        //            text is selected.
        getEventThreadQueuer().invokeAndWait("selectAll",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
                    m_combobox.setSelection(
                            new Point(0, m_combobox.getText().length()));
                    
                    // return value is not used
                    return null;
                }
            
            });
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {    
        int selectedIndex = ((Integer)getEventThreadQueuer().invokeAndWait(
                CComboAdapter.class.getName()
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
                "getText", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return CAPUtil.getWidgetText(
                                m_combobox, m_combobox.getText());
                    }
                });
        return o != null ? o.toString() : null;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isComboEnabled() {        
        boolean isEnabled = ((Boolean)getEventThreadQueuer().invokeAndWait(
                CComboAdapter.class.getName()
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
    protected void selectImpl(final int index) {
        scrollIndexToVisible(index);
        
        Rectangle clickConstraints = 
            (Rectangle)getEventThreadQueuer().invokeAndWait(
                "setClickConstraints",  //$NON-NLS-1$
                new IRunnable() {

                    public Object run() throws StepExecutionException {
                        Rectangle constraints = 
                            SwtUtils.getRelativeWidgetBounds(
                                    getDropdownList(), getDropdownList());
                        int displayedItemCount = getDisplayedItemCount();
                        int numberBelowTop = 0;
                        if (displayedItemCount >= getItemCount()) {
                            numberBelowTop = index;
                        } else {
                            numberBelowTop = Math.max(0, index 
                                - getItemCount() + displayedItemCount);
                        }
                        
                        // Set the constraints based on the numberBelowTop
                        constraints.height = getDropdownList().getItemHeight();
                        constraints.y += (numberBelowTop * constraints.height);

                        return constraints;
                    }
            
                });
        
        // Note that we set scrollToVisible false because we have already done
        // the scrolling.
        getRobot().click(getDropdownList(), clickConstraints, 
            new ClickOptions().setScrollToVisible(false));

    }

    /**
     * {@inheritDoc}
     */
    protected void openDropdownList() {
        if (!isDropdownVisible()) {
            toggleDropdownList();
        }

        long timeout = TimeoutConstants.SERVER_TIMEOUT_WAIT_FOR_POPUP;
        long done = System.currentTimeMillis() + timeout; 
        long now;
        while (!isDropdownVisible() && timeout >= 0) {
            RobotTiming.sleepPreShowPopupDelay();
            now = System.currentTimeMillis();
            timeout = done - now;
        }
        
        if (!isDropdownVisible()) {
            log.debug("Dropdown list still not visible, must be an error"); //$NON-NLS-1$
            throw new StepExecutionException("dropdown list not visible", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.DROPDOWN_LIST_NOT_FOUND));
        }

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
     * Tries to find the dropdown list from the combobox 
     * @return the dropdown of the combobox, or <code>null</code> if the 
     *          dropdown could not be found
     */
    protected List getDropdownList()
        throws StepExecutionException {
        
        return (List)getEventThreadQueuer().invokeAndWait(
            "getDropdownList",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
            
                    Shell mainShell = SwtUtils.getShell(m_combobox);
                    Display d = Display.getCurrent();
                    Shell [] shells = d.getShells();
                    for (int i = 0; i < shells.length; i++) {
                        Shell curShell = shells[i];
                        if (mainShell == curShell.getParent() 
                            && curShell.getChildren().length == 1
                            && curShell.getChildren()[0] instanceof List) {
                            
                            List possibleDropdown = 
                                (List)curShell.getChildren()[0];
                            if (!possibleDropdown.isDisposed()
                                    && possibleDropdown.isVisible()
                                    && isDropdownList(possibleDropdown)) {
                                return possibleDropdown;
                            }
                        }
                    }

                    return null;
                }
            });
    }
    
    /**
     * Verifies that the given list is the dropdown list for this combo box.
     * 
     * @param list  The list to verify.
     * @return <code>true</code> if <code>list</code> is the dropdown list for
     *          this combo box. Otherwise <code>false</code>.
     */
    private boolean isDropdownList(List list) {
        /*
         * Verify that the list is close enough to the combo box.
         */

        Rectangle comboBounds = 
            SwtUtils.getWidgetBounds(m_combobox);
        Rectangle listBounds = SwtUtils.getWidgetBounds(list);
        
        // Expand the bounding rectangle for the combo box by a small amount
        int posFuzz = 5;
        int dimFuzz = posFuzz * 2;
        comboBounds.x -= posFuzz;
        comboBounds.width += dimFuzz;
        comboBounds.y -= posFuzz;
        comboBounds.height += dimFuzz;
        
        return comboBounds.intersects(listBounds);
    }
    
    /**
     * Tries to set the given index as the top element of the CCombo.
     * @param index The index to make visible
     */
    private void scrollIndexToVisible(final int index) {
        getEventThreadQueuer().invokeAndWait(
            "scrollIndexToVisible",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
           
                    getDropdownList().setTopIndex(index);

                    return null;
                }
            
            });
    }
    
    /**
     * 
     * @return  the number of items displayed in the dropdown list, or 0 if
     *          the list is not showing.
     */
    private int getDisplayedItemCount() {
        return ((Integer)getEventThreadQueuer().invokeAndWait(
                "getDisplayedItemCount",  //$NON-NLS-1$
                new IRunnable() {

                public Object run() throws StepExecutionException {
                    List dropdown = getDropdownList();
                    if (dropdown == null) {
                        return new Integer(0);
                    }
                    int listHeight = SwtUtils.getWidgetBounds(dropdown).height;
                    int itemHeight = dropdown.getItemHeight();
                    
                    return new Integer(listHeight / itemHeight);
                }
            
            })).intValue();
    }
    
    /**
     * @return true, if the dropdown of the combobox is visible
     */
    protected boolean isDropdownVisible() {
        Boolean visible = (Boolean)getEventThreadQueuer().invokeAndWait(
            CComboAdapter.class.getName()
            + "isDropdownVisible", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    List dropdownList = getDropdownList();
                    return dropdownList != null
                        ? Boolean.TRUE : Boolean.FALSE;
                }
            });
        return visible.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public String[] getValues() {
        return (String[])getEventThreadQueuer().invokeAndWait(
                "getItems", //$NON-NLS-1$
                new IRunnable() {                        
                    public Object run() {                        
                        return m_combobox.getItems(); 
                    }                        
                });
    }
}
