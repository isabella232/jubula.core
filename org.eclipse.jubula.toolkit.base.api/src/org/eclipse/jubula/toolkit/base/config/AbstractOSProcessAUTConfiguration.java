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
package org.eclipse.jubula.toolkit.base.config;

import java.util.Locale;

import org.eclipse.jubula.tools.registration.AutIdentifier;

/** @author BREDEX GmbH */
public abstract class AbstractOSProcessAUTConfiguration extends
    AbstractAUTConfiguration {
    /** the command */
    private String m_command;
    /** the dir */
    private String m_workingDir;
    /** the args */
    private String[] m_args;
    /** the locale */
    private Locale m_locale;

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param command
     *            the command
     * @param wd
     *            the working directory
     * @param args
     *            the commands arguments
     * @param locale
     *            the AUT locale to use
     */
    public AbstractOSProcessAUTConfiguration(String name, AutIdentifier autID,
        String command, String wd, String[] args, Locale locale) {
        super(name, autID);
        setLocale(locale);
        setCommand(command);
        setWorkingDir(wd);
        setArgs(args);
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return m_command;
    }

    /**
     * @param command the command to set
     */
    private void setCommand(String command) {
        m_command = command;
    }

    /**
     * @return the workingDir
     */
    public String getWorkingDir() {
        return m_workingDir;
    }

    /**
     * @param workingDir the workingDir to set
     */
    private void setWorkingDir(String workingDir) {
        m_workingDir = workingDir;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return m_args;
    }

    /**
     * @param args the args to set
     */
    private void setArgs(String[] args) {
        m_args = args;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return m_locale;
    }

    /**
     * @param locale the locale to set
     */
    private void setLocale(Locale locale) {
        m_locale = locale;
    }
}