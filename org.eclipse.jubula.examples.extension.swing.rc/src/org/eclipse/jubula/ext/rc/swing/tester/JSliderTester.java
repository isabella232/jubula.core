/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.swing.tester;

import javax.swing.JSlider;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.WidgetTester;
import org.eclipse.jubula.rc.common.util.Verifier;

/**
 * Tester Class for the AUT-Agent. This class realizes the technical access to
 * provide testability for new component type: JSlider. By implementing the
 * class "WidgetTester" you have nothing to implement to enable testability of
 * your new component on the "Graphics Component"-level. That means all actions
 * which are available for the "Graphics Component" should work for your new
 * component.
 * 
 * @author BREDEX GmbH
 * 
 */
public class JSliderTester extends WidgetTester {
    /**
     * @return the casted slider instance
     */
    protected JSlider getSlider() {
        return (JSlider) getRealComponent();
    }

    /**
     * Verifies the whether the labels of the UI JSlider are to be shown
     * 
     * @param shown
     *            The shown status to verify.
     */
    public void rcVerifyLabelsExists(boolean shown) {
        final JSlider slider = getSlider();
        final Boolean labelsShown = getEventThreadQueuer().invokeAndWait(
                "doesPaintLabels", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() throws StepExecutionException {
                        return slider.getPaintLabels();
                    }
                });
        Verifier.equals(shown, labelsShown.booleanValue());
    }
}