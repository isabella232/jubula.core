/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.html;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.State;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.ui.rcp.utils.HTMLAutWindowManager;
import org.eclipse.jubula.communication.message.html.OMSelectWindowMessage;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Handler for choosing window in html for OMM
 * @author BREDEX GmbH
 *
 */
public class OMChooseWindow extends AbstractHandler implements IElementUpdater {
    /** name of the parameter used by the client */
    private static final String WINDOW_TITLE_PARAMETER = "org.eclipse.jubula.client.ui.rcp.commands.html.ChooseAuTWindow.parameter.openWindow"; //$NON-NLS-1$
    /** */
    private static final String LAST_SELECTED_WINDOW = "org.eclipse.jubula.client.ui.rcp.commands.html.ChooseAuTWindow.state.lastSelectedWindow"; //$NON-NLS-1$
    /** The logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(OMChooseWindow.class);
    
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        State lastSelectedWindowState = 
                event.getCommand().getState(LAST_SELECTED_WINDOW);
        Map map = event.getParameters();
        String name = (String) map.get(WINDOW_TITLE_PARAMETER);
        if (lastSelectedWindowState != null && name == null) {
            name = (String) lastSelectedWindowState.getValue();
        }
        lastSelectedWindowState.setValue(name);
        HTMLAutWindowManager.getInstance().setLastSelectedWindow(name);
        OMSelectWindowMessage message = new OMSelectWindowMessage();
        message.setWindowTitle(name);
        try {
            if (name != null) {                
                AUTConnection.getInstance().send(message);
            }
        } catch (NotConnectedException nce) {
            if (LOG.isErrorEnabled()) {
                LOG.error(nce.getLocalizedMessage(), nce);
            }
        } catch (CommunicationException ce) {
            if (LOG.isErrorEnabled()) {
                LOG.error(ce.getLocalizedMessage(), ce);
            }
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateElement(UIElement element, Map parameters) {
        String name = (String) parameters.get(WINDOW_TITLE_PARAMETER);
        String lastTitle = HTMLAutWindowManager.getInstance()
                                .getLastSelectedWindow();
        boolean checked = false;
        if (name != null && lastTitle != null && name.equals(lastTitle)) {
            checked = true;
        }
        element.setChecked(checked);
    }

}
