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

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.exception.StepVerifyFailedException;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swt.driver.DragAndDropHelperSwt;
import org.eclipse.jubula.rc.swt.interfaces.IToolItem;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;


/**
 * @author BREDEX GmbH
 * @created 23.03.2007
 */
public class ToolItemImplClass extends AbstractSwtImplClass 
    implements IToolItem {
    /** 
     * The dropdown menu, or <code>null</code> when the dropdown
     * menu is not showing.
     */
    private Menu m_menu;
    
    /**
     * Listener for the showing of the dropdown menu.
     */
    private Listener m_menuOpenListener = new Listener() {

        public void handleEvent(Event event) {
            if (event.widget instanceof Menu) {
                
                // Set menu
                m_menu = (Menu)event.widget;
                
                // Remove listener
                m_menu.getDisplay().removeFilter(SWT.Show, this);
                
                // Add Hide/Dispose listener
                m_menu.addMenuListener(m_menuCloseListener);
            }
        }
        
    };
    
    /**
     * Listener for the hiding of the dropdown menu
     */
    private MenuAdapter m_menuCloseListener = new MenuAdapter() {

        public void menuHidden(MenuEvent e) {
            if (e.widget instanceof Menu) {
                Menu menu = (Menu)e.widget;

                // Remove this listener
                menu.removeMenuListener(this);

                // Reinitialize menu variable
                m_menu = null;
            }
            
        }

    };

    /** The ToolItem */
    private ToolItem m_item = null; 
    
    /** Delegate for menu actions */
    private PopupMenuImplClass m_menuUtil = new PopupMenuImplClass();
    
    /**
     * {@inheritDoc}
     */
    public void gdClick(int count, int button) {
        getRobot().click(m_item, null, ClickOptions.create().setClickCount(
            count).setMouseButton(button));
    }

    /**
     * {@inheritDoc}
     */
    public void gdClick(int count) {
        gdClick(count, 1);
    }

    /**
     * clicks into a component. 
     * @param count amount of clicks
     * @param button what button should be clicked
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException if step execution fails.
     */
    public void gdClickDirect(int count, int button, int xPos, String xUnits, 
        int yPos, String yUnits) throws StepExecutionException {
        
        getRobot().click(m_item, null,
                ClickOptions.create()
                    .setClickCount(count)
                    .setMouseButton(button),
                xPos, 
                xUnits.equalsIgnoreCase(POS_UNIT_PIXEL), yPos, 
                yUnits.equalsIgnoreCase(POS_UNIT_PIXEL));
    }

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_item = (ToolItem)graphicsComponent;
    }

    /**
     * Selects an item from the button's dropdown menu.
     * 
     * @param namePath the menu item to select
     * @param operator operator used for matching
     */
    public void selectMenuItem(String namePath, String operator) {

        // Try to open menu
        openDropdownMenu();

        // Call appropriate delegate method
        m_menuUtil.selectMenuItem(namePath, operator);
    }
    
    /**
     * 
     * @param indexPath the menu item to select
     */
    public void selectMenuItemByIndexpath(String indexPath) {
        // Try to open menu
        openDropdownMenu();

        // Aquire menu
        Menu menu = getDropdown();

        // Set menu component
        m_menuUtil.setComponent(menu);
        
        // Call appropriate delegate method
        m_menuUtil.selectMenuItemByIndexpath(indexPath);
    }

    /**
     * Verifies if a MenuItem is en-/disabled depending on the enabled
     * parameter.
     * @param namePath the menu item to select
     * @param operator operator used for matching
     * @param enabled for checking enabled or disabled
     */
    public void verifyEnabled(String namePath, String operator, 
        boolean enabled) {
        
        // Try to open menu
        openDropdownMenu();

        // Call appropriate delegate method
        m_menuUtil.verifyEnabled(namePath, operator, enabled);

    }
    
    /**
     * Verifies if a MenuItem is en-/disabled depending on the enabled
     * parameter.
     * @param indexPath the menu item to select
     * @param enabled for checking enabled or disabled
     */
    public void verifyEnabledByIndexpath(String indexPath, boolean enabled) {

        // Try to open menu
        openDropdownMenu();

        // Call appropriate delegate method
        m_menuUtil.verifyEnabledByIndexpath(indexPath, enabled);

    }
    
    /**
     * Verifies if the specified menu item exists
     * @param namePath the menu item to verify against 
     * @param operator operator operator used for matching
     * @param exists for checking existence or unexistence
     */
    public void verifyExists(String namePath, String operator, boolean exists) {
        // Try to open menu
        openDropdownMenu();

        // Call appropriate delegate method
        m_menuUtil.verifyExists(namePath, operator, exists);

    }
    
    /**
     * Verifies that the component exists and is visible.
     *
     * @param exists
     *            <code>True</code> if the component is expected to exist
     *            and be visible, otherwise <code>false</code>.
     */
    public void gdVerifyExists(boolean exists) {
        /*
         * The actual testing of the component's existence/non-existence is 
         * implemented in CAPTestCommand.getImplClass. This method only checks
         * that the item has not been disposed.
         */
        verify(exists, "exists", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                return m_item.isDisposed() 
                    ? Boolean.FALSE : Boolean.TRUE; // see findBugs
            }
        });
    }

    /**
     * Verifies if the specified menu item exists
     * @param indexPath the menu item to select
     * @param exists for checking existence or unexistence
     */
    public void verifyExistsByIndexpath(String indexPath, boolean exists) {

        // Try to open menu
        openDropdownMenu();

        // Call appropriate delegate method
        m_menuUtil.verifyExistsByIndexpath(indexPath, exists);
    }
    
    /**
     * Checks if the specified menu item is selected. 
     * @param namePath the menu item to verify against
     * @param operator operator used for matching
     * @param selected for checking selected or not selected
     */
    public void verifySelected(String namePath, String operator, 
        boolean selected) {
        
        // Try to open menu
        openDropdownMenu();

        // Call appropriate delegate method
        m_menuUtil.verifySelected(namePath, operator, selected);
    }
    
    /**
     * Checks if the specified menu item is selected.
     * @param indexPath the menu item to verify against
     * @param selected for checking selected or not selected
     */
    public void verifySelectedByIndexpath(String indexPath, boolean selected) {
        
        // Try to open menu
        openDropdownMenu();

        // Call appropriate delegate method
        m_menuUtil.verifySelectedByIndexpath(indexPath, selected);
    }

    /**
     * Verifies if the textfield has the focus.
     * @param hasFocus The hasFocus property to verify.
     */
    public void gdVerifyFocus(boolean hasFocus) {
        /* 
         * Due to the way focus is handled in SWT, we never receive focus 
         * events, and only a Control can be listed as having focus. This means
         * that the tool item's toolbar can have focus, but NEVER the tool item
         * itself. We therefore assume that the tool item does not have focus.
         */
        Verifier.equals(hasFocus, false);
    }

    /**
     * Verifies the value of the property with the name <code>name</code>.
     * The name of the property has be specified according to the JavaBean
     * specification. The value returned by the property is converted into a
     * string by calling <code>toString()</code> and is compared to the passed
     * <code>value</code>.
     * 
     * @param name The name of the property
     * @param value The value of the property as a string
     * @param operator The operator used to verify
     */
    public void gdVerifyProperty(final String name, String value, 
            String operator) {
        final Item bean = m_item;
        Object prop = getEventThreadQueuer().invokeAndWait("getProperty",  //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    try {
                        return getRobot().getPropertyValue(bean, name);
                    } catch (RobotException e) {
                        throw new StepExecutionException(
                            e.getMessage(), 
                            EventFactory.createActionError(
                                TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
                    }
                }
            });
        final String propToStr = String.valueOf(prop);
        Verifier.match(propToStr, value, operator);
    }

    /**
     * Stores the value of the property with the name <code>name</code>.
     * The name of the property has be specified according to the JavaBean
     * specification. The value returned by the property is converted into a
     * string by calling <code>toString()</code> and is stored to the passed
     * variable.
     * 
     * @param variableName The name of the variable to store the property value in
     * @param propertyName The name of the property
     * @return the property value.
     */
    public String gdStorePropertyValue(String variableName, 
        final String propertyName) {
        final Item bean = m_item;
        Object prop = getEventThreadQueuer().invokeAndWait("getProperty",  //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    try {
                        return getRobot().getPropertyValue(bean, propertyName);
                    } catch (RobotException e) {
                        throw new StepExecutionException(
                            e.getMessage(), 
                            EventFactory.createActionError(
                                TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
                    }
                }
            });
        return String.valueOf(prop);
    }
    
    /**
     * Verifies the selected property.
     * @param selected The selected property value to verify.
     */
    public void gdVerifySelected(boolean selected) {
        Boolean actual = (Boolean)getEventThreadQueuer()
            .invokeAndWait("isSelected", new IRunnable() { //$NON-NLS-1$

                public Object run() {
                    return m_item.getSelection() 
                        ? Boolean.TRUE : Boolean.FALSE; // see findBugs;
                }
            });
        
        Verifier.equals(selected, actual.booleanValue());
    }
    
    /**
     * Verifies the passed text.
     * @param text The text to verify
     * @param operator The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        String value = (String)getEventThreadQueuer().invokeAndWait("getText", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    return SwtUtils.removeMnemonics(m_item.getText());
                }
            });

        Verifier.match(value, text, operator);
    }
    
    /**
     * Verifies the passed text.
     * @param text The text to verify
     */
    public void gdVerifyText(String text) {
        gdVerifyText(text, MatchUtil.DEFAULT_OPERATOR);
    }
    
    /**
     * {@inheritDoc}
     */
    public void gdVerifyEnabled(boolean enabled) {
        boolean isEnabled = ((Boolean)getEventThreadQueuer().invokeAndWait(
                "isEnabled", new IRunnable() { //$NON-NLS-1$
           
                    public Object run() {
                        return m_item.isEnabled() 
                            ? Boolean.TRUE : Boolean.FALSE;
                    }
                
                })).booleanValue();
        
        Verifier.equals(enabled, isEnabled);
    }
    
    /**
     * Action to read the value of a Button to store it in a variable in the Client
     * @param variable the name of the variable
     * @return the text value.
     */
    public String gdReadValue(String variable) {
        return (String)getEventThreadQueuer().invokeAndWait("getText",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
                    return SwtUtils.removeMnemonics(m_item.getText());
                }
            
            });

    }

    /**
     * Opens the dropdown menu for this component by clicking on its chevron
     * on the righthand side.
     * @return the dropdown menu, or <code>null</code> if the dropdown menu did 
     *         not appear.
     */
    private Menu openDropdownMenu() {
        int style = ((Integer)getEventThreadQueuer().invokeAndWait(
                "getStyle",  //$NON-NLS-1$
                new IRunnable() {

                    public Object run() throws StepExecutionException {
                        return new Integer(m_item.getStyle());
                    }

                })).intValue();
        if ((style & SWT.DROP_DOWN) == 0) {
            // ToolItem is not DropDown style
            throw new StepExecutionException(
                "Component does not have a dropdown menu.", //$NON-NLS-1$
                EventFactory.createActionError(
                    TestErrorEvent.DROPDOWN_NOT_FOUND));
        }
        
        // Add menuOpenListener
        getEventThreadQueuer().invokeAndWait("addMenuOpenListener",  //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
                    m_item.getDisplay().addFilter(SWT.Show, m_menuOpenListener);

                    return null;
                }

            });
        try {
            getRobot().click(m_item, null, ClickOptions.create().left(), 95,
                    false, 50, false);


            // FIXME zeb: could this be a race condition?:
            //            menu opening vs. trying to access the menu later
            //
            //            might be a good idea to wait for the Menu-Show event 
            //            (or timeout) before proceeding

            // Set menu component
            m_menuUtil.setComponent(m_menu);

            // If the menu did not open, then we have a problem
            if (m_menu == null) {
                throw new StepExecutionException(
                    "Dropdown menu did not appear.", //$NON-NLS-1$
                    EventFactory.createActionError(
                        TestErrorEvent.DROPDOWN_NOT_FOUND));
            }

            return m_menu;
        } finally {
            // Add menuOpenListener
            getEventThreadQueuer().invokeAndWait("removeMenuOpenListener",  //$NON-NLS-1$
                new IRunnable() {

                    public Object run() throws StepExecutionException {
                        m_item.getDisplay().removeFilter(
                            SWT.Show, m_menuOpenListener);

                        return null;
                    }

                });

        }
    }
    
    /**
     * 
     * @return the dropdown menu for this button, or <code>null</code> if the 
     *         menu is not currently visible.
     */
    private Menu getDropdown() {
        return m_menu;
    }

    /**
     * Opens the popup menu at the given position relative the current component
     * and selects an item at the given position in the popup menu
     * 
     * @param xPos what x position
     * @param yPos what y position
     * @param units should x,y position be pixel or percent values
     * @param indexPath path of item indices
     * @throws StepExecutionException error
     * @deprecated Will be removed with gdPopupSelectByIndexPath with int parameter
     * for MouseButton
     */
    public void gdPopupSelectByIndexPath(int xPos, int yPos, String units,
            String indexPath) throws StepExecutionException {
        gdPopupSelectByIndexPath(xPos, units, yPos, units, indexPath, 3);
    }
    
    /**
     * Opens the popup menu at the given position relative the current component
     * and selects an item at the given position in the popup menu
     * 
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param indexPath path of item indices
     * @param button MouseButton
     * @throws StepExecutionException error
     */
    public void gdPopupSelectByIndexPath(int xPos, String xUnits, int yPos, 
            String yUnits, String indexPath, 
            int button) throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(), 
            xPos, xUnits, yPos, yUnits, button);
        PopupMenuUtil.selectMenuItem(getRobot(), popup, 
            MenuUtil.splitIndexPath(indexPath));
    }
    
    /**
     * Select an item in the popup menu
     * @param indexPath path of item indices
     * @throws StepExecutionException error
     * @deprecated Will be removed with gdPopupSelectByIndexPath with int parameter
     * for MouseButton
     */
    public void gdPopupSelectByIndexPath(String indexPath) 
        throws StepExecutionException {
        gdPopupSelectByIndexPath(indexPath, 3);
    }
    
    /**
     * Select an item in the popup menu
     * @param indexPath path of item indices
     * @param button MouseButton
     * @throws StepExecutionException error
     */
    public void gdPopupSelectByIndexPath(String indexPath, int button) 
        throws StepExecutionException {
        
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(), button);
        PopupMenuUtil.selectMenuItem(getRobot(), popup, 
            MenuUtil.splitIndexPath(indexPath));
    }

    /**
     * Selects an item in the popup menu
     * @param textPath path of item texts 
     * @param operator operator used for matching
     * @throws StepExecutionException error
     * @deprecated Will be removed with gdPopupSelectByTextPath with int parameter
     * for MouseButton
     */
    public void gdPopupSelectByTextPath(String textPath, String operator) 
        throws StepExecutionException {
        gdPopupSelectByTextPath(textPath, operator, 3);
    }
    
    /**
     * Selects an item in the popup menu
     * @param textPath path of item texts 
     * @param operator operator used for matching
     * @param button MouseButton
     * @throws StepExecutionException error
     */
    public void gdPopupSelectByTextPath(String textPath, String operator,
            int button) 
        throws StepExecutionException {
        
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(), button);
        PopupMenuUtil.selectMenuItem(getRobot(), popup, 
            MenuUtil.splitPath(textPath), operator);
    }
    
    /**
     * Selects an item in the popup menu
     * 
     * @param xPos what x position
     * @param yPos what y position
     * @param units should x,y position be pixel or percent values
     * @param textPath path of item texts
     * @param operator operator used for matching
     * @throws StepExecutionException error
     * @deprecated Will be removed with gdPopupSelectByTextPath with int parameter
     * for MouseButton
     */
    public void gdPopupSelectByTextPath(final int xPos, final int yPos,
            final String units, String textPath, String operator)
        throws StepExecutionException {
        gdPopupSelectByTextPath(xPos, units, yPos, units, 
                textPath, operator, 3);
    }
    
    /**
     * Selects an item in the popup menu
     * 
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param textPath path of item texts
     * @param operator operator used for matching
     * @param button MouseButton
     * @throws StepExecutionException error
     */
    public void gdPopupSelectByTextPath(final int xPos, final String xUnits, 
            final int yPos, final String yUnits, String textPath, 
            String operator, int button)
        throws StepExecutionException {
        
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(), 
            xPos, xUnits, yPos, yUnits, button);
        PopupMenuUtil.selectMenuItem(getRobot(), popup, 
            MenuUtil.splitPath(textPath), operator);
    }
    
    /**
     * Checks if the specified context menu entry is enabled.
     * @param indexPath the menu item to verify
     * @param enabled for checking enabled or disabled
     * @param button MouseButton
     */
    public void gdPopupVerifyEnabledByIndexPath(String indexPath,
            boolean enabled, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                button);
        popupVerifyEnabledByIndexPath(indexPath, popup, enabled);
    }
    
    /**
     * Opens the popup menu at the given position relative the current component
     * and checks if the specified context menu entry is enabled.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param indexPath the menu item to verify
     * @param enabled for checking enabled or disabled
     * @param button MouseButton
     */
    public void gdPopupVerifyEnabledByIndexPath(int xPos, String xUnits, 
            int yPos, String yUnits, String indexPath, 
            boolean enabled, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                xPos, xUnits, yPos, yUnits, button);
        popupVerifyEnabledByIndexPath(indexPath, popup, enabled);
    }
    
    /**
     * Checks if the specified context menu entry is enabled.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param enabled for checking enabled or disabled
     * @param button MouseButton
     */
    public void gdPopupVerifyEnabledByTextPath(String textPath,
            String operator, boolean enabled, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                button);
        popupVerifyEnabledByTextPath(textPath, operator, popup, enabled);
    }
    
    /**
     * Opens the popup menu at the given position relative the current component
     * and checks if the specified context menu entry is enabled.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param enabled for checking enabled or disabled
     * @param button MouseButton
     */
    public void gdPopupVerifyEnabledByTextPath(final int xPos, 
            final String xUnits, final int yPos,
            final String yUnits, String textPath, String operator,
                boolean enabled, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                xPos, xUnits, yPos, yUnits, button);
        popupVerifyEnabledByTextPath(textPath, operator, popup, enabled);
    }
    
    /**
     * Checks if the specified context menu entry exists.
     * @param indexPath the menu item to verify
     * @param exists for checking if entry exists
     * @param button MouseButton
     */
    public void gdPopupVerifyExistsByIndexPath(String indexPath,
            boolean exists, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                button);
        popupVerifyExistsByIndexPath(indexPath, popup, exists);
    }
    
    /**
     * Opens the popup menu at the given position relative the current component
     * and checks if the specified context menu entry exists.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param indexPath the menu item to verify
     * @param exists for checking if entry exists
     * @param button MouseButton
     */
    public void gdPopupVerifyExistsByIndexPath(int xPos, String xUnits, 
            int yPos, String yUnits, String indexPath, 
            boolean exists, int button) throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                xPos, xUnits, yPos, yUnits, button);
        popupVerifyExistsByIndexPath(indexPath, popup, exists);
    }
    
    /**
     * Checks if the specified context menu entry exists.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param exists for checking if entry exists
     * @param button MouseButton
     */
    public void gdPopupVerifyExistsByTextPath(String textPath,
            String operator, boolean exists, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                button);
        popupVerifyExistsByTextPath(textPath, operator, popup, exists);
    }
    
    /**
     * Opens the popup menu at the given position relative the current component
     * and checks if the specified context menu entry exists.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param exists for checking if entry exists
     * @param button MouseButton
     */
    public void gdPopupVerifyExistsByTextPath(final int xPos, 
            final String xUnits, final int yPos,
            final String yUnits, String textPath, String operator,
            boolean exists, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                xPos, xUnits, yPos, yUnits, button);
        popupVerifyExistsByTextPath(textPath, operator, popup, exists);
    }
    
    /**
     * Checks if the specified context menu entry is selected.
     * @param indexPath the menu item to verify
     * @param selected for checking if entry is selected
     * @param button MouseButton
     */
    public void gdPopupVerifySelectedByIndexPath(String indexPath,
            boolean selected, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                button);
        popupVerifySelectedByIndexPath(indexPath, popup, selected);
    }
    
    /**
     * Opens the popup menu at the given position relative the current component
     * and checks if the specified context menu entry is selected.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param indexPath the menu item to verify
     * @param selected for checking if entry is selected
     * @param button MouseButton
     */
    public void gdPopupVerifySelectedByIndexPath(int xPos, String xUnits, 
            int yPos, String yUnits, String indexPath, 
            boolean selected, int button) throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                xPos, xUnits, yPos, yUnits, button);
        popupVerifySelectedByIndexPath(indexPath, popup, selected);
    }
    
    /**
     * Checks if the specified context menu entry is selected.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param selected for checking if entry is selected
     * @param button MouseButton
     */
    public void gdPopupVerifySelectedByTextPath(String textPath,
            String operator, boolean selected, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                button);
        popupVerifySelectedByTextPath(textPath, operator, popup, selected);
    }
    
    /**
     * Opens the popup menu at the given position relative the current component
     * and checks if the specified context menu entry is selected.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param selected for checking if entry is selected
     * @param button MouseButton
     */
    public void gdPopupVerifySelectedByTextPath(final int xPos, String xUnits, 
            final int yPos, final String yUnits, String textPath, 
            String operator, boolean selected, int button)
        throws StepExecutionException {
        Menu popup = PopupMenuUtil.showPopup(m_item, getRobot(),
                xPos, xUnits, yPos, yUnits, button);
        popupVerifySelectedByTextPath(textPath, operator, popup, selected);
    }
    
    /**
     * Checks if the specified context menu entry is enabled in the visible Popup
     * @param indexPath the menu item to verify
     * @param popup JPopupMenu
     * @param enabled for checking enabled or disabled
     */
    private void popupVerifyEnabledByIndexPath(String indexPath,
            Menu popup, boolean enabled) {
        final int[] indexItems = MenuUtil.splitIndexPath(indexPath);
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                popup, indexItems);
            if (menuItem == null) {
                throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.NOT_FOUND));
            }
            final boolean isEnabled = MenuUtil.isMenuItemEnabled(menuItem);
            Verifier.equals(enabled, isEnabled);
        } finally {
            try {
                int pathLength = indexItems != null ? indexItems.length : 0;
                PopupMenuUtil.closePopup(getRobot(), popup, pathLength);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
            }
        }
    }
    
    /**
     * Checks if the specified context menu entry is enabled.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param popup JPopupMenu
     * @param enabled for checking enabled or disabled
     */
    private void popupVerifyEnabledByTextPath(String textPath,
            String operator, Menu popup, boolean enabled)
        throws StepExecutionException {
        final String[] pathItems = MenuUtil.splitPath(textPath);
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                    popup, pathItems, operator);
            if (menuItem == null) {
                throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.NOT_FOUND));
            }
            final boolean isEnabled = MenuUtil.isMenuItemEnabled(menuItem);
            Verifier.equals(enabled, isEnabled);
        } finally {
            try {
                int pathLength = pathItems != null ? pathItems.length : 0;
                PopupMenuUtil.closePopup(getRobot(), popup, pathLength);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
            }
        }
    }
    
    /**
     * Checks if the specified context menu entry exists in the visible Popup
     * @param indexPath the menu item to verify
     * @param popup JPopupMenu
     * @param exists for checking if item exists
     */
    private void popupVerifyExistsByIndexPath(String indexPath,
            Menu popup, boolean exists) {
        final int[] indexItems = MenuUtil.splitIndexPath(indexPath);
        boolean isExisting = false;
        final Menu menu = popup;
        try {
            MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), menu, 
                indexItems);
            if (menuItem != null) {
                isExisting = true;
            }
        } finally {
            try {
                int pathLength = indexItems != null ? indexItems.length : 0;
                PopupMenuUtil.closePopup(getRobot(), popup, pathLength);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
            }
        }
        Verifier.equals(exists, isExisting);
    }
    
    /**
     * Checks if the specified context menu entry exists in the visible Popup.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param popup JPopupMenu
     * @param exists for checking if entry exists
     */
    private void popupVerifyExistsByTextPath(String textPath,
            String operator, Menu popup, boolean exists)
        throws StepExecutionException {
        final String[] pathItems = MenuUtil.splitPath(textPath);
        boolean isExisting = false;
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                popup, pathItems, operator);
            if (menuItem != null) {
                isExisting = true;
            }
        } finally {
            try {
                int pathLength = pathItems != null ? pathItems.length : 0;
                PopupMenuUtil.closePopup(getRobot(), popup, pathLength);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
            }
        }
        Verifier.equals(exists, isExisting);
    }
    
    /**
     * Checks if the specified context menu entry is selected in the visible Popup.
     * @param indexPath the menu item to verify
     * @param popup JPopupMenu
     * @param selected for checking if entry is selected
     */
    private void popupVerifySelectedByIndexPath(String indexPath,
            Menu popup, boolean selected)
        throws StepExecutionException {
        final int[] indexItems = MenuUtil.splitIndexPath(indexPath);
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                popup, indexItems);
            if (menuItem == null) {
                throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.NOT_FOUND));
            }
            final boolean isSelected = MenuUtil.isMenuItemSelected(menuItem);
            Verifier.equals(selected, isSelected);
        } finally {
            try {
                int pathLength = indexItems != null ? indexItems.length : 0;
                PopupMenuUtil.closePopup(getRobot(), popup, pathLength);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
            }
        }
    }
    
    /**
     * Checks if the specified context menu entry is selected in the visible Popup.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param popup JPopupMenu
     * @param selected for checking if entry is selected
     */
    private void popupVerifySelectedByTextPath(String textPath, String operator,
            Menu popup, boolean selected)
        throws StepExecutionException {
        final String[] pathItems = MenuUtil.splitPath(textPath);
        try {
            final MenuItem menuItem = MenuUtil.navigateToMenuItem(getRobot(), 
                popup, pathItems, operator);
            if (menuItem == null) {
                throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                        EventFactory.createActionError(
                                TestErrorEvent.NOT_FOUND));
            }
            final boolean isSelected = MenuUtil.isMenuItemSelected(menuItem);
            Verifier.equals(selected, isSelected);
        } finally {
            try {
                int pathLength = pathItems != null ? pathItems.length : 0;
                PopupMenuUtil.closePopup(getRobot(), popup, pathLength);
            } catch (StepExecutionException e) {
                // Menu item is disabled or menu is already closed
                // Do nothing
            }
        }
    }
    
    /**
     * dummy method for "wait for component"
     * @param timeout the maximum amount of time to wait for the component
     * @param delay the time to wait after the component is found
     * {@inheritDoc}
     */
    public void gdWaitForComponent (int timeout, int delay) {
        // do NOT delete this method!
        // do nothing, implementation is in class CAPTestCommand.getImplClass
        // because this action needs a special implementation!
    }

    /**
     * Checks wether two boolean values are equal. The first boolean value is
     * passed to the method, the second one is expected to be returned by the
     * <code>runnable</code>. 
     * @param expected The expected value.
     * @param name The runnable name.
     * @param runnable The runnable.
     * @throws StepVerifyFailedException If the values are not equal.
     */
    protected void verify(boolean expected, String name, IRunnable runnable)
        throws StepVerifyFailedException {
        Boolean actual = (Boolean)getEventThreadQueuer().invokeAndWait(name,
            runnable);
        Verifier.equals(expected, actual.booleanValue());
    }

    /**
     * Simulates a tooltip for demonstration purposes.
     * 
     * @param text The text to show in the tooltip
     * @param textSize The size of the text in points
     * @param timePerWord The amount of time, in milliseconds, used to display a
     *                    single word. A word is defined as a string surrounded
     *                    by whitespace.
     * @param windowWidth The width of the tooltip window in pixels.
     */
    public void gdShowText(final String text, final int textSize, 
        final int timePerWord, final int windowWidth) {

        final Rectangle bounds = (Rectangle)getEventThreadQueuer()
            .invokeAndWait("gdShowText.getBounds", new IRunnable() { //$NON-NLS-1$

                public Object run() {
                    return SwtUtils.getWidgetBounds(m_item);
                }
            });

        SimulatedTooltip sp = (SimulatedTooltip)getEventThreadQueuer()
            .invokeAndWait("gdShowText.initToolTip", new IRunnable() { //$NON-NLS-1$

                public Object run() throws StepExecutionException {
                    return new SimulatedTooltip(timePerWord, text,
                        windowWidth, textSize, bounds);
                }
            
            });
        sp.start();
        try {
            sp.join();
        } catch (InterruptedException e) {
            throw new StepExecutionException(e);
        }
    }
    
    /**
     * Performs a Drag. Moves into the middle of the Component and presses and
     * holds the given modifier and the given mouse button.
     * @param mouseButton the mouse button.
     * @param modifier the modifier, e.g. shift, ctrl, etc.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     */
    public void gdDrag(int mouseButton, String modifier, int xPos, 
            String xUnits, int yPos, String yUnits) {
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        gdClickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
    }
    
    
    /**
     * Performs a Drop. Moves into the middle of the Component and releases
     * the modifier and mouse button pressed by gdDrag.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     */
    public void gdDrop(final int xPos, final String xUnits, final int yPos, 
            final String yUnits, int delayBeforeDrop) {
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final String modifier = dndHelper.getModifier();
        final int mouseButton = dndHelper.getMouseButton();
        // Note: This method performs the drag AND drop action in one runnable
        // in the GUI-Eventqueue because after the mousePress, the eventqueue
        // blocks!
        pressOrReleaseModifiers(modifier, true);
        try {
            getEventThreadQueuer().invokeAndWait("gdDrop", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    //drag
                    getRobot().mousePress(null, null, mouseButton);
                    // drop
                    gdClickDirect(0, mouseButton, xPos, xUnits, yPos, 
                            yUnits);
                    return null;
                }            
            });

            waitBeforeDrop(delayBeforeDrop);

        } finally {
            // Do not perform the mouseRelease inside the EventThreadQueuer
            // (like Control implementation) it does not work for Items!
            getRobot().mouseRelease(null, null, mouseButton);
            pressOrReleaseModifiers(modifier, false);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] {SwtUtils.removeMnemonics(m_item.getText())};
    }
    
}
