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
package org.eclipse.jubula.autagent.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.constants.RcpAccessorConstants;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;


/**
 * @author BREDEX GmbH
 * @created 04.09.2007
 * 
 */
public class StartRcpAutServerCommand extends AbstractStartJavaAut {

    /** 
     * -nl : 
     * the argument that defines the locale for the RCP application. 
     */
    private static final String NL = "-nl"; //$NON-NLS-1$

    /**
     * @param pathSeparator the defined pathSeparator
     * @param cmds the cmd list
     * @param parameters The startup parameters for the AUT.
     */
    private void createDirectAutJavaCallParameter(final String pathSeparator, 
        List cmds, Map parameters) { 
        
        StringBuffer autClassPath = new StringBuffer();
        String autClassPathValue = (String)parameters.get(
                AutConfigConstants.CLASSPATH);
        if (autClassPathValue != null && !StringConstants.EMPTY
                .equals(autClassPathValue)) {
            autClassPath.append(convertClientSeparator(autClassPathValue));
        }

        if (autClassPath.length() > 0) {
            autClassPath.append(pathSeparator);
        }
        final String autJar = (String)parameters.get(
                AutConfigConstants.JAR_FILE);
        String manifestClassPath = getClassPathFromManifest(parameters);
        if (manifestClassPath.length() > 0) {
            autClassPath.append(manifestClassPath).append(pathSeparator);
        }
        if (autClassPath.toString() != null
                && !StringConstants.EMPTY.equals(autClassPath.toString())) {
            cmds.add("-classpath"); //$NON-NLS-1$
            cmds.add(autClassPath.toString());
        }
        cmds.add("-jar"); //$NON-NLS-1$
        cmds.add(autJar);
        final String autArgs = 
            (String)parameters.get(AutConfigConstants.AUT_ARGUMENTS);
        if (autArgs != null) {
            StringTokenizer args = new StringTokenizer(autArgs, 
                WHITESPACE_DELIMITER);
            while (args.hasMoreTokens()) {
                String arg = args.nextToken();
                cmds.add(arg);
            }
        }
    }

    /**
     * @param cmds the cmd list
     * @param parameters The startup parameters for the AUT.
     */
    private void createDirectAutExeCallParameter(List cmds, Map parameters) { 
        
        final String autArgs = 
            (String)parameters.get(AutConfigConstants.AUT_ARGUMENTS);
        if (autArgs != null) {
            StringTokenizer args = new StringTokenizer(autArgs, 
                WHITESPACE_DELIMITER);
            while (args.hasMoreTokens()) {
                String arg = args.nextToken();
                cmds.add(arg);
            }
        }
    }

    /**
     * 
     * @param parameters The parameters for starting the AUT.
     * @return a command line array as list with locale, JRE-parameters and
     * optional debug parameters
     */
    private List createDirectAutJavaCall(final Map parameters) {
        
        // create exec string array
        List cmds = new Vector();
        // add locale
        addLocale(cmds, (Locale)parameters.get(IStartAut.LOCALE));
        // add jre params
        final String jreParams = (String)parameters.get(
                AutConfigConstants.JRE_PARAMETER);
        if (jreParams != null && jreParams.length() > 0) {
            StringTokenizer tok = new StringTokenizer(jreParams, 
                WHITESPACE_DELIMITER);
            while (tok.hasMoreTokens()) {
                cmds.add(tok.nextToken());
            }
        }
        // add debug options (if neccessary)
        addDebugParams(cmds, true);
        return cmds;
    }

    /**
     * Adds the parameters for remote debuggingto the given command List
     * @param cmds the command List
     * @param isDirectExec true if the AUT is started by exec and not by a JVM
     */
    private void addDebugParams(List cmds, boolean isDirectExec) {
        if (IStartAut.BXDEBUG != null) {
            if (isDirectExec) {
                cmds.add("-vmargs -Xms128m -Xmx512m"); //$NON-NLS-1$
            }
            cmds.add("-Xdebug"); //$NON-NLS-1$
            cmds.add("-Xnoagent"); //$NON-NLS-1$
            cmds.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + IStartAut.BXDEBUG); //$NON-NLS-1$
            cmds.add("-Djava.compiler=NONE"); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getServerClassName() {
        return CommandConstants.AUT_SWT_SERVER;
    }

    /**
     * {@inheritDoc}
     */
    protected String getServerClasses() {
        return CommandConstants.AUT_SWT_SERVER_BIN;
    }

    /**
     * {@inheritDoc}
     */
    protected String getServerJar() {
        return CommandConstants.AUT_SWT_SERVER_JAR;
    }

    /**
     * {@inheritDoc}
     */
    protected String[] createCmdArray(String baseCmd, Map parameters) {
        
        List cmds;
        
        if (!isRunningFromExecutable(parameters)) {
            // Start using java
            cmds = createDirectAutJavaCall(parameters);
            cmds.add(0, baseCmd);
                    
            createDirectAutJavaCallParameter(PATH_SEPARATOR, cmds, parameters);
            addLocale(cmds, (Locale)parameters.get(IStartAut.LOCALE));
        } else {
            // Start using executable file
            cmds = new Vector();

            cmds.add(0, baseCmd);
                        
            createDirectAutExeCallParameter(cmds, parameters);
            // add locale
            // Note: This overrides the -nl defined in the <app>.ini file, if
            // any. It will not override a -nl from the command line.
            if (!cmds.contains(NL)) {
                Locale locale = (Locale)parameters.get(IStartAut.LOCALE);
                if (locale != null) {
                    if ((locale.getCountry() != null 
                        && locale.getCountry().length() > 0)
                        || (locale.getLanguage() != null 
                        && locale.getLanguage().length() > 0)) {

                        // Add -nl argument if country and/or language is
                        // available.
                        cmds.add(1, NL);
                        cmds.add(2, locale.toString());
                    }
                }
            }
            addDebugParams(cmds, true);
        }

        String[] cmdArray = (String[])cmds.toArray(new String[cmds.size()]);
        return cmdArray;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected String[] createEnvArray(Map parameters, boolean isAgentSet) {
        
        String [] envArray = super.createEnvArray(parameters, isAgentSet);
        if (envArray == null) {
            envArray = EnvironmentUtils.propToStrArray(
                    EnvironmentUtils.getProcessEnvironment(), 
                    IStartAut.PROPERTY_DELIMITER);
        }
        Vector envList = new Vector(Arrays.asList(envArray));
        envList.addAll(getConnectionProperties(
                parameters, StartSwtAutServerCommand.ENV_VAR_PREFIX, 
                StartSwtAutServerCommand.ENV_VALUE_SEP));
        
        if (isRunnigWithMonitoring(parameters)) {
            
            StringBuffer sb = new StringBuffer();
            sb.append("_JAVA_OPTIONS="); //$NON-NLS-1$
            sb.append(this.getMonitoringAgent(parameters));
            envList.add(sb.toString());   
            envArray = super.createEnvArray(parameters, true);
            
        }        
        envArray = (String [])envList.toArray(new String [envList.size()]);
      
        return envArray;
    }

    /**
     * 
     * @param parameters The AUT Configuration parameters.
     * @param propPrefix The string to prepend to all generated property names.
     * @param valueSeparator The string to use to separate property names from
     *                       property values.
     * @return the list of properties.
     */
    private List getConnectionProperties(Map parameters, 
            String propPrefix, String valueSeparator) {
        
        List props = new ArrayList();
        StringBuffer sb = new StringBuffer();

        if (AutStarter.getInstance().getAutCommunicator() != null) {
            sb = new StringBuffer();
            sb.append(propPrefix).append(RcpAccessorConstants.SERVER_PORT)
            .append(valueSeparator)
            .append(String.valueOf(
                AutStarter.getInstance().getAutCommunicator().getLocalPort()));
            props.add(sb.toString());
        }

        sb = new StringBuffer();
        sb.append(propPrefix).append(RcpAccessorConstants.KEYBOARD_LAYOUT)
            .append(valueSeparator)
            .append((String)parameters.get("KEYBOARD_LAYOUT")); //$NON-NLS-1$
        props.add(sb.toString());
        
        sb = new StringBuffer();
        sb.append(propPrefix).append(AutConfigConstants.AUT_AGENT_HOST)
            .append(valueSeparator)
            .append((String)parameters.get(AutConfigConstants.AUT_AGENT_HOST));
        props.add(sb.toString());

        sb = new StringBuffer();
        sb.append(propPrefix).append(AutConfigConstants.AUT_AGENT_PORT)
            .append(valueSeparator)
            .append((String)parameters.get(AutConfigConstants.AUT_AGENT_PORT));
        props.add(sb.toString());

        sb = new StringBuffer();
        sb.append(propPrefix).append(AutConfigConstants.AUT_NAME)
            .append(valueSeparator)
            .append((String)parameters.get(AutConfigConstants.AUT_NAME));
        props.add(sb.toString());

        return props;
    }

}
