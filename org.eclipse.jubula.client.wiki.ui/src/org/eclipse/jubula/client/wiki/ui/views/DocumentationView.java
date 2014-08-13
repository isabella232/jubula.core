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

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author BREDEX GmbH
 */
public class DocumentationView extends ViewPart {
    /**
     * the browser to display the documentation content
     */
    private Browser m_browser;

    @Override
    public void createPartControl(Composite parent) {
        m_browser = new Browser(parent, SWT.NONE);

        MarkupParser markupParser = new MarkupParser();
        markupParser.setMarkupLanguage(new TracWikiLanguage());

        String htmlContent = markupParser
            .parseToHtml("= H1 = \n == H2 == \n === H3 ==="); //$NON-NLS-1$
        m_browser.setText(htmlContent);
    }

    @Override
    public void setFocus() {
        m_browser.setFocus();
    }
}