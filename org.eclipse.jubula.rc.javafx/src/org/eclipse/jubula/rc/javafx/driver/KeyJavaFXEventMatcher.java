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
package org.eclipse.jubula.rc.javafx.driver;

import java.util.List;

import javafx.event.EventType;
import javafx.scene.input.KeyEvent;

/**
 * Event matcher for key events.
 *
 * @author BREDEX GmbH
 * @created 1.11.2013
 */
public class KeyJavaFXEventMatcher extends DefaultJavaFXEventMatcher<KeyEvent> {

    /**
     * Creates a new matcher
     *
     * @param keyEvent
     *            the key event type that will be checked
     */
    public KeyJavaFXEventMatcher(EventType<KeyEvent> keyEvent) {
        super(keyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects,
            Object graphicsComponent) {
        return false;
    }
}