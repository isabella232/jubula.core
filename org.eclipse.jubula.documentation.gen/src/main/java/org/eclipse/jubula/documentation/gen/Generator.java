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

import org.eclipse.jubula.tools.internal.utils.generator.IProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.Info;

/**
 * Represents a Tex code generator. It generates the Tex code for a given
 * component and a generator specific generation group.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 12986 $
 */
public abstract class Generator {
    /**
     * the processor for this group
     */
    private final IProcessor m_processor;

    /**
     * the Info class for this group
     */
    private final Info m_info;

    /**
     * the group to be generated
     */
    private final ConfigGroup m_group;

    /**
     * The constructor
     * 
     * @param processor
     *            The processor for this generation group
     * @param info
     *            the Info class for this group
     * @param group
     *            the group to be generated
     */
    public Generator(IProcessor processor, Info info, ConfigGroup group) {
        m_processor = processor;
        m_info = info;
        m_group = group;
    }

    /**
     * Generates the Tex code for the component.
     * 
     * @return The Tex code
     */
    public abstract String generate();

    /**
     * @return Returns the group.
     */
    protected ConfigGroup getGroup() {
        return m_group;
    }

    /**
     * @return Returns the info.
     */
    protected Info getInfo() {
        return m_info;
    }

    /**
     * @return Returns the processor.
     */
    protected IProcessor getProcessor() {
        return m_processor;
    }

    /**
     * @param s
     *            The string to be texified
     * @return The texified string
     */
    public static String texify(String s) {
        StringBuffer texified = new StringBuffer();
        // test each character
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String specialChars = "{}%&$#^~_"; //$NON-NLS-1$
            // if it's a special character
            if (specialChars.indexOf(c) != -1) {
                // precede it with a '\'
                texified.append('\\');
            } else if (c == '\\') {
                texified.append("$\\backslash$"); //$NON-NLS-1$
            }
            // } else if (c == '_') {
            // texified.append("$\\underscore$"); //$NON-NLS-1$
            // }
            texified.append(c);
        }
        return texified.toString();
    }
}