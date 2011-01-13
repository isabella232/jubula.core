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
package org.eclipse.jubula.client.core.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author BREDEX GmbH
 * @created Nov 17, 2006
 */
public class ProgressEventDispatcher {
    
    /**
     * gui listener for showing connection window
     */
    private static transient Set<IProgressListener> listener;

    /**
     * Private utility constructor.
     */
    private ProgressEventDispatcher() {
        // do nothing
    }
    
    /**
     * Adds a new progress listener to progress listener list.
     * @param l The progressListener to add.
     */
    public static void addProgressListener(IProgressListener l) {
        getListeners().add(l);
    }

    /**
     * Removes a new progress listener from progress listener list.
     * @param l The progressListener to remove.
     */
    public static void removeProgressListener(IProgressListener l) {
        getListeners().remove(l);
    }

    /**
     * Notifies all progressListeners of the progress listener list and calls <code>progressListener.reactOnProgressEvent(ProgressEvent e)</code>
     * @param e the progressEvent
     */
    public static void notifyListener(ProgressEvent e) {
        Iterator iter = getListeners().iterator();
        while (iter.hasNext()) {
            IProgressListener progressListener = (IProgressListener)iter
                .next();
            if (progressListener != null) {
                progressListener.reactOnProgressEvent(e);
            } else {
                if (listener != null) {
                    listener.remove(progressListener);
                }
            }
        }
    }

    /**
     * @return Set<IProgressListener> 
     */
    private static Set<IProgressListener> getListeners() {
        if (listener == null) {
            listener = new HashSet<IProgressListener>();
        }
        return listener;
    }
}