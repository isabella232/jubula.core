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

import org.eclipse.jubula.tools.utils.generator.ActionInfo;
import org.eclipse.jubula.tools.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.utils.generator.IProcessor;
import org.eclipse.jubula.tools.utils.generator.Info;
import org.eclipse.jubula.tools.utils.generator.ParamInfo;


/**
 * Generates the Tex code for the new actions, that means the actions that the
 * passed component defines.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 12986 $
 */
public class NewActionsGenerator extends Generator {
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
    public NewActionsGenerator(IProcessor processor, Info info,
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

        String begingdrefaction = m_group.getProp("begingdrefaction"); //$NON-NLS-1$
        String gdrefactionentry = m_group.getProp("gdrefactionentry"); //$NON-NLS-1$
        String begingdrefactionparam = m_group.getProp("begingdrefactionparam"); //$NON-NLS-1$
        String gdrefactionparam = m_group.getProp("gdrefactionparam"); //$NON-NLS-1$
        String endgdrefactionparam = m_group.getProp("endgdrefactionparam"); //$NON-NLS-1$
        String endgdrefaction = m_group.getProp("endgdrefaction"); //$NON-NLS-1$
        String gdrefactionnoparams = m_group.getProp("gdrefactionnoparams"); //$NON-NLS-1$
        String gdrefnonewactions = m_group.getProp("gdrefnonewactions"); //$NON-NLS-1$

        StringBuffer buffer = new StringBuffer();
        List<ActionInfo> actions = m_processor.getActions(
            m_info, true);

        if (actions.isEmpty()) {
            buffer.append(gdrefnonewactions).append('\n');
        } else {
            // Sort the actions
            Collections.sort(actions);

            buffer.append(begingdrefaction).append('\n');

            for (ActionInfo ai : actions) {
                StringBuffer params = new StringBuffer();
                params.append(begingdrefactionparam).append('\n');
                List<ParamInfo> pinfos = ai.getParams();
                if (pinfos.isEmpty()) {
                    params.append(gdrefactionnoparams).append('\n');
                } else {
                    for (ParamInfo pi : pinfos) {
                        // the type of the parameter
                        String ptype = pi.getParam().getType();

                        // take only the class name, not the fully qualified
                        // path
                        // i.e. start after the last '.'
                        ptype = ptype.substring(ptype.lastIndexOf('.') + 1);

                        params.append(
                            MessageFormat.format(gdrefactionparam,
                                new Object[] { ptype, pi.getI18nName() }))
                            .append('\n');
                    }
                }
                params.append(endgdrefactionparam).append('\n');

                buffer.append(
                    MessageFormat.format(gdrefactionentry, new Object[] {
                        ai.getI18nName(), ai.getTypeValue(),
                        "\n" + params, ai.getHelpid()})) //$NON-NLS-1$
                    .append('\n');

            }

            buffer.append(endgdrefaction).append('\n');
        }
        return buffer.toString();
    }
}
