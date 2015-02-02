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
 * 
 * Default implementation of {@link IEventThreadQueuer}. Has no association 
 * with any graphics toolkit. Executes operations on either the current thread 
 * (sync) or a newly created thread (async).
 *  
 */
public class DefaultEventThreadQueuer implements IEventThreadQueuer {
    /** {@inheritDoc} */
    public <V> V invokeAndWait(String name, IRunnable<V> runnable)
        throws StepExecutionException {

        return runnable.run();
    }

    /** {@inheritDoc} */
    public void invokeLater(String name, Runnable runnable)
        throws StepExecutionException {

        new Thread(runnable, name).start();
    }
}