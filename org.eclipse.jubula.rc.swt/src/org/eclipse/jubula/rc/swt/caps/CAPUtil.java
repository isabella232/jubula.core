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
package org.eclipse.jubula.rc.swt.caps;

import java.awt.Point;
import java.util.StringTokenizer;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.driver.SwtRobot;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;
import org.eclipse.swt.widgets.Display;
/**
 * Util class for some swt specific commands.
 * 
 * @author BREDEX GmbH
 */
public class CAPUtil {
       
    /**
     * 
     */
    private CAPUtil() { }    
    
    /**
     * Gets the Robot. 
     * @return The Robot
     * @throws RobotException If the Robot cannot be created.
     */
    public static IRobot getRobot() throws RobotException {
        return AUTServer.getInstance().getRobot();
    }
    
    /**
     * Move the mouse pointer from its current position to a few points in
     * its proximity. This is used to initiate a drag operation.
     * 
     */
    public static void shakeMouse() {
        /** number of pixels by which a "mouse shake" offsets the mouse cursor */
        final int mouseShakeOffset = 10;
        
        Point origin = getRobot().getCurrentMousePosition();
        SwtRobot lowLevelRobot = new SwtRobot(Display.getDefault());
        lowLevelRobot.mouseMove(
                origin.x + mouseShakeOffset, 
                origin.y + mouseShakeOffset);
        lowLevelRobot.mouseMove(
                origin.x - mouseShakeOffset, 
                origin.y - mouseShakeOffset);
        lowLevelRobot.mouseMove(origin.x, origin.y);
        if (!EnvironmentUtils.isWindowsOS() 
                && !EnvironmentUtils.isMacOS()) {
            boolean moreEvents = true;
            while (moreEvents) {
                moreEvents = Display.getDefault().readAndDispatch();
            }
        }
    }
    
    /**
     * Presses or releases the given modifier.
     * @param modifier the modifier.
     * @param press if true, the modifier will be pressed.
     * if false, the modifier will be released.
     */
    public static void pressOrReleaseModifiers(String modifier, boolean press) {
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
    

}
