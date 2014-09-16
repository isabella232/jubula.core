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
package org.eclipse.jubula.tools.internal.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class to edit classpath in runtime
 * @author BREDEX GmbH
 *
 */
public class ClassPathHacker {

    /**
     * parameters
     */
    private static final Class[] PARAMETERS = new Class[]{URL.class};

    /**
     * invis contructor
     *
     */
    private ClassPathHacker() {
        //nothing
    }
    
    /**
     * AddFile
     * @param s filename
     * @throws IOException Error
     */
    public static void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }

    /**
     * AddFile
     * @param f file
     * @throws IOException Error
     */
    public static void addFile(File f) throws IOException {
        addURL(f.toURL());
    }

    /**
     * AddFile
     * @param u url
     * @throws IOException Error
     */
    public static void addURL(URL u) throws IOException {
            
        URLClassLoader sysloader = 
            (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.
                getDeclaredMethod("addURL", PARAMETERS); //$NON-NLS-1$
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{ u });
        } catch (NoSuchMethodException t) {
            // no log available here
            throw new IOException(
                "Error, could not add URL to system classloader"); //$NON-NLS-1$
        } catch (IllegalAccessException t) {
            // no log available here
            throw new IOException(
                "Error, could not add URL to system classloader"); //$NON-NLS-1$
        } catch (InvocationTargetException t) {
            // no log available here
            throw new IOException(
                "Error, could not add URL to system classloader"); //$NON-NLS-1$
        }

    }
}
