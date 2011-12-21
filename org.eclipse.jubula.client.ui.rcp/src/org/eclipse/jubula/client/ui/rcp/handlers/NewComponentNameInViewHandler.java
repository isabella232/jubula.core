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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesDecorator;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.ProjectComponentNameMapper;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.IncompatibleTypeException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;


/**
 * @author BREDEX GmbH
 * @created Mar 13, 2009
 */
public class NewComponentNameInViewHandler extends
        AbstractNewComponentNameHandler {

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        EntityManager s = Persistor.instance().openSession();
        IWritableComponentNameMapper compNameMapper = 
            new ProjectComponentNameMapper(
                    new ComponentNamesDecorator(s), 
                    GeneralStorage.getInstance().getProject());

        // Show dialog
        String newName = openDialog(compNameMapper);
        
        try {
            if (newName != null) {
                EntityTransaction tx = 
                    Persistor.instance().getTransaction(s);
                IComponentNamePO newCompName = 
                    performOperation(newName, compNameMapper);
                CompNamePM.flushCompNames(s, 
                        GeneralStorage.getInstance().getProject().getId(), 
                        compNameMapper);
                Persistor.instance().commitTransaction(s, tx);
                compNameMapper.getCompNameCache()
                    .updateStandardMapperAndCleanup(
                        GeneralStorage.getInstance().getProject()
                            .getId());
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        newCompName, DataState.Added, UpdateState.all);
            }
        } catch (IncompatibleTypeException e) {
            ErrorHandlingUtil.createMessageDialog(
                    e, e.getErrorMessageParams(), null);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleGDProjectDeletedException();
        } finally {
            Persistor.instance().dropSession(s);
        }
        
        return null;
    }

}
