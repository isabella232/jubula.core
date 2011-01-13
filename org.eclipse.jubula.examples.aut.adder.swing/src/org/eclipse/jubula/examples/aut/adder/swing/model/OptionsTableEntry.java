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
package org.eclipse.jubula.examples.aut.adder.swing.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a row in the options table.
 *
 * @author BREDEX GmbH
 * @created 29.03.2005
 */
public class OptionsTableEntry {
    /**
     * The list of row columns.
     */
    private List m_columns = new ArrayList();
    /**
     * @param description The first column.
     * @param value The second column.
     */
    public OptionsTableEntry(String description, Object value) {
        m_columns.add(description);
        m_columns.add(value);
    }
    /**
     * @return The first column.
     */
    public String getDescription() {
        return (String)m_columns.get(0);
    }
    /**
     * @param description The first column.
     */
    public void setDescription(String description) {
        m_columns.set(0, description);
    }
    /**
     * @return The second column.
     */
    public Object getValue() {
        return m_columns.get(1);
    }
    /**
     * @param value The second column.
     */
    public void setValue(Object value) {
        m_columns.set(1, value);
    }
    /**
     * @param col The column index.
     * @return The column value.
     */
    public Object getColumn(int col) {
        return m_columns.get(col);
    }
    /**
     * @param col The column index.
     * @return The class of the column value.
     */
    public Class getColumnClass(int col) {
        return m_columns.get(col).getClass();
    }
}
