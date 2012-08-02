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
package org.eclipse.jubula.rc.swing.swing.driver;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RunnableWrapper;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * This class executes an <code>IRunnable</code> instance in the AWT event
 * thread.
 * 
 * @author BREDEX GmbH
 * @created 05.04.2005
 */
public class EventThreadQueuerAwtImpl implements IEventThreadQueuer {
    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(EventThreadQueuerAwtImpl.class);

    /** {@inheritDoc} */
    public Object invokeAndWait(String name, IRunnable runnable)
        throws StepExecutionException {
        return invoke(name, runnable, true);
    }
    
    /** {@inheritDoc} */
    public Object invokeLater(String name, IRunnable runnable) 
        throws StepExecutionException {
        return invoke(name, runnable, false);
    }
    
    /**
     * Invokes the <code>runnable</code> in the Graphics API specific event
     * queue asynchronous.
     * 
     * @param name
     *            The name of this invocation.
     * @param runnable
     *            The runnable.
     * @param now
     *            whether it should be invoked 
     *            now   (==true && inSync) or 
     *            later (==false && aSync)
     * @return The result returned by the runnable, maybe <code>null</code>.
     * @throws StepExecutionException
     *             If the invocation fails or if the runnable throws a
     *             <code>StepExecutionException</code>.
     */
    private Object invoke(String name, IRunnable runnable, boolean now)
        throws StepExecutionException {
        Validate.notNull(runnable, "runnable must not be null"); //$NON-NLS-1$
        
        RunnableWrapper wrapper = new RunnableWrapper(name, runnable);
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                wrapper.run();
            } else {
                if (now) {
                    SwingUtilities.invokeAndWait(wrapper);
                } else {
                    SwingUtilities.invokeLater(wrapper);
                }
            }
            
            StepExecutionException exception = wrapper.getException();
            if (exception != null) {
                throw new InvocationTargetException(exception);
            }
        } catch (InterruptedException ie) {
            // this (the waiting) thread was interrupted -> error
            log.error(ie);
            throw new StepExecutionException(ie);
        } catch (InvocationTargetException ite) {
            // the run() method from IRunnable has thrown an exception
            // -> log on info
            // -> throw a StepExecutionException
            Throwable thrown = ite.getTargetException();
            if (thrown instanceof StepExecutionException) {
                if (log.isInfoEnabled()) {
                    log.info(ite);
                }
                throw (StepExecutionException)thrown;
            } 
            
            // any other (unchecked) Exception from IRunnable.run()
            log.error("exception thrown by '" + wrapper.getName() //$NON-NLS-1$
                + "':", thrown); //$NON-NLS-1$
            throw new StepExecutionException(thrown);
        }
        
        return wrapper.getResult();
    }
}