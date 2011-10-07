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
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITestDataCubeContPO;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jun 29, 2010
 */
public class DeleteTestDataManagerHandler 
    extends AbstractDeleteTreeItemHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        if (activePart instanceof CentralTestDataEditor
                && currentSelection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)currentSelection;
            CentralTestDataEditor editor = (CentralTestDataEditor)activePart;
            if (editor.getEditorHelper().requestEditableState() 
                    != EditableState.OK) {
                return null;
            }

            if (confirmDelete(structuredSelection)) {
                deleteSelection(editor, structuredSelection);
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    protected String getName(Object obj) {
        if (obj instanceof IParameterInterfacePO) {
            return ((IParameterInterfacePO)obj).getName();
        }
        return super.getName(obj);
    }

    /**
     * @param editor
     *            the CentralTestDataEditor
     * @param structuredSelection
     *            the selected elements to delete
     */
    @SuppressWarnings("nls")
    private void deleteSelection(CentralTestDataEditor editor,
            IStructuredSelection structuredSelection) {
        ITestDataCubeContPO cont = (ITestDataCubeContPO)editor
                .getEditorHelper().getEditSupport().getWorkVersion();
        List<String> reusedCubeList = new ArrayList<String>(0);

        for (Iterator<IParameterInterfacePO> it = structuredSelection
                .iterator(); it.hasNext();) {
            IParameterInterfacePO td = it.next();
            if (TestDataCubeBP.isCubeReused(td)) {
                reusedCubeList.add(td.getName());
            }
        }

        if (reusedCubeList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringConstants.NEWLINE);
            for (String s : reusedCubeList) {
                sb.append(StringConstants.SPACE);
                sb.append(StringConstants.MINUS);
                sb.append(StringConstants.SPACE);
                sb.append(s);
                sb.append(StringConstants.NEWLINE);
            }
            ErrorHandlingUtil.createMessageDialog(MessageIDs.I_REUSED_TDC,
                    new Object[] { sb.toString() }, null);
        } else {
            for (Iterator<IParameterInterfacePO> it = structuredSelection
                    .iterator(); it.hasNext();) {
                IParameterInterfacePO td = it.next();
                cont.removeTestDataCube(td);
                DataEventDispatcher.getInstance().fireDataChangedListener(td,
                        DataState.Deleted, UpdateState.onlyInEditor);
            }

            editor.getEditorHelper().setDirty(true);
            DataEventDispatcher.getInstance().fireDataChangedListener(cont,
                    DataState.StructureModified, UpdateState.onlyInEditor);
        }
    }
}
