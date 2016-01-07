/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.converter;

import org.eclipse.jubula.client.archive.schema.Aut;
import org.eclipse.jubula.client.archive.schema.Project;
import org.eclipse.jubula.client.archive.schema.UsedToolkit;

/**
 * @author BREDEX GmbH
 */
public class MobileToolkitIdConverter extends AbstractXmlConverter {
    /** The highest meta data version number, which have to be converted. */
    private static final int HIGHEST_META_DATA_VERSION_NUMBER = 6;

    /** The old toolkit ID. */
    private static final String OLD_TOOLKIT_ID = "ui.toolkit.MobileToolkitPlugin"; //$NON-NLS-1$

    /** The new toolkit ID. */
    private static final String NEW_TOOLKIT_ID = "ui.toolkit.mobile.IOSToolkitPlugin"; //$NON-NLS-1$

    /** {@inheritDoc} */
    protected boolean conversionIsNecessary(Project xml) {
        return xml.getMetaDataVersion() <= HIGHEST_META_DATA_VERSION_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    protected void convertImpl(Project xml) {
        if (xml.getAutToolKit().equals(OLD_TOOLKIT_ID)) {
            xml.setAutToolKit(NEW_TOOLKIT_ID);
        }
        for (Aut aut : xml.getAutList()) {
            if (aut.getAutToolkit().equals(OLD_TOOLKIT_ID)) {
                aut.setAutToolkit(NEW_TOOLKIT_ID);
            }
        }
        for (UsedToolkit usedToolkit : xml.getUsedToolkitList()) {
            if (usedToolkit.getName().equals(OLD_TOOLKIT_ID)) {
                usedToolkit.setName(NEW_TOOLKIT_ID);
            }
        }
    }
}
