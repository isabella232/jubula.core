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
package org.eclipse.jubula.rc.javafx.commands;

import org.eclipse.jubula.rc.common.commands.AbstractActivateApplicationCommand;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.javafx.driver.RobotFactoryJavaFXImpl;

/**
 * @author BREDEX GmbH
 * @created 4.11.2013
 *
 */
public class ActivateApplicationCommand extends
        AbstractActivateApplicationCommand {

    /**
     * {@inheritDoc}
     */
    protected IRobot getRobot() {
        return RobotFactoryJavaFXImpl.INSTANCE.getRobot();
    }
}