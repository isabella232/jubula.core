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
package org.eclipse.jubula.client.ui.rcp.actions;

import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.ui.rcp.businessprocess.AbstractActionBP;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ConnectServerBP;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionGUIController;
import org.eclipse.jubula.client.ui.rcp.utils.ServerManager;
import org.eclipse.jubula.client.ui.rcp.utils.ServerManager.Server;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;


/**
 * @author BREDEX GmbH
 * @created 11.05.2005
 */
public class ChooseServerAction extends AbstractAction implements
    IWorkbenchWindowPulldownDelegate {

    /** the ID of the actions pulldown menu for adding menu contributions */
    public static final String PULLDOWN_MENU_ID = 
        "menu:org.eclipse.jubula.client.ui.rcp.actions.ChooseServerAction.dropdown"; //$NON-NLS-1$
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void runWithEvent(final IAction action, Event event) {
        if (action != null && !action.isEnabled()) {
            return;
        }
        Server server = ConnectServerBP.getInstance().getWorkingServer();
        if (server == null) {
            return;
        }
        DataEventDispatcher.getInstance().fireServerConnectionChanged(
                ServerState.Connecting);
        TestExecutionGUIController.connectToServer(server);
        ConnectServerBP.getInstance().setCurrentServer(server);
    }

    /**
     * {@inheritDoc}
     */
    public Menu getMenu(Control parent) {
        String serverName = StringConstants.EMPTY;
        Server workingServer = 
            ConnectServerBP.getInstance().getWorkingServer();
        MenuManager menuManager = new MenuManager();
        IMenuService menuService = (IMenuService) PlatformUI.getWorkbench()
            .getService(IMenuService.class);
        menuService.populateContributionManager(menuManager, PULLDOWN_MENU_ID);
        Menu fMenu = menuManager.createContextMenu(parent);
        ServerManager serverMgr = ServerManager.getInstance();
        Set<Server> servers = serverMgr.getServers();
        // read all servers from preference store
        for (Server server : servers) {
            StartServerAction action = new StartServerAction(
                server, IAction.AS_CHECK_BOX);
            if (workingServer != null 
                && workingServer.equals(server)) {
                action.setChecked(true);
            } else {
                action.setChecked(false);
            }
            if (serverName != null 
                && !serverName.equals(StringConstants.EMPTY) 
                && !serverName.equals(server.getName())) {
                menuManager.add(new Separator());
            }
            menuManager.add(action);
            serverName = server.getName();
            
        }
 
        return fMenu;
    }
    
    /**
     * {@inheritDoc}
     */
    protected AbstractActionBP getActionBP() {
        return ConnectServerBP.getInstance();
    }

}