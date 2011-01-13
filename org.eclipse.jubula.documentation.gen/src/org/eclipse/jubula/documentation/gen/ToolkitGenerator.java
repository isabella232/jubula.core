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

import org.eclipse.jubula.tools.utils.generator.IProcessor;
import org.eclipse.jubula.tools.utils.generator.Info;
import org.eclipse.jubula.tools.utils.generator.ToolkitInfo;


/**
 * 
 *
 * @author BREDEX GmbH
 * @created Jul 4, 2007
 * @version $Revision: 12986 $
 */
public class ToolkitGenerator extends Generator {

    /**
     * @param processor the CompSystemProcessor
     * @param info the ToolkitInfo
     * @param group the ConfigGroup
     */
    public ToolkitGenerator(IProcessor processor, Info info, 
            ConfigGroup group) {
        super(processor, info, group);
    }

    /**
     * {@inheritDoc}
     */
    public String generate() {
        ToolkitInfo tInfo = (ToolkitInfo)getInfo();
        String gdreftoolkit = getGroup().getProp("gdreftoolkit"); //$NON-NLS-1$
        String inputdescription = getGroup().getProp("inputdescription"); //$NON-NLS-1$
        String inputallcomponents = getGroup().getProp("inputallcomponents"); //$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        sb.append(MessageFormat.format(gdreftoolkit, tInfo.getI18nName()));
        sb.append(MessageFormat.format(inputdescription, tInfo.getShortType()));
        sb.append(MessageFormat.format(inputallcomponents, 
                tInfo.getShortType()));
        String formatted = sb.toString();
        return formatted;
    }

}
