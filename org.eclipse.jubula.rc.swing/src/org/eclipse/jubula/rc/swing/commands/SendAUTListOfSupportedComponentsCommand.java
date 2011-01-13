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
package org.eclipse.jubula.rc.swing.commands;

import java.util.List;

import org.eclipse.jubula.rc.common.commands.AbstractSendAUTListOfSupportedComponentsCommand;
import org.eclipse.jubula.rc.common.exception.GuiDancerUnsupportedComponentException;
import org.eclipse.jubula.rc.common.implclasses.IComponentFactory;
import org.eclipse.jubula.rc.swing.listener.ComponentHandler;
import org.eclipse.jubula.tools.xml.businessmodell.ConcreteComponent;


/**
 * The command object for QueryAUTForComponentsMessage. <br>
 * 
 * execute() registers all components sent by the
 * <code>QueryAUTForComponentsMessage</code> in the AUT server and returns an
 * <code>AUTComponentsMessage</code> containing all components of the AUT
 * which are supported.
 * 
 * timeout() should never be called. <br>
 * 
 * @author BREDEX GmbH
 * @created 04.10.2004
 * 
 */
public class SendAUTListOfSupportedComponentsCommand 
    extends AbstractSendAUTListOfSupportedComponentsCommand {

    /**
     * {@inheritDoc}
     */
    protected void addToHierarchy(IComponentFactory factory, 
        ConcreteComponent c, String technicalName) 
        throws GuiDancerUnsupportedComponentException {
        
        ComponentHandler.addToHierarchy(factory, c.getComponentClass(), 
            technicalName);
    }
    
    /**
     * {@inheritDoc}
     */
    protected List addComponentID(List componentIds, 
        ConcreteComponent concrete) 
        throws GuiDancerUnsupportedComponentException {
        
        componentIds.add(createIdentifier(concrete));
        return componentIds;
    }
}