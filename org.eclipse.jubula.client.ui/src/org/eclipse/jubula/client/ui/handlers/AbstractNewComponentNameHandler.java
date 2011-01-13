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
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameMapper;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.dialogs.EnterLogicalCompNameDialog;
import org.eclipse.jubula.client.ui.dialogs.NewLogicalCompNameDialog;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Mar 13, 2009
 */
public abstract class AbstractNewComponentNameHandler extends AbstractHandler {

    /**
     * Opens the "New Component Name..." dialog.
     * 
     * @param compNameMapper The mapper to use for finding Component Names.
     * @param parentShell The parent Shell for the dialog.
     * @return the name typed into the dialog, or <code>null</code> if the 
     *         dialog was cancelled.
     */
    protected String openDialog(
            IComponentNameMapper compNameMapper, Shell parentShell) {

        EnterLogicalCompNameDialog newNameDialog = 
            new NewLogicalCompNameDialog(compNameMapper, parentShell);
        newNameDialog.setHelpAvailable(true);
        newNameDialog.create();
        DialogUtils.setWidgetNameForModalDialog(newNameDialog);
        Plugin.getHelpSystem().setHelp(newNameDialog.getShell(), 
                ContextHelpIds.NEW_COMPONENT_NAME);
        
        if (newNameDialog.open() == Window.OK) {
            return newNameDialog.getName();
        }

        return null;
    }

    /**
     * Creates the Component Name.
     * 
     * @param newName The name for the new Component Name.
     * @param mapper The mapper to which the new Component Name should be added.
     * @return the newly created Component Name.
     */
    protected IComponentNamePO performOperation(
            String newName, IWritableComponentNameMapper mapper) {
        
        String compType = ComponentBuilder.getInstance()
            .getCompSystem().getMostAbstractComponent().getType();
        IComponentNamePO compNamePo = 
            mapper.getCompNameCache().createComponentNamePO(
                    newName, compType, 
                    CompNameCreationContext.OBJECT_MAPPING);
        return compNamePo;
    }

}