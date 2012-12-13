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

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.caps.AbstractMenuCAPs;
import org.eclipse.jubula.rc.common.caps.AbstractWidgetCAPs;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IWidgetAdapter;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.caps.CAPUtil;
import org.eclipse.jubula.rc.swt.caps.MenuCAPs;
import org.eclipse.jubula.rc.swt.driver.DragAndDropHelperSwt;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.implclasses.EventListener;
import org.eclipse.jubula.rc.swt.implclasses.SimulatedTooltip;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.constants.TimeoutConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;
/**
 * Implements the interface for widgets and supports basic methods
 * which are needed for nearly a lot of components.
 * 
 *  @author BREDEX GmbH
 */
public abstract class WidgetAdapter extends AbstractComponentAdapter
    implements IWidgetAdapter {
    
    /** constants for communication */
    protected static final String POS_UNIT_PIXEL = "Pixel"; //$NON-NLS-1$
    /** constants for communication */
    protected static final String POS_UNI_PERCENT = "Percent"; //$NON-NLS-1$

 
    /**   */
    private Control m_component;
    
    /**
     * Is true, if a popup menu is shown 
     */
    protected static class PopupShownCondition implements
            EventListener.Condition {

        /**
         * the popup menu
         */
        private Menu m_popup = null;
        
        /**
         * 
         * @return the popup menu
         */
        public Menu getPopup() {
            return m_popup;
        }
        
        /**
         * {@inheritDoc}
         * @param event event
         * @return result of the condition
         */
        public boolean isTrue(Event event) {

            if (event.type == SWT.Show && event.widget instanceof Menu) {
                m_popup = (Menu)event.widget;
                return true;
            } 
            
            return false;
        }
    }
    
    /**
     * 
     * @param objectToAdapt 
     */
    protected WidgetAdapter(Object objectToAdapt) {
        m_component = (Control) objectToAdapt;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {
        return m_component;
    }
    
    /**
     * Gets the Robot. 
     * @return The Robot
     * @throws RobotException If the Robot cannot be created.
     */
    protected IRobot getRobot() throws RobotException {
        return AUTServer.getInstance().getRobot();
    }
    /**
     * @return The event thread queuer.
     */
    public IEventThreadQueuer getEventThreadQueuer() {
        return getRobotFactory().getEventThreadQueuer();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropteryValue(final String propertyname) {
        Object prop = getEventThreadQueuer().invokeAndWait("getProperty",  //$NON-NLS-1$
                new IRunnable() {
                    public Object run() throws StepExecutionException {
                        try {
                            return getRobot().getPropertyValue(
                                    getRealComponent(), propertyname);
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
     * {@inheritDoc}
     */
    public boolean isShowing() {
        Boolean actual = (Boolean)getEventThreadQueuer()
                .invokeAndWait("isShowing", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return m_component.isVisible() 
                            ? Boolean.TRUE : Boolean.FALSE; // see findBugs;
                    }
                });
        return actual.booleanValue();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {

        Boolean actual = (Boolean)getEventThreadQueuer()
                .invokeAndWait("isEnabled", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return m_component.isEnabled() 
                            ? Boolean.TRUE : Boolean.FALSE; // see findBugs;
                    }
                });
        return actual.booleanValue();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasFocus() {
        Boolean actual = (Boolean)getEventThreadQueuer()
                .invokeAndWait("hasFocus", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return m_component.isFocusControl() 
                            ? Boolean.TRUE : Boolean.FALSE; // see findBugs;
                    }
                });
        return actual.booleanValue();
    }
    
    /**
     * Shows and returns the popup menu
     * @param button MouseButton
     * @return the popup menu
     */
    public AbstractMenuCAPs showPopup(
            final int button) {
        final Widget component = m_component;
        if (SwtUtils.isMouseCursorInWidget(component)) {
            return showPopup(component, new Runnable() {
                public void run() {
                    RobotTiming.sleepPreShowPopupDelay();
                    
                    getRobot().clickAtCurrentPosition(component, 1, 
                            button);
                }
            });
        }
        return showPopup(50, POS_UNI_PERCENT, 50, 
            POS_UNI_PERCENT, button);
    }

    /**
     * Shows and returns the popup menu
     * 
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param button MouseButton
     * @return the popup menu
     * @throws StepExecutionException error
     */
    public AbstractMenuCAPs showPopup(
            final int xPos, final String xUnits,
            final int yPos, final String yUnits, 
            final int button) throws StepExecutionException {
        final Widget component = m_component;
        return showPopup(component, new Runnable() {
            public void run() {
                RobotTiming.sleepPreShowPopupDelay();
                boolean isAbsoluteUnitsX = 
                    POS_UNIT_PIXEL.equalsIgnoreCase(
                            xUnits);
                boolean isAbsoluteUnitsY = 
                    POS_UNIT_PIXEL.equalsIgnoreCase(
                            yUnits);
                getRobot().click(component, null, 
                    ClickOptions.create().setClickCount(1)
                        .setMouseButton(button), 
                    xPos, isAbsoluteUnitsX, yPos, isAbsoluteUnitsY);
            }
        });
    }

    /**
     * Shows and returns the popup menu
     * 
     * @param component The component for which to open the popup menu.
     * @param showPopup A <code>Runnable</code> that, when run, should display
     *                  a popup menu for the given component.
     * @return the popup menu
     * @throws StepExecutionException error
     */
    private AbstractMenuCAPs showPopup(final Widget component, 
        final Runnable showPopup) throws StepExecutionException {

        PopupShownCondition cond = new PopupShownCondition();
        EventLock lock = new EventLock();
        final EventListener listener = new EventListener(lock, cond);
        final Display d = component.getDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addPopupShownListeners", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                d.addFilter(SWT.Show, listener);
                
                return null;
            }
        });
        
        try {
            // showPopup must run in the current thread in order to
            // avoid a race condition.
            showPopup.run();

            synchronized (lock) {
                long timeout = TimeoutConstants.SERVER_TIMEOUT_WAIT_FOR_POPUP;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (timeout > 0)) {
                    lock.wait(timeout);
                    now = System.currentTimeMillis();
                    timeout = done - now;
                }
            } 
        } catch (InterruptedException e) {
            // ignore
        } finally {
            queuer.invokeAndWait("removePopupShownListeners", new IRunnable() { //$NON-NLS-1$
                public Object run() {
                    d.removeFilter(SWT.Show, listener);
                    
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("popup not shown", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.POPUP_NOT_FOUND));
        }
        
        MenuCAPs contextMenu = new MenuCAPs();
        contextMenu.setComponent(cond.getPopup());
        contextMenu.setContextMenu(true);
        return contextMenu;
    }
    
    /**
     * {@inheritDoc}
     */
    public void showToolTip(final String text, final int textSize, 
        final int timePerWord, final int windowWidth) {

        final Rectangle bounds = (Rectangle)getEventThreadQueuer()
            .invokeAndWait("gdShowText.getBounds", new IRunnable() { //$NON-NLS-1$

                public Object run() {
                    return SwtUtils.getWidgetBounds(m_component);
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
     * {@inheritDoc}
     */
    public void gdDrag(int mouseButton, String modifier, int xPos, 
            String xUnits, int yPos, String yUnits) {
        // Only store the Drag-Information. Otherwise the GUI-Eventqueue
        // blocks after performed Drag!
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        clickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
    }
    
    /**
     * {@inheritDoc}                  
     */
    public void gdDrop(final int xPos, final String xUnits, final int yPos, 
            final String yUnits, int delayBeforeDrop) {
        final DragAndDropHelperSwt dndHelper = DragAndDropHelperSwt
            .getInstance();
        final IRobot robot = getRobot();
        final String modifier = dndHelper.getModifier();
        final int mouseButton = dndHelper.getMouseButton();
        // Note: This method performs the drag AND drop action in one runnable
        // in the GUI-Eventqueue because after the mousePress, the eventqueue
        // blocks!
        try {
            CAPUtil.pressOrReleaseModifiers(modifier, true);

            getEventThreadQueuer().invokeAndWait("gdStartDrag", new IRunnable() { //$NON-NLS-1$
                public Object run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            mouseButton);

                    CAPUtil.shakeMouse();
                    
                    // drop
                    clickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
                    return null;
                }            
            });
            
            AbstractWidgetCAPs.waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, mouseButton);
            CAPUtil.pressOrReleaseModifiers(modifier, false);
        }
    }

    /**
     * clicks into a component. 
     * @param count amount of clicks
     * @param button what button should be clicked
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException error
     */
    protected void clickDirect(int count, int button, int xPos, String xUnits,
            int yPos, String yUnits)
        throws StepExecutionException {

        getRobot().click(
                m_component,
                null,
                ClickOptions.create().setClickCount(count).setMouseButton(
                        button), xPos, xUnits.equalsIgnoreCase(POS_UNIT_PIXEL),
                yPos, yUnits.equalsIgnoreCase(POS_UNIT_PIXEL));
    }
    
    /**
     * {@inheritDoc}
     */
    public int getKeyCode(String mod) {
        return KeyCodeConverter.getKeyCode(mod);
    }
}