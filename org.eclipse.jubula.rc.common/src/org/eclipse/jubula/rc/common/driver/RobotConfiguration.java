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
package org.eclipse.jubula.rc.common.driver;

import org.eclipse.jubula.rc.common.CompSystemConstants;

/**
 * Configuration of the robot 
 *
 * @author BREDEX GmbH
 * @created 10.02.2006
 */
public class RobotConfiguration {
    /** instance */
    private static RobotConfiguration instance = new RobotConfiguration();
    /** window activation method */
    private String m_activationMethod = CompSystemConstants.AAM_NONE;
    
    /**
     * @return instance
     */
    public static RobotConfiguration getInstance() {
        return instance;
    }
    
    /**
     * @return window activation method
     */
    public String getDefaultActivationMethod() {
        return m_activationMethod;
    }
    /**
     * @param activationMethod window activation method
     */
    public void setDefaultActivationMethod(String activationMethod) {
        m_activationMethod = activationMethod;
    }
}
