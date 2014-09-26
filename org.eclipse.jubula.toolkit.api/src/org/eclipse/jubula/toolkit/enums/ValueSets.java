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
    public enum Operator implements LiteralProvider {
        /** value comparison operator */
        equals("equals"), //$NON-NLS-1$
        /** value comparison operator */
        notEquals("not equals"), //$NON-NLS-1$
        /** value comparison operator */
        matches("matches"), //$NON-NLS-1$
        /** value comparison operator */
        simpleMatch("simple match"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Operator(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** @author BREDEX GmbH */
    public enum SearchType implements LiteralProvider {
        /** value comparison operator */
        relative("relative"), //$NON-NLS-1$
        /** value comparison operator */
        absolute("absolute"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private SearchType(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** Constructor */
    private ValueSets() {
        // hide
    }
}
