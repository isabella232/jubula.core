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
package org.eclipse.jubula.rc.common.tester.adapter.factory;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponentAdapter;
/**
 * This factory registry holds all the toolkit specific factory's.
 * These factory's holding the classes which are implementing the component
 * specific interface. It could be an adapter or the specific component
 * implementing the interface.
 * 
 * You must register a factory before a test is started.
 * @author BREDEX GmbH
 *
 */
public class GUIAdapterFactoryRegistry {

    /**
     * Singleton instance of this class
     */
    private static GUIAdapterFactoryRegistry instance = 
        new GUIAdapterFactoryRegistry();

    /**
     * Map that manages the registration. Key is always a class Value is a
     * collection of IAdapterFactory
     */
    private Map m_registrationMap = new HashMap();

    /**
     * Call Constructor only by using getInstance
     */
    private GUIAdapterFactoryRegistry() {
    }

    /**
     * Return the singleton of this class
     * 
     * @return singleton
     */
    public static GUIAdapterFactoryRegistry getInstance() {
        return instance;
    }

    /**
     * Register adapter factory with all its supported classes
     * 
     * @param factory
     *            adapter factory that should be registered
     */
    public void registerFactory(IUIAdapterFactory factory) {
        Class[] supportedClasses = factory.getSupportedClasses();
        for (int i = 0; i < supportedClasses.length; i++) {
            m_registrationMap.put(supportedClasses[i], factory);
        }
    }

    /**
     * Sign off adapter factory from all its supported classes
     * 
     * @param factory
     *            adapter factory that should be signed off
     */
    public void signOffFactory(IAdapterFactory factory) {
        Class[] supportedClasses = factory.getSupportedClasses();
        for (int i = 0; i < supportedClasses.length; i++) {
            m_registrationMap.remove(supportedClasses[i]);
        }
    }
    /**
     * 
     * @param objectToAdapt 
     * @return -
     */
    public IComponentAdapter getAdapter(Object objectToAdapt) {
        Class adapteeclass = objectToAdapt.getClass();
        IUIAdapterFactory factory = (IUIAdapterFactory) m_registrationMap.
                get(adapteeclass);
        Class superclass = adapteeclass;
        while (factory == null && superclass != Object.class) {
            factory = (IUIAdapterFactory) m_registrationMap.
                get(superclass);
            superclass = superclass.getSuperclass();
        }
        // FIXME Here is missing the right Exception!!!
        return factory.getAdapter(objectToAdapt);
        
    }
}
