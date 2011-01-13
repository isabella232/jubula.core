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
package org.eclipse.jubula.client.ui.dialogs;

import java.util.Set;

import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Jun 29, 2010
 */
public class NewTestDataManagerDialog extends EnterTestDataManagerDialog {
    /**
     * @param parentShell see EnterTestDataManagerDialog
     * @param usedNames a set of already used names
     */
    public NewTestDataManagerDialog(Shell parentShell, Set<String> usedNames) {
        super(parentShell, usedNames);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(I18n.getString("NewTestDataCubeDialog.Title")); //$NON-NLS-1$
        setTitleImage(IconConstants.NEW_TESTDATAMANAGER_DIALOG_IMAGE);
        setMessage(I18n.getString("NewTestDataCubeDialog.Message")); //$NON-NLS-1$
        getShell().setText(I18n.getString("NewTestDataCubeDialog.Title")); //$NON-NLS-1$
        return super.createDialogArea(parent);
    }
}
