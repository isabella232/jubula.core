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
package org.eclipse.jubula.rc.swing.swing.implclasses;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ContainerEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.adaptable.ITextRendererAdapter;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.ClickOptions.ClickModifier;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.exception.StepVerifyFailedException;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.swing.driver.RobotFactoryConfig;
import org.eclipse.jubula.rc.swing.swing.driver.KeyCodeConverter;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.constants.TimeoutConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.TimeUtil;


/**
 * @author BREDEX GmbH
 * @created 08.03.2005
 */
public abstract class AbstractSwingImplClass implements
    IImplementationClass {
    /**
     * <code>RENDERER_FALLBACK_TEXT_GETTER_METHOD_1</code>
     */
    public static final String RENDERER_FALLBACK_TEXT_GETTER_METHOD_1 = "getTestableText"; //$NON-NLS-1$

    /**
     * <code>RENDERER_FALLBACK_TEXT_GETTER_METHOD_2</code>
     */
    public static final String RENDERER_FALLBACK_TEXT_GETTER_METHOD_2 = "getText"; //$NON-NLS-1$

    /** The default separator for enumerations of list values. */
    public static final char INDEX_LIST_SEP_CHAR =
        TestDataConstants.VALUE_CHAR_DEFAULT;
    /** The dafault separator of a list of values */
    public static final char VALUE_SEPARATOR =
        TestDataConstants.VALUE_CHAR_DEFAULT;

    /**
     * Is true, if a popup menu is shown
     */
    private static class PopupShownCondition implements
            EventListener.Condition {

        /**
         * the popup menu
         */
        private JPopupMenu m_popup = null;

        /**
         *
         * @return the popup menu
         */
        public JPopupMenu getPopup() {
            return m_popup;
        }

        /**
         * {@inheritDoc}
         * @param event event
         * @return result of the condition
         */
        public boolean isTrue(AWTEvent event) {
            if (event.getID() != ContainerEvent.COMPONENT_ADDED) {
                return false;
            }
            ContainerEvent ce = (ContainerEvent)event;
            if (ce.getChild() instanceof JPopupMenu) {
                m_popup = (JPopupMenu)ce.getChild();
                return true;
            } else if (ce.getChild() instanceof Container) {
                Container popupContainer = (Container)ce.getChild();
                final int length = popupContainer.getComponents().length;
                for (int i = 0; i < length; i++) {
                    if (popupContainer.getComponents()[i]
                                                       instanceof JPopupMenu) {

                        m_popup = (JPopupMenu)popupContainer.getComponents()[i];
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /** constants for communication */
    protected static final String POS_UNIT_PIXEL = "Pixel"; //$NON-NLS-1$
    /** constants for communication */
    protected static final String POS_UNI_PERCENT = "Percent"; //$NON-NLS-1$


    /** the logger */
    private static AutServerLogger log =
        new AutServerLogger(AbstractSwingImplClass.class);

    /** the high lighter for object mapping */
    private final HighLighter m_highLighter = new HighLighter();
    /**
     * The robot factory.
     */
    private IRobotFactory m_robotFactory;

    /**
     * @return The component passed to the implementation class by calling
     *         {@link IImplementationClass#setComponent(Object)}
     */
    public abstract JComponent getComponent();
    /**
     * Gets the Robot factory. The factory is created once per instance.
     *
     * @return The Robot factory.
     */
    protected IRobotFactory getRobotFactory() {
        if (m_robotFactory == null) {
            m_robotFactory = new RobotFactoryConfig().getRobotFactory();
        }
        return m_robotFactory;
    }
    /**
     * Gets the IEventThreadQueuer.
     *
     * @return The Robot
     * @throws RobotException
     *             If the Robot cannot be created.
     */
    protected IRobot getRobot() throws RobotException {
        return getRobotFactory().getRobot();
    }
    /**
     * @return The event thread queuer.
     */
    protected IEventThreadQueuer getEventThreadQueuer() {
        return getRobotFactory().getEventThreadQueuer();
    }
    /**
     * Checks wether two boolean values are equal. The first boolean value is
     * passed to the method, the second one is expected to be returned by the
     * <code>runnable</code>.
     *
     * @param expected
     *            The expected value.
     * @param name
     *            The runnable name.
     * @param runnable
     *            The runnable.
     * @throws StepVerifyFailedException
     *             If the values are not equal.
     */
    protected void verify(boolean expected, String name, IRunnable runnable)
        throws StepVerifyFailedException {
        Boolean actual = (Boolean)getEventThreadQueuer().invokeAndWait(name,
            runnable);
        Verifier.equals(expected, actual.booleanValue());
    }
    /**
     * {@inheritDoc}
     */
    public void highLight(Component component, Color border) {
        try {
            final Component comp = component;
            final Color col = border;
            getEventThreadQueuer().invokeLater(
                    "highLight", new Runnable() { //$NON-NLS-1$
                        public void run() {
                            m_highLighter.highLight(comp, col);
                        }
                    });
        } catch (StepExecutionException bsee) {
            log.error(bsee);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void lowLight(Component component) {
        try {
            final Component comp = component;
            getEventThreadQueuer().invokeLater(
                    "lowLight", new Runnable() { //$NON-NLS-1$
                        public void run() {
                            m_highLighter.lowLight(comp);
                        }
                    });
        } catch (StepExecutionException bsee) {
            log.error(bsee);
        }
    }
    
    /**
     * @param renderer
     *            The component which is used as the renderer
     * @return The string that the renderer displays.
     * @throws StepExecutionException
     *             If the renderer component is not of type <code>JLabel</code>,
     *             <code>JToggleButton</code>, <code>AbstractButton</code>,
     *             <code>JTextComponent</code> or supports one of the fallback 
     *             methods
     */
    public static String getRenderedText(Component renderer)
        throws StepExecutionException {
        String renderedText = resolveRenderedText(renderer);
        if (renderedText != null) {
            return renderedText;
        }
        throw new StepExecutionException(
            "Renderer not supported: " + renderer.getClass(), //$NON-NLS-1$
            EventFactory.createActionError(
                    TestErrorEvent.RENDERER_NOT_SUPPORTED));
    }

    /**
     * @param renderer
     *            The component which is used as the renderer
     * @return The string that the renderer displays or <code>null</code> if it
     *         could not be resolved.
     */
    public static String resolveRenderedText(Component renderer) {
        if (renderer instanceof JLabel) {
            return ((JLabel)renderer).getText();
        } else if (renderer instanceof JToggleButton) {
            return ((JToggleButton)renderer).isSelected() ? Boolean.TRUE
                .toString() : Boolean.FALSE.toString();
        } else if (renderer instanceof AbstractButton) {
            return ((AbstractButton)renderer).getText();
        } else if (renderer instanceof JTextComponent) {
            return ((JTextComponent)renderer).getText();
        } 
        // Check if an adapter exists
        ITextRendererAdapter textRendererAdapter = 
            ((ITextRendererAdapter) AdapterFactoryRegistry
                .getInstance().getAdapter(
                        ITextRendererAdapter.class, renderer));
        if (textRendererAdapter != null) {
            return textRendererAdapter.getText();
        } else if (renderer != null) {
            String[] methodNames = new String[] {
                RENDERER_FALLBACK_TEXT_GETTER_METHOD_1,
                RENDERER_FALLBACK_TEXT_GETTER_METHOD_2 };
            for (int i = 0; i < methodNames.length; i++) {
                String text = getTextFromComponent(renderer, methodNames[i]);
                if (text != null) {
                    return text;
                }
            }
        }
        return null;
    }

    /**
     * @param obj
     *            the object to invoke the method for
     * @param getterName
     *            the name of the getter Method for string retrival
     * @return the return value of the given method name or <code>null</code> if
     *         something went wrong during method invocation
     */
    private static String getTextFromComponent(Object obj, String getterName) {
        String text = null;
        try {
            Method getter = null;
            Class objClass = obj.getClass();
            try {
                getter = objClass.getDeclaredMethod(getterName, null);
            } catch (NoSuchMethodException e) {
                // ignore
            } catch (SecurityException e) {
                // ignore
            }
            if (getter == null) {
                try {
                    getter = objClass.getMethod(getterName, null);
                } catch (NoSuchMethodException e) {
                    return text;
                } catch (SecurityException e) {
                    return text;
                }
            }
            getter.setAccessible(true);
            Object returnValue = getter.invoke(obj, null);
            if (returnValue instanceof String) {
                text = (String) returnValue;
            }
            return text;
        } catch (SecurityException e) {
            return text;
        } catch (IllegalArgumentException e) {
            return text;
        } catch (IllegalAccessException e) {
            return text;
        } catch (InvocationTargetException e) {
            return text;
        }
    }
    
    /**
     * Casts the passed renderer component to a known type and extracts the
     * rendered text.
     *
     * @param renderer
     *            The renderer.
     * @param queueInEventThread
     *            If <code>true</code>, the text extraction is executed in
     *            the event queue thread.
     * @return The rendered text.
     * @throws StepExecutionException
     *             If the passed renderer is not supported. Supported types are
     *             <code>JLabel</code>, <code>JToggleButton</code>,
     *             <code>AbstractButton</code> and <code>JTextComponent</code>
     *
     */
    protected String getRenderedText(final Component renderer,
        boolean queueInEventThread) throws StepExecutionException {

        if (queueInEventThread) {
            return (String)getEventThreadQueuer().invokeAndWait(
                "getRenderedText", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return getRenderedText(renderer);
                    }
                });
        }

        return getRenderedText(renderer);
    }


    /**
     * Get a String representation of the "text" of the component. This String
     * is used in all compare actions and all other places where a simple
     * "as text" view is needed.
     * This has to be replaced either with simple getText() calls on the
     * components or by calls to the cell (or other) renderers of the
     * component using getRenderedText()
     *
     * @return A String representing the text value of the component. If no
     * such value can be found, null is returned.
     */
    protected abstract String getText();

    /**
     * Verifies the <code>enabled</code> property.
     *
     * @param enabled
     *            The <code>enabled</code> property value to verify
     */
    public void gdVerifyEnabled(boolean enabled) {
        verify(enabled, "isEnabled", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                return getComponent().isEnabled()
                    ? Boolean.TRUE : Boolean.FALSE; // see findBugs
            }
        });
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
         * for visibility.
         */
        verify(exists, "exists", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                return getComponent().isShowing()
                    ? Boolean.TRUE : Boolean.FALSE; // see findBugs
            }
        });
    }

    /**
     * Verifies if the component has the focus.
     *
     * @param hasFocus
     *            The hasFocus property to verify.
     */
    public void gdVerifyFocus(boolean hasFocus) {
        verify(hasFocus, "hasFocus", new IRunnable() { //$NON-NLS-1$
            public Object run() {
                // see findBugs
                return getComponent().hasFocus() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
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
    public void gdVerifyProperty(String name, String value, String operator) {
        try {
            final String propToStr = getRobot().
                    getPropertyValue(getComponent(), name);
            Verifier.match(propToStr, value, operator);
        } catch (RobotException e) {
            throw new StepExecutionException(
                    e.getMessage(), EventFactory.createActionError(
                            TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
        }
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
        String propertyName) {
        String propertyValue = StringConstants.EMPTY;
        try {
            propertyValue = getRobot().
                    getPropertyValue(getComponent(), propertyName);
        } catch (RobotException e) {
            throw new StepExecutionException(
                    e.getMessage(), EventFactory.createActionError(
                            TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
        }
        return propertyValue;
    }
    
    /**
     * Clicks the center of the component.
     * @param count Number of mouse clicks
     * @param button Pressed button
     */
    public void gdClick(int count, int button) {
        getRobot().click(getComponent(), null,
                ClickOptions.create()
                    .setClickCount(count)
                    .setMouseButton(button));
    }

    /**
     * clicks into a component.
     *
     * @param count amount of clicks
     * @param button what mouse button should be used
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException error
     */
    public void gdClickDirect(int count, int button, int xPos, String xUnits,
            int yPos, String yUnits) throws StepExecutionException {

        getRobot().click(getComponent(), null,
                ClickOptions.create()
                    .setClickCount(count)
                    .setMouseButton(button),
                xPos, xUnits.equalsIgnoreCase(POS_UNIT_PIXEL),
                yPos, yUnits.equalsIgnoreCase(POS_UNIT_PIXEL));
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

        JPopupMenu popup = showPopup(button);
        selectMenuItem(popup, MenuUtil.splitIndexPath(indexPath));
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
    public void gdPopupSelectByIndexPath(
            int xPos, String xUnits, int yPos, String yUnits, 
            String indexPath, int button) throws StepExecutionException {
        JPopupMenu popup = showPopup(xPos, xUnits, yPos, yUnits, button);
        selectMenuItem(popup, MenuUtil.splitIndexPath(indexPath));
    }

    /**
     * dummy method for "wait for component"
     * @param timeout the maximum amount of time to wait for the component
     * @param delay the time to wait after the component is found
     */
    public void gdWaitForComponent (int timeout, int delay) {
        // do NOT delete this method!
        // do nothing, implementation is in class CAPTestCommand.getImplClass
        // because this action needs a special implementation!
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

        JPopupMenu popup = showPopup(button);
        selectMenuItem(popup, MenuUtil.splitPath(textPath), operator);
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
        gdPopupSelectByTextPath(xPos, units, yPos, 
                units, textPath, operator, 3);
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
            final int yPos, final String yUnits, 
            String textPath, String operator, int button)
        throws StepExecutionException {

        JPopupMenu popup = showPopup(xPos, xUnits, yPos, yUnits, button);
        selectMenuItem(popup, MenuUtil.splitPath(textPath), operator);
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
        JPopupMenu popup = showPopup(button);        
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
            boolean enabled, int button) throws StepExecutionException {
        JPopupMenu popup = showPopup(xPos, xUnits, yPos, yUnits, button);
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
        JPopupMenu popup = showPopup(button);
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
        JPopupMenu popup = showPopup(xPos, xUnits, yPos, yUnits, button);
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
        JPopupMenu popup = showPopup(button);
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
        JPopupMenu popup = showPopup(xPos, xUnits, yPos, yUnits, button);
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
        JPopupMenu popup = showPopup(button);
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
        JPopupMenu popup = showPopup(xPos, xUnits, yPos, yUnits, button);
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
        JPopupMenu popup = showPopup(button);
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
            int yPos, String yUnits, String indexPath, boolean selected, 
            int button) throws StepExecutionException {
        JPopupMenu popup = showPopup(xPos, xUnits, yPos, yUnits, button);
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
        JPopupMenu popup = showPopup(button);
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
    public void gdPopupVerifySelectedByTextPath(final int xPos, 
            final String xUnits, final int yPos,
            final String yUnits, String textPath, String operator,
                boolean selected, int button)
        throws StepExecutionException {
        JPopupMenu popup = showPopup(xPos, xUnits, yPos, yUnits, button);
        popupVerifySelectedByTextPath(textPath, operator, popup, selected);
    }
    
    /**
     * Checks if the specified context menu entry is enabled in the visible Popup
     * @param indexPath the menu item to verify
     * @param popup JPopupMenu
     * @param enabled for checking enabled or disabled
     */
    private void popupVerifyEnabledByIndexPath(String indexPath,
            JPopupMenu popup, boolean enabled) {
        int[] menuItem = MenuUtil.splitIndexPath(indexPath);        
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                popup, menuItem);
        try {
            if (item == null) {
                throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
            }
            verify(enabled, "popupVerifyEnabledByIndexPath", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem)
                            && item.isEnabled()) ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            while ((popup != null) && (popup.isVisible())) {
                MenuUtil.closePopupMenu(getRobot(), popup);
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
            String operator, JPopupMenu popup, boolean enabled)
        throws StepExecutionException {
        
        final String[] itemPath = MenuUtil.splitPath(textPath);
        if (itemPath.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                popup, itemPath, operator);
        try {
            if (item == null) {
                throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
            }
            verify(enabled, "popupVerifyEnabledByTextPath", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem)
                            && item.isEnabled()) ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            while ((popup != null) && (popup.isVisible())) {
                MenuUtil.closePopupMenu(getRobot(), popup);
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
            JPopupMenu popup, boolean exists) {
        int[] menuItem = MenuUtil.splitIndexPath(indexPath); 
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                popup, menuItem);
        try {
            verify(exists, "popupVerifyExistsByIndexPath", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem))
                            ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            while ((popup != null) && (popup.isVisible())) {
                MenuUtil.closePopupMenu(getRobot(), popup);
            }
        }
    }
    
    /**
     * Checks if the specified context menu entry exists in the visible Popup.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param popup JPopupMenu
     * @param exists for checking if entry exists
     */
    private void popupVerifyExistsByTextPath(String textPath,
            String operator, JPopupMenu popup, boolean exists)
        throws StepExecutionException {
        String[] menuItem = MenuUtil.splitPath(textPath);
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_PARAM_VALUE));
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                popup, menuItem, operator);
        try {
            verify(exists, "popupVerifyExistsByTextPath", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem))
                            ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            while ((popup != null) && (popup.isVisible())) {
                MenuUtil.closePopupMenu(getRobot(), popup);
            }
        }
    }
    
    /**
     * Checks if the specified context menu entry is selected in the visible Popup.
     * @param indexPath the menu item to verify
     * @param popup JPopupMenu
     * @param selected for checking if entry is selected
     */
    private void popupVerifySelectedByIndexPath(String indexPath,
            JPopupMenu popup, boolean selected)
        throws StepExecutionException {
        int[] menuItem = MenuUtil.splitIndexPath(indexPath);
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                popup, menuItem);
        try {
            if (item == null) {
                throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
            }
            verify(selected, "popupVerifySelectedByIndexPath", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem)
                            && ((JMenuItem)item).isSelected())
                                ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            while ((popup != null) && (popup.isVisible())) {
                MenuUtil.closePopupMenu(getRobot(), popup);
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
            JPopupMenu popup, boolean selected)
        throws StepExecutionException {
        String[] menuItem = MenuUtil.splitPath(textPath);
        if (menuItem.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        final JComponent item = MenuUtil.navigateToMenuItem(getRobot(),
                popup, menuItem, operator);
        try {
            if (item == null) {
                throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
            }
            verify(selected, "popupVerifySelectedByTextPath", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        // see findBugs
                        return ((item != null) && (item instanceof JMenuItem)
                            && ((JMenuItem)item).isSelected())
                                ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        } finally {
            // close the menu
            while ((popup != null) && (popup.isVisible())) {
                MenuUtil.closePopupMenu(getRobot(), popup);
            }
        }
    }

    /**
     * Shows and returns the popup menu
     * @param button MouseButton
     * @return the popup menu
     */
    protected JPopupMenu showPopup(final int button) {

        Runnable showPopup = new Runnable() {
            public void run() {
                RobotTiming.sleepPreShowPopupDelay();
                ClassLoader oldCl = Thread.currentThread()
                    .getContextClassLoader();
                Thread.currentThread().setContextClassLoader(getComponent()
                        .getClass().getClassLoader());
                if ((getRobot()).isMouseInComponent(getComponent())) {
                    getRobot().clickAtCurrentPosition(
                            getComponent(), 1, button);
                } else {
                    getRobot().click(getComponent(), null, 
                        ClickOptions.create()
                            .setClickCount(1)
                            .setMouseButton(button));
                }
                Thread.currentThread().setContextClassLoader(oldCl);
            }
        };

        return showPopup(showPopup);
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
    private JPopupMenu showPopup(final int xPos, 
            final String xUnits, final int yPos,
            final String yUnits, final int button)
        throws StepExecutionException {

        Runnable showPopup = new Runnable() {
            public void run() {
                RobotTiming.sleepPreShowPopupDelay();
                boolean isAbsoluteCoordinatesX = 
                    xUnits.equalsIgnoreCase(POS_UNIT_PIXEL); 
                boolean isAbsoluteCoordinatesY = 
                    yUnits.equalsIgnoreCase(POS_UNIT_PIXEL); 
                getRobot().click(getComponent(), null, 
                    ClickOptions.create().setMouseButton(button),
                    xPos, isAbsoluteCoordinatesX, 
                    yPos, isAbsoluteCoordinatesY);
            }
        };
        return showPopup(showPopup);
    }

    /**
     * Shows a popup menu using the given runnable and waits for the popup
     * menu to appear.
     *
     * @param showPopupOperation The implementation to use for opening the
     *                           popup menu.
     * @return the popup menu.
     */
    private JPopupMenu showPopup(Runnable showPopupOperation) {
        PopupShownCondition cond = new PopupShownCondition();
        EventLock lock = new EventLock();
        EventListener listener = new EventListener(lock, cond);
        Toolkit.getDefaultToolkit().addAWTEventListener(listener,
                AWTEvent.CONTAINER_EVENT_MASK);

        // showPopupOperation must run in the current thread in order to
        // avoid a race condition.
        showPopupOperation.run();

        synchronized (lock) {
            try {
                long timeout = TimeoutConstants.SERVER_TIMEOUT_WAIT_FOR_POPUP;
                long done = System.currentTimeMillis() + timeout;
                long now;
                while ((!lock.isReleased() || (cond.getPopup() == null)
                        || !cond.getPopup().isShowing())
                        && (timeout > 0)) {
                    lock.wait(timeout);
                    now = System.currentTimeMillis();
                    timeout = done - now;
                }
            } catch (InterruptedException e) {
                // ignore
            } finally {
                Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
            }
        }
        if (!lock.isReleased() || (cond.getPopup() == null)
                || !cond.getPopup().isShowing()) {
            throw new StepExecutionException("popup not shown", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.POPUP_NOT_FOUND));
        }
        return cond.getPopup();
    }

    /**
     * Select a menu item
     *
     * @param popup popup menu
     * @param path path to the menu item
     * @param operator operator used for matching
     */
    private void selectMenuItem(JPopupMenu popup,
                                String[] path,
                                String operator) {
        if (path.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        JMenuItem item = MenuUtil.navigateToMenuItem(getRobot(), popup,
                path, operator);
        if (item == null) {
            throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        MenuUtil.clickMenuItem(getRobot(), item);
    }

    /**
     * Select a menu item
     *
     * @param popup popup menu
     * @param path path to the menu item
     */
    private void selectMenuItem(JPopupMenu popup, int[] path) {
        if (path.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        JMenuItem item = MenuUtil.navigateToMenuItem(getRobot(), popup,
                path);
        if (item == null) {
            throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        MenuUtil.clickMenuItem(getRobot(), item);
    }
    /**
     * @return <code>true</code> if the component is the focusowner
     */
    protected boolean hasFocus() {
        Boolean hasFocus =
            (Boolean)getEventThreadQueuer().invokeAndWait("hasFocus", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() {
                        return getComponent().hasFocus()
                            ? Boolean.TRUE : Boolean.FALSE; // see findBugs
                    }
                });
        return hasFocus.booleanValue();
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

        throw new StepExecutionException(
            I18n.getString(TestErrorEvent.UNSUPPORTED_OPERATION_ERROR),
            EventFactory.createActionError(
                TestErrorEvent.UNSUPPORTED_OPERATION_ERROR));
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
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        final IRobot robot = getRobot();
        gdClickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
        pressOrReleaseModifiers(modifier, true);
        robot.mousePress(null, null, mouseButton);
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
    public void gdDrop(int xPos, String xUnits, int yPos, String yUnits,
            int delayBeforeDrop) {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        final String modifier = dndHelper.getModifier();
        final int mouseButton = dndHelper.getMouseButton();
        try {
            gdClickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, mouseButton);
            pressOrReleaseModifiers(modifier, false);
        }
    }

    /**
     * Presses or releases the given modifier.
     * @param modifier the modifier.
     * @param press if true, the modifier will be pressed.
     * if false, the modifier will be released.
     */
    protected void pressOrReleaseModifiers(String modifier, boolean press) {
        final IRobot robot = getRobot();
        final StringTokenizer modTok = new StringTokenizer(
                KeyStrokeUtil.getModifierString(modifier), " "); //$NON-NLS-1$
        while (modTok.hasMoreTokens()) {
            final String mod = modTok.nextToken();
            final int keyCode = KeyCodeConverter.getKeyCode(mod);
            if (press) {
                robot.keyPress(null, keyCode);
            } else {
                robot.keyRelease(null, keyCode);
            }
        }
    }

    /**
     * Waits the given amount of time. Logs a drop-related error if interrupted.
     *
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    static void waitBeforeDrop(int delayBeforeDrop) {
        TimeUtil.delay(delayBeforeDrop);
    }
    
    /**
     * @param extendSelection
     *            the string to indicate that the selection should be extended
     * @return a ClickModifier for the given extend selection
     */
    protected ClickModifier getClickModifier(String extendSelection) {
        ClickModifier cm = ClickModifier.create();
        if (CompSystemConstants.EXTEND_SELECTION_YES
                .equalsIgnoreCase(extendSelection)) {
            cm.add(ClickModifier.M1);
        }
        return cm;
    }
}
