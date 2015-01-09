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

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jubula.rc.common.adaptable.IPropertyValue;

/**
 * @author BREDEX GmbH
 */
public class BackgroundPropertyValueAdapter 
    implements IPropertyValue<Background> {
    
    /** {@inheritDoc} */
    public String getStringRepresentation(Background b) {
        ToStringBuilder tsb = new ToStringBuilder(b);

        int i = 0;
        for (BackgroundFill backgroundFill : b.getFills()) {
            StringBuilder sb = new StringBuilder("fills");
            sb.append(i++);
            sb.append(" ");
            sb.append(backgroundFill.getFill().toString());
            sb.append(";");
            tsb.append(sb.toString());
        }
        
        return tsb.toString();
    }
}