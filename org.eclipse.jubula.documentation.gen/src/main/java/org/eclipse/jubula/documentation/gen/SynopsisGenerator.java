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
import java.util.List;

import org.eclipse.jubula.tools.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.utils.generator.IProcessor;
import org.eclipse.jubula.tools.utils.generator.Info;


/**
 * Generates the synopsis for a component. The synopsis is the inheritance
 * hierarchy.
 * 
 * @author BREDEX GmbH
 * @created 23.09.2005
 * @version $Revision: 12986 $
 */
public class SynopsisGenerator extends Generator {
    /**
     * The configuration group
     */
    private final ConfigGroup m_group;
    
    /**
     * the component system processor
     */
    private final CompSystemProcessor m_processor;
    
    /**
     * the component info instance
     */
    private final ComponentInfo m_info;
    /**
     * @param processor The CompSystemProcessor
     * @param info The ComponentInfo instance
     * @param group The ConfigGroup
     */
    public SynopsisGenerator(IProcessor processor, Info info,
        ConfigGroup group) {
        super(processor, info, group);
        m_group = getGroup();
        m_processor = (CompSystemProcessor)getProcessor();
        m_info = (ComponentInfo)getInfo();
    }
    /**
     * @param indentation
     *            The intendation level
     * @param value
     *            The string value to print on this line
     * @return The string value to print with intendation and a new line
     */
    private String line(int indentation, String value) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < indentation; i++) {
            buffer.append("  "); //$NON-NLS-1$
        }
        buffer.append(value).append("\n"); //$NON-NLS-1$
        return buffer.toString();
    }
    /**
     * {@inheritDoc}
     *      org.eclipse.jubula.documentation.gen.ComponentInfo,
     *      org.eclipse.jubula.documentation.gen.ConfigGroup)
     */
    public String generate() {
        
        String begingdrefsynopsis = m_group.getProp("begingdrefsynopsis"); //$NON-NLS-1$
        String begingdrefsynopsisnode = m_group.getProp("begingdrefsynopsisnode"); //$NON-NLS-1$
        String gdrefsynopsisnode = m_group.getProp("gdrefsynopsisnode"); //$NON-NLS-1$
        String endgdrefsynopsisnode = m_group.getProp("endgdrefsynopsisnode"); //$NON-NLS-1$
        String endgdrefsynopsis = m_group.getProp("endgdrefsynopsis"); //$NON-NLS-1$
        
        List<ComponentInfo> infos = m_processor
            .getHierarchyCompInfos(m_info);
        
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(line(0, begingdrefsynopsis));
        
        int currentLevel = -1;
        for (ComponentInfo ci : infos) {
            if (ci.getLevel() > currentLevel) {
                currentLevel = ci.getLevel();
                buffer.append(line(currentLevel + 1, begingdrefsynopsisnode));
            }
            buffer.append(
                line(currentLevel + 2, MessageFormat.format(gdrefsynopsisnode,
                     ci.getI18nName(), ci.getTkInfo().getI18nName())));
        }
        
        for (int i = currentLevel; i >= 0; i--) {
            buffer.append(line(i + 1, endgdrefsynopsisnode));
        }
        
        buffer.append(line(0, endgdrefsynopsis));
        
        return buffer.toString();
    }
}
