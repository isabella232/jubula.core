package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;

/**
 * @author BREDEX GmbH
 */
public abstract class AbstractEditorDndSupport {
    /**
     * Executes actions after the drop.
     * 
     * @param node
     *            the dropped node.
     * @param targetEditor
     *            The editor to which the item has been dropped/pasted.
     */
    protected static void postDropAction(INodePO node,
            AbstractJBEditor targetEditor) {
        targetEditor.setFocus();
        targetEditor.refresh();
        targetEditor.getEditorHelper().setDirty(true);
        if (node != null) {
            targetEditor.setSelection(new StructuredSelection(node));
        }
        LocalSelectionTransfer.getInstance().setSelection(null);
    }
    
    /**
     * @param node the node to be moved.
     * @param target the target node.
     * @return the dropped node.
     */
    protected static INodePO moveNode(INodePO node, INodePO target) {
        int newPos = target.getParentNode().indexOf(target);
        node.getParentNode().removeNode(node);
        target.getParentNode().addNode(newPos, node);
        return node;
    }

    /**
     * Copy the parameters from the old exec test case to the new exec test case.
     * 
     * @param origExec  The original exec test case
     * @param newExec   The new exec test case
     */
    protected static void fillExec(IExecTestCasePO origExec, 
        IExecTestCasePO newExec) {
        fillParamNode(origExec, newExec);
        newExec.setName(origExec.getRealName());
        ISpecTestCasePO origSpecTC = origExec.getSpecTestCase();
        
        if (!origExec.getDataManager().equals(
                origSpecTC.getDataManager())) {
            newExec.setHasReferencedTD(false);
            if (newExec.getDataManager().getUniqueIds().isEmpty()) {
                origExec.getDataManager().deepCopy(
                        newExec.getDataManager());
            }
        } else {
            newExec.setHasReferencedTD(true);
        }
        
        for (ICompNamesPairPO origPair : origExec.getCompNamesPairs()) {
            ICompNamesPairPO newPair = PoMaker.createCompNamesPairPO(
                    origPair.getFirstName(), origPair.getSecondName(),
                    origPair.getType());
            newPair.setPropagated(origPair.isPropagated());
            newExec.addCompNamesPair(newPair);
        }
        
        if (newExec instanceof IEventExecTestCasePO
                || origExec instanceof  IEventExecTestCasePO) {
            
            IEventExecTestCasePO newEvent = (IEventExecTestCasePO)newExec;
            IEventExecTestCasePO origEvent = (IEventExecTestCasePO)origExec;
            newEvent.setEventType(origEvent.getEventType());
            newEvent.setReentryProp(origEvent.getReentryProp());
            newEvent.setMaxRetries(origEvent.getMaxRetries());
        }
    }

    /**
     * Copy the parameters from the old Test step to the new Test step.
     * 
     * @param origCap   The original Test step
     * @param newCap    The new Test step
     */
    protected static void fillCap(ICapPO origCap, ICapPO newCap) {
        fillParamNode(origCap, newCap);
        newCap.setComponentName(origCap.getComponentName());
        newCap.setComponentType(origCap.getComponentType());
        newCap.setActionName(origCap.getActionName());
        origCap.getDataManager().deepCopy(newCap.getDataManager());
    }

    /**
     * Copy the parameters from the old reference test suite to the new reference test suite.
     * 
     * @param origRefTS The original reference test suit.
     * @param newRefTS  The new reference test suit.
     */
    protected static void fillRefTestSuit(IRefTestSuitePO origRefTS, 
        IRefTestSuitePO newRefTS) {
        fillNode(origRefTS, newRefTS);
        newRefTS.setName(origRefTS.getRealName());
        newRefTS.setTestSuiteAutID(origRefTS.getTestSuiteAutID());
    }
    
    /**
     * Copy the parameters from the old node to the new node.
     * 
     * @param origNode  The original node.
     * @param newNode   The new node.
     */
    protected static void fillNode(INodePO origNode, INodePO newNode) {
        newNode.setActive(origNode.isActive());
        newNode.setComment(origNode.getComment());
        newNode.setGenerated(origNode.isGenerated());
        newNode.setDescription(origNode.getDescription());
        newNode.setToolkitLevel(origNode.getToolkitLevel());
    }
    
    /**
     * Copy the parameters from the old  paramter node to the new parameter node.
     * 
     * @param origNode  The original parameter node.
     * @param newNode   The new parameter node.
     */
    protected static void fillParamNode(IParamNodePO origNode,
            IParamNodePO newNode) {

        fillNode(origNode, newNode);
        newNode.setName(origNode.getName());
        newNode.setDataFile(origNode.getDataFile());
        newNode.setReferencedDataCube(origNode.getReferencedDataCube());
    }

    /**
     * 
     * @param toDrop The items that were dragged/cut.
     * @param dropTarget The drop/paste target.
     * @param enabledClass toDrop items should be of this class. 
     * @param disabledClass toDrop items should be not of this class. 
     * @return <code>true</code> if the given information indicates that the
     *         drop/paste is valid. Otherwise <code>false</code>.
     */
    public static boolean validateCopy(IStructuredSelection toDrop,
            INodePO dropTarget, Class<?> enabledClass, Class<?> disabledClass) {
        
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null) {
            return false;
        }
        
        for (Object obj : toDrop.toArray()) {
            if (!(enabledClass.isAssignableFrom(obj.getClass()))) {
                return false;
            }
            if (disabledClass != null
                    && disabledClass.isAssignableFrom(obj.getClass())) {
                return false;
            }
        }
        
        return true;
    }
}