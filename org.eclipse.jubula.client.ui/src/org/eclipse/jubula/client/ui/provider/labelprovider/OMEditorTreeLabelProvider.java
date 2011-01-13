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
package org.eclipse.jubula.client.ui.provider.labelprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.Assert;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 15.10.2004
 */
public class OMEditorTreeLabelProvider extends LabelProvider {
    
    /** 
     * mapping from top-level category name to i18n key for top-level
     * category name 
     */
    private static Map<String, String> topLevelCategoryToNameKey =
        new HashMap<String, String>();
    
    static {
        topLevelCategoryToNameKey.put(
                IObjectMappingCategoryPO.MAPPEDCATEGORY,
                "ObjectMappingEditor.Assigned"); //$NON-NLS-1$
        topLevelCategoryToNameKey.put(
                IObjectMappingCategoryPO.UNMAPPEDLOGICALCATEGORY,
                "ObjectMappingEditor.UnAssignedLogic"); //$NON-NLS-1$
        topLevelCategoryToNameKey.put(
                IObjectMappingCategoryPO.UNMAPPEDTECHNICALCATEGORY,
                "ObjectMappingEditor.UnAssignedTech"); //$NON-NLS-1$
    }

    /** the component mapper to use for finding and modifying components */
    private IComponentNameMapper m_compMapper;
    
    /** clipboard */
    private Clipboard m_clipboard = new Clipboard(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell().getDisplay());

    /**
     * Constructor
     * 
     * @param compMapper The component mapper to use for finding and modifying 
     *                   Component Names.
     */
    public OMEditorTreeLabelProvider(IComponentNameMapper compMapper) {
        m_compMapper = compMapper;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void dispose() {
        m_clipboard.dispose();
    }
    
    
    /** 
     * 
     * @param element Object the Object
     * @return Image an Image
     */
    public Image getImage(Object element) {
        boolean isCut = false;
        Object cbContents = m_clipboard.getContents(
                LocalSelectionClipboardTransfer.getInstance());

        if (cbContents instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection)cbContents;
            for (Object selObject : sel.toArray()) {
                if (element == selObject) {
                    isCut = true;
                    break;
                }
            }
        }
        if (element instanceof IComponentNamePO) {
            if (isCut) {
                return IconConstants.LOGICAL_NAME_CUT_IMAGE;
            }
            return IconConstants.LOGICAL_NAME_IMAGE;
        } else if (element instanceof IObjectMappingAssoziationPO) {
            if (isCut) {
                return IconConstants.TECHNICAL_NAME_CUT_IMAGE;
            }
            return IconConstants.TECHNICAL_NAME_IMAGE;
        } else if (element instanceof IObjectMappingCategoryPO) {
            if (isCut) {
                return IconConstants.CATEGORY_CUT_IMAGE;
            }
            return IconConstants.CATEGORY_IMAGE;
        } else if (element instanceof String) {
            // Missing Component Name
            return IconConstants.LOGICAL_NAME_IMAGE;
        }
        
        String elementType = element != null 
            ? element.getClass().getName() : "null"; //$NON-NLS-1$
        Assert.notReached("Element type '" + elementType + "' not supported."); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }
    
    
    /**
     * @param element
     *            Object
     * @return name String
     */
    public String getText(Object element) {
        if (element instanceof IObjectMappingAssoziationPO) {
            IComponentIdentifier compId = 
                ((IObjectMappingAssoziationPO)element).getTechnicalName();
            if (compId != null) {
                return compId.getComponentNameToDisplay();
            }
        } else if (element instanceof IComponentNamePO) {
            return m_compMapper.getCompNameCache().getName(
                    ((IComponentNamePO)element).getGuid());
        } else if (element instanceof IObjectMappingCategoryPO) {
            IObjectMappingCategoryPO category = 
                (IObjectMappingCategoryPO)element;
            StringBuilder nameBuilder = new StringBuilder();
            String catName = category.getName();
            if (getTopLevelCategoryName(catName) != null) {
                catName = getTopLevelCategoryName(catName);
            }
            nameBuilder.append(catName);

            if (Plugin.getDefault().getPreferenceStore()
                    .getBoolean(Constants.SHOWCHILDCOUNT_KEY)) {
                int childListSize = 0;
                childListSize += 
                    category.getUnmodifiableAssociationList().size();
                childListSize += 
                    category.getUnmodifiableCategoryList().size();
                nameBuilder.append(" (").append(childListSize).append(")");  //$NON-NLS-1$//$NON-NLS-2$
            }
            return nameBuilder.toString();
        } else if (element instanceof String) {
            // Missing Component Name
            return (String)element;
        }


        Assert.notReached("Unknown type of element in tree of type " //$NON-NLS-1$
                + element.getClass().getName());
        return StringConstants.EMPTY;
    }
    
    /**
     * 
     * @param key The untranslated name of a top-level category.
     * @return the translated category name, or <code>null</code> if there is
     *         no translation for the given key.
     */
    public static String getTopLevelCategoryName(String key) {
        if (topLevelCategoryToNameKey.containsKey(key)) {
            return I18n.getString(topLevelCategoryToNameKey.get(key));
        }
        
        return null;
    }
}
