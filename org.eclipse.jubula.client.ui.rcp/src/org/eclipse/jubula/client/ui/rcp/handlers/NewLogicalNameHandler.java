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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handler for creating a new Logical Component Name.
 *
 * @author BREDEX GmbH
 * @created Jan 12, 2009
 */
public class NewLogicalNameHandler extends AbstractNewComponentNameHandler {

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof ObjectMappingMultiPageEditor) {
            final ObjectMappingMultiPageEditor omEditor = 
                (ObjectMappingMultiPageEditor)activePart;
            omEditor.getEditorHelper().doEditorOperation(
                    new IEditorOperation() {
                        public void run(IPersistentObject workingPo) {
                            IComponentNameMapper compNameMapper = 
                                omEditor.getEditorHelper().getEditSupport()
                                    .getCompMapper();
                            // Show dialog
                            String newName = openDialog(compNameMapper);
                            if (newName != null) {
                                performOperation(omEditor, newName);
                            }
                        }
                    });
        }
        
        return null;
    }

    /**
     * Creates the new Component Name.
     * 
     * @param omEditor The editor in which the creation is taking place.
     * @param newName The name for the new Component Name.
     */
    private void performOperation(
            ObjectMappingMultiPageEditor omEditor, String newName) {
        
        IObjectMappingPO objMap = omEditor.getAut().getObjMap();
        IWritableComponentNameMapper mapper = 
            omEditor.getEditorHelper().getEditSupport().getCompMapper();
        try {
            IObjectMappingAssoziationPO assoc = 
                PoMaker.createObjectMappingAssoziationPO(
                        null, new ArrayList<String>());
            mapper.changeReuse(assoc, null, 
                    performOperation(newName, mapper).getGuid());
            objMap.getUnmappedLogicalCategory().addAssociation(assoc);
            omEditor.getEditorHelper().setDirty(true);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    objMap.getUnmappedLogicalCategory(), 
                    DataState.StructureModified, UpdateState.onlyInEditor);
            omEditor.getTreeViewer().setExpandedState(
                    objMap.getUnmappedLogicalCategory(), true);
        } catch (IncompatibleTypeException e) {
            ErrorHandlingUtil.createMessageDialog(
                    e, e.getErrorMessageParams(), null);
        } catch (PMException pme) {
            PMExceptionHandler.handlePMExceptionForEditor(pme, omEditor);
        }
    }

}
