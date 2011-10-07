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
package org.eclipse.jubula.client.ui.rcp.provider.labelprovider;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.EditorPart;


/**
 * @author BREDEX GmbH
 * @created 07.12.2004
 */
public class DirtyStarListLabelProvider implements ILabelProvider {

    /**
     * {@inheritDoc}
     */
    public Image getImage(Object element) {
        Image image = null;
        Iterator editors = 
            Plugin.getDefault().getDirtyEditorNames().iterator();
        while (editors.hasNext()) {
            EditorPart editor = (EditorPart)Plugin
            .getEditorByTitle((String)editors.next());

            if (editor.getTitle().equals(element)) {
                image = editor.getTitleImage();
                if (!editor.isDirty() && (editor instanceof IJBEditor)) {
                    IJBEditor seditor = (IJBEditor) editor;
                    image = seditor.getDisabledTitleImage();
                }
                return image;
            }
        }
        return image;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText(Object element) {
        Iterator editors = 
            Plugin.getDefault().getDirtyEditorNames().iterator();
        while (editors.hasNext()) {
            EditorPart  editor = (EditorPart)Plugin
                .getEditorByTitle((String)editors.next());
            if (editor.getTitle().equals(element) && !editor.isDirty()) {
                return element.toString() 
                    + Messages.DirtyStarListLabelProviderAlreadySaved;
            }
        }
        return element.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    public void addListener(ILabelProviderListener listener) {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeListener(ILabelProviderListener listener) {
        // do nothing
    }
}