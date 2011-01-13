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
package org.eclipse.jubula.client.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.ReentryProperty;
import org.eclipse.jubula.client.core.persistence.Hibernator;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.i18n.I18n;


/**
 * 
 * class to manage the defaultEventHandler
 * They will called, if no userdefined eventHandler is available
 * 
 * @author BREDEX GmbH
 * @created 07.04.2005
 *
 */
public final class DefaultEventHandler {
    
    /**
     * <code>handlerMap</code><br>
     * Map with defaultEventHandler for each eventType (key)
     */
    private static Map < String, IEventExecTestCasePO > handlerMap = 
        new HashMap < String, IEventExecTestCasePO > ();
    
    /**
     * private constructor
     */
    private DefaultEventHandler() {
        // nothing
    }
    
    /**
     * get the DefaultEventHandler for given eventType <br>
     * if no eventHandler is available for given eventType will return a
     * standard defaultEventHandler with Reentry Property "EXIT"
     * @param eventId eventType
     * @param rootNode The root node.
     * @return the defaultEventHandler for given eventId
     */
    public static IEventExecTestCasePO getDefaultEventHandler(String eventId,
        INodePO rootNode) {
        
        createMap(rootNode);
        return (handlerMap.get(eventId) != null) 
            ? (IEventExecTestCasePO)handlerMap.get(eventId)
            : (IEventExecTestCasePO)handlerMap.get("Default");  //$NON-NLS-1$
    }
    
    /**
     * fill the map with defaultEventHandler for each supported eventType
     * @param rootNode The root node.
     */
    private static void createMap(INodePO rootNode) {
        handlerMap = new HashMap < String, IEventExecTestCasePO > ();
        if (Hibernator.isPoSubclass(rootNode, ITestSuitePO.class)) {
            ITestSuitePO testSuite = (ITestSuitePO)rootNode;
            Map < String, Integer > map = testSuite.getDefaultEventHandler();
            Set < String > mapKeySet = map.keySet();
            for (String key : mapKeySet) {
                try {
                    handlerMap.put(key, createHandler(key, 
                        ReentryProperty.getProperty(map.get(key)), rootNode));
                } catch (InvalidDataException e) {
                    return;
                }
            }
            handlerMap.put("Default", //$NON-NLS-1$
                createHandler(I18n.getString("DefaultEventHandler.Unknown"), ReentryProperty.EXIT, rootNode)); //$NON-NLS-1$
            return;
        }
        
        // Impl.Class Action Error      
        handlerMap.put("TestErrorEvent.Action", //$NON-NLS-1$
            createHandler(I18n.getString("TestErrorEvent.Action"), ReentryProperty.EXIT, rootNode)); //$NON-NLS-1$
        // Component not found
        handlerMap.put("TestErrorEvent.CompNotFound", //$NON-NLS-1$
            createHandler(I18n.getString("TestErrorEvent.CompNotFound"), ReentryProperty.EXIT, rootNode)); //$NON-NLS-1$
        // Configuration Error      
        handlerMap.put("TestErrorEvent.Config", //$NON-NLS-1$
            createHandler(I18n.getString("TestErrorEvent.Config"), ReentryProperty.CONTINUE, rootNode)); //$NON-NLS-1$
        // Verify Failed      
        handlerMap.put("TestErrorEvent.VerifyFailed", //$NON-NLS-1$
            createHandler(I18n.getString("TestErrorEvent.VerifyFailed"), ReentryProperty.CONTINUE, rootNode)); //$NON-NLS-1$
        // Default Handler in case of unexpected eventType
        handlerMap.put("Default", //$NON-NLS-1$
            createHandler(I18n.getString("DefaultEventHandler.Unknown"), ReentryProperty.EXIT, rootNode)); //$NON-NLS-1$
    }
    
    
    
    /**
     * generate DefaultEventHandler with the given Reentry Property
     * @param eventType type of error event
     * @param prop reentry property, e.g. "EXIT"
     * @param assocNode the associated node
     * @return the eventHandler
     */
    private static IEventExecTestCasePO createHandler(String eventType, 
        ReentryProperty prop, INodePO assocNode) {
        IEventExecTestCasePO eventTC = NodeMaker.createEventExecTestCasePO(
            org.eclipse.jubula.client.core.model.NodeMaker
                .createSpecTestCasePO("EmptyGuiDancerSpecTestCase"), assocNode); //$NON-NLS-1$
        eventTC.setReentryProp(prop);
        eventTC.setName(I18n.getString("DefaultEventHandler.DefEH")); //$NON-NLS-1$
        eventTC.setEventType(eventType);
        return eventTC;    
    }
}