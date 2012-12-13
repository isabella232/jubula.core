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
package org.eclipse.jubula.rc.swing.swing.caps;


import org.eclipse.jubula.rc.common.uiadapter.interfaces.IButtonAdapter;

/**
 * The implementation class for <code>AbstractButton</code> and subclasses. Note
 * the "Abstract" in the class name implies only that this class can test
 * <code>AbstractButton</code> components. The class itself should <em>NOT</em>
 * be designated <code>abstract</code>, as this class is instantiated using
 * reflection.
 * 
 * @author BREDEX GmbH
 */
public class AbstractButtonCAPs 
    extends org.eclipse.jubula.rc.common.caps.AbstractButtonCAPs {

    /**
     * Clicks the button <code>count</code> times.
     * 
     * @param count The number of clicks
     */
    public void gdClick(int count) {
        gdClick(count, 1);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        IButtonAdapter returnvalue = ((IButtonAdapter)getComponent());
        return new String[] { returnvalue.getText() };
    }

}