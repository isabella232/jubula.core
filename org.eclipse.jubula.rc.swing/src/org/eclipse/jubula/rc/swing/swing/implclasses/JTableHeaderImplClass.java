/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.swing.implclasses;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.ObjectUtils;

/**
 * This class implements actions on the Swing JTableHeader.
 *
 * @author BREDEX GmbH
 * @created 24.05.2011
 */
public class JTableHeaderImplClass extends AbstractSwingImplClass {

    /** the table header on which actions are performed */
    private JTableHeader m_tableHeader;
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_tableHeader = (JTableHeader)graphicsComponent;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        final String[] componentTextArray;
        TableColumnModel columnModel = m_tableHeader.getColumnModel();
        if (columnModel == null) {
            componentTextArray = null;
        } else {
            componentTextArray = new String[columnModel.getColumnCount()];
            for (int i = 0; i < componentTextArray.length; i++) {
                componentTextArray[i] = getRenderedHeaderText(m_tableHeader, i);
            }
        }
        return componentTextArray;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return m_tableHeader;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected String getText() {
        Point headerLocation = m_tableHeader.getLocationOnScreen();
        Point currentMousePosition = getRobot().getCurrentMousePosition();
        TableColumnModel columnModel = m_tableHeader.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            Rectangle headerCellBounds = m_tableHeader.getHeaderRect(i);
            headerCellBounds.setLocation(headerLocation);
            
            // causes contains() to also return true if the point
            // is on the edge of the "pre-grow" bounds
            headerCellBounds.grow(1, 1);
            
            if (headerCellBounds.contains(currentMousePosition)) {
                return getRenderedHeaderText(m_tableHeader, i);
            }
        }

        return null;
    }

    /**
     * 
     * @param header The table header.
     * @param columnIndex The column index.
     * @return the text rendered in the header at the given column index. 
     */
    private static String getRenderedHeaderText(
            JTableHeader header, int columnIndex) {
        
        TableColumn column = header.getColumnModel().getColumn(columnIndex);
        if (column != null) {
            Object headerValue = column.getHeaderValue();
            if (headerValue != null) {
                TableCellRenderer renderer = column.getHeaderRenderer();
                if (renderer == null) {
                    renderer = header.getDefaultRenderer();
                }
                if (renderer != null) {
                    return getRenderedText(
                            renderer.getTableCellRendererComponent(
                                    header.getTable(), headerValue, false, 
                                    false, -1, columnIndex));
                }
                
                return ObjectUtils.toString(headerValue);
            }
        }
        
        return null;
    }
}
