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
package org.eclipse.jubula.rc.javafx;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javafx.stage.Stage;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.javafx.driver.RobotFactoryConfig;
import org.eclipse.jubula.rc.javafx.listener.AbstractFXAUTEventHandler;
import org.eclipse.jubula.rc.javafx.listener.CheckListener;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.listener.MappingListener;
import org.eclipse.jubula.rc.javafx.listener.RecordListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AutServer controlling the AUT. <br>
 * A quasi singleton: the instance is created from main(). <br>
 * Expected arguments to main are, see also
 * StartAUTServerCommand.createCmdArray():
 * <ul>
 * <li>The name of host the client is running on, must be InetAddress conform.</li>
 * <li>The port the JubulaClient is listening to.</li>
 * <li>The main class of the AUT.</li>
 * <li>Any further arguments are interpreted as arguments to the AUT.</li>
 * <ul>
 * When a connection to the JubulaClient could made, any errors will send as a
 * message to the JubulaClient.
 *
 * Changing the mode to OBJECT_MAPPING results in installing an
 * JavaFXEventFilter on the assumed primary stage, which listens to the mouse-
 * and key.events relevant for OBJECT_MAPPING.
 *
 * Changing the mode removes the installed MappingListener.
 *
 * @author BREDEX GmbH
 * @created 24.09.2013
 */
public class JavaFXAUTServer extends AUTServer {

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(JavaFXAUTServer.class);

    /**
     * constructor instantiates the listeners
     */
    public JavaFXAUTServer() {
        super(new MappingListener(), new RecordListener(), new CheckListener());
    }

    @Override
    protected void addToolkitEventListener(BaseAUTListener listener) {
        if (listener instanceof AbstractFXAUTEventHandler) {
            addToolkitEventListener((AbstractFXAUTEventHandler) listener);
        }

    }

    /**
     * Adds a handler to the stage
     *
     * @param handler
     *            the handler
     */
    private void addToolkitEventListener(AbstractFXAUTEventHandler handler) {
        List<Object> stages = ComponentHandler
                .getAssignableFromType(Stage.class);
        for (final Object object : stages) {
            handler.addHandler((Stage) object);
        }
    }

    @Override
    protected void addToolkitEventListeners() {
        addToolkitEventListener(new ComponentHandler());
    }

    @Override
    protected void removeToolkitEventListener(BaseAUTListener listener) {
        if (listener instanceof AbstractFXAUTEventHandler) {
            removeToolkitEventListener((AbstractFXAUTEventHandler) listener);
        }
    }

    /**
     * removes a handler from the stage
     *
     * @param handler
     *            the handler
     */
    private void removeToolkitEventListener(AbstractFXAUTEventHandler handler) {
        List<Object> stages = ComponentHandler
                .getAssignableFromType(Stage.class);
        for (Object object : stages) {
            handler.removeHandler((Stage) object);
        }
    }

    @Override
    protected void startTasks() throws ExceptionInInitializerError,
            InvocationTargetException, NoSuchMethodException {
        addToolKitEventListenerToAUT();
        invokeAUT();
    }

    @Override
    public IRobot getRobot() {
        IRobotFactory robotFactory = new RobotFactoryConfig().getRobotFactory();
        return robotFactory.getRobot();
    }

}
