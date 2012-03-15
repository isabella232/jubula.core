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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;


/**
 * @author BREDEX GmbH
 * @created 05.12.2006
 */
public class ComboBoxHelper extends AbstractComboBoxHelper {

    /** number of clicks to select the whole text */
    public static final int CLICK_COUNT_FOR_SELECTING_ALL = 1;


    /** The logger. */
    private static AutServerLogger log = new AutServerLogger(
            ComboBoxHelper.class);

    /** The Robot. */
    private IRobot m_robot;
    /** The event thread queuer. */
    private IEventThreadQueuer m_eventThreadQueuer;
    /** the implementation class */
    private ComboImplClass m_implClass;
    
    /**
     * Creates a new instance.
     * @param implClass The implementation class of <code>JComboBox</code>.
     */
    public ComboBoxHelper(ComboImplClass implClass) {
        Validate.notNull(implClass, "The ComboBox implementation class must not be null"); //$NON-NLS-1$
        m_robot = implClass.getRobot();
        m_eventThreadQueuer = implClass.getEventThreadQueuer();
        m_implClass = implClass;
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
     * @return a rectangle, where the arrow icon is expected, relative to the
     *         combo box's location.
     */
    protected Rectangle findArrowIconArea() {
        final Control editor = m_implClass.getComponent();
        Rectangle r = null;
        if (editor == null) {
            throw new StepExecutionException("could not find editor", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }

        r = (Rectangle)m_eventThreadQueuer.invokeAndWait(
            ComboBoxHelper.class.getName()
            + "findArrowIconArea", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    return SwtUtils.getRelativeWidgetBounds(editor, editor);
                }
            });
        
        // Assume that the arrow icon is approximately square
        r.x += r.width - r.height;
        r.width = r.height;
        
        return r;
    }
    
    /**
     * Toggles the combobox dropdown list by clicking on the combo box.
     */
    protected void toggleDropdownList() {
        Combo comboBox = (Combo)m_implClass.getComponent();
        
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
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        final Combo combo = (Combo)m_implClass.getComponent();
        
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
     * 
     * {@inheritDoc}
     */
    protected boolean isComboEnabled() {
        final Combo combo = (Combo)m_implClass.getComponent();
        
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
     * {@inheritDoc}
     */
    protected void selectImpl(int index) {
        
        // Press 'Escape' key to close the dropdown list
        final Combo combo = (Combo)m_implClass.getComponent();
        m_robot.keyType(combo, SWT.ESC);

        // Currently no method to select elements via mouse clicks 
        selectComboIndex(index);
    }
    
    /**
     * 
     * @param index the index to select.
     * @see Combo#select(int)
     */
    private void selectComboIndex(final int index) {
        final Combo combo = (Combo)m_implClass.getComponent();
        m_eventThreadQueuer.invokeAndWait("selectComboIndex", new IRunnable() { //$NON-NLS-1$
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
            m_eventThreadQueuer.invokeAndWait("combo.selectAll", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        Combo comboBox = (Combo)m_implClass.getComponent();
                        int textLength = StringUtils.length(comboBox.getText());
                        comboBox.setSelection(new Point(0, textLength));
                        return null;
                    }
                });
        } else {
            m_robot.keyStroke(m_robot.getSystemModifierSpec() + " A"); //$NON-NLS-1$
        }
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
        m_robot.click(editor, null, ClickOptions.create().setClickCount(
                count.intValue()));
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
                    Combo comboBox = (Combo)m_implClass.getComponent();
                    return comboBox.getText();
                }
            });
        return o != null ? o.toString() : null;
    }
    
    /**
     * @return true, if combo is not read_only
     */
    private boolean isComboEditable() {
        final Combo combo = (Combo)m_implClass.getComponent();
        
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
     * Returns the number of items contained in the combo list.
     * @return  the number of items.
     */
    protected int getItemCount() {
        final Combo combo = (Combo)m_implClass.getComponent();
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
        final Combo combo = (Combo)m_implClass.getComponent();
        return (String)m_implClass.getEventThreadQueuer().invokeAndWait(
                "getItem", //$NON-NLS-1$
                new IRunnable() {
                    
                    public Object run() {
                    
                        return combo.getItem(index); 
                    }
                    
                });

    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void openDropdownList() {
        // FIXME zeb: Figure out a way to check the status of the dropdown list
        toggleDropdownList();

    }

}