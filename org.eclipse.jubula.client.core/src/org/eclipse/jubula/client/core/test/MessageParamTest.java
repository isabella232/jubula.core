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

import org.eclipse.jubula.communication.message.MessageParam;

/**
 * @author BREDEX GmbH
 * @created 14.10.2004
 */
public class MessageParamTest extends TestCase {

    /**
     * 
     * @param args Arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MessageParamTest.class);
    }

    /**
     * Test method for creation a new MessageParameter
     *
     */
    public final void testMessageParam() {
        MessageParam messageParam = new MessageParam(
                //value
                "Hans", //$NON-NLS-1$
                //type
                "java.lang.String"); //$NON-NLS-1$
        assertEquals("invalid value", //$NON-NLS-1$
                "Hans", //$NON-NLS-1$
                messageParam.getValue()); 
        assertEquals("invalid type", //$NON-NLS-1$
                "java.lang.String", //$NON-NLS-1$
                messageParam.getType());
    }

}
