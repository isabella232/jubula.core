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
package org.eclipse.jubula.client.ui.controllers.dnd;

import java.util.Iterator;

import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.model.EventExecTestCaseGUI;
import org.eclipse.jubula.client.ui.model.GuiNode;
import org.eclipse.jubula.client.ui.model.SpecTestCaseGUI;
import org.eclipse.swt.dnd.TransferData;


/**
 * @author BREDEX GmbH
 * @created 17.05.2005
 */
public class EventHandlerDropTargetListener extends ViewerDropAdapter {
    /**
     * <code>m_editor</code>
     */
    private AbstractTestCaseEditor m_editor;

    /**
     * @param editor the editor which contains the viewer.
     */
    public EventHandlerDropTargetListener(AbstractTestCaseEditor editor) {
        super(editor.getEventHandlerTreeViewer());
        m_editor = editor;
        boolean scrollExpand = Plugin.getDefault().getPreferenceStore().
            getBoolean(Constants.TREEAUTOSCROLL_KEY);
        setScrollExpandEnabled(scrollExpand);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean performDrop(Object data) {
        if (m_editor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return false;
        }
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        Iterator iter = (transfer.getSelection()).iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (!(obj instanceof GuiNode)) {
                return false;
            }
            GuiNode node = (GuiNode)obj;
            if (node instanceof SpecTestCaseGUI) {
                GuiNode target = (GuiNode)getCurrentTarget();
                SpecTestCaseGUI specTcGUI = (SpecTestCaseGUI)node;
                if (target != node) {
                    addEventHandler(target, specTcGUI);
                }
            }
        }
        return true;
    }

    /**
     * @param target
     *            the target to drop on.
     * @param specTcGUI
     *            the TestCase used as EventHandler.
     */
    private void addEventHandler(GuiNode target, SpecTestCaseGUI specTcGUI) {
        if (target == null || target instanceof EventExecTestCaseGUI) {

            m_editor.addEventHandler(specTcGUI, (SpecTestCaseGUI)m_editor
                    .getEventHandlerTreeViewer().getInput());
            LocalSelectionTransfer.getInstance().setSelection(null);
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean validateDrop(Object target, int operation,
        TransferData transferType) {
        if (LocalSelectionTransfer.getInstance().getSelection() == null) {
            return false;
        }
        LocalSelectionTransfer transfer = LocalSelectionTransfer.getInstance();
        Iterator iter = transfer.getSelection().iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (!(obj instanceof GuiNode)) {
                return false;
            }
            GuiNode node = (GuiNode)obj;
            if (!(node instanceof SpecTestCaseGUI)) {
                return false;
            }
            GuiNode parent = (GuiNode)getViewer().getInput();
            if (node.getContent().hasCircularDependences(
                parent.getContent())) {
                return false;
            }
        }
        return true;
    }
}
