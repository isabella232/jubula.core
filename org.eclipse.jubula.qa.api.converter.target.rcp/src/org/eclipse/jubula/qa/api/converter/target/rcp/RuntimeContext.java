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
package org.eclipse.jubula.qa.api.converter.target.rcp;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.client.exceptions.ExecutionExceptionHandler;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @created 19.11.2014
 */
public class RuntimeContext {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(RuntimeContext.class);
    
    private static class CheckFailedExecutionHandler 
        implements ExecutionExceptionHandler {
        /** nesting level counter */
        private Stack<Boolean> m_stack = new Stack<Boolean>();

        /**
         * @param defaultHandling
         *            whether to suppress CheckFailedExceptions by default
         */
        public CheckFailedExecutionHandler(Boolean defaultHandling) {
            getStack().push(defaultHandling);
        }    
        
        /** special handling supports ignoring of check failed exceptions */
        public void handle(ExecutionException arg0) throws ExecutionException {
            if ((arg0 instanceof CheckFailedException) && getStack().peek()) {
                return;
            }
            throw arg0;
        }

        /**
         * @return the stack
         */
        public Stack<Boolean> getStack() {
            return m_stack;
        }
    }
    
    /** the AUT */
    private AUT m_aut;
    
    /** the object map to use */
    private ObjectMapping om;

    /** the event handler for this runtime context */
    private CheckFailedExecutionHandler m_eventHandler;

    /**
     * @param aut
     *            the AUT
     * @param suppressCheckFailedDefault
     *            whether to suppress CheckFailedExceptions by default
     */
    public RuntimeContext(AUT aut, boolean suppressCheckFailedDefault) {
        setAUT(aut);
        m_eventHandler = new CheckFailedExecutionHandler(
                suppressCheckFailedDefault);
        aut.setHandler(m_eventHandler);
        
        // load object mapping - hint: feel free to adjust
        URL resource = RuntimeContext.class.getClassLoader().getResource(
                "om.properties"); //$NON-NLS-1$
        try {
            om = MakeR.createObjectMapping(resource.openStream());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * @return the AUT
     */
    public AUT getAUT() {
        return m_aut;
    }

    /**
     * @param aut the AUT to set
     */
    private void setAUT(AUT aut) {
        m_aut = aut;
    }

    /**
     * Gets a component identifier for a given logical component name 
     * from the object mapping for the AUT
     * @param name the logical component name
     * @return the component identifier
     */
    public ComponentIdentifier getIdentifier(String name) {
        return om.get(name);
    }
    
    /**
     * Begins local ignoring of
     * {@link org.eclipse.jubula.client.exceptions.CheckFailedException}.
     * 
     * Call {@link org.eclipse.jubula.qa.api.converter.target.rcp.RuntimeContext.endLocalEventHandling()}
     * to end the scope.
     */
    public void beginIgnoreCheckFailed() {
        getEventStack().push(true);
    }

    /**
     * @return the current event stack
     */
    private Stack<Boolean> getEventStack() {
        return m_eventHandler.getStack();
    }

    /**
     * Ends the scope of local event handling and restores previous state.
     */
    public void endLocalEventHandling() {
        getEventStack().pop();
    }
    
    /**
     * Begins local respecting of
     * {@link org.eclipse.jubula.client.exceptions.CheckFailedException}.
     * 
     * Call {@link org.eclipse.jubula.qa.api.converter.target.rcp.RuntimeContext.endLocalEventHandling()}
     * to end the scope.
     */
    public void doNotIgnoreCheckFailed() {
        getEventStack().push(false);
    }
}