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

import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.tester.ListTester;
import org.eclipse.jubula.toolkit.enums.ValueSets.BinaryChoice;

/**
 * @author BREDEX GmbH
 */
public class ListViewTester extends ListTester {
    @Override
    public void rcDragValue(int mouseButton, String modifier, String value,
        String operator, String searchType) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelectValue(value, operator, searchType, BinaryChoice.no.rcValue(),
                mouseButton, 0);
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }

    @Override
    public void rcDropValue(String value, String operator, String searchType,
        int delayBeforeDrop) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        try {
            rcSelectValue(value, operator, searchType,
                    BinaryChoice.no.rcValue(), dndHelper.getMouseButton(), 0);
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }

    @Override
    public void rcDragIndex(int mouseButton, String modifier, int index) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelectIndex(String.valueOf(index), BinaryChoice.no.rcValue(),
                mouseButton, 0);
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }

    @Override
    public void rcDropIndex(int index, int delayBeforeDrop) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        try {
            rcSelectIndex(String.valueOf(index), BinaryChoice.no.rcValue(),
                    dndHelper.getMouseButton(), 0);
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
}