/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.wiki.ui.views;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.wiki.ui.i18n.Messages;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewer;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * @author BREDEX GmbH
 */
public class DescriptionView extends ViewPart {
    /** the viewer to display the documentation */
    private MarkupViewer m_viewer;

    /**  the m_listener we register with the selection service */
    private ISelectionListener m_listener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart sourcepart,
            ISelection selection) {
            // we ignore our own selections
            if (sourcepart != DescriptionView.this) {
                showSelection(selection);
            }
        }

        /**
         * @param selection the selection to show
         */
        private void showSelection(ISelection selection) {
            if (selection instanceof StructuredSelection) {
                StructuredSelection structuredSelection = 
                    (StructuredSelection) selection;
                Object firstElement = structuredSelection.getFirstElement();
                if (firstElement instanceof INodePO) {
                    INodePO node = (INodePO) firstElement;
                    final String comment = node.getComment();
                    if (StringUtils.isNotEmpty(comment)) {
                        m_viewer.setMarkup(comment);
                        return;
                    }
                }
            }
            m_viewer.setMarkup(Messages.NoDescriptionAvailable);
        }
    };

    /** {@inheritDoc} */
    public void createPartControl(Composite parent) {
        m_viewer = new MarkupViewer(parent, null, SWT.MULTI | SWT.WRAP
            | SWT.V_SCROLL);
        m_viewer.setMarkupLanguage(new TracWikiLanguage());

        MarkupViewerConfiguration configuration = 
            new MarkupViewerConfiguration(m_viewer);
        m_viewer.configure(configuration);

        m_viewer.getTextWidget().setEditable(false);

        getSite().getWorkbenchWindow().getSelectionService()
            .addSelectionListener(m_listener);
    }

    /** {@inheritDoc} */
    public void setFocus() {
        m_viewer.getTextWidget().setFocus();
    }
    
    /** {@inheritDoc} */
    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService()
            .removeSelectionListener(m_listener);
        super.dispose();
    }
}