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

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.constants.TimeoutConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created 06.12.2006
 */
public class CComboBoxHelper extends AbstractComboBoxHelper {

    /** The logger. */
    private static AutServerLogger log = new AutServerLogger(
            CComboBoxHelper.class);

    /** The Robot. */
    private IRobot m_robot;
    /** The event thread queuer. */
    private IEventThreadQueuer m_eventThreadQueuer;
    /** the implementation class */
    private CComboImplClass m_implClass;
    
    /**
     * Creates a new instance.
     * @param implClass The implementation class of <code>JComboBox</code>.
     */
    public CComboBoxHelper(CComboImplClass implClass) {
        Validate.notNull(implClass, "The CComboBox implementation class must not be null"); //$NON-NLS-1$
        m_robot = implClass.getRobot();
        m_eventThreadQueuer = implClass.getEventThreadQueuer();
        m_implClass = implClass;
    }

    /**
     * Tries to find the dropdown list from the combobox 
     * @return the dropdown of the combobox, or <code>null</code> if the 
     *          dropdown could not be found
     */
    protected List getDropdownList()
        throws StepExecutionException {
        
        return (List)m_eventThreadQueuer.invokeAndWait(
            "getDropdownList",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
            
                    Shell mainShell = SwtUtils.getShell(
                        m_implClass.getComponent());
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
            SwtUtils.getWidgetBounds(m_implClass.getComponent());
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
     * @return true, if the dropdown of the combobox is visible
     */
    protected boolean isDropdownVisible() {
        Boolean visible = (Boolean)m_eventThreadQueuer.invokeAndWait(
            CComboBoxHelper.class.getName()
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
     * 
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
     * Toggles the combobox dropdown list by clicking on the combo box.
     */
    protected void toggleDropdownList() {
        CCombo comboBox = (CCombo)m_implClass.getComponent();
        
        // Click in the center of the pulldown button
        Rectangle r = findArrowIconArea();

        if (log.isDebugEnabled()) {
            log.debug("Toggling dropdown by clicking on rectangle: " + r  //$NON-NLS-1$
                + "within component: " + comboBox); //$NON-NLS-1$
        }

        m_robot.click(comboBox, r, 
                ClickOptions.create().setScrollToVisible(false)
                    .setConfirmClick(false));
    }

    /**
     * @return true, if combo is not read_only
     */
    private boolean isComboEditable() {
        final CCombo combo = (CCombo)m_implClass.getComponent();

        Integer comboStyle = (Integer)m_eventThreadQueuer.invokeAndWait(
            ComboBoxHelper.class.getName()
            + "isComboEditable", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    return new Integer(combo.getStyle());
                }
            });
        
        return ((comboStyle.intValue() & SWT.READ_ONLY) == 0);
    }

    /**
     * Select the whole text of the textfield.
     */
    public void selectAll() {
        
        // Get focus
        selectNone();

        // FIXME zeb: Find a platform-independant way to select all text
        //            without calling CCombo methods directly.
        //            The current problem with clicking twice in the text area
        //            is that if there is any white space, only part of the
        //            text is selected.
        m_eventThreadQueuer.invokeAndWait("selectAll",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
            
                    CCombo combo = (CCombo)m_implClass.getComponent();
                    combo.setSelection(new Point(0, combo.getText().length()));
                    
                    // return value is not used
                    return null;
                }
            
            });

    }
    
    /**
     * Give the text field focus without selecting any of the text.
     */
    public void selectNone() {
        // FIXME zeb: This places the caret at the center of the component, 
        //            which may or may not be correct. Where should the text
        //            be inserted?
        click(new Integer(
            AbstractComboBoxHelper.CLICK_COUNT_FOR_SELECTING_NONE));
    }
    
    /**
     * {@inheritDoc}
     */
    public void click(Integer count) {
        Control editor = m_implClass.getComponent();
        if (editor == null) {
            throw new StepExecutionException("no editor found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }

        m_robot.click(editor, null,
            ClickOptions.create().setClickCount(count.intValue()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEditable() {
        return isComboEditable();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getSelectedValue() {
        Object o = m_eventThreadQueuer.invokeAndWait(
            "getSelectedItem", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    CCombo comboBox = (CCombo)m_implClass.getComponent();
                    return comboBox.getText();
                }
            });
        return o != null ? o.toString() : null;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void selectImpl(final int index) {
        scrollIndexToVisible(index);
        
        Rectangle clickConstraints = 
            (Rectangle)m_eventThreadQueuer.invokeAndWait(
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
        m_robot.click(getDropdownList(), clickConstraints, 
            new ClickOptions().setScrollToVisible(false));

    }

    /**
     * Tries to set the given index as the top element of the CCombo.
     * @param index The index to make visible
     */
    private void scrollIndexToVisible(final int index) {
        m_eventThreadQueuer.invokeAndWait(
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
        return ((Integer)m_eventThreadQueuer.invokeAndWait(
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
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        final CCombo combo = (CCombo)m_implClass.getComponent();
        
        int selectedIndex = ((Integer)m_eventThreadQueuer.invokeAndWait(
                ComboBoxHelper.class.getName()
                + "getSelectedIndex", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        return new Integer(combo.getSelectionIndex());
                    }
                })).intValue();

        return selectedIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void input(String text, boolean replace)
        throws StepExecutionException, IllegalArgumentException {
    
        Validate.notNull(text, "text must not be null"); //$NON-NLS-1$
        Control editor = m_implClass.getComponent();
        if (editor == null) {
            throw new StepExecutionException("could not find editor", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        if (replace) {
            selectAll();
        } else {
            selectNone();
        }
        m_robot.type(editor, text);
        m_robot.keyType(null, SWT.KEYPAD_CR);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isComboEnabled() {
        final CCombo combo = (CCombo)m_implClass.getComponent();
        
        boolean isEnabled = ((Boolean)m_eventThreadQueuer.invokeAndWait(
                ComboBoxHelper.class.getName()
                + "isComboEnabled", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        return combo.isEnabled() ? Boolean.TRUE : Boolean.FALSE;
                    }
                })).booleanValue();

        return isEnabled;
    }

    /**
     * @return a rectangle, where the arrow icon is expected, relative to the 
     *         combo box's location.
     */
    protected Rectangle findArrowIconArea() {
        final Control comboBox = m_implClass.getComponent();
        Rectangle r = null;
        if (comboBox == null) {
            throw new StepExecutionException("could not find editor", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }

        r = (Rectangle)m_eventThreadQueuer.invokeAndWait(
            ComboBoxHelper.class.getName()
            + "findArrowIconArea", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    return SwtUtils.getRelativeWidgetBounds(comboBox, comboBox);
                }
            });
        
        // Assume that the arrow icon is approximately square
        r.x += r.width - r.height;
        r.width = r.height;
        
        return r;
    }

    /**
     * Returns the number of items contained in the combo list.
     * @return  the number of items.
     */
    protected int getItemCount() {
        final CCombo combo = (CCombo)m_implClass.getComponent();
        return ((Integer)m_implClass.getEventThreadQueuer().invokeAndWait(
                "getItemCount", //$NON-NLS-1$
                new IRunnable() {
                    
                    public Object run() {
                    
                        return new Integer(combo.getItemCount()); 
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
        final CCombo combo = (CCombo)m_implClass.getComponent();
        return (String)m_implClass.getEventThreadQueuer().invokeAndWait(
                "getItem", //$NON-NLS-1$
                new IRunnable() {
                    
                    public Object run() {
                    
                        return combo.getItem(index); 
                    }
                    
                });

    }

}