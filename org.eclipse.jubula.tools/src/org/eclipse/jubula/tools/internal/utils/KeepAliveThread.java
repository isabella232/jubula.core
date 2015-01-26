/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.tools.internal.utils;

/**
 * Marker class to prevent JVM shutdown
 * 
 * @author BREDEX GmbH
 * @see http://eclip.se/457600#c8
 */
public class KeepAliveThread extends Thread {
    /**
     * Default
     */
    public KeepAliveThread() {
        super();
    }

    /**
     * @param target
     *            the target
     * @param name
     *            the name
     */
    public KeepAliveThread(Runnable target, String name) {
        super(target, name);
    }

    /**
     * @param target
     *            the target
     */
    public KeepAliveThread(Runnable target) {
        super(target);
    }

    /**
     * @param name
     *            the name
     */
    public KeepAliveThread(String name) {
        super(name);
    }

    /**
     * @param group
     *            the group
     * @param target
     *            the target
     * @param name
     *            the name
     * @param stackSize
     *            the stackSize
     */
    public KeepAliveThread(ThreadGroup group, Runnable target, String name,
            long stackSize) {
        super(group, target, name, stackSize);
    }

    /**
     * @param group
     *            the group
     * @param target
     *            the target
     * @param name
     *            the name
     */
    public KeepAliveThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    /**
     * @param group
     *            the group
     * @param target
     *            the target
     */
    public KeepAliveThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    /**
     * @param group
     *            the group
     * @param name
     *            the name
     */
    public KeepAliveThread(ThreadGroup group, String name) {
        super(group, name);
    }
}