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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Jun 24, 2009
 */
public class NewLogicalCompNameDialog extends EnterLogicalCompNameDialog {

    /**
     * @param compNamesMapper see EnterLogicalCompNameDialog
     * @param parentShell  see EnterLogicalCompNameDialog
     */
    public NewLogicalCompNameDialog(IComponentNameMapper compNamesMapper,
            Shell parentShell) {
        super(compNamesMapper, parentShell);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.NewLogicalNameDialogTitle);
        setTitleImage(IconConstants.NEW_COMPONENT_DIALOG_IMAGE);
        setMessage(Messages.NewLogicalNameDialogMessage);
        getShell().setText(Messages.NewLogicalNameDialogTitle);
        return super.createDialogArea(parent);
    }

    
}
