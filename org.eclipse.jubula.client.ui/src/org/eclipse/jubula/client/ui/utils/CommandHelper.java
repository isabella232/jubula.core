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
package org.eclipse.jubula.client.ui.utils;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.views.AbstractGDTreeView;
import org.eclipse.jubula.client.ui.views.TestSuiteBrowser;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;


/**
 *  Helper Class to programmatically execute commands 
 *
 * @author BREDEX GmbH
 * @created Jul 28, 2010
 */
public abstract class CommandHelper {
    /** standard logging */
    private static Log log = LogFactory.getLog(CommandHelper.class);
    
    /**
     * Constructor
     */
    private CommandHelper() {
    // hide
    }
    
    /**
     * @param node
     *            the node to open the editor for; may also be null
     * @param gdtv
     *            the gdtree view this node hast been found for
     */
    public static void openEditorForNode(INodePO node, 
        AbstractGDTreeView gdtv) {
        if (node instanceof ISpecTestCasePO || node instanceof IExecTestCasePO
                || node instanceof IEventExecTestCasePO
                || node instanceof ICapPO) {
            if (gdtv instanceof TestSuiteBrowser) {
                executeCommand(CommandIDs.OPEN_TESTSUITE_EDITOR_COMMAND_ID);
            } else {
                executeCommand(CommandIDs.OPEN_TESTCASE_EDITOR_COMMAND_ID);
            }
        } else if (node instanceof ITestSuitePO) {
            executeCommand(CommandIDs.OPEN_TESTSUITE_EDITOR_COMMAND_ID);
        } else if (node instanceof IRefTestSuitePO
                || node instanceof ITestJobPO) {
            executeCommand(CommandIDs.OPEN_TESTJOB_EDITOR_COMMAND_ID);
        } else if (node instanceof ITestDataCubePO) {
            executeCommand(CommandIDs.OPEN_CENTRAL_TESTDATA_EDITOR_COMMAND_ID);
        }
    }

    /**
     * Execute the given commmandId using the workbench handler service
     * 
     * @param commandID
     *            the command to execute
     * @return The return value from the execution; may be null.
     */
    public static Object executeCommand(String commandID) {
        return executeCommand(commandID, null);
    }
    
    /**
     * Execute the given commmandId using the given part site for handler
     * service retrievement
     * 
     * @param commandID
     *            the command to execute
     * @param site
     *            the site to get the handler service from; may be <code>null</code>
     * @return The return value from the execution; may be null.
     */
    public static Object executeCommand(String commandID, 
        IWorkbenchPartSite site) {
        IHandlerService handlerService;
        Class<IHandlerService> hsc = IHandlerService.class;
        if (site != null) {
            handlerService = (IHandlerService)site.getService(hsc);
        } else {
            handlerService = (IHandlerService)PlatformUI.getWorkbench()
                    .getService(hsc);
        }
        try {
            return handlerService.executeCommand(commandID, null);
        } catch (CommandException e) {
            log.warn("Error occurred while executing command: " + commandID); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * @param menuManager
     *            the menu to add the command contribution item for
     * @param commandId
     *            the id to create the item for
     */
    public static void createContributionPushItem(IMenuManager menuManager,
            String commandId) {
        menuManager.add(createContributionItem(commandId, null, null,
                CommandContributionItem.STYLE_PUSH));
    }

    /**
     * Creates and returns a contribution item representing the command with the
     * given ID.
     * 
     * @param commandId
     *            The ID of the command for which to create a contribution item.
     * @param params a map of parameters for this command
     * @param style
     *            The style to use for the contribution item. See the
     *            CommandContributionItem STYLE_* contants.
     * @param label
     *            the label to display for this item
     * @return the created contribution item.
     */
    public static IContributionItem createContributionItem(String commandId,
            Map<?, ?> params, String label, int style) {
        return new CommandContributionItem(
                new CommandContributionItemParameter(PlatformUI.getWorkbench(),
                        null, commandId, params, null, null, null, label, null,
                        null, style, null, false));
    }
}
