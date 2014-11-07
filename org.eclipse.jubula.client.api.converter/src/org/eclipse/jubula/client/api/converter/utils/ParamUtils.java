/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter.utils;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.ui.rcp.views.dataset.AbstractDataSetPage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @created 05.11.2014
 */
public class ParamUtils {
    
    /**
     * private constructor
     */
    private ParamUtils() {
        // private
    }

    /**
     * Returns a parameter value for a node
     * @param node the node
     * @param param the parameter
     * @param locale the language
     * @return the value
     */
    public static String getValueForParam(IParameterInterfacePO node,
            IParamDescriptionPO param, Locale locale) {
        String paramType = param.getType();
        String value = AbstractDataSetPage.getGuiStringForParamValue(
                node, param, 0, locale);
        if (value.startsWith(StringConstants.EQUALS_SIGN)) {
            value = StringUtils.substringAfter(value,
                    StringConstants.EQUALS_SIGN);
        } else if (paramType.equals("java.lang.String")) { //$NON-NLS-1$
            value = StringConstants.QUOTE + value + StringConstants.QUOTE;
        }
        return value;
    }
    
    
}
