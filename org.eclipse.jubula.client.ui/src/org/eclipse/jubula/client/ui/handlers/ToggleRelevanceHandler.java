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
package org.eclipse.jubula.client.ui.handlers;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.tools.exception.JBFatalException;
import org.eclipse.jubula.tools.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 * @created Mar 3, 2011
 */
public class ToggleRelevanceHandler extends AbstractTestResultViewHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        ITestResultSummaryPO selectedSummary = getSelectedSummary(event);

        if (selectedSummary != null) {
            final EntityManager sess = Hibernator.instance().openSession();
            try {
                final EntityTransaction tx = Hibernator.instance()
                        .getTransaction(sess);

                ITestResultSummaryPO transactionSummary = sess
                        .merge(selectedSummary);

                transactionSummary.setTestsuiteRelevant(!transactionSummary
                        .isTestsuiteRelevant());

                Hibernator.instance().commitTransaction(sess, tx);
                ClientTestFactory.getClientTest()
                        .fireTestresultSummaryChanged();
            } catch (PMException e) {
                throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                        MessageIDs.E_DATABASE_GENERAL);
            } catch (ProjectDeletedException e) {
                throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                        MessageIDs.E_PROJECT_NOT_FOUND);
            } finally {
                Hibernator.instance().dropSession(sess);
            }
        }

        return null;
    }
}
