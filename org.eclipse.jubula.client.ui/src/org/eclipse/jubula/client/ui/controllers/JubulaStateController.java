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
package org.eclipse.jubula.client.ui.controllers;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.commands.AUTModeChangedCommand;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.ui.actions.OMMarkInAutAction;
import org.eclipse.jubula.client.ui.actions.OMSetCategoryToMapInto;
import org.eclipse.jubula.client.ui.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.communication.message.ChangeAUTModeMessage;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;


/**
 * React on all changes in the views and editors.
 *
 * @author BREDEX GmbH
 * @created 24.02.2005
 */
public class JubulaStateController {
    /** instance of this class */
    private static JubulaStateController instance;
    
    /** last IWorkbenchPart on reactOnChange */
    private IWorkbenchPart m_lastPart = null;
    
    /** last Selection on reactOnChange */
    private ISelection m_lastSelection = null;
    
    /** The SelectionListener at the SelectionService of Eclipse */
    private ISelectionListener m_selListener;
    
    /**
     *  The contructor.
     */
    private JubulaStateController() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().
            addPartListener(new IPartListener() {

                public void partActivated(IWorkbenchPart part) {
                    // do nothing
                }
                public void partBroughtToTop(IWorkbenchPart part) {
                    // do nothing
                }
                public void partDeactivated(IWorkbenchPart part) {
                    // do nothing
                }
                public void partClosed(IWorkbenchPart part) {
                    removeSelectionListenerFromSelectionService();  
                }
                public void partOpened(IWorkbenchPart part) {
                    // do nothing
                }
            });
    }
    
    /**
     * @return The (singleton) instance of this class.
     */
    public static JubulaStateController getInstance() {
        if (instance == null) {
            instance = new JubulaStateController();
        }
        return instance;
    }

    /**
     * Adds the <code>SelectionListener</code> to the central SelectionService
     * of Eclipse. The anonym class listens to selections.
     */
    public void addSelectionListenerToSelectionService() {
        m_selListener = new ISelectionListener() {
            public void selectionChanged(IWorkbenchPart part,
                ISelection selection) {
                
                reactOnChange(part, selection);
            }
        };
        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getSelectionService().addSelectionListener(m_selListener);
    }
    
    /**
     * Removes the <code>SelectionListener</code> from the central SelectionService
     * of Eclipse.
     */
    public void removeSelectionListenerFromSelectionService() {
        if (m_selListener != null) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getSelectionService().removeSelectionListener(m_selListener);
        }
    }
    
    /**
     * Reacts on the changes from the SelectionService of Eclipse.
     * @param part The Workbenchpart (an editor or a view).
     * @param sel The selection in this workbenchpart.
     */
    void reactOnChange(IWorkbenchPart part, ISelection sel) {
        if (part.equals(m_lastPart)
                && sel.equals(m_lastSelection)
                && m_lastPart != null
                && m_lastSelection != null) {

            return;
        }
        m_lastPart = part;
        m_lastSelection = sel;
        if (!(IPageLayout.ID_PROBLEM_VIEW.equals(part.getSite().getId()))) {
            checkGlobalActions();
        }
        if (sel instanceof IStructuredSelection
            && ((IStructuredSelection)sel).size() > 0) {

            IStructuredSelection structuredSel = (IStructuredSelection)sel;
            if (part instanceof ObjectMappingMultiPageEditor) {
                checkObjectMappingEditor(part, structuredSel);
            }    
        }
    }
    
    /**
     * Checks the selection in the ObjectMappingEditor an en-/disables the actions.
     */
    private void checkGlobalActions() {
        // Is AUT running and when, in what mode
        checkAutState();
    }

    /**
     * Checks the selection in the ObjectMappingEditor an en-/disables the actions.
     * @param sel ISelection
     * @param part IWorkBenchPart
     */
    private void checkObjectMappingEditor(IWorkbenchPart part, 
            IStructuredSelection sel) {
        
        if (sel.size() == 1) {
            if (!(sel.getFirstElement() 
                    instanceof IObjectMappingAssoziationPO)) {
                OMMarkInAutAction.setEnabled(false);
            }
        }
        IAUTMainPO connectedAut = TestExecution.getInstance().getConnectedAut();
        if (connectedAut != null) {
            ObjectMappingMultiPageEditor editor = 
                (ObjectMappingMultiPageEditor)part;
            switch (AUTModeChangedCommand.getAutMode()) {
                case ChangeAUTModeMessage.OBJECT_MAPPING:
                    if (editor.getAut().equals(connectedAut)) {
                        OMSetCategoryToMapInto.setEnabled(true);
                        break;
                    }
                default:
                    OMSetCategoryToMapInto.setEnabled(false);
                    break;
            }
        }
    }
    

    /**
     * Checks if there is a Aut running and in what state
     */
    private void checkAutState() {
        // FIXME zeb implement enablement for object mapping actions
        //           *or* switch over to handler implementation
    }
    
}