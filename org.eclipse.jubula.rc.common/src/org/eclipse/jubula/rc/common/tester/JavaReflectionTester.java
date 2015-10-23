/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.tester.interfaces.ITester;
import org.eclipse.jubula.rc.common.util.ReflectionUtil;
/**
 * Tester class for the Reflection Component
 * 
 * @author Bredex GmbH
 * @created 27.10.2015
 */
public abstract class JavaReflectionTester implements ITester {
    
    /**
     * Queuer for executing the methods in the UI-Thread of the AUT
     */
    private IEventThreadQueuer m_threadQueuer;

    /**
     * Constructor
     */
    public JavaReflectionTester() {
        m_threadQueuer = getEventThreadQueuer();
    }

    /**
     * This is used to get the toolkit dependent Thread Queuer
     * @return the UI-Thread queuer
     */
    protected abstract IEventThreadQueuer getEventThreadQueuer();

    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @return returns null or if the invoked method return
     *         java.util.Properties, the string representation of the properties
     */
    public String rcInvokeMethod(final String fqcn, final String name,
            final String signature, final String args, final String argsSplit) {
        Object result = m_threadQueuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                createRunnable(fqcn, name, signature, args, argsSplit));
        return null;
    }

    /**
     * Invokes the specified Method
     * 
     * @param variableName name of the variable of the cap. This isn't used.
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @param signature signature of the method
     * @param args arguments for the method
     * @param argsSplit separator for the Arguments
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeMethodStoreReturn(final String variableName,
            final String fqcn, final String name, final String signature,
            final String args, final String argsSplit) {
        Object result = m_threadQueuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                createRunnable(fqcn, name, signature, args, argsSplit));
        return result.toString();
    }

    /**
     * Creates the runnable object which will invoke the specified Method
     * @param fqcn fully qualified class name
     * @param name method name
     * @param signature method signature
     * @param args method arguments
     * @param argsSplit separator for the arguments
     * @return the IRunnable object
     */
    private IRunnable<Object> createRunnable(final String fqcn,
            final String name, final String signature, final String args,
            final String argsSplit) {
        return new IRunnable<Object>() {

            public Object run() {
                ClassLoader uiClassloader = Thread.currentThread()
                        .getContextClassLoader();
                try {
                    return ReflectionUtil.invokeMethod(fqcn, name,
                            signature, args, argsSplit, uiClassloader);
                } catch (Throwable e) {
                    ReflectionUtil.handleException(e);
                }
                return null;
            }
        };
    }

    /**
     * Invokes the specified Method
     * 
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @return returns null or if the invoked method return
     *         java.util.Properties, the string representation of the properties
     */
    public String rcInvokeMethod(final String fqcn, final String name) {
        Object result = m_threadQueuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                createRunnable(fqcn, name));
        return null;
    }

    /**
     * Invokes the specified Method
     * 
     * @param variableName name of the variable of the cap. This isn't used.
     * @param fqcn Fully qualified class name
     * @param name name of the Method
     * @return returns the string representation of the return value of the
     *         invoked method
     */
    public String rcInvokeMethodStoreReturn(final String variableName,
            final String fqcn, final String name) {
        Object result = m_threadQueuer.invokeAndWait("invokeMethod", //$NON-NLS-1$
                createRunnable(fqcn, name));
        return result.toString();
    }

    /**
     * Creates the runnable object which will invoke the specified Method
     * @param fqcn fully qualified class name
     * @param name method name
     * @return the IRunnable object
     */
    private IRunnable<Object> createRunnable(final String fqcn,
            final String name) {
        return new IRunnable<Object>() {

            public Object run() {
                ClassLoader uiClassloader = Thread.currentThread()
                        .getContextClassLoader();
                try {
                    return ReflectionUtil.invokeMethod(fqcn, name,
                            uiClassloader);
                } catch (Throwable e) {
                    ReflectionUtil.handleException(e);
                }
                return null;
            }
        };
    }

    /**
     * Not used because of default mapping
     * @param graphicsComponent not used
     */
    public void setComponent(Object graphicsComponent) {
        // Nothing here.
    }

    /**
     * Not used because of default mapping
     * @return null
     */
    public String[] getTextArrayFromComponent() {
        // Nothing here.
        return null;
    }
}
