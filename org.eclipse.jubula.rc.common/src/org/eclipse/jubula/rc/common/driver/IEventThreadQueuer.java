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
package org.eclipse.jubula.rc.common.driver;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * Executes an {@link org.eclipse.jubula.rc.common.driver.IRunnable} in the
 * Graphics API specific event queue. All implmentation classes which
 * access AWT/Swing components require this mechanismn, as the
 * AWT/Swing components are not thread-safe. The progamming model in an
 * implmentation class is as follows:
 * 
 * <pre>
 * IRobotFactory factory = new RobotFactoryConfig().getRobotFactory();
 * IEventThreadQueuer queuer = factory.getEventThreadQueuer();
 * queuer.invokeAndWait(threadName, new IRunnable() {
 *     public Object run() {
 *         //...
 *         return result;
 *     }
 * });
 * </pre>
 *
 * @author BREDEX GmbH
 * @created 05.04.2005
 */
public interface IEventThreadQueuer {
    /**
     * Invokes the <code>runnable</code> in the Graphics API specific event
     * queue and blocks until termination of <code>runnable</code>.
     * 
     * @param name
     *            The name of this invocation.
     * @param runnable
     *            The runnable.
     * @return The result returned by the runnable, maybe <code>null</code>.
     * @throws StepExecutionException
     *             If the invocation fails or if the runnable throws a
     *             <code>StepExecutionException</code>.
     */
    public Object invokeAndWait(String name, IRunnable runnable)
        throws StepExecutionException;
    
    
    /**
     * Invokes the <code>runnable</code> in the Graphics API specific event
     * queue asynchronous.
     * 
     * @param name
     *            The name of this invocation.
     * @param runnable
     *            The runnable.
     * @return The result returned by the runnable, maybe <code>null</code>.
     * @throws StepExecutionException
     *             If the invocation fails or if the runnable throws a
     *             <code>StepExecutionException</code>.
     */
    public Object invokeLater(String name, IRunnable runnable)
        throws StepExecutionException;
}
