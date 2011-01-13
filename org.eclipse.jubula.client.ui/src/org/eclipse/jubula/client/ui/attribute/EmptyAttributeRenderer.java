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
package org.eclipse.jubula.client.ui.attribute;

import org.eclipse.swt.widgets.Composite;

/**
 * This class renders absolutely nothing. It can be used as a placeholder if
 * a proper renderer cannot be found.
 *
 * @author BREDEX GmbH
 * @created 20.05.2008
 */
public final class EmptyAttributeRenderer extends AbstractAttributeRenderer {

    /**
     * {@inheritDoc}
     */
    public void renderAttribute(Composite parent) {
        // Render nothing
    }

}
