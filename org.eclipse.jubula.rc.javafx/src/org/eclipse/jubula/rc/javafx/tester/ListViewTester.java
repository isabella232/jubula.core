/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.ListTester;

/**
 * @author BREDEX GmbH
 */
public class ListViewTester extends ListTester {
    @Override
    public void rcDragValue(int mouseButton, String modifier, String value,
        String operator, String searchType) {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcDropValue(String value, String operator, String searchType,
        int delayBeforeDrop) {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcDragIndex(int mouseButton, String modifier, int index) {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcDropIndex(int index, int delayBeforeDrop) {
        StepExecutionException.throwUnsupportedAction();
    }
}