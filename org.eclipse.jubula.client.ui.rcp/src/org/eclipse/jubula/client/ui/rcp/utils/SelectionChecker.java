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
package org.eclipse.jubula.client.ui.rcp.utils;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;


/**
 * Utility class to check the selection in Jubula trees.
 *
 * @author BREDEX GmbH
 * @created 05.04.2005
 */
public class SelectionChecker {
    /** Constant for ExecTestSuites (= every selection is an execTestSuite) */
    public static final int EXEC_TESTSUITE = 0;
    
    /** Constant for SpecTestCases (= every selection is a specTestCase) */
    public static final int SPEC_TESTCASE = 1;
    
    /** Constant for CAPs (= every selection is a cap) */
    public static final int CAP = 2;
    
    /** Constant for ExecTestCases (= every selection is an execTestCase) */
    public static final int EXEC_TESTCASE = 3;
    
    /** Constant for ExecTestCases with a SpecTestCase as parent (= every selection is an execTestCase with a specTestCase as parent) */
    public static final int CHILD_EXEC_TC = 5;
    
    /** Constant for EventTestcases (= every selection is a EventTestcase)*/
    public static final int EVENT_TESTCASE = 6;
    
    /** Constant for Category (= every selection is a Category) */
    public static final int CATEGORY = 7;
    
    /** Constant for Project */
    public static final int PROJECT = 8;
    
    /** Constant for technical name (om editor) */
    public static final int OM_TECH_NAME = 9;
    
    /** Constant for logical name (om editor) */
    public static final int OM_LOGIC_NAME = 10;
    
    /** Constant for category (om editor) */
    public static final int OM_CATEGORY = 11;
    
    /** Constant for main categories (om editor) */
    public static final int OM_MAIN_CATEGORY = 12;
    
    /** Number of node types */
    private static final int NUMBER_OF_TYPES = 13;
    
    
    
    /**
     * Private constructor
     */
    private SelectionChecker() {
        // do nothing
    }
    
    /**
     * Counts the selected objects as follows:
     * <blockquote>
     * <p>counter[EXEC_TESTSUITE] : counter of ExecTestSuites</p>
     * <p>counter[SPEC_TESTCASE] : counter of SpecTestCases</p>
     * <p>counter[CAP] : counter of CAPs</p>
     * <p>counter[EXEC_TESTCASE] : counter of ExecTestCases</p>
     * <p>counter[SPEC_TESTSUITE] : counter of SpecTestSuites</p>
     * <p>counter[CHILD_EXEC_TC] : counter of ExecTestCases with a SpecTestCase as parent</p>
     * <p>counter[EVENT_TESTCASE] : counter of EventTestCases</p>
     * <p>counter[CATEGORY] : counter of Categories</p>
     * <p>counter[PROJECT] : counter of Projects</p>
     * <p>counter[OM_TECH_NAME] : counter of Technical Names in OM Editor</p>
     * <p>counter[OM_LOGIC_NAME] : counter of Logical Names in OM Editor</p>
     * <p>counter[OM_CATEGORY] : counter of Categories in OM Editor</p>
     * <p>counter[OM_MAIN_CATEGORY] : counter of main Categories in OM Editor</p>
     * </blockquote>
     * @param selection The actual selection.
     * @return A array of integers, that counts the selected objects.
     */
    public static int[] selectionCounter(IStructuredSelection selection) {
        int[] counter = new int[NUMBER_OF_TYPES];
        // initialize array with 0
        Arrays.fill(counter, 0);
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            Object sel = iter.next();
            if (sel instanceof ITestSuitePO) {
                counter[EXEC_TESTSUITE]++;
            } else if (sel instanceof ISpecTestCasePO) {
                counter[SPEC_TESTCASE]++;
            } else if (sel instanceof ICapPO) {
                counter[CAP]++;
            } else if (sel instanceof IEventExecTestCasePO) {
                counter[EVENT_TESTCASE]++;
            } else if (sel instanceof IExecTestCasePO) {
                if (((IExecTestCasePO)sel).getParentNode() 
                        instanceof ISpecTestCasePO) {
                    counter[CHILD_EXEC_TC]++;
                } else {
                    counter[EXEC_TESTCASE]++;
                }
            } else if (sel instanceof ICategoryPO) {
                counter[CATEGORY]++;
            } else if (sel instanceof IProjectPO) {
                counter[PROJECT]++;
            } else if (sel instanceof IObjectMappingAssoziationPO) {
                counter[OM_TECH_NAME]++;
            } else if (sel instanceof IComponentNamePO) {
                counter[OM_LOGIC_NAME]++;
            } else if (sel instanceof IObjectMappingCategoryPO
                    && ((IObjectMappingCategoryPO)sel).getParent() == null) {
                counter[OM_MAIN_CATEGORY]++;
            } else if (sel instanceof IObjectMappingCategoryPO) {
                counter[OM_CATEGORY]++;
            }
        }
        return counter;
    }
    
    /**
     * Checks if the elements of the given selection have the same class type. 
     * @param selection the selection to check.
     * @return true if all elements of the selection have the same class type,
     * false otherwise.
     */
    public static boolean identicalType(StructuredSelection selection) {
        Class type = null;
        Iterator iter = selection.iterator(); 
        if (iter.hasNext()) {
            type = iter.next().getClass();
        } else {
            return true;
        }
        while (iter.hasNext()) {
            Class compareType = iter.next().getClass();
            if (!(type.getName().equals(compareType.getName()))) {
                return false;
            }
        }
        return true;
    }
    
}
