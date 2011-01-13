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
package org.eclipse.jubula.client.ui.search;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.filter.GDFilteredTree;
import org.eclipse.jubula.client.ui.filter.GDPatternFilter;
import org.eclipse.jubula.client.ui.provider.DecoratingCellLabelProvider;
import org.eclipse.jubula.client.ui.provider.contentprovider.AbstractGDTreeViewContentProvider;
import org.eclipse.jubula.client.ui.search.result.BasicSearchResult;
import org.eclipse.jubula.client.ui.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.FilteredTree;


/**
 * @author BREDEX GmbH
 * @created 07.12.2005
 */
public class SearchResultPage extends AbstractSearchResultPage 
    implements IProjectLoadedListener {
    /** double click listener */
    private DoubleClickListener m_doubleClickListener = 
        new DoubleClickListener();

    /**
     * <code>m_control</code>
     */
    private Control m_control;

    /** {@inheritDoc} */
    public void createControl(Composite parent) {
        Composite topLevelComposite = new Composite(parent, SWT.NONE);
        setControl(topLevelComposite);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 2;
        layout.marginWidth = Layout.MARGIN_WIDTH;
        layout.marginHeight = Layout.MARGIN_HEIGHT;
        topLevelComposite.setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.grabExcessHorizontalSpace = true;
        topLevelComposite.setLayoutData(layoutData);

        final FilteredTree ft = new GDFilteredTree(topLevelComposite, SWT.MULTI
                | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
                new GDPatternFilter(), true);

        setTreeViewer(ft.getViewer());

        ColumnViewerToolTipSupport.enableFor(getTreeViewer());
        getTreeViewer().addDoubleClickListener(m_doubleClickListener);
        getTreeViewer().setContentProvider(
                new SearchResultContentProvider());
        getTreeViewer().setLabelProvider(
                new DecoratingCellLabelProvider(new LabelProvider(), Plugin
                        .getDefault().getWorkbench().getDecoratorManager()
                        .getLabelDecorator()));
        getTreeViewer().setSorter(new ViewerSorter());
        getSite().setSelectionProvider(getTreeViewer());
        
        DataEventDispatcher.getInstance().addProjectLoadedListener(this, true);
        
        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.GUIDANCER_SEARCH_RESULT_VIEW);
    }

    /**
     * The label provider of the table.
     * 
     * @author BREDEX GmbH
     * @created 07.12.2005
     */
    private static class LabelProvider extends ColumnLabelProvider {
        /**
         * {@inheritDoc}
         */
        public String getText(Object element) {
            if (element instanceof SearchResultElement) {
                return ((SearchResultElement)element).getName();
            }
            return super.getText(element);
        }

        /**
         * {@inheritDoc}
         */
        public Image getImage(Object element) {
            if (element instanceof SearchResultElement) {
                SearchResultElement elem = (SearchResultElement)element;
                return elem.getImage();
            }
            return super.getImage(element);
        }

        /**
         * {@inheritDoc}
         */
        public String getToolTipText(Object element) {
            if (element instanceof SearchResultElement) {
                SearchResultElement sr = (SearchResultElement)element;
                String comment = sr.getComment();
                if (comment != null) {
                    return comment;
                }
            }
            return super.getToolTipText(element);
        }
    }

    /** {@inheritDoc} */
    public void setFocus() {
        getTreeViewer().getControl().setFocus();
    }

    /**
     * The content provider of the table.
     */
    private static class SearchResultContentProvider extends
            AbstractGDTreeViewContentProvider {
        /** {@inheritDoc} */
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof BasicSearchResult) {
                BasicSearchResult sr = (BasicSearchResult)parentElement;
                return sr.getResultList().toArray();
            }
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
    }

    /**
     * DoubleClickListener for the TableViewer
     * 
     * @author BREDEX GmbH
     * @created 07.12.2005
     */
    private static class DoubleClickListener implements IDoubleClickListener {

        /** {@inheritDoc} */
        public void doubleClick(DoubleClickEvent event) {
            if (!(event.getSelection() instanceof IStructuredSelection)) {
                return;
            }
            SearchResultElement<Long> element = 
                (SearchResultElement)((IStructuredSelection)event
                    .getSelection()).getFirstElement();
            element.jumpToResult();
        }

    }

    /** {@inheritDoc} */
    public void dispose() {
        getSite().setSelectionProvider(null);
        getTreeViewer().removeDoubleClickListener(m_doubleClickListener);
        DataEventDispatcher.getInstance().removeProjectLoadedListener(this);
        super.dispose();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(I18n.getString("SearchResultPage.ResultPageLabel"));
        Object viewerInput = getTreeViewer().getInput();
        if (viewerInput != null) {
            BasicSearchResult sr = (BasicSearchResult) viewerInput;
            ISearchQuery query = sr.getQuery();
            if (query != null) {
                sb.append(query.getLabel());
            }
        }
        return sb.toString(); 
    }

    /**
     * @param control
     *            the control to set
     */
    private void setControl(Control control) {
        m_control = control;
    }

    /**
     * @return the control
     */
    public Control getControl() {
        return m_control;
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        if (GeneralStorage.getInstance().getProject() == null) {
            Plugin.getDisplay().syncExec(new Runnable() {
                public void run() {
                    getTreeViewer().setInput(null);
                }
            });
        }
    }
}
