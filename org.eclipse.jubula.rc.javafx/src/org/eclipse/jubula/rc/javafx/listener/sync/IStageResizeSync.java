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
package org.eclipse.jubula.rc.javafx.listener.sync;

import javafx.stage.Stage;

/**
 * Allows synchronization on a Stage that will likely be soon resized by the 
 * system (shown, maximized, or restored Stages). The goal of this class is to
 * prevent fetching incorrect component bounds while a Stage is being resized.
 * 
 * @see <a href="http://bugzilla.bredex.de/1393">GUIdancer Bug 1393</a>
 */
public interface IStageResizeSync {

    /**
     * Registers <code>stage</code> with the receiver.
     * 
     * @param stage the Stage to register.
     */
    public void register(Stage stage);

    /**
     * Deregisters <code>stage</code> with the receiver.
     * 
     * @param stage the Stage to deregister.
     */
    public void deregister(Stage stage);
    
    /**
     * Blocks the calling thread until the Stage has been sufficiently resized
     * to deliver reliable component bounds.
     */
    public void await();
}
