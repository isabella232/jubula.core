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
package org.eclipse.jubula.client.core.businessprocess.db;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMObjectDeletedException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.NodePM.AbstractCmdHandleChild;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.exception.InvalidDataException;


/**
 * @author BREDEX GmbH
 * @created 12.12.2005
 */
public class TestCaseBP  extends NodeBP {
    /**
     * Utility class
     */
    private TestCaseBP() {
    // do nothing
    }

    /**
     * Add an existing SpecTC to another SpecTC by reference. Lock the TC if
     * this is the first usage to prevent accidental deletion before the
     * editor is saved.
     * @param editSupport holds the DB session for locking purposes
     * @param targetTC TC where the reference shall be added.
     * @param referencedTC TC to be used as a reference.
     * @param position Index position for the ExecTC, null means append.
     * @throws PMAlreadyLockedException if the referenced TC was never
     * referenced before and is edited by another user.
     * @throws PMDirtyVersionException if the referenced TC was modified
     * @throws PMObjectDeletedException
     *             if the po as deleted by another concurrently working user
     * @return The ExecTC used to reference the SpecTC
     */
    public static IExecTestCasePO addReferencedTestCase(
        EditSupport editSupport, INodePO targetTC, ISpecTestCasePO referencedTC,
        Integer position) throws PMAlreadyLockedException,
        PMDirtyVersionException, PMObjectDeletedException {

        handleFirstReference(editSupport, referencedTC, false);
        IExecTestCasePO newExecTC = NodeMaker.createExecTestCasePO(
            referencedTC);
        if (position != null) {
            targetTC.addNode(position.intValue(), newExecTC);
        } else {
            targetTC.addNode(newExecTC);
        }       

        return newExecTC;
    }

    /**
     * Lock the TC if it is reused for the first time. This prevents deletion of
     * the TC while the editor is not saved.
     * 
     * @param editSupport
     *            holding the DB session for locking purposes
     * @param referencedTC
     *            TC to be used as a reference.
     * @param isReferencedByThisAction
     *            tells if there was a reference created by the current action,
     *            i.e. there is one references even if no other TC in the db
     *            references this one.
     * @throws PMAlreadyLockedException
     *             if the TC is locked by someone else
     * @throws PMDirtyVersionException
     *             if the TC was modified outside this instance of the
     *             application.
     * @throws PMObjectDeletedException
     *             if the po as deleted by another concurrently working user
     */
    private static void handleFirstReference(EditSupport editSupport,
        ISpecTestCasePO referencedTC, boolean isReferencedByThisAction)
        throws PMAlreadyLockedException, PMDirtyVersionException,
        PMObjectDeletedException {
        int minSize = 0;
        if (isReferencedByThisAction) {
            minSize = 1;
        }
        
        if (NodePM.getInternalExecTestCases(referencedTC.getGuid(), 
            referencedTC.getParentProjectId()).size() <= minSize) {
            lockPO(editSupport, referencedTC);
        }
    }
    
    /**
     * Adds an EventHandler to a TestCase.
     * 
     * @param editSupport
     *            holds the DB session for locking purposes
     * @param targetTC
     *            TC where the reference shall be added.
     * @param eventHandlerTC
     *            TC to be used as a referenced event handler.
     * @throws InvalidDataException
     *             if the TC can not be used as an event handler
     * @throws PMAlreadyLockedException
     *             if there is already a lock on this TC
     * @throws PMDirtyVersionException
     *             if the TC was modified outside of this application instance.
     * @throws PMObjectDeletedException
     *             if the po as deleted by another concurrently working user
     */
    public static void addEventHandler(EditSupport editSupport, 
        ISpecTestCasePO targetTC, IEventExecTestCasePO eventHandlerTC) 
        throws InvalidDataException, PMAlreadyLockedException, 
        PMDirtyVersionException, PMObjectDeletedException {
        
        handleFirstReference(editSupport, eventHandlerTC.getSpecTestCase(),
            true);
        targetTC.addEventTestCase(eventHandlerTC);
    }

    /**
     * Creates and persists a new SpecTestCase with the given name, 
     * the given parent and the given position to insert in the parent.
     * The parent must be the Project or a Category!
     * The position can be null. If null, the SpecTestCase will be inserted
     * as last element.
     * 
     * @param name the name of the new SpecTestCase
     * @param parent the parent of thenew SpecTestCase. 
     * Must be the Project or a Category!
     * @param position the position to insert. If null, SpecTestCase will be 
     * added as last element. 
     * @return the new SpecTestCasePO
     * @throws PMSaveException in case of save failed
     * @throws PMAlreadyLockedException in case of already locked
     * @throws PMException in case of exception
     * @throws ProjectDeletedException in case of project deleted
     */
    public static ISpecTestCasePO createNewSpecTestCase(String name, 
        INodePO parent, Integer position) throws PMSaveException, 
        PMAlreadyLockedException, PMException, ProjectDeletedException {
        
        Integer pos = position;
        ISpecTestCasePO specTC = NodeMaker.createSpecTestCasePO(name);
        final AbstractCmdHandleChild cmdHandleChild = NodePM
            .getCmdHandleChild(parent, specTC);
        if (pos != null && pos < 0) {
            pos = null;
        }
        NodePM.addAndPersistChildNode(parent, specTC, pos, cmdHandleChild);
        return specTC;
    }

    /**
     * Returns a GuiNode which is able to contain SpecTestCasePOs in 
     * hierarchical relation to the given node. If the current node is not a 
     * possible container, its parent will be checked and so on.
     * @param currentNode the current node to check
     * @return a container for SpecTestCasePOs.
     */
    public static INodePO getSpecTestCaseContainer(INodePO currentNode) {
        INodePO parent = currentNode;
        Class parentClass = Hibernator.getClass(parent);
        while (!(Hibernator.isPoClassSubclass(
                    parentClass, IProjectPO.class) || Hibernator
                        .isPoClassSubclass(parentClass, ICategoryPO.class))) {
            parent = parent.getParentNode();
            parentClass = Hibernator.getClass(parent);
        }
        return parent;
    }
    
    /**
     * @param specTc
     *            the spec test case to test
     * @return true if editable --> belongs to current project; false otherwise
     *         or if specTc == null
     */
    public static boolean belongsToCurrentProject(ISpecTestCasePO specTc) {
        if (specTc != null) {
            EqualsBuilder eb = new EqualsBuilder();
            eb.append(specTc.getParentProjectId(), GeneralStorage.getInstance()
                    .getProject().getId());
            return eb.isEquals();
        }
        return false;
    }
}
