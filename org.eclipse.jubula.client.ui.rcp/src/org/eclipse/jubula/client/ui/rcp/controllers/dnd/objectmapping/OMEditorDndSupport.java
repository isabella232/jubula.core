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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;



/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Object Mapping Editor.
 *
 * @author BREDEX GmbH
 * @created 20.03.2008
 */
public class OMEditorDndSupport {

    /**
     * Private constructor
     */
    private OMEditorDndSupport() {
        // Do nothing
    }

    /**
     * Assigns the given Component Names to the given association.
     * 
     * @param compNamesToMove The Component Names to assign.
     * @param target The association to which the Component Names will be
     *               assigned.
     * @param editor Editor in which the assignment is taking place.
     */
    public static void checkTypeCompatibilityAndMove(
            List<IComponentNamePO> compNamesToMove, 
            IObjectMappingAssoziationPO target, 
            ObjectMappingMultiPageEditor editor) {

        IWritableComponentNameMapper compMapper = 
            editor.getEditorHelper().getEditSupport().getCompMapper();
        IObjectMappingCategoryPO unmappedTechnical =
            editor.getAut().getObjMap().getUnmappedTechnicalCategory();
        for (IComponentNamePO compName : compNamesToMove) {
            String compNameGuid = compName.getGuid();
            if (!target.getLogicalNames().contains(compNameGuid)) {
                IObjectMappingAssoziationPO oldAssoc = 
                    editor.getOmEditorBP().getAssociation(compNameGuid);
                try {
                    compMapper.changeReuse(target, null, compNameGuid);
                    compMapper.changeReuse(oldAssoc, compNameGuid, null);
                    if (getSection(target).equals(
                            unmappedTechnical)) {
                        // Change section to mapped, creating new categories 
                        // if necessary.
                        IObjectMappingCategoryPO mapped =
                            editor.getAut().getObjMap().getMappedCategory();
                        IObjectMappingCategoryPO newCategory = 
                            editor.getOmEditorBP().createCategory(
                                    mapped, target.getCategory());
                        target.getCategory().removeAssociation(target);
                        newCategory.addAssociation(target);
                    }
                    cleanupAssociation(editor, oldAssoc);
                } catch (IncompatibleTypeException e) {
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.E_OM_WRONG_COMP_TYPE);
                } catch (PMException pme) {
                    PMExceptionHandler.handlePMExceptionForEditor(pme, editor);
                }
            }
        }
        DataEventDispatcher.getInstance().fireDataChangedListener(
                editor.getAut().getObjMap(),
                DataState.StructureModified, 
                UpdateState.onlyInEditor);
        
        editor.getTreeViewer().setExpandedState(target, true);
    }

    /**
     * Performs any necessary "cleanup" on an Object Mapping Association. This
     * includes moving the association to the appropriate "section" 
     * (ex. Unmapped Technical Components or Mapped Components).
     * 
     * @param editor The editor in which the cleanup is to occur.
     * @param assoc The association to cleanup.
     */
    private static void cleanupAssociation(ObjectMappingMultiPageEditor editor,
            IObjectMappingAssoziationPO assoc) {

        if (assoc != null 
                && assoc.getLogicalNames().isEmpty()) {
            IObjectMappingCategoryPO fromCategory = 
                assoc.getCategory();
            if (assoc.getTechnicalName() != null) {
                // Change section to unmapped tech, creating new 
                // categories if necessary.
                IObjectMappingCategoryPO unmappedTech =
                    editor.getAut().getObjMap()
                    .getUnmappedTechnicalCategory();
                IObjectMappingCategoryPO newCategory = 
                    editor.getOmEditorBP().createCategory(
                            unmappedTech, fromCategory);
                fromCategory.removeAssociation(assoc);
                newCategory.addAssociation(assoc);
            } else {
                // Association has no logical names and no technical
                // name. It should be deleted.
                fromCategory.removeAssociation(assoc);
            }
        }
    }
    
    /**
     * Moves the given Component Names to the given category. This removes 
     * whatever mappings in which the Component Names were involved in the 
     * context of the supported editor.
     * 
     * @param compNamesToMove The Component Names to move.
     * @param target The category to which the Component Names will be
     *               moved.
     * @param editor Editor in which the move is taking place.
     */
    public static void checkTypeCompatibilityAndMove(
            List<IComponentNamePO> compNamesToMove, 
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {
        
        IObjectMappingCategoryPO unmappedComponentNames =
            editor.getAut().getObjMap().getUnmappedLogicalCategory();
        IObjectMappingCategoryPO targetSection = getSection(target);
        if (unmappedComponentNames.equals(targetSection)) {
            IWritableComponentNameMapper compMapper = 
                editor.getEditorHelper().getEditSupport().getCompMapper();
            for (IComponentNamePO compName : compNamesToMove) {
                String compNameGuid = compName.getGuid();
                IObjectMappingAssoziationPO oldAssoc =
                    editor.getOmEditorBP().getAssociation(compNameGuid);
                try {
                    IObjectMappingAssoziationPO newAssoc = 
                        PoMaker.createObjectMappingAssoziationPO(
                                null, new HashSet<String>());
                    compMapper.changeReuse(newAssoc, null, compNameGuid);
                    compMapper.changeReuse(oldAssoc, compNameGuid, null);
                    target.addAssociation(newAssoc);
                    if (oldAssoc != null) {
                        if (oldAssoc.getLogicalNames().isEmpty()) {
                            // Change section to unmapped tech, creating new 
                            // categories if necessary.
                            IObjectMappingCategoryPO unmappedTech =
                                editor.getAut().getObjMap()
                                .getUnmappedTechnicalCategory();
                            IObjectMappingCategoryPO newCategory = 
                                editor.getOmEditorBP().createCategory(
                                        unmappedTech, oldAssoc.getCategory());
                            oldAssoc.getCategory().removeAssociation(oldAssoc);
                            newCategory.addAssociation(oldAssoc);
                        }
                    }
                } catch (IncompatibleTypeException e) {
                    ErrorHandlingUtil.createMessageDialog(
                            e, e.getErrorMessageParams(), null);
                } catch (PMException pme) {
                    PMExceptionHandler.handlePMExceptionForEditor(pme, editor);
                }
            }
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    editor.getAut().getObjMap(), 
                    DataState.StructureModified, 
                    UpdateState.onlyInEditor);
            
            editor.getTreeViewer().refresh(target);
            editor.getTreeViewer().setExpandedState(target, true);
        }
    }

    /**
     * Moves the given associations to the given category.
     * 
     * @param toMove The associations to move.
     * @param target The category into which the associations will be moved.
     * @param editor The editor in which the move is occurring.
     */
    public static void checkAndMoveAssociations(
            List<IObjectMappingAssoziationPO> toMove, 
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {
        
        IObjectMappingCategoryPO unmappedTechNames = 
            editor.getAut().getObjMap().getUnmappedTechnicalCategory();
        IObjectMappingCategoryPO newSection = getSection(target);
        for (IObjectMappingAssoziationPO assoc : toMove) {
            IObjectMappingCategoryPO oldSection = getSection(assoc);
            
            if (oldSection.equals(newSection)) {
                IObjectMappingCategoryPO fromCategory = assoc.getCategory();
                fromCategory.removeAssociation(assoc);
                target.addAssociation(assoc);
            } else if (unmappedTechNames.equals(newSection)) {
                IObjectMappingCategoryPO unmappedCompNames = 
                    editor.getAut().getObjMap().getUnmappedLogicalCategory();
                
                IWritableComponentNameMapper compMapper = 
                    editor.getEditorHelper().getEditSupport().getCompMapper();
                for (String compNameGuid 
                        : new ArrayList<String>(assoc.getLogicalNames())) {
                    try {
                        compMapper.changeReuse(assoc, compNameGuid, null);
                        IObjectMappingAssoziationPO compNameAssoc = 
                            PoMaker.createObjectMappingAssoziationPO(
                                    null, new HashSet<String>());
                        compMapper.changeReuse(
                                compNameAssoc, null, compNameGuid);
                        unmappedCompNames.addAssociation(compNameAssoc);
                    } catch (IncompatibleTypeException e) {
                        ErrorHandlingUtil.createMessageDialog(
                                e, e.getErrorMessageParams(), null);
                    } catch (PMException pme) {
                        PMExceptionHandler.handlePMExceptionForEditor(
                                pme, editor);
                    }
                }
                
                IObjectMappingCategoryPO fromCategory = assoc.getCategory();
                fromCategory.removeAssociation(assoc);
                target.addAssociation(assoc);
                
            }
        }

        if (!toMove.isEmpty()) {
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    editor.getAut().getObjMap(), 
                    DataState.StructureModified, 
                    UpdateState.onlyInEditor);
            editor.getTreeViewer().setExpandedState(target, true);
        }

    }

    /**
     * Checks if there are categories to be merged. Ask User if he wants to merge and
     * returns the result
     * 
     * @param toBeMoved The categories that are being moved.
     * @param targetCategory The target category.
     * @return
     *      boolean
     */
    public static boolean isMergeIfNeeded(
            List<IObjectMappingCategoryPO> toBeMoved, 
            IObjectMappingCategoryPO targetCategory) {
        boolean doIt = true;

        for (IObjectMappingCategoryPO categoryToMove : toBeMoved) {
            IObjectMappingCategoryPO existingCategory = null;
            for (IObjectMappingCategoryPO child 
                    : targetCategory.getUnmodifiableCategoryList()) {
                if (child.getName().equals(categoryToMove.getName())) {
                    existingCategory = child;
                    break;                            
                }
            }

            if (existingCategory != null) {
                Dialog dialog = ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.Q_MERGE_CATEGORY, new Object[]{
                        existingCategory.getName(),
                        targetCategory.getName()},
                        null);
                if (dialog.getReturnCode() == Window.CANCEL) {
                    doIt = false;
                }
            }
        }
        return doIt;
    }

    /**
     * 
     * @param toMove The associations for which the move operation is 
     *               to be checked.
     * @param target The target category for which the move operation is to be
     *               checked.
     * @param editor The editor in which the move operation would occur.
     * @return <code>true</code> if the arguments represent a valid move 
     *         operation. Otherwise <code>false</code>.
     */
    public static boolean canMoveAssociations(
            List<IObjectMappingAssoziationPO> toMove, 
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {
        
        for (IObjectMappingAssoziationPO assoc : toMove) {
            if (!canMove(assoc, target, editor)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 
     * @param assoc The association for which the move operation is 
     *               to be checked.
     * @param target The target category for which the move operation is to be
     *               checked.
     * @param editor The editor in which the move operation would occur.
     * @return <code>true</code> if the arguments represent a valid move 
     *         operation. Otherwise <code>false</code>.
     */
    private static boolean canMove(IObjectMappingAssoziationPO assoc,
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {

        IObjectMappingCategoryPO oldSection = getSection(assoc);
        IObjectMappingCategoryPO newSection = getSection(target);
        IObjectMappingCategoryPO unmappedTechnicalNames =
            editor.getAut().getObjMap().getUnmappedTechnicalCategory();
        
        return unmappedTechnicalNames.equals(newSection) 
            || oldSection.equals(newSection);
    }

    /**
     * 
     * @param target The target category into which the Component Names would
     *               be moved.
     * @param editor The editor in which the move operation would occur.
     * @return <code>true</code> if the arguments represent a valid move 
     *         operation. Otherwise <code>false</code>.
     */
    public static boolean canMoveCompNames(
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {

        return getSection(target).equals(
                editor.getAut().getObjMap().getUnmappedLogicalCategory());
    }

    /**
     * 
     * @param startCategory The category to check.
     * @return the top-level category to which the given category belongs.
     */
    public static IObjectMappingCategoryPO getSection(
            IObjectMappingCategoryPO startCategory) {
        
        return startCategory != null ? startCategory.getSection() : null;
    }
    
    /**
     * 
     * @param assoc The association to check.
     * @return the top-level category to which the given association belongs.
     */
    public static IObjectMappingCategoryPO getSection(
            IObjectMappingAssoziationPO assoc) {

        return assoc != null ? assoc.getSection() : null;
    }

}
