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
package org.eclipse.jubula.client.ui.controllers;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.widgets.CompNamePopUpTextField;
import org.eclipse.jubula.client.ui.widgets.JBText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created 04.08.2005
 */
public class CompNamePopupTextFieldToolTipListener implements Listener  {

    /** The shell, the parent of the tool tip */
    private static final  Shell SHELL = Plugin.getShell();
    
    /** the tool tip */
    private Shell m_toolTipShell;

    /** the label */
    private JBText m_toolTipTextField;
    
    /**
     * {@inheritDoc}
     * creates the popup
     */
    public void handleEvent(Event event) {
        m_toolTipTextField = null;
        final Listener labelListener = new Listener() {
            public void handleEvent(Event e) {
                switch (e.type) {
                    case SWT.MouseDown:
                    case SWT.MouseExit:
                        if (e.widget instanceof Label) {
                            ((Label) e.widget).getShell().dispose();
                            m_toolTipShell = null;
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        if ((SWT.MouseExit == event.type || SWT.Dispose == event.type)
            && m_toolTipShell != null) {

            disposeToolTip();
        }
        if (SWT.MouseHover == event.type) {
            if (m_toolTipShell != null) {
                disposeToolTip();
            }
            CompNamePopUpTextField textField = 
                (CompNamePopUpTextField)event.widget;
            GC gc = new GC(textField);
            int textPixels = gc.textExtent(textField.getText() + "  ").x; //$NON-NLS-1$
            gc.dispose();
            int textFieldPixels = textField.getBounds().width;
            if (textPixels <= textFieldPixels) {
                return;
            }
            m_toolTipShell = new Shell(SHELL, SWT.ON_TOP | SWT.TOOL);
            if (textField.getText() != null 
                    && textField.getText().length() > 0) {
                m_toolTipShell.setLayout(new FillLayout());
                m_toolTipTextField = new JBText(
                        m_toolTipShell, Layout.MULTI_TEXT);
                m_toolTipTextField.setEnabled(false);
                Display display = Plugin.getDisplay();
                m_toolTipTextField.setForeground(Layout.DEFAULT_OS_COLOR);
                m_toolTipTextField.setBackground(
                    display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                m_toolTipTextField.setText(textField.getText());
                m_toolTipTextField.addListener(SWT.MouseExit, labelListener);
                m_toolTipTextField.addListener(SWT.MouseDown, labelListener);
                Rectangle textFieldBounds = textField.getBounds();
                Rectangle parentBounds = textField.getParent().getBounds();
                Point ptRelTF = textField.toDisplay(
                    parentBounds.x, parentBounds.y);
                int x = ptRelTF.x;
                int y = ptRelTF.y + textFieldBounds.height;
                int height = Dialog.convertHeightInCharsToPixels(
                    Layout.getFontMetrics(m_toolTipTextField), 
                    m_toolTipTextField.getLineCount() + 1);
                m_toolTipShell.setBounds(x, y, textFieldBounds.width, height);
                m_toolTipShell.setVisible(true);
            }
        }
    }
    
    
    /**
     * Disposes the ToolTip
     */
    private void disposeToolTip() {
        m_toolTipShell.dispose();
        m_toolTipTextField = null;
    }    
}