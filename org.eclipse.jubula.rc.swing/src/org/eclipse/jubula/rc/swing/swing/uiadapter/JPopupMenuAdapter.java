/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.swing.uiadapter;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.eclipse.jubula.rc.common.uiadapter.interfaces.IMenuAdapter;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IMenuItemAdapter;
/**
 * Implementation of the menu interface for adapting the <code>JPopupMenu</code>.
 * In Swing we have three implementations of the menu interface because
 * the Interface is used for the <code>JMenubar</code>, the <code>JPopupMenu</code> and the <code>JMenu</code>.
 * All these behave same in the implementation.
 * 
 * @author BREDEX GmbH
 *
 */
public class JPopupMenuAdapter extends AbstractComponentAdapter
    implements IMenuAdapter {
    /** */
    private JPopupMenu m_contextMenu;
    
    /**
     * 
     * @param adaptee 
     */
    public JPopupMenuAdapter(Object adaptee) {
        m_contextMenu = (JPopupMenu) adaptee;
    }
    
    /** {@inheritDoc} */
    public Object getRealComponent() {
        return m_contextMenu;
    }

    /** {@inheritDoc} */
    public IMenuItemAdapter[] getItems() {
        Object[] menuItems = m_contextMenu.getSubElements();
        List adapters = new LinkedList();
        for (int i = 0; i < menuItems.length; i++) {
            if (menuItems[i] instanceof JMenuItem) {
                adapters.add(new JMenuItemAdapter(menuItems[i]));
            }
        }
        IMenuItemAdapter[] allitems = new IMenuItemAdapter[adapters.size()];
        adapters.toArray(allitems);
        return allitems;
    }

    /** {@inheritDoc} */
    public int getItemCount() {
        return m_contextMenu.getSubElements().length;
    }

    
}
