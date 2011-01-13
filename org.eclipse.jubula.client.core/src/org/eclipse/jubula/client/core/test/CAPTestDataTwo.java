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
package org.eclipse.jubula.client.core.test;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.NodeMaker;

/**
 * 
 * class with test data for CAPs, only for test usage!!!
 * @author BREDEX GmbH
 * @created 12.10.2004
 *
 */
public class CAPTestDataTwo {
  
    /**
     * correct CAP1
     */
    private static ICapPO capSetFocus1 = null;
        
    /**
     * correct CAP2
     */
    private static ICapPO capInputValue1 = null;
    /**
     * capSetFocus2
     */
    private static ICapPO capSetFocus2 = null;
    /**
     * capInputValue2
     */
    private static ICapPO capInputValue2 = null;
    /**
     * Verify sum
     */
    private static ICapPO capVerifySum = null;
    
        
    /**
     * correct CAP3
     */
    private static ICapPO capAdd = null;
    
    /**
     * private constructor.
     */
    private CAPTestDataTwo() {
        // not to use
    }
    
    /**
     * @return Returns the capSetFocus1.
     */
    public static ICapPO getCapSetFocus1() {
        if (capSetFocus1 == null) {           
            capSetFocus1 = NodeMaker.createCapPO(
                    "setFocus1", // CapPO name //$NON-NLS-1$
                    "value1", // component name //$NON-NLS-1$
                    "javax.swing.text.JTextComponent", // component type //$NON-NLS-1$
                    "CompSystem.Click"); //$NON-NLS-1$
        }
        return capSetFocus1;
    }
    
    /**
     * 
     * @return Resturns capSetFocus2
     */
    public static ICapPO getCapSetFocus2() {
        if (capSetFocus2 == null) {           
            capSetFocus2 = NodeMaker.createCapPO(
                    "setFocus2", //$NON-NLS-1$
                    "value2", //$NON-NLS-1$
                    "javax.swing.text.JTextComponent", //$NON-NLS-1$
                    "CompSystem.Click"); //$NON-NLS-1$   
        }
        return capSetFocus2;
    }
    /**
     * @return Returns the capInputValue1.
     */
    public static ICapPO getCapInputValue1() {
        if (capInputValue1 == null) {           
            capInputValue1 = NodeMaker.createCapPO(
                    "InputValue1", // CapPO name //$NON-NLS-1$
                    "value1", // component name //$NON-NLS-1$
                    "javax.swing.text.JTextComponent", // component type //$NON-NLS-1$
                    "CompSystem.InputText"); //$NON-NLS-1$
        }
        return capInputValue1;
    }
    
    /**
     * @return Returns the capInputValue2.
     */
    public static ICapPO getCapInputValue2() {
        if (capInputValue2 == null) {            
            capInputValue2 = NodeMaker.createCapPO(
                    "InputValue2", // CapPO name //$NON-NLS-1$
                    "value2", // component name //$NON-NLS-1$
                    "javax.swing.text.JTextComponent", // component type //$NON-NLS-1$
                    "CompSystem.InputText"); //$NON-NLS-1$
        }
        return capInputValue2;
    }
    /**
     * 
     * @return cap
     */
    public static ICapPO getCapVerifySum() {
        if (capVerifySum == null) {            
            capVerifySum = NodeMaker.createCapPO(
                    "VerifySum", //$NON-NLS-1$
                    "sum", //$NON-NLS-1$
                    "javax.swing.text.JTextComponent", //$NON-NLS-1$
                    "CompSystem.VerifyText"); //$NON-NLS-1$
        }
        return capVerifySum;
    }
    /**
     * @return Returns the AddCap3.
     */
    public static ICapPO getCapAdd() {
        if (capAdd == null) {
            capAdd = NodeMaker.createCapPO(
                    "ClickAdd", // CapPO name //$NON-NLS-1$
                    "equal", // component name //$NON-NLS-1$
                    "javax.swing.AbstractButton", // component type //$NON-NLS-1$
                    "guidancerJButtonClick"); //$NON-NLS-1$
        }
        return capAdd;
    }
}
