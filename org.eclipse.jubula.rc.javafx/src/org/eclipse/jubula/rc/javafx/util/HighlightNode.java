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
package org.eclipse.jubula.rc.javafx.util;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

/**
 * Util class to highlight nodes
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 */
public class HighlightNode {

    /** The old effect of the Node **/
    private static Map<Node, Effect> oldEffects = new HashMap<>();

    /**
     * private Constructor
     */
    private HighlightNode() {
        // private Constructor
    }

    /**
     * Use this method only from the FX-Thread! Draws a border around the given
     * Node
     *
     * @param n
     *            the Node
     */
    public static void drawHighlight(Node n) {
        // If the effect property is bound to another property it is not allowed
        // to change it.
        if (n.effectProperty().isBound()) {
            return;
        }
        if (n.getEffect() != null) {
            oldEffects.put(n, n.getEffect());
        }
        n.setEffect(new InnerShadow(10, Color.GREEN));
    }

    /**
     * Use this method only from the FX-Thread! Removes the Border
     *
     * @param n
     *            the Node
     */
    public static void removeHighlight(Node n) {
        if (n.effectProperty().isBound()) {
            return;
        }
        n.setEffect(oldEffects.remove(n));
    }
}
