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

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jubula.client.core.businessprocess.RunningAutBP;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Property Tester for AUT Identifiers (Running AUTs).
 *
 * @author BREDEX GmbH
 * @created May 10, 2010
 */
public class RunningAutPropertyTester extends PropertyTester {

    /** 
     * ID of the "isDefined" property, which describes whether an AUT Definition
     * for the given AUT Identifier exists in the current project. 
     */
    public static final String IS_DEFINED = "isDefined"; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(RunningAutPropertyTester.class);

    /**
     * {@inheritDoc}
     */
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {

        if (receiver instanceof AutIdentifier) {
            AutIdentifier autId = (AutIdentifier)receiver;
            if (property.equals(IS_DEFINED)) {
                boolean expectedBoolean = expectedValue instanceof Boolean 
                    ? ((Boolean)expectedValue).booleanValue() : true;
                boolean isDefined = RunningAutBP.isAutDefined(autId);
                return isDefined == expectedBoolean;
            }

            LOG.warn(I18n.getString("PropertyTester.PropertyNotSupported", //$NON-NLS-1$
                    new String[] { property }));
            return false;
        }

        String receiverClass = receiver != null ? receiver.getClass().getName()
                : "null"; //$NON-NLS-1$
        LOG.warn(I18n.getString("PropertyTester.TypeNotSupported", //$NON-NLS-1$
                new String[] { receiverClass }));
        return false;
    }

}
