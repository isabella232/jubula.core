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

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.wiki.ui.i18n.Messages;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author BREDEX GmbH
 */
public class DescriptionView extends ViewPart {
    /** the viewer to display the documentation */
    private Browser m_browser;
    /** the markup parser to use */ 
    private MarkupParser m_markupParser;
    
    /** the m_listener we register with the selection service */
    private ISelectionListener m_listener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart sourcepart,
            ISelection selection) {
            // we ignore our own selections
            if (sourcepart != DescriptionView.this) {
                showSelection(selection);
            }
        }

        /**
         * @param selection
         *            the selection to show
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
                        m_browser.setText(m_markupParser
                            .parseToHtml(comment));
                        return;
                    }
                }
            }
            m_browser.setText(Messages.NoDescriptionAvailable);
        }
    };

    /** {@inheritDoc} */
    public void createPartControl(Composite parent) {
        m_browser = new Browser(parent, SWT.NONE);
        /*
         *  taken from 
         *      org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupEditor
         *      line 247 and following
         *  bug 260479: open hyperlinks in a browser
         */
        m_browser.addLocationListener(new LocationListener() {
            public void changed(LocationEvent event) {
                event.doit = false;
            }

            public void changing(LocationEvent event) {
                // if it looks like an absolute URL
                if (event.location.matches("([a-zA-Z]{3,8})://?.*")) { //$NON-NLS-1$

                    // workaround for browser problem (bug 262043)
                    int idxOfSlashHash = event.location.indexOf("/#"); //$NON-NLS-1$
                    if (idxOfSlashHash != -1) {
                        // allow javascript-based scrolling to work
                        if (!event.location.startsWith("file:///#")) { //$NON-NLS-1$
                            event.doit = false;
                        }
                        return;
                    }
                    // workaround end

                    event.doit = false;
                    try {
                        PlatformUI.getWorkbench().getBrowserSupport().createBrowser("org.eclipse.ui.browser") //$NON-NLS-1$
                                .openURL(new URL(event.location));
                    } catch (Exception e) {
                        new URLHyperlink(new Region(0, 1), 
                            event.location).open();
                    }
                }
            }
        });
        
        m_markupParser = new MarkupParser();
        m_markupParser.setMarkupLanguage(new MediaWikiLanguage());

        getSite().getWorkbenchWindow().getSelectionService()
            .addSelectionListener(m_listener);
    }

    /** {@inheritDoc} */
    public void setFocus() {
        m_browser.setFocus();
    }

    /** {@inheritDoc} */
    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService()
            .removeSelectionListener(m_listener);
        super.dispose();
    }
}