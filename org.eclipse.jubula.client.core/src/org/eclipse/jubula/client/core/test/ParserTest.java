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

/**
 * @author BREDEX GmbH
 * @created 17.08.2007
 */
public class ParserTest extends TestCase {

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @SuppressWarnings("nls")
    public void testParser() {
        String patters[] = {
            "''",
            "hallo",
            "=PARTREF_NOREF",
            "=PARTREF NOREF",
            "=PARTREF:NOREF",
            "hallo=REF",
            "hallo\\=REF",
            "hallo={REF}",
            "hallo\\=\\{REF\\}",
            "hallo={REF}hallo",
            "aa'={VV}'bb={XX}",            
            "aa\\'={VV}\\'bb={XX}",            

            "hallo$VAR",
            "hallo\\$VAR",
            "hallo${VAR}",
            "hallo\\=\\{VAR\\}",
            "hallo${VAR}hallo",
            "aa'={VV}'bb={XX}",            
            "aa\\'={VV}\\'bb={XX}",            
};

//        for (String value : patters) {
//            Parser p = new Parser(value);
//            System.out.println(p);          
//        }
    }

    @SuppressWarnings("nls")
    public void testParserFail() {
        String patters[] = {
            "hallo=",
            "hallo={REF",
            "hallo={REF hallo",
            "aa'={VV}bb={XX}",            
            "=:BAD",

            "hallo$",
            "hallo${VAR",
            "hallo${VAR hallo",
            "aa'${VV}bb${XX}",            
            "$:BAD",
        };
    }
}
