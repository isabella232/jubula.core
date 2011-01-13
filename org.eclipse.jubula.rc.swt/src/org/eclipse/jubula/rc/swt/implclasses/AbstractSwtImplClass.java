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

import java.util.StringTokenizer;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IBaseImplementationClass;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.driver.RobotFactoryConfig;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.utils.TimeUtil;
import org.eclipse.swt.widgets.Item;


/**
 * @author BREDEX GmbH
 * @created 30.03.2007
 */
public abstract class AbstractSwtImplClass implements IBaseImplementationClass {

    /** The default separator for enumerations of list values. */
    public static final char INDEX_LIST_SEP_CHAR = 
        TestDataConstants.VALUE_CHAR_DEFAULT;
    
    /** The dafault separator of a list of values */
    public static final char VALUE_SEPARATOR = 
        TestDataConstants.VALUE_CHAR_DEFAULT;
    
    /** constants for communication */
    protected static final String POS_UNIT_PIXEL = "Pixel"; //$NON-NLS-1$
    
    /** constants for communication */
    protected static final String POS_UNI_PERCENT = "Percent"; //$NON-NLS-1$
    
    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(AbstractSwtImplClass.class);

    /** The robot factory. */
    private IRobotFactory m_robotFactory;
    
    
    
    /**
     * Gets the Robot factory. The factory is created once per instance.
     * @return The Robot factory.
     */
    protected IRobotFactory getRobotFactory() {
        if (m_robotFactory == null) {
            m_robotFactory = new RobotFactoryConfig().getRobotFactory();
        }
        return m_robotFactory;
    }
    
    /**
     * Gets the Robot. 
     * @return The Robot
     * @throws RobotException If the Robot cannot be created.
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
     * Presses or releases the given modifier.
     * @param modifier the modifier.
     * @param press if true, the modifier will be pressed.
     * if false, the modifier will be released.
     */
    public void pressOrReleaseModifiers(String modifier, boolean press) {
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
    public static void waitBeforeDrop(int delayBeforeDrop) {
        TimeUtil.delay(delayBeforeDrop);
    }

    /**
     * for action which have no valid implementation in this toolkit this
     * method is called and an appropriate exception is thrown.
     */
    protected void throwUnsupportedAction() {
        throw new StepExecutionException(TestErrorEvent
                .UNSUPPORTED_OPERATION_IN_TOOLKIT_ERROR, 
                EventFactory.createUnsupportedActionError());
    }

    
    /**
     * Returns an array of representation strings that corresponds to the given
     * array of items or null if the given array is null;
     * @param itemArray the item array whose item texts have to be read
     * @return array of item texts corresponding to the given item array
     */
    protected final String[] getTextArrayFromItemArray(Item[] itemArray) {
        final String[] itemTextArray;
        if (itemArray == null) {
            itemTextArray = null;
        } else {
            itemTextArray = new String[itemArray.length];
            for (int i = 0; i < itemArray.length; i++) {
                Item item = itemArray[i];
                if (item == null) {
                    itemTextArray[i] = null;
                } else {
                    itemTextArray[i] = SwtUtils.removeMnemonics(item.getText());
                }
            }
        }
        
        return itemTextArray;
    }

}
