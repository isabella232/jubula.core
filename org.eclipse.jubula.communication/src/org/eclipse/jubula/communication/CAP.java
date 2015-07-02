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
package org.eclipse.jubula.communication;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

/**
 * CAPs are executed remotely on the {@link org.eclipse.jubula.client.AUT AUT}<br>
 * <b>C</b>: {@link org.eclipse.jubula.tools.ComponentIdentifier Component} to address <br>
 * <b>A</b>: Action to perform<br>
 * <b>P</b>: Parameter to use <br>
 * 
 * @author BREDEX GmbH
 * @created 13.10.2014
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CAP {
    /**
     * @return the component identifier used for this CAP; might be
     *         <code>null</code> e.g. for CAPs without a component identifier
     *         mapping
     */
    ComponentIdentifier getComponentIdentifier();
    
    /**
     * Class following builder pattern to create CAP instances
     * @author BREDEX GmbH
     */
    public static class Builder {
        
        /** Identifier for Boolean parameters */
        private static final String BOOLEAN_IDENTIFIER = Boolean.class
                .getName();

        /** Identifier for Integer parameters */
        private static final String INTEGER_IDENTIFIER = Integer.class
                .getName();

        /** Identifier for String parameters */
        private static final String STRING_IDENTIFIER = String.class.getName();

        /** the name of the method */
        private String m_rcMethod;
        
        /** the component identifier */
        private IComponentIdentifier m_ci = null;
        
        /** the parameters of the CAP */
        private List<MessageParam> m_params =
                new ArrayList<MessageParam>();

        /** whether component has default mapping */
        private boolean m_defaultMapping = true;
        
        /**
         * Builder for creating CAPs
         * @param rcMethod name of the rc method of the CAP
         */
        public Builder(String rcMethod) {
            m_rcMethod = rcMethod;
        }
        
        /**
         * @param value the value of the string parameter
         * @return the modified builder
         */
        public Builder addParameter(String value) {
            m_params.add(new MessageParam(String.valueOf(value),
                    STRING_IDENTIFIER));
            return this;
        }
        
        /**
         * @param value the value of the integer parameter
         * @return the modified builder
         */
        public Builder addParameter(Integer value) {
            m_params.add(new MessageParam(String.valueOf(value),
                    INTEGER_IDENTIFIER));
            return this;
        }
        
        /**
         * @param value the value of the boolean parameter
         * @return the modified builder
         */
        public Builder addParameter(Boolean value) {
            m_params.add(new MessageParam(String.valueOf(value),
                    BOOLEAN_IDENTIFIER));
            return this;
        }
        
        /**
         * Sets the component identifier.
         * @param ci the component identifier
         * @return the modified builder
         */
        public Builder setComponentIdentifier(IComponentIdentifier ci) {
            m_ci = ci;
            return this;
        }
        
        /**
         * Sets whether the component has a default mapping or not
         * (Default value is <code>true</code>).
         * @param defaultMapping whether the component has default mapping
         * @return the modified builder
         */
        public Builder setDefaultMapping(Boolean defaultMapping) {
            m_defaultMapping = defaultMapping;
            return this;
        }
        
        /**
         * build CAP instance
         * @return CAP instance
         */
        public CAP build() {
            MessageCap messageCap = new MessageCap();
            messageCap.setMethod(m_rcMethod);
            messageCap.sethasDefaultMapping(m_defaultMapping);
            messageCap.setCi(m_ci);
            for (MessageParam param : m_params) {
                messageCap.addMessageParam(param);
            }
            return messageCap;
        }

    }
}
