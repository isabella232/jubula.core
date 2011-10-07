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
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;


/**
 * Utility methods for "Delete" handlers
 *
 * @author BREDEX GmbH
 * @created Mar 6, 2009
 */
public class DeleteHandlerHelper {

    /**
     * Private constructor for utility class.
     */
    private DeleteHandlerHelper() {
        // Nothing to initialize
    }
    
    /**
     * Pops up a "confirmDelete" dialog.
     * 
     * @param itemNames
     *            The names of the items to be deleted.
     * @return <code>true</code>, if "yes" was clicked, 
     *         <code>false</code> otherwise.
     */
    public static boolean confirmDelete(Collection<String> itemNames) {
        String label = StringConstants.EMPTY;
        if (itemNames.size() == 1) {
            label = NLS.bind(Messages.DeleteTreeItemActionDeleteOneItem,
                itemNames.iterator().next());
        } else if (itemNames.size() == 0) {
            return false;  
        } else {
            label = NLS.bind(Messages.DeleteTreeItemActionDeleteMultipleItems,
                itemNames.size());
        }
        MessageDialog dialog = new MessageDialog(Plugin.getShell(), 
                Messages.DeleteTreeItemActionShellTitle,
            null, 
            label, MessageDialog.QUESTION, new String[] {
                Messages.DialogMessageButton_YES,
                Messages.DialogMessageButton_NO }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        if (dialog.getReturnCode() != 0) { //1= the NO button was pressed
            return false;
        }
        return true;
    }

    /**
     * Closes the editor for the given Node
     * @param node the node of the editor to be closed.
     */
    public static void closeOpenEditor(IPersistentObject node) {
        IEditorPart editor = Utils.getEditorByPO(node);
        if (editor != null) {
            editor.getSite().getPage().closeEditor(editor, false);
        }
    }

}
