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
package org.eclipse.jubula.rc.common.tester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.ClickOptions.ClickModifier;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IWidgetComponent;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.common.util.ReflectionUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
/**
 * Implementation of basic functions for a lot of graphics components
 * except for context menus and menus. 
 * 
 * @author BREDEX GmbH
 */
public class WidgetTester extends AbstractUITester {
    /** the name of the reflectively called remote control method to wait for a component  */
    public static final String RC_METHOD_NAME_WAIT_FOR_COMPONENT = "rcWaitForComponent"; //$NON-NLS-1$

    /** the name of the reflectively called remote control method for existence checking */
    public static final String RC_METHOD_NAME_CHECK_EXISTENCE = "rcVerifyExists"; //$NON-NLS-1$

    /**
     * Casts the IComponentAdapter to an IWidgetAdapter for better access
     * @return The widgetAdapter
     */
    private IWidgetComponent getWidgetAdapter() {
        return (IWidgetComponent) getComponent();
    }
    
    /**
     * Verifies that the component exists and is visible.
     *
     * @param exists  <code>True</code> if the component is expected to exist
     *            and be visible, otherwise <code>false</code>.
     */
    public void rcVerifyExists(boolean exists) {
        Verifier.equals(exists, getWidgetAdapter().isShowing());
    }

    /**
     * Verifies if the component has the focus.
     * @param hasFocus <code>True</code> if the component is expected to has 
     *                  the focus, otherwise <code>false</code>
     */
    public void rcVerifyFocus(boolean hasFocus) {
        Verifier.equals(hasFocus, getWidgetAdapter().hasFocus());
    }
    /**
     * Verifies if the component is enabled
     * @param enabled <code>True</code> if the component is expected to be 
     *                  enabled, otherwise <code>false</code>
     */
    public void rcVerifyEnabled(boolean enabled) {
        Verifier.equals(enabled, getWidgetAdapter().isEnabled());
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
    public void rcVerifyProperty(final String name, String value,
            String operator) {
        
        final IWidgetComponent bean = (IWidgetComponent) getComponent();
        bean.getPropteryValue(name);

        final String propToStr = bean.getPropteryValue(name);
        Verifier.match(propToStr, value, operator);
    }

    
    /**
     * Clicks the center of the component.
     * @param count Number of mouse clicks
     * @param button Pressed button
     */
    public void rcClick(int count, int button) {
        getRobot().click(getComponent().getRealComponent(), null,
                ClickOptions.create()
                    .setClickCount(count)
                    .setMouseButton(button));
    }
    
    /**
     * Clicks the center of the component with the MouseButton 1
     * @param count Number of mouse clicks
     */
    public void rcClick(int count) {
        rcClick(count, 1);
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
    public void rcClickDirect(int count, int button, int xPos, String xUnits,
            int yPos, String yUnits) throws StepExecutionException {

        getRobot().click(getComponent().getRealComponent(), null,
                ClickOptions.create()
                    .setClickCount(count)
                    .setMouseButton(button),
                xPos, xUnits.equalsIgnoreCase(
                        ValueSets.Unit.pixel.rcValue()),
                yPos, yUnits.equalsIgnoreCase(
                        ValueSets.Unit.pixel.rcValue()));
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
    public void rcDrag(int mouseButton, String modifier, int xPos,
            String xUnits, int yPos, String yUnits) {
        getWidgetAdapter().rcDrag(mouseButton, modifier, xPos, xUnits,
                yPos, yUnits);
    }

    /**
     * Performs a Drop. Moves into the middle of the Component and releases
     * the modifier and mouse button pressed by rcDrag.
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public void rcDrop(int xPos, String xUnits, int yPos, String yUnits,
            int delayBeforeDrop) {

        getWidgetAdapter().rcDrop(xPos, xUnits, yPos, yUnits,
               delayBeforeDrop);
    }
    
    /**
     * dummy method for "wait for component"
     * @param timeout the maximum amount of time to wait for the component
     * @param delay the time to wait after the component is found
     * {@inheritDoc}
     */
    public void rcWaitForComponent (int timeout, int delay) {
        // do NOT delete this method!
        // do nothing, implementation is in class CAPTestCommand.getImplClass
        // because this action needs a special implementation!
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
    public String rcStorePropertyValue(String variableName, 
        final String propertyName) {
        IWidgetComponent bean = (IWidgetComponent) getComponent();

        return bean.getPropteryValue(propertyName);
    }
    
    /**
     * Select an item in the popup menu
     * @param indexPath path of item indices
     * @param button MouseButton
     * @throws StepExecutionException error
     */
    public void rcPopupSelectByIndexPath(String indexPath, int button)
        throws StepExecutionException {

        AbstractMenuTester popup = getWidgetAdapter().showPopup(button);
        popup.selectMenuItemByIndexpath(indexPath);
    }
    
    /**
     * Selects an item in the popup menu
     * @param textPath path of item texts
     * @param operator operator used for matching
     * @param button MouseButton
     * @throws StepExecutionException error
     */
    public void rcPopupSelectByTextPath(String textPath, String operator,
            int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter().showPopup(button);
        popup.selectMenuItem(textPath, operator);
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
    public void rcPopupSelectByTextPath(final int xPos, final String xUnits, 
            final int yPos, final String yUnits, 
            String textPath, String operator, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter()
                .showPopup(xPos, xUnits, yPos, yUnits, button);
        popup.selectMenuItem(textPath, operator);
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
    public void rcPopupSelectByIndexPath(
            int xPos, String xUnits, int yPos, String yUnits, 
            String indexPath, int button) throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter()
                .showPopup(xPos, xUnits, yPos, yUnits, button);
        popup.selectMenuItemByIndexpath(indexPath);
    }
    
    /**
     * Checks if the specified context menu entry is enabled.
     * @param indexPath the menu item to verify
     * @param enabled for checking enabled or disabled
     * @param button MouseButton
     */
    public void rcPopupVerifyEnabledByIndexPath(String indexPath,
            boolean enabled, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter().showPopup(button);        
        popup.verifyEnabledByIndexpath(indexPath, enabled);
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
    public void rcPopupVerifyEnabledByIndexPath(int xPos, String xUnits, 
            int yPos, String yUnits, String indexPath, 
            boolean enabled, int button) throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter()
                .showPopup(xPos, xUnits, yPos, yUnits, button);
        popup.verifyEnabledByIndexpath(indexPath, enabled);
    }
    
    /**
     * Checks if the specified context menu entry is enabled.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param enabled for checking enabled or disabled
     * @param button MouseButton
     */
    public void rcPopupVerifyEnabledByTextPath(String textPath,
            String operator, boolean enabled, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter().showPopup(button);
        popup.verifyEnabled(textPath, operator, enabled);
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
    public void rcPopupVerifyEnabledByTextPath(final int xPos, 
            final String xUnits, final int yPos,
            final String yUnits, String textPath, String operator,
                boolean enabled, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter()
                .showPopup(xPos, xUnits, yPos, yUnits, button);
        popup.verifyEnabled(textPath, operator, enabled);
    }
    
    /**
     * Checks if the specified context menu entry is selected.
     * @param indexPath the menu item to verify
     * @param selected for checking if entry is selected
     * @param button MouseButton
     */
    public void rcPopupVerifySelectedByIndexPath(String indexPath,
            boolean selected, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter().showPopup(button);
        popup.verifySelectedByIndexpath(indexPath, selected);
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
    public void rcPopupVerifySelectedByIndexPath(int xPos, String xUnits, 
            int yPos, String yUnits, String indexPath, boolean selected, 
            int button) throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter()
                .showPopup(xPos, xUnits, yPos, yUnits, button);
        popup.verifySelectedByIndexpath(indexPath, selected);
    }
    
    /**
     * Checks if the specified context menu entry is selected.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param selected for checking if entry is selected
     * @param button MouseButton
     */
    public void rcPopupVerifySelectedByTextPath(String textPath,
            String operator, boolean selected, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter().showPopup(button);
        popup.verifySelected(textPath, operator, selected);
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
    public void rcPopupVerifySelectedByTextPath(final int xPos, 
            final String xUnits, final int yPos,
            final String yUnits, String textPath, String operator,
                boolean selected, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter()
                .showPopup(xPos, xUnits, yPos, yUnits, button);
        popup.verifySelected(textPath, operator, selected);
    }
    
    /**
     * Checks if the specified context menu entry exists.
     * @param indexPath the menu item to verify
     * @param exists for checking if entry exists
     * @param button MouseButton
     */
    public void rcPopupVerifyExistsByIndexPath(String indexPath,
            boolean exists, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter().showPopup(button);
        popup.verifyExistsByIndexpath(indexPath, exists);      
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
    public void rcPopupVerifyExistsByIndexPath(int xPos, String xUnits, 
            int yPos, String yUnits, String indexPath, 
            boolean exists, int button) throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter()
                .showPopup(xPos, xUnits, yPos, yUnits, button);
        popup.verifyExistsByIndexpath(indexPath, exists);
    }
    
    /**
     * Checks if the specified context menu entry exists.
     * @param textPath the menu item to verify
     * @param operator operator used for matching
     * @param exists for checking if entry exists
     * @param button MouseButton
     */
    public void rcPopupVerifyExistsByTextPath(String textPath,
            String operator, boolean exists, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter().showPopup(button);
        popup.verifyExists(textPath, operator, exists);
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
    public void rcPopupVerifyExistsByTextPath(final int xPos, 
            final String xUnits, final int yPos,
            final String yUnits, String textPath, String operator,
            boolean exists, int button)
        throws StepExecutionException {
        AbstractMenuTester popup = getWidgetAdapter()
                .showPopup(xPos, xUnits, yPos, yUnits, button);
        popup.verifyExists(textPath, operator, exists);
    }
    /**
     * 
     * @param extendSelection
     *            the string to indicate that the selection should be extended
     * @return a ClickModifier for the given extend selection
     */
    protected ClickModifier getClickModifier(String extendSelection) {        
        ClickModifier cm = ClickModifier.create();
        if (ValueSets.BinaryChoice.yes.rcValue()
                .equalsIgnoreCase(extendSelection)) {
            cm.add(ClickModifier.M1);
        }
        return cm;
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
    public void rcShowText(final String text, final int textSize,
        final int timePerWord, final int windowWidth) {
        getWidgetAdapter().showToolTip(text, textSize,
                timePerWord, windowWidth);
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
            final int keyCode = getKeyCode(mod);
            if (press) {
                robot.keyPress(null, keyCode);
            } else {
                robot.keyRelease(null, keyCode);
            }
        }
    }
    
    /**
     * Gets the key code for a specific modifier
     * @param mod the modifier
     * @return the integer key code value
     */
    protected int getKeyCode(String mod) {
        return getWidgetAdapter().getKeyCode(mod);
    }
    
    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param timeout the timeout
     */
    public void rcInvokeMethod(final String fqcn, final String name,
            final String signature, final String args, final String argsSplit,
            int timeout) {
        IRobotFactory factory = getComponent().getRobotFactory();
        IEventThreadQueuer queuer = factory.getEventThreadQueuer();
        try {
            Object result = queuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                    createCallable(fqcn, name, signature, args, argsSplit),
                    timeout);
        } catch (TimeoutException e) {
            throw new StepExecutionException(e.toString(), EventFactory
                    .createActionError(TestErrorEvent.CONFIRMATION_TIMEOUT));
        }
    }

    /**
     * Invokes the specified Method
     * 
     * @param variableName name of the variable of the cap. This isn't used.
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @param timeout the timeout
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeMethodStoreReturn(final String variableName,
            final String fqcn, final String name, final String signature,
            final String args, final String argsSplit, int timeout) {
        IRobotFactory factory = getComponent().getRobotFactory();
        IEventThreadQueuer queuer = factory.getEventThreadQueuer();
        try {
            Object result = queuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                    createCallable(fqcn, name, signature, args, argsSplit),
                    timeout);
            return result == null ? StringConstants.NULL : result.toString();
        } catch (TimeoutException e) {
            throw new StepExecutionException(e.toString(), EventFactory
                    .createActionError(TestErrorEvent.CONFIRMATION_TIMEOUT));
        }
    }
    
    /**
     * Creates the runnable object which will invoke the specified Method
     * @param fqcn fully qualified class name
     * @param name method name
     * @param signature method signature
     * @param args method arguments
     * @param argsSplit separator for the arguments
     * @return the IRunnable object
     */
    private Callable<Object> createCallable(final String fqcn,
            final String name, final String signature, final String args,
            final String argsSplit) {
        return new Callable<Object>() {

            public Object call() {
                ClassLoader uiClassloader = Thread.currentThread()
                        .getContextClassLoader();
                try {
                    Class<?> clazz = Class.forName(fqcn, true,
                            uiClassloader);
                    Class[] parameterClasses = {};
                    Object[] argObjects = {};
                    if (!StringUtils.isEmpty(signature)
                            && !StringUtils.isEmpty(args)) {
                        parameterClasses = ReflectionUtil
                                .getParameterClasses(signature, uiClassloader);
                        argObjects = ReflectionUtil.getParameterValues(args,
                                argsSplit, parameterClasses);
                    }
                    List<Object> argList = new ArrayList<Object>(
                            Arrays.asList(argObjects));
                    argList.add(0, getComponent().getRealComponent());
                    List<Class> clsList = new ArrayList<Class>(
                            Arrays.asList(parameterClasses));
                    clsList.add(0, getComponent().getRealComponent()
                            .getClass());
                    return MethodUtils.invokeStaticMethod(clazz, name,
                            argList.toArray(),
                            clsList.toArray(
                                    new Class[parameterClasses.length
                                            + 1]));
                } catch (Throwable e) {
                    ReflectionUtil.handleException(e);
                }
                return null;
            }
        };
    }

    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param timeout the timeout
     * @return returns null or if the invoked method return
     *         java.util.Properties, the string representation of the properties
     */
    public String rcInvokeMethod(final String fqcn, final String name,
            int timeout) {
        IRobotFactory factory = getComponent().getRobotFactory();
        IEventThreadQueuer queuer = factory.getEventThreadQueuer();
        try {
            Object result = queuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                    createCallable(fqcn, name), timeout);
        } catch (TimeoutException e) {
            throw new StepExecutionException(e.toString(), EventFactory
                    .createActionError(TestErrorEvent.CONFIRMATION_TIMEOUT));
        }
        return null;
    }

    /**
     * Invokes the specified Method
     * 
     * @param variableName name of the variable of the cap. This isn't used.
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param timeout the timeout
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeMethodStoreReturn(final String variableName,
            final String fqcn, final String name, int timeout) {
        IRobotFactory factory = getComponent().getRobotFactory();
        IEventThreadQueuer queuer = factory.getEventThreadQueuer();
        try {
            Object result = queuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                    createCallable(fqcn, name), timeout);
            return result == null ? StringConstants.NULL : result.toString();
        } catch (TimeoutException e) {
            throw new StepExecutionException(e.toString(), EventFactory
                    .createActionError(TestErrorEvent.CONFIRMATION_TIMEOUT));
        }
    }
    
    /**
     * Creates the runnable object which will invoke the specified Method
     * @param fqcn fully qualified class name
     * @param name method name
     * @return the IRunnable object
     */
    private Callable<Object> createCallable(final String fqcn,
            final String name) {
        return new Callable<Object>() {

            public Object call() {
                ClassLoader uiClassloader = Thread.currentThread()
                        .getContextClassLoader();
                try {
                    Class<?> clazz = Class.forName(fqcn, true,
                            uiClassloader);
                    Object[] param = new Object[] {
                            getComponent().getRealComponent()};
                    Class[] paramClass = new Class[] {
                            getComponent().getRealComponent().getClass()
                    };
                    return MethodUtils.invokeStaticMethod(clazz, name,
                            param, paramClass);
                } catch (Throwable e) {
                    ReflectionUtil.handleException(e);
                }
                return null;
            }
        };
    }

    /**
     * Waits the given amount of time. Logs a drop-related error if interrupted.
     *
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button
     */
    public static void waitBeforeDrop(int delayBeforeDrop) {
        TimeUtil.delay(delayBeforeDrop);
    }

    /** {@inheritDoc} */
    public String[] getTextArrayFromComponent() {
        return null;
    }
}
