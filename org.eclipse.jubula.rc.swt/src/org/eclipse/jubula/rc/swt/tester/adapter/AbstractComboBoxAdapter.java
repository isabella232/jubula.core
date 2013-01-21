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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComboBoxAdapter;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
/**
 * Implementation of the Interface <code>IComboBoxAdapter</code> as a abstract
 * adapter for the swt comboboxes.
 * This class needs specific methods of its subclasses therefore it is abstract.
 * 
 * @author BREDEX GmbH
 *
 */
public abstract class AbstractComboBoxAdapter extends WidgetAdapter 
    implements IComboBoxAdapter {
    
    /** number of clicks to give focus without selecting any text */
    public static final int CLICK_COUNT_FOR_SELECTING_NONE = 3;

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        AbstractComboBoxAdapter.class);

    /**
     * 
     * @param objectToAdapt 
     */
    protected AbstractComboBoxAdapter(Object objectToAdapt) {
        super(objectToAdapt);
    }
    
    /** {@inheritDoc} */
    public void select(final String value, String operator, 
        final String searchType) 
        throws StepExecutionException, IllegalArgumentException {
    
        Validate.notNull(value, "text must not be null"); //$NON-NLS-1$
        
        Integer[] indices = findIndicesOfValue(value, operator, searchType);
        Arrays.sort(indices);
        if (indices.length == 0) {
            throw new StepExecutionException("Text '" + value //$NON-NLS-1$ 
                + "' not found", //$NON-NLS-1$ 
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        select(indices[0].intValue());
    }
    
    /**
     * {@inheritDoc}
     */
    public void select(int index) {
        
        int comboItemCount = getItemCount();

        if (index >= comboItemCount
            || index < 0) {
            throw new StepExecutionException("Combo Box index '" + index //$NON-NLS-1$
                + "' is out of range", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.INVALID_INDEX));
        }

        if (isComboEnabled()) {
            // FIXME zeb: Needs special handling if style is not DROP_DOWN
            openDropdownList();
            selectImpl(index);
        }
    }

    /**
     * @return <code>true</code> if the combo box is currently enabled.
     */
    protected abstract boolean isComboEnabled();

    /**
     * 
     * @param index idx
     */
    protected abstract void selectImpl(int index);
    
    /**
     * Opens the combobox dropdown list. May also be
     * called if the list is already visible.
     */
    protected abstract void openDropdownList();

    /**
     * Finds the indices of the list elements that are rendered with the passed
     * values.
     * 
     * @param value
     *            the value
     * @param operator
     *            operator to use
     * @param searchType 
     *            Determines where the search begins ("relative" or "absolute")
     * @return The array of indices. It's length is equal to the length of the
     *         values array, but may contains <code>null</code> elements for
     *         all values that are not found in the list
     */
    private Integer[] findIndicesOfValue(final String value, 
            final String operator, final String searchType) {
        
        final Set indexSet = new HashSet();

        for (int i = getStartingIndex(searchType); i < getItemCount(); ++i) {
            String str = getItem(i);
            if (MatchUtil.getInstance().match(str, value, operator)) {
                indexSet.add(new Integer(i));
            }
        }
        
        Integer[] indices = new Integer[indexSet.size()];
        indexSet.toArray(indices);
        return indices;
    }

    /**
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @return The index from which to begin a search, based on the search type
     *         and (if appropriate) the currently selected item.
     */
    private int getStartingIndex(final String searchType) {
        int startingIndex = 0;
        if (searchType.equalsIgnoreCase(
                CompSystemConstants.SEARCH_TYPE_RELATIVE)) {
            startingIndex = getSelectedIndex();
        }
        return startingIndex;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(String value, String operator) {
        Integer[] indices = null;
        if (operator.equals(MatchUtil.NOT_EQUALS)) {
            indices = findIndicesOfValue(value,
                MatchUtil.EQUALS, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
            return indices.length == 0;
        } 
        indices = findIndicesOfValue(value,
            operator, CompSystemConstants.SEARCH_TYPE_ABSOLUTE);
        return indices.length > 0;
    }

    /**
     * Returns the number of items contained in the combo list.
     * @return  the number of items.
     */
    protected abstract int getItemCount();

    /**
     * Returns the item at the given, zero-relative index in the combo list. 
     * Throws an exception if the index is out of range.
     * @param index the index of the item to return
     * @return  the item at the given index
     */
    protected abstract String getItem(final int index);
    
    /**
     * {@inheritDoc}
     */
    public void input(String text, boolean replace)
        throws StepExecutionException, IllegalArgumentException {
    
        Validate.notNull(text, "text must not be null"); //$NON-NLS-1$
        Control editor = (Control) getRealComponent();
        if (editor == null) {
            throw new StepExecutionException("could not find editor", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        if (replace) {
            selectAll();
        } else {
            selectNone();
        }
        getRobot().type(editor, text);
        getRobot().keyType(null, SWT.KEYPAD_CR);
    }
    
    /**
     * {@inheritDoc}
     */
    public void click(Integer count) {
        Control editor = (Control) getRealComponent();
        if (editor == null) {
            throw new StepExecutionException("no editor found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        getRobot().click(editor, null, ClickOptions.create().setClickCount(
                count.intValue()));
    }
    
    /**
     * Give the text field focus without selecting any of the text.
     */
    public void selectNone() {
        // FIXME zeb: This places the caret at the center of the component, 
        //            which may or may not be correct. Where should the text
        //            be inserted?
        click(new Integer(
            CLICK_COUNT_FOR_SELECTING_NONE));
    }
    /**
     * Toggles the combobox dropdown list by clicking on the combo box.
     */
    protected void toggleDropdownList() {
        
        // Click in the center of the pulldown button
        Rectangle r = findArrowIconArea();
            
        if (log.isDebugEnabled()) {
            log.debug("Toggling dropdown by clicking on rectangle: " + r  //$NON-NLS-1$
                + "within component: " + getRealComponent()); //$NON-NLS-1$
        }

        getRobot().click(getRealComponent(), r,
            ClickOptions.create().setScrollToVisible(false)
                .setConfirmClick(false));

    }
    
    /**
     * @return a rectangle, where the arrow icon is expected, relative to the
     *         combo box's location.
     */
    protected Rectangle findArrowIconArea() {
        final Control editor = (Control) getRealComponent();
        Rectangle r = null;
        if (editor == null) {
            throw new StepExecutionException("could not find editor", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }

        r = (Rectangle)getEventThreadQueuer().invokeAndWait(
             AbstractComboBoxAdapter.class.getName()
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
     * @return true, if combo is not read_only
     */
    private boolean isComboEditable() {
        
        Integer comboStyle = (Integer)getEventThreadQueuer().invokeAndWait(
            AbstractComboBoxAdapter.class.getName()
            + "isComboEditable", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    return new Integer(((Widget)getRealComponent()).getStyle());
                }
            });

        return ((comboStyle.intValue() & SWT.READ_ONLY) == 0);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEditable() {
        return isComboEditable();
    }
}
