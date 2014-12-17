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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;


/**
 * @author BREDEX GmbH
 * @created Mar 13, 2009
 */
public class MergeComponentNameInViewHandler 
        extends AbstractMergeComponentNameHandler {

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        // Get model objects from selection
        Set<IComponentNamePO> compNames = getComponentNames(getSelection());

        // Dialog
        IComponentNamePO selectedCompNamePo = openDialog(compNames);

        if (selectedCompNamePo == null) {
            // cancel operation
            return null;
        }

        EntityManager masterSession = GeneralStorage.getInstance()
                .getMasterSession();
        Persistor persistor = Persistor.instance();
        EntityTransaction tx = persistor.getTransaction(masterSession);

        // Make sure that we're using Component Names from the Master Session
        Set<IComponentNamePO> inSessionCompNames = 
                new HashSet<IComponentNamePO>();

        try {
            for (IComponentNamePO cn : compNames) {
                IComponentNamePO compName = masterSession
                        .find(cn.getClass(), cn.getId());
                masterSession.refresh(compName);
                persistor.lockPO(masterSession, compName);
                inSessionCompNames.add(compName);
            }
            
            performOperation(inSessionCompNames, selectedCompNamePo);
            persistor.commitTransaction(masterSession, tx);
            fireChangeEvents(inSessionCompNames);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        }

        return null;
    }

}
