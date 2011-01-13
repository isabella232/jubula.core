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
package org.eclipse.jubula.client.ui.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jubula.client.ui.model.CapGUI;
import org.eclipse.jubula.client.ui.model.CategoryGUI;
import org.eclipse.jubula.client.ui.model.EventExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.ExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.RefTestSuiteGUI;


/**
 * @author BREDEX GmbH
 * @created 18.02.2009
 */
public class GuiNodeNameViewerSorter extends ViewerSorter {
    /**
     * {@inheritDoc}
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        // Show categories before all other elements
        if (e1 instanceof CategoryGUI && !(e2 instanceof CategoryGUI)) {
            return -1;
        }

        if (e2 instanceof CategoryGUI && !(e1 instanceof CategoryGUI)) {
            return 1;
        }
        
        // Show Event Handler before all other nested exec test cases
        if (e1 instanceof EventExecTestCaseGUI 
                && !(e2 instanceof EventExecTestCaseGUI)) {
            return -1;
        }

        if (e2 instanceof EventExecTestCaseGUI 
                && !(e1 instanceof EventExecTestCaseGUI)) {
            return 1;
        }
        
        // do not sort the sequence of exec test cases or caps in spec test cases
        if (e1 instanceof ExecTestCaseGUI 
                || e2 instanceof ExecTestCaseGUI
                || e1 instanceof CapGUI 
                || e2 instanceof CapGUI
                || e1 instanceof RefTestSuiteGUI 
                || e2 instanceof RefTestSuiteGUI) {
            return 0;
        }
        
        return super.compare(viewer, e1, e2);
    }
}
