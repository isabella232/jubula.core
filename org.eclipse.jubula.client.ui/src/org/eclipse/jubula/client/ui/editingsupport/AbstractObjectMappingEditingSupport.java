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
package org.eclipse.jubula.client.ui.editingsupport;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameMapper;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.ui.provider.contentprovider.objectmapping.ObjectMappingRow;
import org.eclipse.jubula.client.ui.widgets.CompNamePopupTextCellEditor;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;


/**
 * Base class for support for cell editing in the Object Mapping Editor's table
 * view.
 * 
 * @author BREDEX GmbH
 * @created Oct 23, 2008
 */
public abstract class AbstractObjectMappingEditingSupport extends
        EditingSupport {

    /** Mapping from technical name type to editor */
    private Map<String, CompNamePopupTextCellEditor> m_editorMap;

    /** The mapper to use for finding and modifying Component Names */
    private IComponentNameMapper m_compNameMapper;
    
    /**
     * Constructor
     * 
     * @param compNameMapper
     *            The mapper to use for finding and modifying Component Names.
     * @param viewer
     *            The viewer where the editing will take place.
     */
    public AbstractObjectMappingEditingSupport(
            IComponentNameMapper compNameMapper, TableViewer viewer) {
        super(viewer);
        m_compNameMapper = compNameMapper;
        m_editorMap = new HashMap<String, CompNamePopupTextCellEditor>();
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected boolean canEdit(Object element) {
        return true;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected CellEditor getCellEditor(Object element) {
        if (element instanceof ObjectMappingRow) {
            ObjectMappingRow row = (ObjectMappingRow)element;
            IObjectMappingAssoziationPO assoc = row.getAssociation();
            if (assoc != null) {
                IComponentIdentifier compId = assoc.getTechnicalName();
                if (compId != null) {
                    String typeFilter = compId.getSupportedClassName();
                    CompNamePopupTextCellEditor editor =
                        new CompNamePopupTextCellEditor(
                                m_compNameMapper, 
                                getViewer().getTable());
                    editor.setFilter(typeFilter);
                    m_editorMap.put(typeFilter, editor);
                    return editor;
                }
            }
        }
        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void setValue(Object element, Object value) {
        doSetValue(element, value);
        getViewer().update(element, null);
    }

    /**
     * Set the new value for the given element.
     * 
     * @param element The element for which the value must be changed.
     * @param value The new value.
     */
    protected abstract void doSetValue(Object element, Object value);
    
    /**
     * 
     * {@inheritDoc}
     */
    public TableViewer getViewer() {
        return (TableViewer)super.getViewer();
    }

}
