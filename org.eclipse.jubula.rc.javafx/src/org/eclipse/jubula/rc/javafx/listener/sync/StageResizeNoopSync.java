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
 * Performs no synchronization whatsoever. This concrete implementation can be
 * used for platforms that do not require synchronization.
 * 
 */
final class StageResizeNoopSync implements IStageResizeSync {

    @Override
    public void register(Stage stage) {
        // intentionally empty
    }

    @Override
    public void deregister(Stage stage) {
        // intentionally empty
    }

    @Override
    public void await() {
        // intentionally empty
    }

}
