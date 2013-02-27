/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;


import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponentAdapter;
import org.eclipse.jubula.rc.common.tester.interfaces.ITester;
import org.eclipse.jubula.tools.constants.TestDataConstants;
/**
 * Implementation of basic functions for all tester classes. This class
 * gives the basic functions which are needed for testing.
 * 
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractUITester implements ITester {
    /** The default separator of a list of values */
    protected static final char VALUE_SEPARATOR = 
        TestDataConstants.VALUE_CHAR_DEFAULT;
    
    /** The default separator for enumerations of list values. */
    protected static final char INDEX_LIST_SEP_CHAR = 
        TestDataConstants.VALUE_CHAR_DEFAULT;
    
    /** */
    private IRobotFactory m_robotFactory;    

    /**    */
    private IComponentAdapter m_component;

    /**
     * Gets the Robot. 
     * @return The Robot
     * @throws RobotException If the Robot cannot be created.
     */
    protected IRobot getRobot() throws RobotException {
        return AUTServer.getInstance().getRobot();
    }
    /**
     * Gets the Robot factory. The factory is created once per instance.
     * @return The Robot factory.
     */
    protected IRobotFactory getRobotFactory() {
        m_robotFactory = m_component.getRobotFactory();
        return m_robotFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        AdapterFactoryRegistry afr =  AdapterFactoryRegistry.getInstance();
        m_component = null;
        m_component = (IComponentAdapter) afr.getAdapter(
                IComponentAdapter.class, graphicsComponent);
    }
    
    /**
     * Getter for the stored GraphicalComponent
     * @return gets the stored IComponentAdapter
     */
    public IComponentAdapter getComponent() {
        return m_component;
    }
    
}