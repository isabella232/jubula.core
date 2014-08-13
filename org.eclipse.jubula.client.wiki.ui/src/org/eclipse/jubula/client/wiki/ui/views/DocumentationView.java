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

import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewer;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author BREDEX GmbH
 */
public class DocumentationView extends ViewPart {
    /** the viewer to display the documentation */
    private MarkupViewer m_viewer;

    @Override
    public void createPartControl(Composite parent) {
        m_viewer = new MarkupViewer(parent, null, SWT.MULTI | SWT.WRAP
            | SWT.V_SCROLL);
        m_viewer.setMarkupLanguage(new TracWikiLanguage());

        MarkupViewerConfiguration configuration = 
            new MarkupViewerConfiguration(m_viewer);
        m_viewer.configure(configuration);
        
        m_viewer.getTextWidget().setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append("= H1 = \n"); //$NON-NLS-1$
        sb.append("= H2 = \n"); //$NON-NLS-1$
        sb.append("= H3 = \n"); //$NON-NLS-1$
        sb.append("= H4 = \n"); //$NON-NLS-1$
        sb.append(" * bullets list \n"); //$NON-NLS-1$
        sb.append(" * another bullet \n"); //$NON-NLS-1$
        sb.append("http://www.eclipse.org/jubula \n"); //$NON-NLS-1$
        
        m_viewer.setMarkup(sb.toString());
    }

    @Override
    public void setFocus() {
        m_viewer.getTextWidget().setFocus();
    }
}