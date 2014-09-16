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

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.tools.internal.utils.generator.ActionInfo;
import org.eclipse.jubula.tools.internal.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.IProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.Info;
import org.eclipse.jubula.tools.internal.utils.generator.ParamInfo;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ValueSetElement;


/**
 * Generates a component skeleton, inputting generated files from other
 * configuration groups, as well as written descriptions for each CAP element.
 * 
 * @author BREDEX GmbH
 * @created 23.05.2006
 * @version $Revision: 12986 $
 */
public class ComponentSkeletonGenerator extends Generator {

    /**
     * The ComponentInfo whose skeleton is to be generated
     */
    private ComponentInfo m_compInfo;

    /**
     * Our generation Group
     */
    private ConfigGroup m_group;

    /**
     * The CompSystemProcessor;
     */
    private CompSystemProcessor m_processor;

    /**
     * @param processor
     *            our CompSystemProcessor
     * @param info
     *            the respective ComponentInfo object
     * @param group
     *            our ComponentSkeleton configuration group
     */
    public ComponentSkeletonGenerator(IProcessor processor, Info info,
            ConfigGroup group) {
        super(processor, info, group);
        m_group = getGroup();
        m_compInfo = (ComponentInfo)getInfo();
        m_processor = (CompSystemProcessor)getProcessor();
    }

    /**
     * {@inheritDoc} org.eclipse.jubula.documentation.gen.Info,
     * org.eclipse.jubula.documentation.gen.ConfigGroup)
     * 
     * @return the formated component skeleton contents
     */
    @SuppressWarnings("unchecked")
    public String generate() {

        String gdrefcomponent = m_group.getProp("gdrefcomponent"); //$NON-NLS-1$
        String gdrefdescription = m_group.getProp("gdrefdescription"); //$NON-NLS-1$
        String inputcapdescr = m_group.getProp("inputcapdescr"); //$NON-NLS-1$
        String inputsynopsis = m_group.getProp("inputsynopsis"); //$NON-NLS-1$
        String inputinheritedactions = m_group.getProp("inputinheritedactions"); //$NON-NLS-1$
        String inputnewactions = m_group.getProp("inputnewactions"); //$NON-NLS-1$
        String gdrefused = m_group.getProp("gdrefused"); //$NON-NLS-1$
        String gdhelpid = m_group.getProp("gdhelpid"); //$NON-NLS-1$
        String gdrefusedseparator = m_group.getProp("valuesetseparator"); //$NON-NLS-1$
        String gdrefusedtoolkit = m_group.getProp("gdrefusedtoolkit"); //$NON-NLS-1$
        // String newpage = m_group.getProp("newpage"); //$NON-NLS-1$

        StringBuffer sb = new StringBuffer();
        String compIntName = m_compInfo.getShortType();
        String extName = m_compInfo.getI18nName();
        String compCapPath = compIntName + File.separator
                + "comp-" + compIntName; //$NON-NLS-1$
        String compTitle = MessageFormat.format(gdrefcomponent, extName);
        sb.append(compTitle);
        // format the helpid. the first parameter is the actual id, and
        // the second one is what is displayed
        String helpid = MessageFormat.format(gdhelpid, m_compInfo.getHelpid(),
                extName);
        String descrInput = MessageFormat.format(inputcapdescr, compCapPath,
                m_compInfo.getTkInfo().getShortType());
        // the above two go into our description command
        sb.append(MessageFormat.format(gdrefdescription, helpid + descrInput));
        // three of our other generated groups
        String synopsisFilename = MessageFormat.format(inputsynopsis,
                compIntName);
        sb.append(formatInput(synopsisFilename));
        // not pretty, but it works
        // if (compIntName.equals("JTree")) { //$NON-NLS-1$
        // sb.append(newpage);
        // }
        String newActionsFilename = MessageFormat.format(inputnewactions,
                compIntName);
        sb.append(formatInput(newActionsFilename));
        String inheritedFilename = MessageFormat.format(inputinheritedactions,
                compIntName);
        sb.append(formatInput(inheritedFilename));

        // details for each action
        List<ActionInfo> actions = m_processor.getActions(m_compInfo, true);
        Collections.sort(actions);
        for (ActionInfo action : actions) {
            sb.append(generateActionDescr(action));
        }
        List usingComps = (m_processor).getUsingComps(m_compInfo);
        Collections.sort(usingComps);
        Iterator i = usingComps.iterator();
        StringBuffer usedby = new StringBuffer();
        boolean isFirst = true;
        while (i.hasNext()) {
            ComponentInfo ci = (ComponentInfo)i.next();
            if (!isFirst) {
                usedby.append(gdrefusedseparator);
            }
            usedby.append(ci.getI18nName());
            usedby.append(MessageFormat.format(gdrefusedtoolkit, ci.getTkInfo()
                    .getI18nName()));
            isFirst = false;
        }
        if (usedby.length() > 0) {
            sb.append(MessageFormat.format(gdrefused, usedby.toString()));
        }
        return sb.toString();
    }

    /**
     * generates a TeX input string for the component, based on the filename
     * given
     * 
     * @param filename
     *            the filename to be input
     * @return a formatted input command
     */
    private String formatInput(String filename) {
        String inputbase = m_group.getProp("inputbase"); //$NON-NLS-1$
        String tkName = m_compInfo.getTkInfo().getShortType();
        String inputFormatted = MessageFormat.format(inputbase, tkName,
                filename);
        return inputFormatted;
    }

    /**
     * @param actionInfo
     *            the action whose description is to be generated
     * @return a string containing the generated action information
     */
    @SuppressWarnings("unchecked")
    private String generateActionDescr(ActionInfo actionInfo) {
        String gdhelpid = m_group.getProp("gdhelpid"); //$NON-NLS-1$
        String gdrefactiondescription = m_group
                .getProp("gdrefactiondescription"); //$NON-NLS-1$
        String begingdrefparam = m_group.getProp("begingdrefparam"); //$NON-NLS-1$
        String gdrefparamentry = m_group.getProp("gdrefparamentry"); //$NON-NLS-1$
        String endgdrefparam = m_group.getProp("endgdrefparam"); //$NON-NLS-1$
        String gdrefnoparams = m_group.getProp("gdrefnoparams"); //$NON-NLS-1$
        String inputcapdescr = m_group.getProp("inputcapdescr"); //$NON-NLS-1$

        StringBuffer sb = new StringBuffer();

        // format path for description input
        String actionIntName = actionInfo.getShortName();
        String compIntName = m_compInfo.getShortType();
        String actionCapDir = compIntName + File.separator + actionIntName;
        String actionCapPath = actionCapDir + File.separator + "action-" //$NON-NLS-1$
                + actionIntName;
        String toolkit = m_compInfo.getTkInfo().getShortType();
        // format the helpid, similar to for component
        String helpid = MessageFormat.format(gdhelpid, actionInfo.getHelpid(),
                actionInfo.getI18nName());
        String descrInput = MessageFormat.format(inputcapdescr, actionCapPath,
                toolkit);
        String containingCompName = actionInfo.getContainerComp().getI18nName();
        sb.append(MessageFormat.format(gdrefactiondescription, actionInfo
                .getI18nName(), containingCompName, helpid + descrInput));

        List<ParamInfo> params = actionInfo.getParams();
        if (params.isEmpty()) {
            sb.append(gdrefnoparams);
        } else {
            sb.append(begingdrefparam);
            // parameters are output in the order in which they were read in.
            for (ParamInfo paramInfo : params) {
                String paramCapPath = actionCapDir + File.separator
                        + "param-" + paramInfo.getShortName(); //$NON-NLS-1$
                helpid = MessageFormat.format(gdhelpid, paramInfo.getHelpid(),
                        paramInfo.getI18nName());
                descrInput = MessageFormat.format(inputcapdescr, paramCapPath,
                        toolkit);
                String valueSet = generateParamValueSet(paramInfo);
                String paramDefault = generateParamDefault(paramInfo);
                sb.append(MessageFormat.format(gdrefparamentry, paramInfo
                        .getI18nName(), paramInfo.getI18nType(), valueSet,
                        paramDefault, helpid + descrInput));
            }
            sb.append(endgdrefparam);
        }
        return sb.toString();
    }

    /**
     * @param paramInfo
     *            the parameter for which the default value should be generated
     * @return the default value for the param or the "no default" property if
     *         null
     */
    private String generateParamDefault(ParamInfo paramInfo) {
        String paramDefault = paramInfo.getParam().getDefaultValue();
        if (paramDefault == null) {
            return m_group.getProp("gdrefparamnodefault"); //$NON-NLS-1$
        }
        return texify(paramDefault);
    }

    /**
     * @param paramInfo
     *            the parameter for which a value set should be generated
     * @return the formatted value set for the param
     */
    private String generateParamValueSet(ParamInfo paramInfo) {
        Param param = paramInfo.getParam();
        if (param.hasValueSet()) {
            String valuesetseparator = m_group.getProp("valuesetseparator"); //$NON-NLS-1$
            StringBuffer values = new StringBuffer();
            for (Iterator i = param.valueSetIterator(); i.hasNext();) {
                String value = ((ValueSetElement)i.next()).getValue();
                // escape e.g. underscores
                value = Generator.texify(value);
                values.append(value + valuesetseparator + ' ');
            }
            return values.toString();
        }
        return m_group.getProp("gdrefparamnovalues"); //$NON-NLS-1$
    }
}
