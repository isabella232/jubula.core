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


import org.eclipse.jubula.rc.common.caps.AbstractButtonCAPs;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IButtonAdapter;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;


/**
 * The Toolkit specific implementation for <code>SWTButton</code> and subclasses.
 *
 * @author BREDEX GmbH
 */
public class ButtonCAPs extends AbstractButtonCAPs {

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] {
                SwtUtils.removeMnemonics(
                        ((IButtonAdapter)getComponent()).getText())};
    }
    
}