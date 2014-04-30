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
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.constants.TestexecConstants;

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
    /** checks if the given URI is relative and resolve it to the absolute against the base URL
     * @param basePath the base directory
     * @param path a text path given by user
     * @return absolute against the base URL path or EXIT_INVALID_ARG_VALUE ("-2") if invalid URL was given
     */
    public static String resolveAgainstBasePath(
            String path, String basePath) {
        URI uri = URI.create(path);
        if (!(uri.isAbsolute()
                || uri.getPath().startsWith(StringConstants.SLASH))) {
            if (StringUtils.isEmpty(basePath)) {
                return String.valueOf(TestexecConstants.INVALID_VALUE);
            }
            uri = new File(basePath).toURI().resolve(uri);
        }
        return uri.toString();
    }

}
