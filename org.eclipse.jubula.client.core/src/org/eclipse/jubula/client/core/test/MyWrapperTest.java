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

/**
 * @author BREDEX GmbH
 * @created 01.11.2007
 */
public class MyWrapperTest {

    /**
     * @param args
     */
    @SuppressWarnings("nls")
    public static void main(String[] args) {
        System.out.println("Integer.valueOf(int)");
        System.out.println("wandelt Primitiv --> passenden Wrapper-Objekt");
        System.out.println("Integer i = Integer.valueOf(25);");
        Integer i = Integer.valueOf(25);
        System.out.println("i = " + i + "\n" );
        System.out.println("------------------------------------------------");
        //
        System.out.println("Integer.valueOf(String)");
        System.out.println("wandelt String --> passenden Wrapper-Objekt");
        i = Integer.valueOf("25");
        System.out.println("Integer i = Integer.valueOf(\"25\");");
        System.out.println("i = " + i);
        System.out.println("------------------------------------------------");
    }

}
