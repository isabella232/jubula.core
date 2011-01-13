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
package org.eclipse.jubula.rc.common.businessprocess;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * @author BREDEX GmbH
 * @created 05.05.2006
 */
public class ReflectionBP {
    
    /** The logger */
    private static final AutServerLogger LOG = new AutServerLogger(
        ReflectionBP.class);

    /**
     * hidden utility Contructor
     */
    private ReflectionBP() {
        // nothing
    }
    
    /**
     * Invokes the method with the given name with the given parameter
     * @param name tne name of the mothod to invoke
     * @param object the Object of the method
     * @param paramTypes the parameter types od the methods arguments
     * @param args the arguments of the method
     */
    public static void invokeMethod(String name, Object object, 
        Class[] paramTypes, Object[] args) {
        
        try {
            Method m = object.getClass().getMethod(name, paramTypes);
            m.invoke(object, args);
        } catch (SecurityException e) {
            LOG.fatal("Security manager indicated a security violation!", e); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            LOG.fatal(e);
        } catch (NoSuchMethodException e) {
            LOG.fatal("Class: " + object.getClass().getName()  //$NON-NLS-1$
                + " does not contain method: " + name + "!"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (IllegalAccessException e) {
            LOG.fatal(e);
        } catch (InvocationTargetException e) {
            LOG.fatal("InvocationTargetException: ", e); //$NON-NLS-1$
            LOG.fatal("TargetException: ", e.getTargetException()); //$NON-NLS-1$
        }
    }
    
    /**
     * Invokes the method with the given name with the given parameter
     * @param name name tne name of the mothod to invoke
     * @param object object the Object of the method
     * @param args args the arguments of the method
     * {@inheritDoc}
     */
    public static void invokeMethod(String name, Object object, Object[] args) {
        Object[] arguments = args;
        if (args == null) {
            arguments = new Object[0];
        }  
        final int argsLength = arguments.length;
        Class paramTypes [] = new Class[argsLength];
        for (int i = 0; i < argsLength; i++) {
            paramTypes[i] = arguments[i].getClass();
        }
        invokeMethod(name, object, paramTypes, arguments);
    }
}