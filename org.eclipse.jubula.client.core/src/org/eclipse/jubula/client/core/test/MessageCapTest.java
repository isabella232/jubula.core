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
package org.eclipse.jubula.client.core.test;

import junit.framework.TestCase;

import org.eclipse.jubula.communication.message.MessageCap;
import org.eclipse.jubula.tools.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;


/**
 * 
 *
 * @author BREDEX GmbH
 * @created 14.10.2004
 *
 */
public class MessageCapTest extends TestCase {

    /**
     * 
     * @param args The arguments
     */
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MessageCapTest.class);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        IComponentIdentifier ci = new ComponentIdentifier();
        ci.setComponentClassName("javax.swing.JButton"); //$NON-NLS-1$
        
    }

    /**
     * The method to test
     *
     */
    public final void testMessageCap() {
        MessageCap messageCap = new MessageCap();
        messageCap.setMethod("guidancerClick"); //$NON-NLS-1$
        assertEquals("invalid method", //$NON-NLS-1$
                "guidancerClick", //$NON-NLS-1$
                messageCap.getMethod());
    }

}
