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
package org.eclipse.jubula.client.ui.provider.labelprovider.decorators;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;

/**
 * @author Markus Tiede
 * @created 29.06.2011
 */
public class OMQualityDecorator extends AbstractLightweightLabelDecorator {
    /**
     * {@inheritDoc}
     */
    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof IObjectMappingAssoziationPO) {
            IObjectMappingAssoziationPO assoc = 
                (IObjectMappingAssoziationPO)element;
            int status = getQualitySeverity(assoc.getCompIdentifier());
            ImageDescriptor overlay = null;
            switch (status) {
                case IStatus.OK:
                    overlay = IconConstants.GREEN_DOT_IMAGE_DESCRIPTOR;
                    break;
                case IStatus.WARNING:
                    overlay = IconConstants.YELLOW_DOT_IMAGE_DESCRIPTOR;
                    break;
                case IStatus.ERROR:
                    overlay = IconConstants.RED_DOT_IMAGE_DESCRIPTOR;
                    break;
                default:
                    break;
            }
            decoration.addOverlay(overlay);
        }
    }

    /**
     * @param identifier
     *            the identifier to check for its quality
     * @return an IStatus severity indicating the quality
     */
    public static int getQualitySeverity(IComponentIdentifier identifier) {
        if (identifier != null) {
            int noOfMatchedComps = identifier
                    .getNumberOfOtherMatchingComponents();
            if (identifier.isEqualOriginalFound()) {
                if (noOfMatchedComps == 1) {
                    return IStatus.OK;
                }
                return IStatus.WARNING;
            }
            return IStatus.ERROR;
        }
        return IStatus.CANCEL;
    }
}
