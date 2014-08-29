/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.qa.api;

import junit.framework.Assert;

import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.junit.Test;

/** @author BREDEX GmbH */
public class TestValueSets {
    /** the actual test method */
    @Test
    public void test() {
        Assert.assertEquals(Operator.equals.getValue(), "equals"); //$NON-NLS-1$
    }
}