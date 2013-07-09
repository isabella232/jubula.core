package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
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
        if (node != null) {
            targetEditor.setSelection(new StructuredSelection(node));
        }
        targetEditor.getEditorHelper().setDirty(true);
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
}