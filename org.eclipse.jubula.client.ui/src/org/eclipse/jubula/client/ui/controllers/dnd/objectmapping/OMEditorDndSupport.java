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
package org.eclipse.jubula.client.ui.controllers.dnd.objectmapping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.jubula.client.ui.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.utils.Utils;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;



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
                        IObjectMappingCategoryPO targetFromCategory = 
                            target.getCategory();
                        target.getCategory().removeAssociation(target);
                        DataEventDispatcher.getInstance()
                            .fireDataChangedListener(
                                targetFromCategory, 
                                DataState.StructureModified, 
                                UpdateState.onlyInEditor);
                        newCategory.addAssociation(target);
                    }
                    cleanupAssociation(editor, oldAssoc);
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            editor.getAut().getObjMap().getMappedCategory(), 
                            DataState.StructureModified, 
                            UpdateState.onlyInEditor);
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            editor.getAut().getObjMap()
                                .getUnmappedLogicalCategory(),
                            DataState.StructureModified, 
                            UpdateState.onlyInEditor);
                    
                    editor.getTreeViewer().setExpandedState(target, true);
                    editor.getTreeViewer().setSelection(
                            new StructuredSelection(target));
                } catch (IncompatibleTypeException e) {
                    Utils.createMessageDialog(
                            e, e.getErrorMessageParams(), null);
                } catch (PMException pme) {
                    PMExceptionHandler.handlePMExceptionForEditor(pme, editor);
                }
            }
        }
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
                DataEventDispatcher.getInstance()
                    .fireDataChangedListener(
                            newCategory, DataState.StructureModified, 
                            UpdateState.onlyInEditor);
            } else {
                // Association has no logical names and no technical
                // name. It should be deleted.
                fromCategory.removeAssociation(assoc);
                editor.getEditorHelper().getEditSupport()
                    .getSession().remove(assoc);
            }
            DataEventDispatcher.getInstance()
                .fireDataChangedListener(
                    fromCategory, DataState.StructureModified, 
                    UpdateState.onlyInEditor);
        }
    }
    
    /**
     * Assigns the Component Name with GUID <code>newCompNameGuid</code> to
     * the given association, and unmaps the Component Name with GUID 
     * <code>originalCompNameGuid</code> from the given association.
     * 
     * @param assoc The association for which the Component Names will be 
     *              swapped.
     * @param originalCompNameGuid The GUID of the Component Name to unmap.
     * @param newCompNameGuid The GUID of the Component Name to assign.
     * @param editor The editor in which the swap is taking place.
     */
    public static void checkAndSwapComponentNames(
            IObjectMappingAssoziationPO assoc, String originalCompNameGuid, 
            String newCompNameGuid, ObjectMappingMultiPageEditor editor) {
        IWritableComponentNameMapper compMapper = 
            editor.getEditorHelper().getEditSupport().getCompMapper();
        IObjectMappingCategoryPO unmappedTechnical =
            editor.getAut().getObjMap().getUnmappedTechnicalCategory();
        IObjectMappingCategoryPO unmappedCompNames =
            editor.getAut().getObjMap().getUnmappedLogicalCategory();
        IObjectMappingCategoryPO mappedCompNames =
            editor.getAut().getObjMap().getMappedCategory();
        IObjectMappingAssoziationPO oldAssoc = 
            editor.getOmEditorBP().getAssociation(newCompNameGuid);

        if (oldAssoc == assoc) {
            // Don't perform any action if the swap is taking place for 
            // the same association.
            return;
        }
        
        try {
            compMapper.changeReuse(
                    assoc, originalCompNameGuid, newCompNameGuid);
            compMapper.changeReuse(oldAssoc, newCompNameGuid, null);

            if (getSection(assoc).equals(unmappedTechnical)) {
                // Change section to mapped, creating new categories 
                // if necessary.
                IObjectMappingCategoryPO mapped =
                    editor.getAut().getObjMap().getMappedCategory();
                IObjectMappingCategoryPO newCategory = 
                    editor.getOmEditorBP().createCategory(
                            mapped, assoc.getCategory());
                assoc.getCategory().removeAssociation(assoc);
                newCategory.addAssociation(assoc);
            }
            if (oldAssoc != null && oldAssoc.getLogicalNames().isEmpty()) {
                // Change section to unmapped tech, creating new 
                // categories if necessary.
                IObjectMappingCategoryPO newCategory = 
                    editor.getOmEditorBP().createCategory(
                            unmappedTechnical, oldAssoc.getCategory());
                oldAssoc.getCategory().removeAssociation(oldAssoc);
                newCategory.addAssociation(oldAssoc);
            }
            
            if (compMapper.getCompNameCache().getCompNamePo(
                    originalCompNameGuid) != null) {
                IObjectMappingAssoziationPO newAssoc =
                    PoMaker.createObjectMappingAssoziationPO(
                            null, originalCompNameGuid);
                unmappedCompNames.addAssociation(newAssoc);
                editor.getTreeViewer().refresh(unmappedCompNames);
            }

            // FIXME zeb: these change events should ensure that all viewers are
            //            updated properly. it's very likely possible to fire
            //            specific events instead of these general ones if
            //            performance in this case becomes an issue.
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    unmappedCompNames, DataState.StructureModified, 
                    UpdateState.onlyInEditor);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    mappedCompNames, DataState.StructureModified, 
                    UpdateState.onlyInEditor);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    unmappedTechnical, DataState.StructureModified, 
                    UpdateState.onlyInEditor);

            editor.getTableViewer().refresh();
            editor.getEditorHelper().setDirty(true);
        } catch (IncompatibleTypeException e) {
            Utils.createMessageDialog(e, e.getErrorMessageParams(), null);
        } catch (PMException pme) {
            PMExceptionHandler.handlePMExceptionForEditor(pme, editor);
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
                                null, new ArrayList<String>());
                    compMapper.changeReuse(newAssoc, null, compNameGuid);
                    compMapper.changeReuse(oldAssoc, compNameGuid, null);
                    target.addAssociation(newAssoc);
                    if (oldAssoc != null) {
                        IObjectMappingCategoryPO fromCategory = 
                            oldAssoc.getCategory();
                        
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
                            DataEventDispatcher.getInstance()
                                .fireDataChangedListener(
                                        newCategory.getParent() != null 
                                            ? newCategory.getParent() 
                                            : newCategory, 
                                        DataState.StructureModified, 
                                        UpdateState.onlyInEditor);
                        }
                        
                        DataEventDispatcher.getInstance()
                            .fireDataChangedListener(
                                fromCategory, 
                                DataState.StructureModified, 
                                UpdateState.onlyInEditor);
                    }

                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            newAssoc.getCategory(), 
                            DataState.StructureModified, 
                            UpdateState.onlyInEditor);

                    editor.getTreeViewer().refresh(target);
                    editor.getTreeViewer().setExpandedState(target, true);
                } catch (IncompatibleTypeException e) {
                    Utils.createMessageDialog(
                            e, e.getErrorMessageParams(), null);
                } catch (PMException pme) {
                    PMExceptionHandler.handlePMExceptionForEditor(pme, editor);
                }
            }
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
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        fromCategory, DataState.StructureModified, 
                        UpdateState.onlyInEditor);
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        target, DataState.StructureModified, 
                        UpdateState.onlyInEditor);
                editor.getTreeViewer().setExpandedState(target, true);
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
                                    null, new ArrayList<String>());
                        compMapper.changeReuse(
                                compNameAssoc, null, compNameGuid);
                        unmappedCompNames.addAssociation(compNameAssoc);
                    } catch (IncompatibleTypeException e) {
                        Utils.createMessageDialog(
                                e, e.getErrorMessageParams(), null);
                    } catch (PMException pme) {
                        PMExceptionHandler.handlePMExceptionForEditor(
                                pme, editor);
                    }
                }
                
                IObjectMappingCategoryPO fromCategory = assoc.getCategory();
                fromCategory.removeAssociation(assoc);
                target.addAssociation(assoc);
                
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        fromCategory, DataState.StructureModified, 
                        UpdateState.onlyInEditor);
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        target, DataState.StructureModified, 
                        UpdateState.onlyInEditor);
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        unmappedCompNames, DataState.StructureModified, 
                        UpdateState.onlyInEditor);
                editor.getTreeViewer().setExpandedState(target, true);
            }
        }
    }

    /**
     * Moves the categories in <code>toMove</code> to <code>target</code>.
     * 
     * @param toMove The categories to move.
     * @param target The category into which the other categories will be moved.
     * @param editor The editor in which the move occurs.
     */
    public static void checkAndMoveCategories(
            List<IObjectMappingCategoryPO> toMove, 
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {
        
        // FIXME zeb workaround for delete-orphan mapping om categories to 
        //           child om categories
        // workaround: disallow moving of categories
//        IObjectMappingCategoryPO newSection = 
//            editor.getOmEditorBP().getSection(target);
//        for (IObjectMappingCategoryPO category : toMove) {
//            IObjectMappingCategoryPO oldSection =
//                editor.getOmEditorBP().getSection(category);
//            
//            if (!target.equals(category)
//                    && oldSection.equals(newSection)
//                    && !editor.getOmEditorBP().existCategory(
//                            target, category.getName())) {
//                moveAndMergeCategory(target, category);
//            }
//        }
        // FIXME zeb end workaround
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
                Dialog dialog = Utils.createMessageDialog(
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
     * move a category to a target destination, if already one category exists, move
     * children into it
     * @param target The target category.
     * @param srcCategory The category to move/merge. 
     */
    private static void moveAndMergeCategory(IObjectMappingCategoryPO target, 
            IObjectMappingCategoryPO srcCategory) {

        IObjectMappingCategoryPO existingCategory = null;
        srcCategory.getParent().removeCategory(srcCategory);

        for (IObjectMappingCategoryPO child 
                : target.getUnmodifiableCategoryList()) {
            if (child.getName().equals(srcCategory.getName())) {
                existingCategory = child;
                break;                            
            }
        }
        if (existingCategory != null) {
            for (IObjectMappingAssoziationPO child 
                    : new ArrayList<IObjectMappingAssoziationPO>(
                            srcCategory.getUnmodifiableAssociationList())) {
                
                srcCategory.removeAssociation(child);
                existingCategory.addAssociation(child);
            }
            for (IObjectMappingCategoryPO child 
                    : new ArrayList<IObjectMappingCategoryPO>(
                            srcCategory.getUnmodifiableCategoryList())) {
                moveAndMergeCategory(existingCategory, child);
            }
        } else {
            target.addCategory(srcCategory);
        }
        
    }
    
    /**
     * 
     * @param toMove The categories for which the move operation is 
     *               to be checked.
     * @param target The target category for which the move operation is to be
     *               checked.
     * @param editor The editor in which the move operation would occur.
     * @return <code>true</code> if the arguments represent a valid move 
     *         operation. Otherwise <code>false</code>.
     */
    public static boolean canMoveCategories(
            List<IObjectMappingCategoryPO> toMove, 
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {

        // FIXME zeb workaround for delete-orphan mapping om categories to 
        //           child om categories
        // workaround: disallow moving of categories
//        for (IObjectMappingCategoryPO category : toMove) {
//            if (!canMove(category, target, editor)) {
//                return false;
//            }
//        }
//        
//        return true;
        return false;
        // FIXME zeb end workaround
    }

    /**
     * 
     * @param category The category for which the move operation is 
     *               to be checked.
     * @param target The target category for which the move operation is to be
     *               checked.
     * @param editor The editor in which the move operation would occur.
     * @return <code>true</code> if the arguments represent a valid move 
     *         operation. Otherwise <code>false</code>.
     */
    private static boolean canMove(
            IObjectMappingCategoryPO category,
            IObjectMappingCategoryPO target, 
            ObjectMappingMultiPageEditor editor) {

        IObjectMappingCategoryPO newSection = getSection(target);
        IObjectMappingCategoryPO oldSection = getSection(category);
        
        return !target.equals(category)
                && oldSection.equals(newSection)
                && !editor.getOmEditorBP().existCategory(
                        target, category.getName());
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
