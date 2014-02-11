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
package org.eclipse.jubula.rc.javafx.listener;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.stage.Stage;

import org.eclipse.jubula.rc.common.listener.AUTEventListener;
import org.eclipse.jubula.rc.javafx.util.HighlightNode;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;

/**
 * @author BREDEX GmbH
 * @created 15.10.2013
 */
public abstract class AbstractFXAUTEventHandler implements AUTEventListener {

    /** The current Node **/
    private Node m_currentNode;

    /**
     * Sets the current <code>Node</code> that will be, or is, highlighted
     *
     * @param n
     *            the <code>Node</code> to be highlighted
     */
    public void setCurrentNode(Node n) {
        if (m_currentNode != null) {
            lowlightCurrentNode();
        }
        m_currentNode = n;
    }

    /**
     * Returns the current <code>Node</code> that will be, or is, highlighted
     *
     * @return the currentNode
     */
    public Node getCurrentNode() {
        return m_currentNode;
    }

    /**
     * Highlights the current Node
     */
    public void highlightCurrentNode() {
        if (m_currentNode != null) {
            HighlightNode.drawHighlight(m_currentNode);
        }
    }

    /**
     * Lowlights the current Node
     */
    public void lowlightCurrentNode() {
        if (m_currentNode != null) {
            HighlightNode.removeHighlight(m_currentNode);
        }
    }

    /**
     * Adds a <code>MouseHandler</code> to the given stage
     *
     * @param s
     *            the Stage
     */
    public abstract void addHandler(Stage s);

    /**
     * Removes a <code>MouseHandler</code> from the given stage
     *
     * @param s
     *            the Stage
     */
    public abstract void removeHandler(Stage s);

    @Override
    public void cleanUp() {
        HighlightNode.clean();
    }

    @Override
    public void update() {

    }

    @Override
    public boolean highlightComponent(IComponentIdentifier comp) {
        Node n = ComponentHandler.findNodeByID(comp);
        if (n != null) {
            setCurrentNode(n);
            // Highlight only in JAVAFX Thread
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    highlightCurrentNode();
                }
            });

            return true;
        }
        return false;
    }

    @Override
    public long[] getEventMask() {

        return null;
    }
}
