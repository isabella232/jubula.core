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
package org.eclipse.jubula.client.analyze.ui.internal.listener;

import java.util.Map;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.internal.Renderer;
import org.eclipse.jubula.client.analyze.internal.helper.RendererSelectionHelper;
import org.eclipse.jubula.client.analyze.ui.definition.IResultRendererUI;
import org.eclipse.jubula.client.analyze.ui.internal.QueryResult;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * Handles the DoubleClickBehavior of the Tree in the AnalyzeSearchResultView
 * @author volker
 * 
 */
public class AnalyzeTreeViewerDoubleClickListener implements
        IDoubleClickListener {
    
    /** The ResultType */
    private String m_resultType;
    
    /** The chosen Renderer */
    private IResultRendererUI m_chosenRenderer;
    
    /** The Composite */
    private final Composite m_comp;
    
    /**
     * The AnalyzeTreeViewerDoubleClickListener
     * @param composite The given Composite
     */
    public AnalyzeTreeViewerDoubleClickListener(Composite composite) {
        this.m_comp = composite;
    }
    
    /** @return The chosen Renderer */
    public IResultRendererUI getChosenRenderer() {
        return m_chosenRenderer;
    }
    
    /** @param renderer The Renderer that is to be set as the chosen Renderer for this Analyze */
    public void setChosenRenderer (IResultRendererUI renderer) {
        this.m_chosenRenderer = renderer;
    }
    
    /**
     * @return The ResultType
     */
    public String getResultType() {
        return m_resultType;
    }
    
    /**
     * handles the behavior after a doubleClick on an Analyze in the AnalyzeTree
     * @param event The given Event
     */
    public void doubleClick(DoubleClickEvent event) {
        // get the QueryResult from the Viewer
        QueryResult qr = (QueryResult) event.getViewer().getInput();
        TreeSelection selection = (TreeSelection) event.getSelection();
        // check if the selected element is an Analyze.
        Object firstElement = selection.getFirstElement();
        if (firstElement instanceof Analyze) {
            Analyze analyze = (Analyze) firstElement;
            AnalyzeResult result = null;

            for (Map.Entry<Analyze, AnalyzeResult> a : qr
                    .getResultMap().entrySet()) {
                if (analyze.getID().equals(a.getKey().getID())) {
                    result = a.getValue();
                }
            }
            
            Renderer ren = RendererSelectionHelper.getActiveRenderer(analyze);
            IResultRendererUI rendererUI = (IResultRendererUI) ren
                    .getRendererInstance();
            setChosenRenderer(rendererUI);
            rendererUI.renderResult(result, m_comp);

            if (m_comp.getLayout() instanceof StackLayout) {
                StackLayout sl = (StackLayout) m_comp.getLayout();
                sl.topControl = rendererUI.getTopControl();
                m_comp.layout();
            }
        }
    }
}
