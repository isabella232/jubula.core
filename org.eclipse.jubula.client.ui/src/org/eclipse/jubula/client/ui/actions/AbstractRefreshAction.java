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
package org.eclipse.jubula.client.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.ui.actions.ActionFactory;


/**
 * @author BREDEX GmbH
 * @created 31.05.2006
 */
public class AbstractRefreshAction extends Action {

    /**
     * Creates a new action.
     */
    public AbstractRefreshAction() {
        super();
        setText(Messages.ActionBuilderRefreshItem);
        setActionDefinitionId("org.eclipse.ui.file.refresh"); //$NON-NLS-1$
        setImageDescriptor(IconConstants.REFRESH_IMAGE_DESCRIPTOR); 
        setDisabledImageDescriptor(IconConstants.
                REFRESH_DISABLED_IMAGE_DESCRIPTOR);
        setId(ActionFactory.REFRESH.getId());
    }
}