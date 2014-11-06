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
    public enum AUTActivationMethod implements LiteralProvider {
        /** aut activation method */
        autDefault("AUT_DEFAULT"), //$NON-NLS-1$
        /** aut activation method */
        none("NONE"), //$NON-NLS-1$
        /** aut activation method */
        titlebar("TITLEBAR"), //$NON-NLS-1$
        /** aut activation method */
        northwest("NW"), //$NON-NLS-1$
        /** aut activation method */
        northeast("NE"), //$NON-NLS-1$
        /** aut activation method */
        southwest("SW"), //$NON-NLS-1$
        /** aut activation method */
        southeast("SE"), //$NON-NLS-1$
        /** aut activation method */
        center("CENTER"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private AUTActivationMethod(String rcValue) {
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
    public enum BinaryChoice implements LiteralProvider {
        /** binary choice option */
        yes("yes"), //$NON-NLS-1$
        /** binary choice option */
        no("no"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private BinaryChoice(String rcValue) {
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
    public enum Direction implements LiteralProvider {
        /** direction value */
        up("up"), //$NON-NLS-1$
        /** direction value */
        down("down"), //$NON-NLS-1$
        /** direction value */
        left("left"), //$NON-NLS-1$
        /** direction value */
        right("right"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Direction(String rcValue) {
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
    public enum KeyStroke implements LiteralProvider {
        /** key stroke */
        delete("DELETE"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private KeyStroke(String rcValue) {
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
    public enum Modifier implements LiteralProvider {
        /** modifier value */
        none("none"), //$NON-NLS-1$
        /** modifier value */
        shift("shift"), //$NON-NLS-1$
        /** modifier value */
        control("control"), //$NON-NLS-1$
        /** modifier value */
        alt("alt"), //$NON-NLS-1$
        /** modifier value */
        meta("meta"), //$NON-NLS-1$
        /** modifier value */
        cmd("cmd"), //$NON-NLS-1$
        /** modifier value */
        mod("mod"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Modifier(String rcValue) {
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
        /** search type value */
        relative("relative"), //$NON-NLS-1$
        /** search type value */
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
    
    /** @author BREDEX GmbH */
    public enum TreeDirection implements LiteralProvider {
        /** direction value */
        up("up"), //$NON-NLS-1$
        /** direction value */
        down("down"), //$NON-NLS-1$
        /** direction value */
        next("next"), //$NON-NLS-1$
        /** direction value */
        previous("previous"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private TreeDirection(String rcValue) {
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
    public enum Unit implements LiteralProvider {
        /** unit value */
        pixel("Pixel"), //$NON-NLS-1$
        /** unit value */
        percent("Percent"); //$NON-NLS-1$

        /** holds the value necessary for the RC side */
        private final String m_rcValue;

        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private Unit(String rcValue) {
            this.m_rcValue = rcValue;
        }

        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue;
        }
    }
    
    /** @author BREDEX GmbH 
     * The InteractionMode is e.g. used to define which mouse button is pressed.
     */
    public enum InteractionMode implements LiteralProvider {
        /** primary value*/
        primary(1),
        /** tertiary value*/
        tertiary(2),
        /** secondary value*/
        secondary(3);
        
        /** holds the value necessary for the RC side */
        private final Integer m_rcValue;
        
        /**
         * Constructor
         * 
         * @param rcValue
         *            the remote control side value
         */
        private InteractionMode(Integer rcValue) {
            m_rcValue = rcValue;
        }
        
        /**
         * @return the value
         */
        public String rcValue() {
            return m_rcValue.toString();
        }
        
        /**
         * @return the real value with correct type
         */
        public Integer rcRealValue() {
            return m_rcValue;
        }
    }
    /** Constructor */
    private ValueSets() {
        // hide
    }
}
