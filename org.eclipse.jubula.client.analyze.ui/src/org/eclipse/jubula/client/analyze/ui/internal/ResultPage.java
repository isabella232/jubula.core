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
package org.eclipse.jubula.client.analyze.ui.internal;

import java.util.Map;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeResult;
import org.eclipse.jubula.client.analyze.internal.Renderer;
import org.eclipse.jubula.client.analyze.internal.helper.RendererSelectionHelper;
import org.eclipse.jubula.client.analyze.ui.internal.definition.IResultRendererUI;
import org.eclipse.jubula.client.analyze.ui.internal.listener.AnalyzeTreeViewerDoubleClickListener;
import org.eclipse.jubula.client.analyze.ui.internal.provider.QueryTreeContentProvider;
import org.eclipse.jubula.client.analyze.ui.internal.provider.QueryTreeLabelProvider;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.part.Page;

/**
 * 
 * @author volker
 *
 */
public class ResultPage extends Page implements
    ISearchResultPage {

    /** The Control of the Page */
    private Control m_control;
     
    /** the ID */
    private String m_id;
    
    /** The queryResult */
    private QueryResult m_queryResult;
    
    /** The ListViewer of this class */
    private TreeViewer m_tvl;
    
    /** The Stack-Layout Composite with the sash */
    private Composite m_stackComp;
    
    /** The parentComposite */
    private Composite m_parentComp;
    
    /** The DoubleClickListener */
    private AnalyzeTreeViewerDoubleClickListener m_listener;
    
    /** 
     * @return m_lvr the TreeViewer of the ResultPage
     */
    public TreeViewer getLeftTreeViewer() {
        return m_tvl;
    }

    /** 
     * @return Composite with the Stack-Layout Composite of this Page 
     */
    public Composite getStackComposite() {
        return m_stackComp;
    }
    
    /**
     *  @return The Click Listener of the Tree
     */
    public AnalyzeTreeViewerDoubleClickListener getListener() {
        return m_listener;
    }
    
    /** 
     * @param listener The Listener that is to be set 
     */
    public void setListener(AnalyzeTreeViewerDoubleClickListener listener) {
        this.m_listener = listener;
    }
    /**
     * @return The QueryResult
     */
    public QueryResult getQueryResult() {
        return m_queryResult;
    }
    
    /**
     * @param queryResult The given QueryResult
     */
    public void setQueryResult(QueryResult queryResult) {
        this.m_queryResult = queryResult;
    }
    /**
     * @param parent The given Composite
     */
    private void setParentComposite(Composite parent) {
        this.m_parentComp = parent;
    }
    
    /**
     * @return The parentComposite
     */
    private Composite getParentComposite() {
        return m_parentComp;
    }
    /**
     * @param comp
     *            The Composite with the Stack-Layout Composite of this Page
     */
    public void setStackComposite(Composite comp) {
        this.m_stackComp = comp;
    }
    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        
        setQueryResult(null);
        setParentComposite(null);
        
        Composite par = new Composite(parent, SWT.NONE);
        FillLayout f = new FillLayout(SWT.HORIZONTAL);
        par.setLayout(f);
        
        Composite c = new Composite(par, SWT.NONE);
        StackLayout s = new StackLayout();
        c.setLayout(s);
        setParentComposite(c);
        setControl(par);

    }
    
    /** {@inheritDoc} */
    public void dispose() {
        
    }
    /**
     * @param control
     *            the control to set
     */
    private void setControl(Control control) {
        m_control = control;
    }
    /** {@inheritDoc} */
    public Control getControl() {
        return m_control;
    }
    /** {@inheritDoc} */
    public void setFocus() {
        this.getControl().setFocus();
    }
    
    /** {@inheritDoc} */
    public Object getUIState() {
        return null;
    }
    /** {@inheritDoc} */
    public void setInput(ISearchResult search, Object uiState) {
        if (search instanceof QueryResult) {
            setQueryResult((QueryResult) search);

            if (getQueryResult().getResultMap().size() == 1) {
                Analyze analyze = null;
                AnalyzeResult result = null;
                for (Map.Entry<Analyze, AnalyzeResult> a : getQueryResult()
                        .getResultMap().entrySet()) {
                    analyze = a.getKey();
                    result = a.getValue();
                }
                if (analyze != null && result != null) {
                    Renderer ren = RendererSelectionHelper
                            .getActiveRenderer(analyze);
                    IResultRendererUI rendererUI = (IResultRendererUI) ren
                            .getRendererInstance();
                    // setChosenRenderer(rendererUI);
                    rendererUI.renderResult(result, getParentComposite());

                    if (getParentComposite().getLayout() 
                            instanceof StackLayout) {
                        StackLayout sl = (StackLayout) getParentComposite()
                                .getLayout();
                        sl.topControl = rendererUI.getTopControl();
                        getParentComposite().layout();
                    }
                    getParentComposite().layout();
                }
            }
            if (getQueryResult().getResultMap().size() > 1) {
                // create a new SashForm
                SashForm sash = new SashForm(getParentComposite(),
                        SWT.HORIZONTAL);
                // create a new TreeViewer
                m_tvl = new TreeViewer(sash);
                m_tvl.setContentProvider(new QueryTreeContentProvider());
                m_tvl.setLabelProvider(new QueryTreeLabelProvider());
                StackLayout stack = new StackLayout();
                Composite cmp = new Composite(sash, SWT.NONE);
                // use a StackLayout to enable to change the content of this
                // Composite
                cmp.setLayout(stack);
                setStackComposite(cmp);
                m_listener = new AnalyzeTreeViewerDoubleClickListener(cmp);
                m_tvl.addDoubleClickListener(m_listener);
                sash.setWeights(new int[] { 40, 60 });
                // set the Control
                // setControl(sash);

                TreeViewer tv = new TreeViewer(getStackComposite());
                StackLayout l = (StackLayout) getStackComposite().getLayout();

                l.topControl = tv.getControl();
                getStackComposite().layout();

                getLeftTreeViewer().setInput(search);
                getLeftTreeViewer().expandAll();

                TreePath[] paths = getLeftTreeViewer().getExpandedTreePaths();
                if (paths.length > 0) {
                    TreeSelection sel = new TreeSelection(paths);
                    getLeftTreeViewer().setSelection(sel, true);
                }

                StackLayout s = (StackLayout) getParentComposite().getLayout();
                s.topControl = sash;

                getParentComposite().layout();
            }
        }
    }
    
    /** {@inheritDoc} */
    public void setViewPart(ISearchResultViewPart part) {
    }
    /** {@inheritDoc} */
    public void restoreState(IMemento memento) {
    }
    /** {@inheritDoc} */
    public void saveState(IMemento memento) {
    }
    /** {@inheritDoc} */
    public void setID(String id) {
        m_id = id;
    }
 
    /** {@inheritDoc} */
    public String getID() {
        return m_id;
    }
    /** {@inheritDoc} */
    public String getLabel() {
        return "";
    }
}
