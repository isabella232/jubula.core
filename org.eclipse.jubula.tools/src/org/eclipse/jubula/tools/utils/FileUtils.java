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
package org.eclipse.jubula.tools.utils;

import java.io.File;
import java.io.IOException;

/**
 * @author BREDEX GmbH
 * @created Apr 12, 2006
 */
public class FileUtils {
    
    /** private constructor */
    private FileUtils() {
        // utility class
    }
    /**
     * checks if a path is writable
     * @param path
     *      String
     * @return
     *      boolean
     */
    public static boolean isValidPath(String path) {
        File dir = new File(path);
        boolean valid = true;
        if (dir.isDirectory()
                && dir.exists()) {
            File file = new File(dir.getAbsolutePath() + "/tmp.xml"); //$NON-NLS-1$
            try {
                boolean created = false;
                if (!file.exists()) {
                    file.createNewFile();
                    created = true;
                }
                if (!file.canWrite()) {
                    valid = false;
                }
                if (created) {
                    file.delete();
                }
            } catch (IOException e) {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
    }
    /** checks if the given path is absolute and otherwise resolves it to the absolute against the base path
     * @param basePath the base path
     * @param path a path given by user
     * @return absolute path (the initial one or the resolved against the base path one)
     */
    public static String resolveAgainstBasePath(
            String path, String basePath) {
        if (path == null || basePath == null) {
            return null;
        }
        File baseDir = new File(basePath);
        File fpath = new File(path);
        if (!fpath.isAbsolute()) {
            fpath = new File(baseDir, path);
        }
        return fpath.toString();
    }

}
