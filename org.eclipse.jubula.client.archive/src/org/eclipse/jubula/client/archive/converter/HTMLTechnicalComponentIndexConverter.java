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
package org.eclipse.jubula.client.archive.converter;

import java.util.List;

import org.eclipse.jubula.client.schema.Aut;
import org.eclipse.jubula.client.schema.OmCategory;
import org.eclipse.jubula.client.schema.OmEntry;
import org.eclipse.jubula.client.schema.Project;
import org.eclipse.jubula.client.schema.TechnicalName;

/**
 * Converter for ticket #3546
 * 
 * This converter appends a [1] index to all hierarchy names of mapped technical
 * components for an HTML object mapping
 * 
 * @author BREDEX GmbH
 * @created Jan 14, 2011
 */
public class HTMLTechnicalComponentIndexConverter extends AbstractXmlConverter {
    /**
     * <code>REQUIRED_METADATA_VERSION_NUMBER</code>
     */
    private static final int REQUIRED_METADATA_VERSION_NUMBER = 6;

    /**
     * <code>OLD_HTML_TOOLKIT_ID</code>
     */
    private static final String OLD_HTML_TOOLKIT_ID = "com.bredexsw.guidancer.HtmlToolkitPlugin"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    protected boolean conversionIsNecessary(Project xml) {
        if (xml.getMetaDataVersion() < REQUIRED_METADATA_VERSION_NUMBER) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected void convertImpl(Project xml) {
        for (Aut autXml : xml.getAutList()) {
            if (autXml.getAutToolkit().equals(OLD_HTML_TOOLKIT_ID)) {
                OmCategory mappedCat = autXml.getObjectMapping().getMapped();
                convertCategory(mappedCat);
            }
        }
    }

    /**
     * @param cat
     *            the category to convert recursively
     */
    private void convertCategory(OmCategory cat) {
        List<OmCategory> childCat = cat.getCategoryList();
        for (OmCategory omCat : childCat) {
            convertCategory(omCat);
        }

        List<OmEntry> listOfMappings = cat.getAssociationList();
        for (OmEntry oe : listOfMappings) {
            fixTechnicalName(oe.getTechnicalName());
        }
    }

    /**
     * @param technicalName
     *            the technical name to fix
     */
    private void fixTechnicalName(TechnicalName technicalName) {
        List<String> hierarchyNames = technicalName.getHierarchyNameList();
        String[] hNameArray = hierarchyNames.toArray(new String[hierarchyNames
                .size()]);
        // do not convert the last name as this is most likely the component
        // name / given name itself
        for (int i = 0; i < hNameArray.length - 1; i++) {
            String chierarchyName = hNameArray[i];
            if (!chierarchyName.matches("^.*\\[[0-9]+\\]$")) { //$NON-NLS-1$
                hNameArray[i] = chierarchyName.concat("[1]"); //$NON-NLS-1$
            }
        }
        technicalName.setHierarchyNameArray(hNameArray);
    }
}
