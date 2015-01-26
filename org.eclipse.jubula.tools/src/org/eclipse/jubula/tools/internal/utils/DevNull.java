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
package org.eclipse.jubula.tools.internal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class reads buffered from an inputstream in a seperated thread. 
 * Used for clearing the inputstream and errorstream of an external process.
 *
 * @author BREDEX GmbH
 * @created 10.08.2004
 */
public class DevNull extends IsAliveThread {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(DevNull.class);
    
    /** the error stream if a SUN VM does not know -javaagent */
    private static final String UNRECOGNIZED_SUN_JO = 
        "Unrecognized option: \"-javaagent"; //$NON-NLS-1$
    
    /** the input stream to read from */
    private InputStream m_inputStream;

    /** picks up the error stream if -javaagent is unknown */
    private String m_line;
    
    /**
     * public constructor
     * @param inputStream the inputstream to trash
     */
    public DevNull(InputStream inputStream) {
        super("Stream Redirect"); //$NON-NLS-1$
        m_inputStream = inputStream;
    }
    
    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(m_inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                System.out.println("AUT:" + line); //$NON-NLS-1$
                if (line.indexOf(UNRECOGNIZED_SUN_JO) > -1) {
                    m_line = line;
                }
                line = br.readLine();
            }
        } catch (IOException ioe) {
            log.debug("input stream closed", ioe); //$NON-NLS-1$
        }
    }

    /**
     * @return the 'Unrecognized option' error stream if javaagent 
     * is to be used and jdk older than 1.5, or null
     */
    public String getLine() {
        return m_line;
    }
}
