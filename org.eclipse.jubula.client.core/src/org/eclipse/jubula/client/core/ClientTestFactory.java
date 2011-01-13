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
package org.eclipse.jubula.client.core;


/**
 * @author BREDEX GmbH
 * @created 21.07.2005
 */
public class ClientTestFactory {
    
    /** ClientTest instance */
    private static IClientTest clientTest;
    
    /** private constructor */
    private ClientTestFactory () {
        //
    }
    
    /**
     * 
     * @return  ClientTest instance
     */
    public static IClientTest getClientTest() {
        if (clientTest == null) {
            Class c = null;
            
            try { 
                c = Class.forName("org.eclipse.jubula.client.core.ClientTest", //$NON-NLS-1$
                    true, ClientTestFactory.class.getClassLoader());
            } catch (ClassNotFoundException e) {
                Runtime.getRuntime().exit(1);
            }
            
            try {
                clientTest = (IClientTest)c.newInstance();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return clientTest;
    }
}
