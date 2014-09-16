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
import java.util.Collections;
import java.util.List;

import org.eclipse.jubula.tools.internal.utils.generator.ActionInfo;
import org.eclipse.jubula.tools.internal.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.IProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.Info;
import org.eclipse.jubula.tools.internal.utils.generator.ParamInfo;


/**
 * Generates the Tex code for the inherited actions.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 12986 $
 */
public class InheritedActionsGenerator extends Generator {
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
    public InheritedActionsGenerator(IProcessor processor, Info info,
        ConfigGroup group) {
        super(processor, info, group);
        m_group = getGroup();
        m_processor = (CompSystemProcessor)getProcessor();
        m_info = (ComponentInfo)getInfo();
    }

    /**
     * {@inheritDoc}
     *      org.eclipse.jubula.documentation.gen.ComponentInfo,
     *      org.eclipse.jubula.documentation.gen.ConfigGroup)
     */
    public String generate() {
        
        

        String begingdrefinherits = m_group.getProp("begingdrefinherits"); //$NON-NLS-1$
        String gdrefinheritsentry = m_group.getProp("gdrefinheritsentry"); //$NON-NLS-1$
        String begingdrefactionparam = m_group.getProp("begingdrefactionparam"); //$NON-NLS-1$
        String gdrefactionparam = m_group.getProp("gdrefactionparam"); //$NON-NLS-1$
        String endgdrefactionparam = m_group.getProp("endgdrefactionparam"); //$NON-NLS-1$
        String endgdrefinherits = m_group.getProp("endgdrefinherits"); //$NON-NLS-1$
        String gdrefnoinherits = m_group.getProp("gdrefnoinherits"); //$NON-NLS-1$

        StringBuffer buffer = new StringBuffer();
        List<ActionInfo> actions = m_processor.getActions(
            m_info, false);

        if (actions.isEmpty()) {
            buffer.append(gdrefnoinherits).append('\n');
        } else {
            // Sort the actions
            Collections.sort(actions);

            buffer.append(begingdrefinherits);

            for (ActionInfo ai : actions) {
                StringBuffer params = new StringBuffer();
                params.append(begingdrefactionparam);
                for (Object o : ai.getParams()) {

                    ParamInfo pi = (ParamInfo)o;
                    // the type of the parameter
                    String ptype = pi.getParam().getType();

                    // take only the class name, not the fully qualified path
                    // i.e. start after the last '.'
                    ptype = ptype.substring(ptype.lastIndexOf('.') + 1);

                    params.append(
                        MessageFormat.format(gdrefactionparam, new Object[] {
                            ptype, pi.getI18nName() })).append('\n');
                }
                params.append(endgdrefactionparam).append('\n');

                ComponentInfo definingComp = ai.getContainerComp();
                Object[] entries = new Object[] { ai.getI18nName(),
                        ai.getTypeValue(), "\n" + params, //$NON-NLS-1$
                        definingComp.getI18nName(), 
                        definingComp.getTkInfo().getI18nName(), ai.getHelpid()
                };
                buffer
                    .append(MessageFormat.format(gdrefinheritsentry, entries))
                    .append('\n');
            }

            buffer.append(endgdrefinherits).append('\n');
        }
        return buffer.toString();
    }
}
