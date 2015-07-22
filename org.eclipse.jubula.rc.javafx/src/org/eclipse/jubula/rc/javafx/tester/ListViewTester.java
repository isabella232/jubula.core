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

import java.awt.Point;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.ListTester;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.driver.RobotJavaFXImpl;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.util.NodeTraverseHelper;
import org.eclipse.jubula.toolkit.enums.ValueSets.BinaryChoice;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

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
            ((RobotJavaFXImpl)getRobot()).shakeMouse();
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
            ((RobotJavaFXImpl)getRobot()).shakeMouse();
        } finally {
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /** {@inheritDoc} */
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        Point awtPoint = getRobot().getCurrentMousePosition();
        final Point2D point = new Point2D(awtPoint.x, awtPoint.y);
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNodeAtMousePosition", new Callable<Object>() { //$NON-NLS-1$
                    @Override
                    public Object call() throws Exception {
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        ((ListView<?>) getRealComponent()).layout();

                        List<ListCell> tCells = NodeTraverseHelper
                                .getInstancesOf((Parent) getRealComponent(),
                                        ListCell.class);
                        for (ListCell cell : tCells) {
                            if (NodeBounds.checkIfContains(point, cell)) {
                                return cell;
                            }
                        }
                        throw new StepExecutionException(
                                "No table node found at mouse position: " //$NON-NLS-1$
                                        + "X: " + point.getX() //$NON-NLS-1$
                                        + "Y: " + point.getY(), //$NON-NLS-1$
                                EventFactory
                                        .createActionError(
                                                TestErrorEvent.NOT_FOUND));
                    }
                });
        return result;
    }
    
}