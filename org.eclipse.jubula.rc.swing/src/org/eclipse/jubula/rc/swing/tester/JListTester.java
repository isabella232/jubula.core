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
package org.eclipse.jubula.rc.swing.tester;

import org.eclipse.jubula.rc.common.tester.ListTester;
import org.eclipse.jubula.rc.swing.utils.SwingUtils;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class JListTester extends ListTester {
        
    /**
     * {@inheritDoc}
     */
    protected int getSystemDefaultModifier() {
        return SwingUtils.getSystemDefaultModifier();
    }
    
}
