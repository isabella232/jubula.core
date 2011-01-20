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
package org.eclipse.jubula.toolkit.common.businessprocess;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.toolkit.common.IToolKitProvider;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.i18n.Messages;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.xml.businessmodell.ToolkitPluginDescriptor;
import org.eclipse.swt.widgets.Composite;


/**
 * @author BREDEX GmbH
 * @created 25.06.2007
 */
public class ToolkitSupportBP {

    /** The logger */
    private static Log log = LogFactory.getLog(ToolkitSupportBP.class);
    
    /** Map of {@link IToolKitProvider} */
    private static Map<ToolkitPluginDescriptor, IToolKitProvider> 
    toolkitProvider = 
        new HashMap<ToolkitPluginDescriptor, IToolKitProvider>();

    /**
     * Utility Constructor.
     */
    private ToolkitSupportBP() {
        // nothing
    }
 
    /**
     * Gets the AutConfigComposite for the toolkit with the given name.
     * @param toolkit the name of the toolkit.
     * @param parent the parent Composite of the AutConfigComposite
     * @param style the style
     * @param autConfig the Map of Aut-Configuration.
     * @param autName the name of the AUT that will be using this configuration.
     * @return the dependent AutConfigComposite
     */
    public static Composite getAutConfigComposite(String toolkit, 
        Composite parent, int style, Map<String, String> autConfig,
        String autName) throws ToolkitPluginException {
        
        IToolKitProvider provider = getToolkitProvider(toolkit);
        Composite autConf = provider.getAutConfigDialog(parent, style, 
            autConfig, autName);
        String toolkitName = toolkit;
        while (autConf == null) {
            provider = getSuperToolkitProvider(toolkitName);
            toolkitName = getToolkitDescriptor(toolkitName).getName();
            if (provider == null) {
                break;
            }
            autConf = provider.getAutConfigDialog(
                parent, style, autConfig, autName);
        }
        if (autConf == null) {
            throwToolkitPluginException(
                Messages.NoAutConfigFound + StringConstants.COLON 
                + StringConstants.SPACE
                + String.valueOf(toolkit), null);
        }
        return autConf;
    }
    
    /**
     * Gets the level of the given toolkit name.
     * @param toolkitName the name of the toolkit.
     * @return the level.
     * @throws ToolkitPluginException if no plugin can be found for the given
     *         toolkit name.
     */
    public static String getToolkitLevel(String toolkitName) 
        throws ToolkitPluginException {
        
        ToolkitPluginDescriptor descr = getToolkitDescriptor(toolkitName);
        return descr.getLevel();
    }
    
    /**
     * Gets the ToolkitProvider with the given name.
     * @param name the name of the Toolkit
     * @return the {@link IToolKitProvider}.
     */
    private static IToolKitProvider getToolkitProvider(String name) 
        throws ToolkitPluginException {
        
        if (name == null) {    
            final String msg = Messages.ToolkitNameIsNull 
                + StringConstants.EXCLAMATION_MARK;
            log.error(msg);
            throwToolkitPluginException(msg, null);
        }
        final ToolkitPluginDescriptor descr = getToolkitDescriptor(name);
        return toolkitProvider.get(descr);
    }
    
    /**
     * Gets the {@link IToolKitProvider} of the included (extended) toolkit
     * of the toolkit with the given name.
     * @param name the name of the extending toolkit
     * @return the {@link IToolKitProvider} of the super toolkit.
     */
    private static IToolKitProvider getSuperToolkitProvider(String name) 
        throws ToolkitPluginException {
        
        if (name == null) {
            final String msg = Messages.ToolkitNameIsNull 
                + StringConstants.EXCLAMATION_MARK;
            log.error(msg);
            throwToolkitPluginException(msg, null);
        }
        final ToolkitPluginDescriptor descr = getToolkitDescriptor(name);
        final String superId = descr.getIncludes();
        final IToolKitProvider superToolkitProv = getToolkitProvider(superId);
        return toolkitProvider.get(superToolkitProv);
    }
    
    /**
     * 
     * @param toolkitId the id of the toolkit
     * @return the {@link ToolkitPluginDescriptor} of the toolkit with the 
     * given id.
     */
    private static ToolkitPluginDescriptor getToolkitDescriptor(
        String toolkitId) throws ToolkitPluginException {
        
        if (toolkitId == null) {
            final String msg = Messages.ToolkitNameIsNull 
                + StringConstants.EXCLAMATION_MARK;
            log.error(msg);
            throwToolkitPluginException(msg, null);
        }
        for (ToolkitPluginDescriptor descr : toolkitProvider.keySet()) {
            if (toolkitId.equals(descr.getToolkitID())) {
                return descr;
            }
        }
        final String msg = Messages.NoToolkitPluginDescriptorFound
            + StringConstants.COLON + StringConstants.SPACE
            + String.valueOf(toolkitId);
        log.error(msg);
        throwToolkitPluginException(msg, null);
        return null;
    }
    
    /**
     * Adds a new {@link IToolKitProvider} with the dependent 
     * {@link ToolkitPluginDescriptor} as key.
     * @param descr a ToolkitPluginDescriptor (key)
     * @param provider a IToolKitProvider (value)
     */
    public static void addToolkitProvider(ToolkitPluginDescriptor descr, 
        IToolKitProvider provider) {
        
        toolkitProvider.put(descr, provider);
    }
    
    /**
     * Throws a ToolkitPluginException with the given parameter.
     * @param message a message
     * @param cause a cause. Can be null.
     * @throws ToolkitPluginException
     */
    private static void throwToolkitPluginException(String message, 
        Throwable cause) throws ToolkitPluginException {
        
        if (cause == null) {
            throw new ToolkitPluginException(message);
        } 
        throw new ToolkitPluginException(message, cause);
    }
}
