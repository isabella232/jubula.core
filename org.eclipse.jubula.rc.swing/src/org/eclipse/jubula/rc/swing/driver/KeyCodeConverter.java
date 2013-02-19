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
package org.eclipse.jubula.rc.swing.driver;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.swing.utils.SwingUtils;
import org.eclipse.jubula.tools.objects.event.EventFactory;


/**
 * Utility Class to convert key descriptions into the virtual key codes.
 *
 * @author BREDEX GmbH
 * @created Jan 30, 2008
 */
public class KeyCodeConverter {
    
    /**
     * The Converter Map.
     */
    private static Map converterTable = null;
    
    static {
        converterTable = new HashMap();
        converterTable.put(CompSystemConstants.MODIFIER_NONE, new Integer(-1));
        converterTable.put(CompSystemConstants.MODIFIER_SHIFT, 
                new Integer(KeyEvent.VK_SHIFT));
        converterTable.put(CompSystemConstants.MODIFIER_CONTROL, 
                new Integer(KeyEvent.VK_CONTROL));
        converterTable.put(CompSystemConstants.MODIFIER_ALT, 
                new Integer(KeyEvent.VK_ALT));
        converterTable.put(CompSystemConstants.MODIFIER_META, 
                new Integer(KeyEvent.VK_META));
        converterTable.put(CompSystemConstants.MODIFIER_CMD, 
                new Integer(KeyEvent.VK_META));
        converterTable.put(CompSystemConstants.MODIFIER_MOD,
                new Integer(SwingUtils.getSystemDefaultModifier()));
    }
    

    /**
     * Utility Constructor.
     */
    private KeyCodeConverter() {
        // nothing
    }
    
    /**
     * Gets the Virtual-Key-Code of the given key.
     * @param key a description of the key, e.g. "control", "alt", etc.
     * @return the Virtual-Key-Code of the given key or -1 if key is "none".
     */
    public static int getKeyCode(String key) {
        if (key == null) {
            throw new RobotException("Key is null!", //$NON-NLS-1$
                    EventFactory.createConfigErrorEvent());
        }
        final Integer keyCode = (Integer)converterTable.get(key.toLowerCase());
        if (keyCode == null) {
            throw new RobotException("No KeyCode found for key '" + key + "'", //$NON-NLS-1$//$NON-NLS-2$
                    EventFactory.createConfigErrorEvent());
        }
        return keyCode.intValue();
    }
    
}
