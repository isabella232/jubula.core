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
package org.eclipse.jubula.examples.extension.swing.aut;

import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JSlider;

/**
 * Example AUT for Jubula Extension Mechanism
 * 
 * @author BREDEX GmbH
 */
public class JSliderExampleAUT extends JFrame {
    /** version id */
    private static final long serialVersionUID = 1L;

    /** constructor */
    public JSliderExampleAUT() {
        super("JSlider Example AUT"); //$NON-NLS-1$
        JSlider slider = new JSlider();
        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(slider);
    }

    /**
     * main method
     * 
     * @param args
     *            cmdline arguments
     */
    public static void main(String args[]) {
        JSliderExampleAUT f = new JSliderExampleAUT();
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setSize(300, 80);
        f.setVisible(true);
    }
}