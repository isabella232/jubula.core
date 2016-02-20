/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.archive.converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.archive.schema.Aut;
import org.eclipse.jubula.client.archive.schema.ObjectMapping;
import org.eclipse.jubula.client.archive.schema.OmCategory;
import org.eclipse.jubula.client.archive.schema.OmEntry;
import org.eclipse.jubula.client.archive.schema.Project;

/**
 * This converter deletes the unnecessary object mapping assoziations when they occur multiple times,
 * see http://eclip.se/469940
 * 
 * @author BREDEX GmbH
 */
public class ObjectMappingAssoziationConverter extends AbstractXmlConverter {

    /** The highest meta data version number, which have to be converted. */
    private static final int HIGHEST_META_DATA_VERSION_NUMBER = 6;

    /**
     * {@inheritDoc}
     */
    protected boolean conversionIsNecessary(Project xml) {
        return xml.getMetaDataVersion() <= HIGHEST_META_DATA_VERSION_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    protected void convertImpl(Project xml) {
        for (Aut aut : xml.getAutList()) {
            ObjectMapping om = aut.getObjectMapping();
            OmCategory rootCategory = om.getMapped();
            convertCategory(rootCategory);
        }
    }

    /**
     * Removes duplicate entries in object mapping
     * @param category the category
     */
    private void convertCategory(OmCategory category) {
        List<OmEntry> entryList = category.getAssociationList();
        for (OmEntry entry : entryList) {
            List<String> logicalNameList = entry.getLogicalNameList();
            Set<String> set = new HashSet<String>(logicalNameList);
            if (logicalNameList.size() != set.size()) {
                logicalNameList.clear();
                logicalNameList.addAll(set);
            }
        }
        List<OmCategory> subcategoryList = category.getCategoryList();
        for (OmCategory subcategory : subcategoryList) {
            convertCategory(subcategory);
        }
    }
}
