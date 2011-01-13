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

}
