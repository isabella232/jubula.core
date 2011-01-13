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
package org.eclipse.jubula.toolkit.generate.interfaces.createinterfaces;

import org.eclipse.jubula.tools.utils.generator.AbstractComponentBuilder;
import org.eclipse.jubula.tools.utils.generator.ToolkitConfig;
import org.eclipse.jubula.tools.xml.businessmodell.CompSystem;

/**
 * @author BREDEX GmbH
 * @created Dec 1, 2009
 */
public class BuildCompSystem extends AbstractComponentBuilder {
    
    /**
     * @param config Toolkitconfig
     */
    public BuildCompSystem(ToolkitConfig config) {
        super(config);
    }
    
    /**
     * {@inheritDoc}
     */
    public CompSystem getCompSystem() {
        return super.getCompSystem();
    }    
}
