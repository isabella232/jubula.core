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

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.tools.exception.GDException;


/**
 * @author BREDEX GmbH
 * @created Sep 19, 2007
 */
public class MissingReferenceDecorator extends LabelProvider
    implements ILightweightLabelDecorator {

    /**
     * {@inheritDoc}
     */
    public void decorate(Object element, IDecoration decoration) {
        if (element instanceof IReusedProjectPO) {
            Long projID;
            IReusedProjectPO rProj = (IReusedProjectPO)element;
            try {
                projID = ProjectPM.findProjectId(rProj.getProjectGuid(), rProj
                        .getMajorNumber(), rProj.getMinorNumber());
            } catch (GDException e) {
                projID = null;
            }
            if (projID == null) {
                decoration.addOverlay(
                        IconConstants.INCOMPLETE_DATA_IMAGE_DESCRIPTOR);
            }
        }
    }
}
