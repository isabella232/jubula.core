/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.converter;

import org.eclipse.jubula.client.archive.schema.Cap;
import org.eclipse.jubula.client.archive.schema.Category;
import org.eclipse.jubula.client.archive.schema.ParamDescription;
import org.eclipse.jubula.client.archive.schema.Project;
import org.eclipse.jubula.client.archive.schema.TestCase;
import org.eclipse.jubula.client.archive.schema.TestCase.Teststep;

/**
 * @author BREDEX GmbH
 * @created Sep 30, 2014
 */
public class TreeDirectionConverter extends AbstractXmlConverter {

    /** The highest major version number, which have to be converted. */
    private static final int HIGHEST_MAJOR_VERSION_NUMBER = 8;

    /** The highest minor version number, which have to be converted. */
    private static final int HIGHEST_MINOR_VERSION_NUMBER = 1;

    /** The old name. */
    private static final String OLD_DIRECTION_NAME = "CompSystem.Direction"; //$NON-NLS-1$

    /** The new name. */
    private static final String NEW_DIRECTION_NAME = "CompSystem.TreeDirection"; //$NON-NLS-1$

    /**
     * @return True, if project version is lower or equal than 8.1, otherwise false.
     * {@inheritDoc}
     */
    protected boolean conversionIsNecessary(Project xml) {
        return xml.getMajorProjectVersion() <= HIGHEST_MAJOR_VERSION_NUMBER
                && xml.getMinorProjectVersion() <= HIGHEST_MINOR_VERSION_NUMBER;
    }

    /**
     * Rename all references.
     * {@inheritDoc}
     */
    protected void convertImpl(Project xml) {
        for (Category c : xml.getCategoryList()) {
            convertCategory(c);
        }
    }
    
    /**
     * Converts a given category to use new name for each tree component
     * @param cat the category
     */
    private void convertCategory(Category cat) {
        for (Category subCat : cat.getCategoryList()) {
            convertCategory(subCat);
        }
        for (TestCase tc : cat.getTestcaseList()) {
            for (Teststep ts : tc.getTeststepList()) {
                Cap cap = ts.getCap();
                if (cap != null && cap.getComponentType().contains("Tree")) { //$NON-NLS-1$
                    for (ParamDescription p : cap
                            .getParameterDescriptionList()) {
                        if (p.getName().equals(OLD_DIRECTION_NAME)) {
                            p.setName(NEW_DIRECTION_NAME);
                        }
                    }
                }
            }
        }
    }
}
