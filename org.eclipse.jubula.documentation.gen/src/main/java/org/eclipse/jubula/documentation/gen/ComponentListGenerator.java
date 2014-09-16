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
package org.eclipse.jubula.documentation.gen;

import java.text.MessageFormat;

import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.IProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.Info;


/**
 * Generates an input line for a component
 *
 * @author BREDEX GmbH
 * @created 30.05.2006
 * @version $Revision: 12986 $
 */
public class ComponentListGenerator extends Generator {

    /**
     * @param processor the CompSystemProcessor
     * @param info the ComponentInfo
     * @param group the associated group
     */
    public ComponentListGenerator(IProcessor processor, Info info,
        ConfigGroup group) {
        super(processor, info, group);
    }

    /**
     * {@inheritDoc}
     * @param processor
     * @param info
     * @param group
     * @return a generated component list
     */
    public String generate() {
        String inputcomponent = getGroup().getProp("inputcomponent"); //$NON-NLS-1$
        ComponentInfo comp = (ComponentInfo) getInfo();
        return MessageFormat.format(inputcomponent, comp.getShortType(), 
            comp.getI18nName(), comp.getTkInfo().getShortType());
    }

}
