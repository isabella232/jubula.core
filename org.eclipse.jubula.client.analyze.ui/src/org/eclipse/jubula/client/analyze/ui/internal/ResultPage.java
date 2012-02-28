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

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.analyze.ui.internal.listener.AnalyzeTreeViewerDoubleClickListener;
import org.eclipse.jubula.client.analyze.ui.internal.provider.QueryTreeContentProvider;
import org.eclipse.jubula.client.analyze.ui.internal.provider.QueryTreeLabelProvider;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
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
        // create a new SashForm
        SashForm sash = new SashForm(parent, SWT.HORIZONTAL);

        // create a new TreeViewer
        m_tvl = new TreeViewer(sash);
        m_tvl.setContentProvider(new QueryTreeContentProvider());
        m_tvl.setLabelProvider(new QueryTreeLabelProvider());

        StackLayout stack = new StackLayout();
        
        Composite cmp = new Composite(sash, SWT.NONE);
        // use a StackLayout to enable to change the content of this Composite
        cmp.setLayout(stack);
        setStackComposite(cmp);
        m_listener = new AnalyzeTreeViewerDoubleClickListener(cmp);
        m_tvl.addDoubleClickListener(m_listener);

        sash.setWeights(new int[] { 40, 60 });
        // set the Control
        setControl(sash);
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
        m_queryResult = (QueryResult) search;
        
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
