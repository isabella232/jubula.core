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
package org.eclipse.jubula.toolkit.enums;

/** @author BREDEX GmbH */
public final class ValueSets {

    /** @author BREDEX GmbH */
    public enum Operator {
        /** value comparison operator */
        equals("equals"), //$NON-NLS-1$
        /** value comparison operator */
        notEquals("not equals"), //$NON-NLS-1$
        /** value comparison operator */
        matches("matches"), //$NON-NLS-1$
        /** value comparison operator */
        simpleMatch("simple match"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_value;

        /**
         * Constructor
         * 
         * @param value
         *            the value
         */
        private Operator(String value) {
            this.m_value = value;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return m_value;
        }
    }

    /** Constructor */
    private ValueSets() {
        // hide
    }
}
