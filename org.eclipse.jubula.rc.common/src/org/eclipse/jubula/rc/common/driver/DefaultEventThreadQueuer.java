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

    /**
     * 
     * {@inheritDoc}
     */
    public Object invokeAndWait(String name, IRunnable runnable)
        throws StepExecutionException {

        return runnable.run();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void invokeLater(String name, Runnable runnable)
        throws StepExecutionException {

        new Thread(runnable, name).start();
    }

}
