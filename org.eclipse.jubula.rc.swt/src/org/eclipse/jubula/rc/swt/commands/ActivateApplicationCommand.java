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
package org.eclipse.jubula.rc.swt.commands;

import org.eclipse.jubula.rc.common.commands.AbstractActivateApplicationCommand;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.swt.driver.RobotFactoryConfig;


/**
 * @author BREDEX GmbH
 * @created 10.02.2006
 * 
 */
public class ActivateApplicationCommand 
    extends AbstractActivateApplicationCommand {
    
    /**
     * {@inheritDoc}
     */
    protected IRobot getRobot() {
        return new RobotFactoryConfig().getRobotFactory().getRobot();
    }
}