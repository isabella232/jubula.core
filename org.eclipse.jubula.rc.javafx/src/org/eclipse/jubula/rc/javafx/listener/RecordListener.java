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

import org.eclipse.jubula.rc.common.listener.AUTEventListener;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

/**
 * Not implemented!
 *
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public class RecordListener implements AUTEventListener {

    @Override
    public long[] getEventMask() {

        return null;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void update() {

    }

    @Override
    public boolean highlightComponent(IComponentIdentifier comp) {

        return false;
    }

}
