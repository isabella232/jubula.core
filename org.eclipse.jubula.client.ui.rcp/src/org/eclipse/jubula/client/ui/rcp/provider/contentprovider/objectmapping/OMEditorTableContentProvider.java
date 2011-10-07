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
package org.eclipse.jubula.client.ui.rcp.provider.contentprovider.objectmapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.ui.rcp.editors.PersistableEditorInput;


/**
 * Content provider for the Object Mapping Editor's table view.
 *
 * @author BREDEX GmbH
 * @created Oct 21, 2008
 */
public class OMEditorTableContentProvider 
        implements IStructuredContentProvider {

    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof IObjectMappingPO) {
            Set<ObjectMappingRow> rowSet = new HashSet<ObjectMappingRow>();
            for (IObjectMappingAssoziationPO assoc 
                    : ((IObjectMappingPO)inputElement).getMappings()) {
                
                String techName = null;
                if (assoc.getTechnicalName() != null) {
                    techName = assoc.getTechnicalName().getName();
                }
                List<String> logicalNames = assoc.getLogicalNames();
                
                if (logicalNames.isEmpty() && techName != null) {
                    rowSet.add(new ObjectMappingRow(
                            assoc, ObjectMappingRow.NO_COMP_NAME));
                }
                
                for (int i = 0; i < logicalNames.size(); i++) {
                    rowSet.add(new ObjectMappingRow(assoc, i));
                }
            }

            return rowSet.toArray();
        } else if (inputElement instanceof IAUTMainPO) {
            return getElements(((IAUTMainPO)inputElement).getObjMap());
        } else if (inputElement instanceof PersistableEditorInput) {
            return getElements(((PersistableEditorInput)inputElement)
                    .getEditSupport().getWorkVersion());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // Nothing to dispose.
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Do nothing.
    }

}
