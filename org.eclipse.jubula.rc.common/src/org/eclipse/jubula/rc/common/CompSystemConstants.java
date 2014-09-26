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
package org.eclipse.jubula.rc.common;

/**
 * @author BREDEX GmbH
 * @created Jul 22, 2010
 */
public interface CompSystemConstants {
    // ----------------------------------------------------
    // ---------  ImplClass Parameters  -------------------
    // ----------------------------------------------------
    /** Constant for parameter to extend current selection */
    public static final String EXTEND_SELECTION_YES = "yes"; //$NON-NLS-1$
    /** Constant for parameter to begin a new selection */
    public static final String EXTEND_SELECTION_NO = "no"; //$NON-NLS-1$

    /** Constant for UP */
    public static final String DIRECTION_UP = "up"; //$NON-NLS-1$
    /** Constant for DOWN */
    public static final String DIRECTION_DOWN = "down"; //$NON-NLS-1$
    /** Constant for LEFT */
    public static final String DIRECTION_LEFT = "left"; //$NON-NLS-1$
    /** Constant for RIGHT */
    public static final String DIRECTION_RIGHT = "right"; //$NON-NLS-1$

    /** constants for communication */
    public static final String POS_UNIT_PIXEL = "Pixel"; //$NON-NLS-1$
    /** constants for communication */
    public static final String POS_UNIT_PERCENT = "Percent"; //$NON-NLS-1$
    
    /** Constant for relative path type */
    public static final String TREE_PATH_TYPE_RELATIVE = "relative"; //$NON-NLS-1$
    /** Constant for absolute path type */
    public static final String TREE_PATH_TYPE_ABSOLUTE = "absolute"; //$NON-NLS-1$

    /** Constant for UP */
    public static final String TREE_MOVE_UP = "up"; //$NON-NLS-1$
    /** Constant for DOWN */
    public static final String TREE_MOVE_DOWN = "down"; //$NON-NLS-1$
    /** Constant for NEXT */
    public static final String TREE_MOVE_NEXT = "next"; //$NON-NLS-1$
    /** Constant for RIGHT */
    public static final String TREE_MOVE_PREVIOUS = "previous"; //$NON-NLS-1$
    
    /** Constant for AUT activation method */
    public static final String AAM_AUT_DEFAULT = "AUT_DEFAULT"; //$NON-NLS-1$
    /** Constant for AUT activation method */
    public static final String AAM_NONE = "NONE"; //$NON-NLS-1$
    /** Constant for AUT activation method */
    public static final String AAM_TITLE = "TITLEBAR"; //$NON-NLS-1$
    /** Constant for AUT activation method */
    public static final String AAM_NORTHWEST = "NW"; //$NON-NLS-1$
    /** Constant for AUT activation method */
    public static final String AAM_NORTHEAST = "NE"; //$NON-NLS-1$
    /** Constant for AUT activation method */
    public static final String AAM_SOUTHWEST = "SW"; //$NON-NLS-1$
    /** Constant for AUT activation method */
    public static final String AAM_SOUTHEAST = "SE";  //$NON-NLS-1$
    /** Constant for AUT activation method */
    public static final String AAM_CENTER = "CENTER"; //$NON-NLS-1$

    /** */
    public static final String MODIFIER_NONE = "none"; //$NON-NLS-1$
    /** */
    public static final String MODIFIER_SHIFT = "shift"; //$NON-NLS-1$
    /** */
    public static final String MODIFIER_CONTROL = "control"; //$NON-NLS-1$
    /** */
    public static final String MODIFIER_ALT = "alt"; //$NON-NLS-1$
    /** */
    public static final String MODIFIER_META = "meta"; //$NON-NLS-1$
    /** */
    public static final String MODIFIER_CMD = "cmd"; //$NON-NLS-1$
    /** */
    public static final String MODIFIER_MOD = "mod"; //$NON-NLS-1$
    
    /** */
    public static final String KEY_STROKE_DELETE = "DELETE"; //$NON-NLS-1$
}