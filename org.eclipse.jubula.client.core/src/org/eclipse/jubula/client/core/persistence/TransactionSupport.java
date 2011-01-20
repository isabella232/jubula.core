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
package org.eclipse.jubula.client.core.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;


/**
 * This class supports database transactions 
 *
 * @author BREDEX GmbH
 * @created 09.09.2005
 *
 */
public class TransactionSupport {

    /**
     * Interface for an transaction action
     * 
     *
     * @author BREDEX GmbH
     * @created 12.09.2005
     *
     */
    public interface ITransactAction {
        
        /**
         * Executes the transaction
         * @param sess The database session
         * @throws PMException in any case of db error
         * @throws ProjectDeletedException if the project was deleted in another
         * instance
         */
        public void run(EntityManager sess) throws PMException, 
        ProjectDeletedException;
    }
    
    
    /**
     * The database session
     */
    private EntityManager m_session;
    
    /**
     * Constructor
     * @param session the database session
     */
    public TransactionSupport(EntityManager session) {
        Validate.notNull(session);
        m_session = session;
    }

    /**
     * 
     * @param action the ITransactAction to execute.
     * @throws PMException .
     * @throws ProjectDeletedException if the project was deleted in another
     * instance
     */
    public void transact(ITransactAction action) throws PMException, 
        ProjectDeletedException {
        EntityTransaction tx = null;       
        tx = Hibernator.instance().getTransaction(m_session);
        action.run(m_session);
        Hibernator.instance().commitTransaction(m_session, tx);
    }
}
