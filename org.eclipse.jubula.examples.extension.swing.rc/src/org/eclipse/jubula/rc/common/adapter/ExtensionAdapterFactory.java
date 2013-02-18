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
package org.eclipse.jubula.rc.common.adapter;

import javax.swing.JSlider;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.swing.tester.JSliderAdapter;
/**
 * Adapterfactory for new adapters. 
 * This class makes your new adapters usable for testing.
 * One factory could be used for all adapters implemented.
 * @author BREDEX GmbH
 *
 */
public class ExtensionAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    public Class[] getSupportedClasses() {
        return new Class[] { JSlider.class };
    }

    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class targeted, Object objectToAdapt) {
        if (objectToAdapt instanceof JSlider) {
            return new JSliderAdapter(objectToAdapt);
        }
        return null;
    }

}
