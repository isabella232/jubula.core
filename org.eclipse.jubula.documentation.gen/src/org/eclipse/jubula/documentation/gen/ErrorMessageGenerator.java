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

import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.utils.generator.IProcessor;
import org.eclipse.jubula.tools.utils.generator.Info;


/**
 * 
 *
 * @author BREDEX GmbH
 * @created Nov 19, 2005
 * @version $Revision: 13203 $
 */
public class ErrorMessageGenerator extends Generator {
    /**
     * The error information
     */
    private ErrorInfo m_info;
    /**
     * The error group
     */
    private ConfigGroup m_group;

    /**
     * @param processor A processor to get and process the data for us
     * @param info All desired info about the error
     * @param group The TexGen group as defined by the properties file
      */
    public ErrorMessageGenerator(IProcessor processor, Info info,
        ConfigGroup group) {
        super(processor, info, group);
        m_info = (ErrorInfo)getInfo();
        m_group = getGroup();
    }

    /**
     * 
     * {@inheritDoc}
    * @return A string containing the generated output
     */
    public String generate() {
        
        String gderrormessage = m_group.getProp("gderrormessage"); //$NON-NLS-1$
        
        // format the text so that it can be output as
        // a string from a LaTeX file.
        String errorCode = StringConstants.EMPTY;
        // escape special TeX characters e.g. underscores
        String text = Generator.texify((m_info).getErrorCode());
        Object[] params = {StringConstants.EMPTY, errorCode, text};
        StringBuffer sb = new StringBuffer();
        sb.append(MessageFormat.format(gderrormessage, params) + '\n');
        
        return sb.toString();
    }

}
