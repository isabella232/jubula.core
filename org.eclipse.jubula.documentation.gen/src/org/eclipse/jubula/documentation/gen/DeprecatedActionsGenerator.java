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

import org.eclipse.jubula.tools.utils.generator.ActionInfo;
import org.eclipse.jubula.tools.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.utils.generator.IProcessor;
import org.eclipse.jubula.tools.utils.generator.Info;
import org.eclipse.jubula.tools.utils.generator.ToolkitInfo;


/**
 * 
 *
 * @author BREDEX GmbH
 * @created Aug 8, 2007
 * @version $Revision: 12986 $
 */
public class DeprecatedActionsGenerator extends Generator {

    /**
     * @param processor the processor
     * @param info the action info
     * @param group the config group
     */
    public DeprecatedActionsGenerator(IProcessor processor, Info info,
            ConfigGroup group) {
        super(processor, info, group);
    }

    /**
     * {@inheritDoc}
     */
    public String generate() {
        // get generator properties
        final String gdrefdepitem = getGroup().getProp("gdrefdepitem"); //$NON-NLS-1$
        final String gdrefdepinputdescr = getGroup().getProp("gdrefdepinputdescr"); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        // get the three infos we need for output
        ActionInfo aInfo = (ActionInfo)getInfo();
        ComponentInfo cInfo = aInfo.getContainerComp();
        ToolkitInfo tkInfo = cInfo.getTkInfo();
        // format with 3 arguments
        String inputdescrFormatted = MessageFormat.format(gdrefdepinputdescr,
                        tkInfo.getShortType(), cInfo.getShortType(),
                        aInfo.getShortName());
        sb.append(MessageFormat.format(gdrefdepitem, tkInfo.getI18nName(), 
                cInfo.getI18nName(), aInfo.getI18nName(), inputdescrFormatted));
        
        return sb.toString();
    }

}
