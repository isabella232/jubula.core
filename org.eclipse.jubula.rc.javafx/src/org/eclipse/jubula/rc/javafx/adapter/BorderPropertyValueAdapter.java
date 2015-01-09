/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.rc.javafx.adapter;

import javafx.scene.layout.Border;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.rc.common.adaptable.IPropertyValue;

/**
 * @author BREDEX GmbH
 */
public class BorderPropertyValueAdapter 
    implements IPropertyValue<Border> {
    
    /** {@inheritDoc} */
    public String getStringRepresentation(Border b) {
        ToStringBuilder tsb = new ToStringBuilder(b);

        return tsb.toString();
    }
}