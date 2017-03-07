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
package org.eclipse.jubula.client.ui.rcp.views.dataset;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IParamChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.views.AbstractJBTreeView;
import org.eclipse.jubula.client.ui.rcp.views.CompNamesView;
import org.eclipse.jubula.client.ui.views.IJBPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.properties.PropertySheet;


/**
 * View for Data Sets at TestCases.
 *
 * @author BREDEX GmbH
 * @created 11.02.2005
 */
public class DataSetView extends PageBookView 
    implements IProjectLoadedListener, IParamChangedListener, 
        IContributedContentsView, ISelectionListener {

    /**
     * {@inheritDoc}
     */
    protected IPage createDefaultPage(PageBook book) {
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage(Messages.DataSetViewNoDataSetInfo);
        return page;
    }

    /**
     * {@inheritDoc}
     */
    protected PageRec doCreatePage(IWorkbenchPart part) {
        if (part instanceof AbstractJBTreeView
            || part.getAdapter(IJBEditor.class) != null) {
            AbstractDataSetPage page;
            if (part instanceof CentralTestDataEditor) {
                page = new TestDataCubeDataSetPage();
            } else {
                page = new ParamNodeDataSetPage();
            }
            initPage(page);
            page.createControl(getPageBook());
            DataEventDispatcher.getInstance().addDataChangedListener(
                    page, true);
            return new PageRec(part, page);
        } else if (part instanceof IJBPart) {
            return new PageRec(part, createDefaultPage(getPageBook()));
        }

        // Use the default page     
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
        if (pageRecord.page instanceof IDataChangedListener) {
            DataEventDispatcher.getInstance().removeDataChangedListener(
                    (IDataChangedListener)pageRecord.page);
        }
        pageRecord.page.dispose();
        pageRecord.dispose();
    }

    /**
     * {@inheritDoc}
     */
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        if (page != null) {
            return page.getActivePart();
        } 
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isImportant(IWorkbenchPart part) {
        return !(part == this
            || part instanceof CompNamesView
            || part instanceof PropertySheet);
    }

    /**
     * {@inheritDoc}
     */
    public IWorkbenchPart getContributingPart() {
        return getCurrentContributingPart();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void init(IViewSite site) throws PartInitException {
        site.getPage().addSelectionListener(this);
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addParamChangedListener(this, true);
        ded.addProjectLoadedListener(this, true);
        super.init(site);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        final DataEventDispatcher dispatcher = 
            DataEventDispatcher.getInstance();
        dispatcher.removeParamChangedListener(this);
        dispatcher.removeProjectLoadedListener(this);
        super.dispose();
    }
    
    /**
     * {@inheritDoc}
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        // we ignore "unimportant" selections or null selection
        if (!isImportant(part) || selection == null) {
            return;
        }

        IPage currentPage = getCurrentPage();
        if (currentPage instanceof ISelectionListener) {
            // pass the selection to the page       
            ((ISelectionListener)currentPage).selectionChanged(part, selection);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class adapter) {
        if (this.getClass().equals(adapter)) {
            return this;
        }
        return super.getAdapter(adapter);
    }

    /**
     * @return Returns the parent.
     */
    public Composite getParentComposite() {
        return (Composite)getCurrentPage().getControl();
    }

    /**
     * {@inheritDoc}
     */
    public void handleParamChanged(Object caller) {
        if (getCurrentPage() instanceof IParamChangedListener) {
            ((IParamChangedListener)getCurrentPage()).
                handleParamChanged(caller);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        IPage curPage = getCurrentPage();
        if (curPage instanceof IProjectLoadedListener) {
            ((IProjectLoadedListener)curPage).handleProjectLoaded();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        Plugin.showStatusLine(this);
        super.setFocus();
    }
}