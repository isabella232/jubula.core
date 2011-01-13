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
package org.eclipse.jubula.tools.constants;

/**
 * @author BREDEX GmbH
 * @created Apr 17, 2008
 */
public final class SwtAUTHierarchyConstants {

    /** the key for widget.getData(key); to get the hardcoded widget name (--> GUIdancer convention) */
    public static final String WIDGET_NAME = "GD_COMP_NAME"; //$NON-NLS-1$
    /** Used for assigning components generated names based on their ID in the corresponding plugin.xml */
    public static final String RCP_NAME = "GD_RCP_COMP_NAME"; //$NON-NLS-1$

    /** private constructor */
    private SwtAUTHierarchyConstants() {
        // constant class
    }
    
}
