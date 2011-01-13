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
package org.eclipse.jubula.client.ui.utils;

import org.eclipse.jubula.client.ui.Plugin;

/**
 * @author BREDEX GmbH
 * @created Nov 9, 2005
 */
public abstract class GDThread extends Thread {

    /**
     * Contructor
     */
    public GDThread() {
        addErrorHandler();
    }

    /**
     * @param target
     *      Runnable
     * @param name
     *      String
     */
    public GDThread(Runnable target, String name) {
        super(target, name);
        addErrorHandler();

    }

    /**
     * @param target
     *      Runnable
     */
    public GDThread(Runnable target) {
        super(target);
        addErrorHandler();
    }

    /**
     * @param name
     *      String
     */
    public GDThread(String name) {
        super(name);
        addErrorHandler();
    }

    /**
     * @param group
     *      ThreadGroup
     * @param target
     *      Runnable
     * @param name
     *      Runnable
     * @param stackSize
     *      long
     */
    public GDThread(ThreadGroup group, Runnable target, 
        String name, long stackSize) {
        super(group, target, name, stackSize);
        addErrorHandler();
    }

    /**
     * @param group
     * ThreadGroup
     * @param target
     * Runnable
     * @param name
     * String
     */
    public GDThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        addErrorHandler();
    }

    /**
     * @param group
     * ThreadGroup
     * @param target
     * String
     */
    public GDThread(ThreadGroup group, Runnable target) {
        super(group, target);
        addErrorHandler();
    }

    /**
     * 
     * @param group
     * ThreadGroup
     * @param name
     * String
     */
    public GDThread(ThreadGroup group, String name) {
        super(group, name);
        addErrorHandler();
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        try {
            super.start();
        } catch (RuntimeException e) {
            Plugin.getDefault().handleError(e);
            errorOccured();
        }
    }

    /**
     * do sth after an error occured.
     *
     */
    protected abstract void errorOccured();

    /**
     * adds a ErrorHandler
     *
     */
    private void addErrorHandler() {
        setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                Plugin.getDefault().handleError(e);
                errorOccured();
            }
        });
    }


}
