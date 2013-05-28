/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.param;

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.GeneralLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * The content provider for the parameter names used by the TreeViewer.
 * @author BREDEX GmbH
 */
public class ParameterDescriptionLabelProvider extends GeneralLabelProvider {

    /**
     * {@inheritDoc}
     * @return No special image (e.g. plus icon on Windows),
     *         if the element is a parameter description,
     *         otherwise the same method {@link GeneralLabelProvider#getImage(Object)}
     *         from the super class is called.
     */
    public Image getImage(Object element) {
        if (element instanceof IParamDescriptionPO) {
            return IconConstants.ORIGINAL_DATA_IMAGE;
        }
        return super.getImage(element);
    }

    /**
     * {@inheritDoc}
     * @return The parameter name, if the element is a parameter description,
     *         otherwise the same method {@link GeneralLabelProvider#getText(Object)}
     *         from the super class is called.
     */
    public String getText(Object element) {
        if (element instanceof IParamDescriptionPO) {
            IParamDescriptionPO paramDesc = (IParamDescriptionPO) element;
            StringBuffer sb = new StringBuffer();
            return sb.append("[") //$NON-NLS-1$
                .append(getShortTypeName(paramDesc))
                .append(": ") //$NON-NLS-1$
                .append(paramDesc.getName())
                .append("]") //$NON-NLS-1$
                .toString();
        }
        return super.getText(element);
    }

    /**
     * @param paramDesc The parameter description.
     * @return The short type name of the given parameter description, e.g.
     *         the name <code>String</code>, if the parameter has the type
     *         {@link java.lang.String}.
     */
    private static String getShortTypeName(IParamDescriptionPO paramDesc) {
        String typeName = paramDesc.getType();
        int i = typeName.lastIndexOf('.');
        return typeName.substring(i + 1);
    }

    /**
     * Shows the GUID of a parameter description as tool tip text.
     * {@inheritDoc}
     */
    public String getToolTipText(Object element) {
        if (element instanceof IParamDescriptionPO) {
            IParamDescriptionPO paramDesc = (IParamDescriptionPO) element;
            return "id=" + paramDesc.getUniqueId(); //$NON-NLS-1$
        }
        return super.getToolTipText(element);
    }

}
