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

import org.eclipse.jubula.tools.internal.utils.generator.IProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.Info;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitInfo;


/**
 * @author BREDEX GmbH
 * @created Aug 2, 2007
 * @version $Revision: 12986 $
 */
public class ToolkitListGenerator extends Generator {

    /**
     * @param processor the processor
     * @param info the toolkit info
     * @param group the config group
     */
    public ToolkitListGenerator(IProcessor processor, Info info,
            ConfigGroup group) {
        super(processor, info, group);
    }

    /**
     * {@inheritDoc}
     */
    public String generate() {
        String inputtoolkitlist = getGroup().getProp("inputtoolkitlist"); //$NON-NLS-1$
        ToolkitInfo tkInfo = (ToolkitInfo) getInfo();
        return MessageFormat.format(inputtoolkitlist, tkInfo.getShortType(), 
            tkInfo.getI18nName());
    }

}
