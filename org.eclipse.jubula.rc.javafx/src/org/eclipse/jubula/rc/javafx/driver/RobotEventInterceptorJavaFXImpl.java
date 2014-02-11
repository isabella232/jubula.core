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
package org.eclipse.jubula.rc.javafx.driver;

import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.IRobotEventInterceptor;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;

/**
 * This class intercepts JavaFX events defined by the
 * <code>InterceptorOptions</code>. When <code>intercept()</code> is called, a
 * new
 * {@link org.eclipse.jubula.rc.RobotEventConfirmerJavaFXImpl.driver.awtimpl.RobotEventConfirmerAwtImpl}
 * is created and enabled, so that the confirmer starts collecting events at
 * once.
 *
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
class RobotEventInterceptorJavaFXImpl implements IRobotEventInterceptor {
    /**
     * {@inheritDoc}
     */
    public IRobotEventConfirmer intercept(InterceptorOptions options) {
        RobotEventConfirmerJavaFXImpl confirmer =
                new RobotEventConfirmerJavaFXImpl(
                options);
        confirmer.setEnabled(true);
        return confirmer;
    }
}
