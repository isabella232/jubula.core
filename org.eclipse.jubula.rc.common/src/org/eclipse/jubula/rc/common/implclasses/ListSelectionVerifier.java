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
package org.eclipse.jubula.rc.common.implclasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.tools.constants.TestDataConstants;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.utils.StringParsing;


/**
 * @author BREDEX GmbH
 * @created 28.08.2007
 */
public class ListSelectionVerifier {

    /**
     * Storage for slection items
     */
    private class SelectionItem {
        /** display value of item */
        private String m_value;
        /** is this item selected */
        private boolean m_selected;
        /** was this item matched */
        private boolean m_visited = false;
        
        /**
         * set all data
         * @param value display value of item
         * @param selected is this item selected
         */
        public SelectionItem(String value, boolean selected) {
            m_value = value;
            m_selected = selected;
        }

        /**
         * @return the visited
         */
        public boolean isVisited() {
            return m_visited;
        }

        /**
         * @param visited the visited to set
         */
        public void setVisited(boolean visited) {
            m_visited = visited;
        }

        /**
         * @return the selected
         */
        public boolean isSelected() {
            return m_selected;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return m_value;
        }        
    }
    
    /** list of selection items */
    private List m_itemList = new ArrayList();
    
    /**
     * Add an item to the list of selection items
     * @param index position in the list
     * @param value text value of item
     * @param isSelected slection state of item
     */
    public void addItem(int index, String value, boolean isSelected) {
        m_itemList.add(index, new SelectionItem(value, isSelected));
    }
    
    /**
     * Verifies that a user supplied pattern is consisted with the currently
     * selected items. Every pattern must match at least one item, every
     * selected item must be matched by at least one pattern (if no additional
     * selections are allowed).
     * 
     * @param patternString
     *            Comma separated list of patterns
     * @param op
     *            the operation, i.e. equals or match
     * @param allowAdditionalSelections
     *            if true, more selection than matched by the pattern are allowed
     * @param isSelected check for selection state
     */
    public void verifySelection(String patternString, String op,
        boolean allowAdditionalSelections, boolean isSelected) {
        String[] pattern = StringParsing.splitToArray(patternString,
            TestDataConstants.VALUE_CHAR_DEFAULT,
            TestDataConstants.ESCAPE_CHAR_DEFAULT);
        for (int i = 0; i < pattern.length; ++i) {
            boolean hit = false;
            for (Iterator iter = m_itemList.iterator(); iter.hasNext();) {
                SelectionItem item = (SelectionItem)iter.next();

                if (MatchUtil.getInstance().match(item.getValue(), pattern[i],
                    op)) {
                    if (item.isSelected() != isSelected) {
                        throw new StepExecutionException(
                            "Unselected element matches " //$NON-NLS-1$
                                + patternString, EventFactory
                                .createVerifyFailed(item.getValue(),
                                    pattern[i], op));

                    }
                    item.setVisited(true);
                    hit = true;
                }
            }
            if (!hit && isSelected) {
                throw new StepExecutionException("No list element matches " //$NON-NLS-1$
                    + patternString, EventFactory.createVerifyFailed(
                        this.toString(), pattern[i], op));

            }
        }
        if (!allowAdditionalSelections) {
            for (Iterator iter = m_itemList.iterator(); iter.hasNext();) {
                SelectionItem item = (SelectionItem)iter.next();
                if (item.isSelected() && !item.isVisited()) {
                    throw new StepExecutionException("More list element match", //$NON-NLS-1$
                        EventFactory.createVerifyFailed(this.toString(), 
                            patternString, op)); 

                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer res = new StringBuffer();
        for (Iterator iter = m_itemList.iterator(); iter.hasNext();) {
            SelectionItem element = (SelectionItem)iter.next();
            if (element.isSelected()) {
                res.append(element.getValue());
                res.append(TestDataConstants.VALUE_CHAR_DEFAULT);
            }
        }
        if (res.length() > 0) {
            res.deleteCharAt(res.length() - 1);
        }
        return res.toString();
    }
    
}
