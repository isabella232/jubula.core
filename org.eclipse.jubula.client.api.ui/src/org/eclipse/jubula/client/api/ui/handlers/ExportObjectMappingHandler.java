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
package org.eclipse.jubula.client.api.ui.handlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jubula.client.api.ui.utils.OMAssociation;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.i18n.Messages;
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
import org.eclipse.jubula.toolkit.client.api.ui.internal.OMClassGenerator;
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
    
    /** the class generator for the OM class */
    private OMClassGenerator m_omClassGenerator = new OMClassGenerator();

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof ObjectMappingMultiPageEditor) {
            final ObjectMappingMultiPageEditor omEditor = 
                (ObjectMappingMultiPageEditor)activePart;
            IAUTMainPO aut = omEditor.getAut();
            
            int exportType = determineExportType();
            if (exportType != -1) {
                FileDialog saveDialog = createSaveDialog(aut, exportType);
                String path = saveDialog.open();
                if (path != null) {
                    Utils.storeLastDirPath(saveDialog.getFilterPath());
                    fillMap(omEditor, aut);
                    // map is filled and can be written to class or file
                    OMAssociation omAssociations =
                            generateEncodedAssociations();
                    omAssociations.setTargetClassName(
                            saveDialog.getFileName());
                    try (BufferedWriter writer = new BufferedWriter(
                            new FileWriter(path))) {
                        switch (exportType) {
                            case 0: // Write Java Class
                                writer.append(m_omClassGenerator
                                        .generate(omAssociations));
                                break;
                            case 1: // Write Properties File
                                writer.append(omAssociations
                                        .getEncodedAssociations());
                                break;
                            default: // Nothing
                                break;
                        }
                    } catch (IOException e) {
                        ErrorHandlingUtil.createMessageDialog(
                            new JBException(e.getMessage(), e,
                                    MessageIDs.E_FILE_NO_PERMISSION));
                    }
                }
            }
        }
        return null;
    }

    /**
     * fills the map with the encoded object mapping associations
     * @param omEditor the object mapping editor
     * @param aut the aut
     */
    private void fillMap(final ObjectMappingMultiPageEditor omEditor,
            IAUTMainPO aut) {
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
    }

    /**
     * Creates the save dialogue
     * @param aut the aut (needed for the name)
     * @param exportType the type of the export
     * @return the save dialogue
     */
    private FileDialog createSaveDialog(IAUTMainPO aut, int exportType) {
        String fileName = StringConstants.EMPTY;
        String fileExtension = StringConstants.EMPTY;
        switch (exportType) {
            case 0: // Write Java Class
                fileExtension = ".java"; //$NON-NLS-1$
                fileName = "OM" + fileExtension; //$NON-NLS-1$
                break;
            case 1: // Write Properties File
                fileExtension = ".properties"; //$NON-NLS-1$
                fileName = "objectMapping" + StringConstants.UNDERSCORE //$NON-NLS-1$
                        + aut.getName() + fileExtension;
                break;
            default: // Nothing
                break;
        }
        
        FileDialog saveDialog = new FileDialog(getActiveShell(), SWT.SAVE);
        saveDialog.setFileName(fileName);
        saveDialog.setFilterExtensions(
                new String[] { StringConstants.STAR + fileExtension });
        saveDialog.setOverwrite(true);
        String filterPath = Utils.getLastDirPath();
        saveDialog.setFilterPath(filterPath);
        return saveDialog;
    }

    /**
     * Opens a question dialogue to determine the desired export type
     * @return the export type
     *      <code>0</code> for a Java Class File
     *      <code>1</code> for a Properties File
     */
    private int determineExportType() {
        String dialogTitle = Messages.ExportObjectMappingDialogTitle;
        String dialogMessage = Messages.ExportObjectMappingDialogMessage;
        MessageDialog dialog = new MessageDialog(getActiveShell(), dialogTitle,
                null, dialogMessage, MessageDialog.QUESTION,
                new String[] {
                    Messages.ExportObjectMappingDialogChoiceJavaClass,
                    Messages.ExportObjectMappingDialogChoicePropertiesFile },
                0);
        return dialog.open();
    }

    /**
     * @return StringBuffer containing the map with the encoded object mappings
     */
    private OMAssociation generateEncodedAssociations() {
        StringBuffer encodedAssociations = new StringBuffer();
        Map<String, String> identifierMap = new HashMap<String, String>();
        for (String key : m_map.keySet()) {
            String value = m_map.get(key);
            encodedAssociations.append(key + StringConstants.EQUALS_SIGN + value
                    + StringConstants.NEWLINE);
            identifierMap.put(key, translateToJavaIdentifier(key));
        }
        return new OMAssociation(encodedAssociations, identifierMap);
    }

    /**
     * Translates a string to a valid java identifier
     * @param key the string
     * @return a valid java identifier
     */
    private String translateToJavaIdentifier(String key) {
        String modifiedKey = key;
        String [] exceptions = new String[] {
            StringConstants.DOT,
            StringConstants.SPACE,
            StringConstants.BACKSLASH,
            StringConstants.SLASH,
            StringConstants.STAR,
            StringConstants.COLON,
            StringConstants.LEFT_BRACKET,
            StringConstants.RIGHT_BRACKET,
            StringConstants.LEFT_PARENTHESES,
            StringConstants.RIGHT_PARENTHESES,
            StringConstants.EQUALS_SIGN,
            StringConstants.PLUS,
            StringConstants.MINUS,
            StringConstants.PIPE};
        for (String exception : exceptions) {
            modifiedKey = modifiedKey.replace(
                    exception, StringConstants.UNDERSCORE);
        }
        return modifiedKey;
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
            for (String compUUID : assoziation.getLogicalNames()) {
                String compName = m_compMapper.getCompNameCache()
                        .getName(compUUID);
                ComponentIdentifier identifier = (ComponentIdentifier) objMap
                        .getTechnicalName(compUUID);
                m_map.put(compName, SerilizationUtils.encode(identifier));
            }
        }
    }
    
}