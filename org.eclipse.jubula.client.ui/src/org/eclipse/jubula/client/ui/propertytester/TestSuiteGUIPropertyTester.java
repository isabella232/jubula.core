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
package org.eclipse.jubula.client.ui.propertytester;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.osgi.util.NLS;


/**
 * Property tester for Test Suite GUI Nodes.
 *
 * @author BREDEX GmbH
 * @created 28.04.2009
 */
public class TestSuiteGUIPropertyTester extends PropertyTester {

    /** the id of the "isInCurrentProject" property */
    public static final String HAS_AUT = "hasAUT"; //$NON-NLS-1$

    /** the logger */
    private static final Log LOG = 
        LogFactory.getLog(TestSuiteGUIPropertyTester.class);
    
    /**
     * {@inheritDoc}
     */
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        
        if (receiver instanceof ITestSuitePO) {
            ITestSuitePO testSuite = (ITestSuitePO)receiver;
            if (property.equals(HAS_AUT)) {
                
                boolean hasAUT = testSuite.getAut() != null ? true : false;
                boolean expectedBoolean = expectedValue instanceof Boolean 
                    ? ((Boolean)expectedValue).booleanValue() : true;
                return hasAUT == expectedBoolean;
            }

            LOG.warn(NLS.bind(Messages.PropertyTesterPropertyNotSupported,
                    new String [] {property}));
            return false;
        }

        String receiverClass = 
            receiver != null ? receiver.getClass().getName() : "null"; //$NON-NLS-1$
        LOG.warn(NLS.bind(Messages.PropertyTesterTypeNotSupported,
                new String [] {receiverClass}));
        return false;
    }
}
