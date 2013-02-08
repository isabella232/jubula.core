/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common;


import org.eclipse.jubula.rc.common.listener.AUTEventListener;
import org.eclipse.jubula.rc.common.tester.adapter.factory.GUIAdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.tester.adapter.factory.IUIAdapterFactory;



/**
 * This is a specialized version of the AUTServer.
 * It is used for toolkits which uses the AdapterFactory for their components.
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractAdapterBasedRemoteController extends AUTServer {

    /** 
     * private constructor instantiates the listeners
     * @param mappingListener new instance of toolkit mapping server
     * @param recordListener new instance of toolkit record server
     * @param checkListener new instance of toolkit check server
     */
    protected AbstractAdapterBasedRemoteController(
            AUTEventListener mappingListener, AUTEventListener recordListener,
            AUTEventListener checkListener) {
        super(mappingListener, recordListener, checkListener);
    }
    /**
     * 
     * @return the toolkit specific adapter factory.
     */
    public abstract IUIAdapterFactory getToolkitFactory();

    /**
     * {@inheritDoc}
     */
    public void start(boolean isRcpAccessible) {
        super.start(isRcpAccessible);

        GUIAdapterFactoryRegistry.getInstance().registerFactory(
                getToolkitFactory());
    }


}
