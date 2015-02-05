/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.components;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.eclipse.jubula.rc.common.components.FindComponentBP;

/**
 * @author BREDEX GmbH
 * @created 10.10.2013
 */
public class FindJavaFXComponentBP extends FindComponentBP {
    /** {@inheritDoc} */
    public String getCompName(Object currComp) {
        if (currComp instanceof Scene) {
            return null;
        } else if (currComp instanceof Stage) {
            return null;
        } else if (currComp instanceof Node) {
            return ((Node) currComp).getId();
        } else {
            return ((MenuItem) currComp).getId();
        }
    }

    /** {@inheritDoc} */
    public boolean isAvailable(Object currComp) {
        if (currComp instanceof Scene) {
            // null check, because it can happen that
            // a scene instance is found but the
            // Window property isn't set.
            // And therefore the scene isn't available
            Window w = ((Scene) currComp).getWindow();
            return w != null && w.isShowing();
        } else if (currComp instanceof Stage) {
            return ((Stage) currComp).isShowing();
        } else if (currComp instanceof Node) {
            Node currNode = (Node) currComp;
            EventTarget parent = ParentGetter.get(currNode);
            return currNode.isVisible() && parent != null
                    && isAvailable(parent);
        } else {
            return ((MenuItem) currComp).isVisible();
        }
    }
}
