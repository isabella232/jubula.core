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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.ClientTestFactory;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.dialogs.EnterCommentDialog;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.exception.GDFatalException;
import org.eclipse.jubula.tools.exception.GDProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Aug 23, 2010
 */
public class AddTestResultSummaryCommentHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        ITestResultSummaryPO selectedSummary = getSelectedSummary(event);

        if (selectedSummary != null) {
            final String origTitle = selectedSummary.getCommentTitle();
            final String origDetail = selectedSummary.getCommentDetail();

            EnterCommentDialog dialog = new EnterCommentDialog(HandlerUtil
                    .getActiveShell(event), new IValidator() {
                        public IStatus validate(Object value) {
                            if (value instanceof String) {
                                return (((String)value).length() < 4000) 
                                    ? Status.OK_STATUS : Status.CANCEL_STATUS; 
                            }
                            return Status.OK_STATUS;
                        }
                    }, origTitle, origDetail);
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            Plugin.getHelpSystem().setHelp(dialog.getShell(),
                    ContextHelpIds.ADD_COMMENT);
            int result = dialog.open();
            if (result != Window.OK) {
                return null;
            }
            String newTitle = dialog.getCommentTitle();
            String newDetails = dialog.getCommentDetail();
            if (!StringUtils.equals(origTitle, newTitle)
                    || !StringUtils.equals(origDetail, newDetails)) {
                performOperation(selectedSummary, newTitle, newDetails);
            }
        }
        
        return null;
    }

    /**
     * @param event
     *            the execution event
     * @return the selected summary or null if no selection available
     */
    private ITestResultSummaryPO getSelectedSummary(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)selection;
            Object selectedObject = structuredSelection.getFirstElement();
            if (selectedObject instanceof ITestResultSummaryPO) {
                return (ITestResultSummaryPO)selectedObject;
            }
        }

        return null;
    }

    /**
     * perform model changes
     * 
     * @param selectedSummary the summary to change the comment for
     * @param newTitle the new comment title
     * @param newDetails the new comment details
     */
    private void performOperation(ITestResultSummaryPO selectedSummary,
            String newTitle, String newDetails) {
        
        final EntityManager sess = Hibernator.instance().openSession();
        try {            
            final EntityTransaction tx = 
                Hibernator.instance().getTransaction(sess);

            ITestResultSummaryPO transactionSummary = 
                sess.merge(selectedSummary);
            
            transactionSummary.setCommentTitle(newTitle);
            transactionSummary.setCommentDetail(newDetails);
            
            Hibernator.instance().commitTransaction(sess, tx);
            ClientTestFactory.getClientTest().fireTestresultSummaryChanged();
        } catch (PMException e) {
            throw new GDFatalException("storing of metadata failed.", e, //$NON-NLS-1$
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (GDProjectDeletedException e) {
            throw new GDFatalException("storing of metadata failed.", e, //$NON-NLS-1$
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            Hibernator.instance().dropSession(sess);
        }
        
    }
}
