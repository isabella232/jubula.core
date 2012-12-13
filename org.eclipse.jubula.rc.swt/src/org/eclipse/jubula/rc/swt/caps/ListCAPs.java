/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.caps;

import org.eclipse.jubula.rc.common.caps.AbstractListCAPs;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class ListCAPs extends AbstractListCAPs {


    /**
     * {@inheritDoc}
     */
    protected int getSystemDefaultModifier() {
        return SwtUtils.getSystemDefaultModifier();
    }

}
