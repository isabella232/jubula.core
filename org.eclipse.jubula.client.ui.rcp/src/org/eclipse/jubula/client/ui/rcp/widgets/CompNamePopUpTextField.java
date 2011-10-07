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
package org.eclipse.jubula.client.ui.rcp.widgets;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;


/**
 * A SWT.Text field with a pop up list.
 * @author BREDEX GmbH
 * @created 15.08.2005
 */
@SuppressWarnings("synthetic-access")
public final class CompNamePopUpTextField extends CheckedCompNameText {
    
    /**
     * @author BREDEX GmbH
     * @created Dec 8, 2008
     */
    private final class IContentProposalListener2Implementation implements
            IContentProposalListener2 {
        /**
         * {@inheritDoc}
         */
        public void proposalPopupClosed(ContentProposalAdapter adapter) {
            m_popupOpen = false;
        }

        /**
         * {@inheritDoc}
         */
        public void proposalPopupOpened(ContentProposalAdapter adapter) {
            m_popupOpen = true;
        }
    }

    /** data key to init pop up or not */
    public static final String INITPOPUP = "INITPOPUP"; //$NON-NLS-1$
    
    /** the max. lines of the popup */
    protected static final int HEIGHT = 8;
    
    /** the min. width (pixel) of the popup */
    protected static final int WIDTH = 130;
    
    /** KeyCode for SPACE */
    protected static final char SPACE = ' ';  

    /** intern */
    private CompNamesProposalProvider m_contentProposalProvider; 

    /** intern */
    private ILabelProvider m_labelProvider;

    /** is the opup with the content proposal open? */
    private boolean m_popupOpen = false;
    
    /** was the data modified */
    private boolean m_modified = false;

    /** track popup state */
    private IContentProposalListener2 m_popupListener;

    /**
     * Contructs a text field. When pressing STRG+SPACE a list pops up.
     * 
     * @param compMapper The Component Name mapper to use.
     * @param composite The parent composite.
     * @param style The style of the text field.
     */
    public CompNamePopUpTextField(IComponentNameMapper compMapper,
            Composite composite, int style) {
        
        super(composite, style);
        KeyStroke ks = null;
        try {
            ks = KeyStroke.getInstance("Ctrl+Space"); //$NON-NLS-1$
        } catch (ParseException e) {
            // ignore until you want to play with the constant above
        }
        m_labelProvider = new LabelProvider() {

            public Image getImage(Object element) {
                CompNamesProposal p = (CompNamesProposal)element;
                char type = p.getLabel().charAt(0);
                switch (type) {
                    case 'G': // GLOBAL
                        return IconConstants.GLOBAL_NAME_IMAGE;
                    case 'L': // LOCAL
                        return IconConstants.LOCAL_NAME_IMAGE;
                    case 'A': // AUT
                        return IconConstants.AUT_COMP_NAME_IMAGE;
                    default:
                        return null;
                }
            }

            public String getText(Object element) {
                CompNamesProposal p = (CompNamesProposal)element;
                return p.getLabel().substring(1);
            }

            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

        };
        m_popupListener = new IContentProposalListener2Implementation();
        m_contentProposalProvider = new CompNamesProposalProvider(compMapper);
        enableContentProposal(m_contentProposalProvider, 
                ks, createTriggerChars());
    }

    /**
     * used in properties view
     */
    public void activateSelectionEvent() {
        addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                m_modified = true;
            }
        });
        addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (!m_popupOpen) {
                    if (m_modified) {
                        fireSelectionEvent();
                    }
                }
                
            }
        });
    } 
    
    /**
     * fire a pseudo event to trigger callers
     */
    private void fireSelectionEvent() {
        getDisplay().syncExec(new Runnable() {

            public void run() {
                if (!CompNamePopUpTextField.this.isDisposed()) {
                    Event modifyEvent = new Event();
                    modifyEvent.widget = CompNamePopUpTextField.this;
                    modifyEvent.type = SWT.Modify;
                    CompNamePopUpTextField.this.notifyListeners(SWT.Modify,
                            modifyEvent);
                }
            }

        });

    }

    /**
     * Do the actual registration of the provider
     * @param contentProposalProvider provider for the content
     * @param keyStroke activation KeyStroke
     * @param autoActivationCharacters characters which will automatically
     * start the proposal
     */
    private void enableContentProposal(
            IContentProposalProvider contentProposalProvider,
            KeyStroke keyStroke, char[] autoActivationCharacters) {
        ContentProposalAdapter contentProposalAdapter = 
            new ContentProposalAdapter(this,
                new TextContentAdapter(), contentProposalProvider,
                keyStroke, autoActivationCharacters);
        contentProposalAdapter.setLabelProvider(m_labelProvider);

        contentProposalAdapter
                .addContentProposalListener(m_popupListener);
        contentProposalAdapter
                .setProposalAcceptanceStyle(
                        ContentProposalAdapter.PROPOSAL_REPLACE);
    }

    /**
     * @return the characters that should trigger a content proposal
     */
    private char[] createTriggerChars() {
        char[] trigger = new char[2 * 26 + 10 + 2];
        int index = 0;
        for (char c = 'a'; c <= 'z'; ++c) {
            trigger[index++] = c;
            trigger[index++] = Character.toUpperCase(c);
        }
        for (int i = 0; i < 10; ++i) {
            trigger[index++] = Character.forDigit(i, 10);
        }
        trigger[index++] = '_';
        trigger[index++] = '\b';
        return trigger;
    }

    /**
     * {@inheritDoc}
     */
    protected void checkSubclass() {
        // do nothing
    }
    
    /**
     * @param filter match against type from comp system
     */
    public void setFilter(String filter) {
        if (m_contentProposalProvider != null) {
            m_contentProposalProvider.setTypeFilter(filter);
        }
    }

    /**
     * @return the popupOpen
     */
    public boolean isPopupOpen() {
        return m_popupOpen;
    }
    
    /**
     * 
     * @param compMapper The Component Name mapper to use.
     */
    public void setComponentNameMapper(IComponentNameMapper compMapper) {
        if (m_contentProposalProvider != null) {
            m_contentProposalProvider.setComponentNameMapper(compMapper);
        }
    }
}