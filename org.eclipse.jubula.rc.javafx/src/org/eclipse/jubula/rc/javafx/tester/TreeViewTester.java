package org.eclipse.jubula.rc.javafx.tester;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.geometry.Point2D;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;

import org.eclipse.jubula.rc.common.CompSystemConstants;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractTreeTester;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.util.NodeBounds;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;

/**
 * Tester Class for the <code>TreeView</code>. If you are looking for more
 * implemented actions on Trees look at <code>TreeOperationContext</code>.
 *
 * @author BREDEX GmbH
 * @created 19.11.2013
 */
public class TreeViewTester extends AbstractTreeTester {

    @Override
    public void rcVerifyTextAtMousePosition(String txt, String operator) {
        checkNodeText(new Object[] { getNodeAtMousePosition() }, txt, operator);
    }

    @Override
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        Point awtPoint = getRobot().getCurrentMousePosition();
        final Point2D point = new Point2D(awtPoint.x, awtPoint.y);
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getNodeBounds", new Callable<Object>() { //$NON-NLS-1$
                    @Override
                    public Object call() throws Exception {
                        // Update the layout coordinates otherwise
                        // we would get old position values
                        ((TreeView) getRealComponent()).layout();

                        List<Object> tCells = ComponentHandler
                                .getInstancesOfType(TreeCell.class);
                        for (Object o : tCells) {
                            TreeCell<?> cell = (TreeCell<?>) o;
                            if (NodeBounds.checkIfContains(point, cell)) {
                                return cell.getTreeItem();
                            }
                        }
                        throw new StepExecutionException(
                                "No tree node found at mouse position: " //$NON-NLS-1$
                                        + "X: " + point.getX() //$NON-NLS-1$
                                        + "Y: " + point.getY(), //$NON-NLS-1$
                                EventFactory
                                        .createActionError(
                                                TestErrorEvent.NOT_FOUND));
                    }
                });
        return result;
    }

    @Override
    public void rcDragByTextPath(int mouseButton, String modifier,
        String pathType, int preAscend, String treeTextPath, String operator) {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelect(pathType, preAscend, treeTextPath, operator, 0, 1,
                CompSystemConstants.EXTEND_SELECTION_NO);
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);

    }

    @Override
    public void rcDropByTextPath(String pathType, int preAscend,
            String treeTextPath, String operator, int delayBeforeDrop) {

        try {
            rcSelect(pathType, preAscend, treeTextPath, operator, 0, 1,
                    CompSystemConstants.EXTEND_SELECTION_NO);
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }

    @Override
    public void rcDragByIndexPath(int mouseButton, String modifier,
            String pathType, int preAscend, String treeIndexPath) {

        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelectByIndices(pathType, preAscend, treeIndexPath, 0, 1,
                CompSystemConstants.EXTEND_SELECTION_NO);
        pressOrReleaseModifiers(modifier, true);
        getRobot().mousePress(null, null, mouseButton);
    }

    @Override
    public void rcDropByIndexPath(String pathType, int preAscend,
            String treeIndexPath, int delayBeforeDrop) {
        try {
            rcSelectByIndices(pathType, preAscend, treeIndexPath, 0, 1,
                    CompSystemConstants.EXTEND_SELECTION_NO);
            waitBeforeDrop(delayBeforeDrop);
        } finally {
            final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }

}
