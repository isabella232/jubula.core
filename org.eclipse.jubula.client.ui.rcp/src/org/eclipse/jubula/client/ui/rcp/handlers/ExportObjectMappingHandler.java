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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICompIdentifierPO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.utils.SerilizationUtils;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created 07.10.2014
 */
public class ExportObjectMappingHandler extends AbstractHandler {
        
    /** map containing all object mappings */
    private SortedMap<String, String> m_map = new TreeMap<String, String>();

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof ObjectMappingMultiPageEditor) {
            final ObjectMappingMultiPageEditor omEditor = 
                (ObjectMappingMultiPageEditor)activePart;
            IAUTMainPO aut = omEditor.getAut();
            IObjectMappingPO objMap = aut.getObjMap();
            IObjectMappingCategoryPO mappedCategory =
                    objMap.getMappedCategory();
            for (IObjectMappingCategoryPO category
                    : mappedCategory.getUnmodifiableCategoryList()) {
                writeAssociationsToMap(category);
            }
            // map is filled and can be written to file
            FileDialog saveDialog = new FileDialog(getActiveShell(), SWT.SAVE);
            saveDialog.setFileName("objectMapping" + StringConstants.UNDERSCORE //$NON-NLS-1$
                    + aut.getName() + ".properties"); //$NON-NLS-1$
            saveDialog.setFilterExtensions(new String[] { "*.properties" }); //$NON-NLS-1$
            saveDialog.setOverwrite(true);
            String filterPath = Utils.getLastDirPath();
            saveDialog.setFilterPath(filterPath);
            String path = saveDialog.open();
            if (path != null) {
                Utils.storeLastDirPath(saveDialog.getFilterPath());
                try (BufferedWriter writer = new BufferedWriter(
                        new FileWriter(path))) {
                    for (String key : m_map.keySet()) {
                        String value = m_map.get(key);
                        writer.append(key + StringConstants.EQUALS_SIGN + value
                                + StringConstants.NEWLINE);
                    }
                } catch (IOException e) {
                    ErrorHandlingUtil.createMessageDialog(
                            new JBException(e.getMessage(), e,
                                    MessageIDs.E_FILE_NO_PERMISSION));
                }
            }
        }
        return null;
    }

    /**
     * Writes all object mapping associations from a given category (and recursively from
     * all sub-categories) into the map
     * @param category the category
     */
    private void writeAssociationsToMap(IObjectMappingCategoryPO category) {
        List<IObjectMappingCategoryPO> subcategoryList =
                category.getUnmodifiableCategoryList();
        if (!subcategoryList.isEmpty()) {
            for (IObjectMappingCategoryPO subcategory : subcategoryList) {
                writeAssociationsToMap(subcategory);
            }
        }
        for (IObjectMappingAssoziationPO assoziation
                : category.getUnmodifiableAssociationList()) {
            ICompIdentifierPO compIdentifier = assoziation.getTechnicalName();
            String compName = compIdentifier.getComponentName();
            try {
                m_map.put(compName, SerilizationUtils.encode(compIdentifier));
            } catch (IOException e) {
                ErrorHandlingUtil.createMessageDialog(
                        new JBException(e.getMessage(), e,
                                MessageIDs.E_EXPORT_OM_ERROR));
            }
        }
    }
    
}