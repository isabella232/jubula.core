/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.ui.internal.helper;


import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.analyze.ExtensionRegistry;
import org.eclipse.jubula.client.analyze.definition.IContext;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.Context;
import org.eclipse.jubula.client.analyze.internal.helper.ProjectContextHelper;
import org.eclipse.jubula.client.core.model.IExecObjContPO;
import org.eclipse.jubula.client.core.model.ISpecObjContPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;


/**
 * 
 * @author volker
 *
 */
public class ContextHelper {

    /**  The Selection from the UI */
    private static Object selection;
    
  
    /** Empty because of HelperClass */
    private ContextHelper() {
    }
    
    /**
     * checks the the isActive state the the Analyzes' Context.
     * @param analyze
     *            The Given Analyze
     * @return true if the ContributionItem is enabled, false if it is disabled
     *         caused by a not matching Context
     */
    public static boolean isEnabled(Analyze analyze) {

        ProjectContextHelper.setObjContType(""); //$NON-NLS-1$
        String[] contextData = contextDataToArray(analyze.getContextType());
        
        for (Map.Entry<String, Context> c : ExtensionRegistry.getContexts()
                .entrySet()) {
            Context context = c.getValue();

            for (int i = 0; i < contextData.length; i++) {

                // check if the Element is the Context of the given Analyze
                if (contextData[i].equals(context.getID())
                        && !(context.getID().equals("projectloaded"))) { //$NON-NLS-1$

                    IContext iCon = (IContext) context.getContextInstance();

                    ISelectionService serv = PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getSelectionService();
                    ISelection sel = serv.getSelection();

                    if (sel instanceof IStructuredSelection) {
                        
                        IStructuredSelection structuredSel = 
                                (IStructuredSelection) sel;

                        // if the Selection is an instance of IExecObjCont or ISpecObjCont
                        // it is handled as a project because IExecObjCont and ISpecObjCont
                        // Objects cannot be traversed by the TreeTraverser
                        if (structuredSel.getFirstElement() 
                                instanceof IExecObjContPO) {

                            setSelection(GeneralStorage.
                                    getInstance().getProject());
                            ProjectContextHelper.
                            setObjContType("IExecObjContPO"); //$NON-NLS-1$
                        } else if (structuredSel.getFirstElement() 
                                instanceof ISpecObjContPO) {
                            setSelection(GeneralStorage.
                                    getInstance().getProject());
                            ProjectContextHelper.
                            setObjContType("ISpecObjContPO"); //$NON-NLS-1$
                        } else {
                            setSelection(structuredSel.getFirstElement());
                        }
                        
                        if (iCon.isActive(getSelection())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * checks the the isActive state the the Analyzes' Context. It is used for the ToolbarEntries
     * @param analyze
     *            The Given Analyze
     * @return true if the ContributionItem is enabled, false if it is disabled
     *         caused by a not matching Context
     */
    public static boolean isEnabledProject(Analyze analyze) {
        
        String[] contextData = contextDataToArray(analyze.getContextType());
        
        for (Map.Entry<String, Context> c : ExtensionRegistry.getContexts()
                .entrySet()) {
            Context context = c.getValue();
            IContext iCon = (IContext) context.getContextInstance();
            
            for (int i = 0; i < contextData.length; i++) {

                // check if the Context of the given Analyze is "projectLoaded"
                if (contextData[i].equals("projectloaded") //$NON-NLS-1$
                        && context.getID().equals("projectloaded")) { //$NON-NLS-1$

                    if (GeneralStorage.getInstance().getProject() != null) {

                        ProjectContextHelper.setObjContType("project"); //$NON-NLS-1$
                        setSelection(GeneralStorage.getInstance().getProject());
                        if (iCon.isActive(getSelection())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns an Array of single ContextStrings from the given Analyzes
     * ContextStrings
     * 
     * @param context
     *            The given ContextString
     * @return The ContextArray
     */
    public static String[] contextDataToArray(String context) {

        final String pattern = ","; //$NON-NLS-1$

        Pattern p = Pattern.compile(pattern);
        String[] contextData = p.split(context, 0);
        for (int i = 0; i < contextData.length; i++) {
            contextData[i] = contextData[i].trim().toLowerCase();
        }
        return contextData;
    }

    /**
     * @return The Selection
     */
    public static Object getSelection() {
        return selection;
    }

    /**
     * @param sel
     *            The Selection
     */
    public static void setSelection(Object sel) {
        ContextHelper.selection = sel;
    }
}
