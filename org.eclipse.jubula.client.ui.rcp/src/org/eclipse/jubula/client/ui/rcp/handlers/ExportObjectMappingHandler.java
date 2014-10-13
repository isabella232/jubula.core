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
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.LogicComponentNotManagedException;
import org.eclipse.jubula.client.internal.utils.SerilizationUtils;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
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
    
    /** the component name mapper to use */
    private IComponentNameMapper m_compMapper;

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof ObjectMappingMultiPageEditor) {
            final ObjectMappingMultiPageEditor omEditor = 
                (ObjectMappingMultiPageEditor)activePart;
            IAUTMainPO aut = omEditor.getAut();
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
                IObjectMappingPO objMap = aut.getObjMap();
                IObjectMappingCategoryPO mappedCategory =
                        objMap.getMappedCategory();
                m_compMapper = omEditor.getEditorHelper().getEditSupport()
                        .getCompMapper();
                try {
                    writeAssociationsToMap(objMap, mappedCategory);
                } catch (LogicComponentNotManagedException | IOException e) {
                    ErrorHandlingUtil.createMessageDialog(new JBException(e
                            .getMessage(), e, MessageIDs.E_EXPORT_OM_ERROR));
                }
                // map is filled and can be written to file
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
     * Writes all object mapping associations from a given category (and
     * recursively from all sub-categories) into the map
     * 
     * @param objMap
     *            object mapping to retrieve technical names from
     * @param category
     *            the category
     * @throws LogicComponentNotManagedException when there is a problem with
     *      assigning component identifiers to their logical names
     * @throws IOException when there is a problem with encoding
     */
    private void writeAssociationsToMap(final IObjectMappingPO objMap,
            IObjectMappingCategoryPO category)
        throws LogicComponentNotManagedException, IOException {
        List<IObjectMappingCategoryPO> subcategoryList =
                category.getUnmodifiableCategoryList();
        if (!subcategoryList.isEmpty()) {
            for (IObjectMappingCategoryPO subcategory : subcategoryList) {
                writeAssociationsToMap(objMap, subcategory);
            }
        }
        for (IObjectMappingAssoziationPO assoziation
                : category.getUnmodifiableAssociationList()) {
            String compUUID = assoziation.getLogicalNames().get(0);
            String compName = m_compMapper.getCompNameCache().getName(compUUID);
            ComponentIdentifier identifier = (ComponentIdentifier) objMap
                    .getTechnicalName(compUUID);
            m_map.put(compName, SerilizationUtils.encode(identifier));
        }
    }
    
}