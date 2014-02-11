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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.javafx.driver.RobotFactoryConfig;

/**
 * @param <T>
 *            Type of the component
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
public class AbstractComponentAdapter<T> implements IComponent {

    /** the component */
    private T m_component;

    /**
     * Used to store the component into the adapter.
     *
     * @param objectToAdapt
     *            the object to adapt
     */
    public AbstractComponentAdapter(T objectToAdapt) {
        m_component = objectToAdapt;
    }

    @Override
    public T getRealComponent() {
        return m_component;
    }

    @Override
    public IRobotFactory getRobotFactory() {
        IRobotFactory robotFactory = new RobotFactoryConfig().getRobotFactory();
        return robotFactory;
    }

    /**
     * Gets the Robot.
     *
     * @return The Robot
     * @throws RobotException
     *             If the Robot cannot be created.
     */
    protected IRobot getRobot() throws RobotException {
        return getRobotFactory().getRobot();
    }

}
