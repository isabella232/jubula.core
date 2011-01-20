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
package org.eclipse.jubula.client.ui.widgets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


/**
 * @author BREDEX GmbH
 * @created 13.02.2006
 */
public abstract class AbstractToolTipListener implements Listener {

    /***/
    private Control m_toolTipOwner;
    /***/
    private JBText m_toolTipContent;
    /***/
    private Shell m_tip;
    /***/
    private Listener m_labelListener = new LabelListener();
    
    /**
     * Constructor
     * @param toolTipOwner the owner of this Tooltip
     */
    public AbstractToolTipListener(Control toolTipOwner) {
        m_toolTipOwner = toolTipOwner;
        m_toolTipOwner.addListener(SWT.Dispose, this);
        m_toolTipOwner.addListener(SWT.KeyDown, this);
        m_toolTipOwner.addListener(SWT.MouseMove, this);
        m_toolTipOwner.addListener(SWT.MouseHover, this);
        m_toolTipContent = new JBText(m_toolTipOwner.getShell(), 
            Layout.MULTI_TEXT);
    }
    
    /**
     * {@inheritDoc}
     * @param event
     */
    public void handleEvent(Event event) {
        switch (event.type) {
            case SWT.Dispose:
            case SWT.KeyDown:
            case SWT.MouseMove: {
                if (m_tip == null) { 
                    break;
                }
                m_tip.dispose();
                m_tip = null;
                m_toolTipContent = null;
                break;
            }
            case SWT.MouseHover: {
                Point point = new Point(event.x, event.y);
                Item item = getItem(m_toolTipOwner, point);
                if (item != null) {
                    if (m_tip != null && !m_tip.isDisposed()) {
                        m_tip.dispose();
                    }
                    if (!checkCreateToolTip(item, point, getToolTipParent())) {
                        return;
                    }
                    m_tip = new Shell(m_toolTipOwner.getShell(), 
                        SWT.ON_TOP | SWT.TOOL);            
                    m_tip.setLayout(new FillLayout());
                    m_toolTipContent = new JBText(m_tip, Layout.MULTI_TEXT 
                        | SWT.READ_ONLY);
                    m_toolTipContent.setForeground(m_toolTipOwner.getDisplay()
                        .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                    m_toolTipContent.setBackground(m_toolTipOwner.getDisplay()
                        .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                    m_toolTipContent.setData("_TABLEITEM", item); //$NON-NLS-1$
                    m_toolTipContent.setText(getTextOfItem(item, 
                        getColumnForToolTip()));
                    m_toolTipContent.addListener(SWT.MouseExit, 
                        m_labelListener);
                    m_toolTipContent.addListener(SWT.MouseDown, 
                        m_labelListener);
                    
                    Rectangle itemBounds = getBoundsOfItem(item, 
                        getColumnForToolTip());
                    int height = Dialog.convertHeightInCharsToPixels(
                        Layout.getFontMetrics(m_toolTipContent), 
                        m_toolTipContent.getLineCount() + 2);
                    Point pt = m_toolTipOwner.toDisplay(itemBounds.x, 
                        itemBounds.y);
                    m_tip.setBounds(pt.x, pt.y, itemBounds.width, height);
                    m_tip.setVisible(true);
                }
            }
            default : {
                break;
            }
        }
    }
    
    /**
     * @return the parent of the toolTip (e.g. tree or table, ...)
     */
    protected abstract Composite getToolTipParent();

    /**
     * <p><b>Should be overwritten from subclass.</b></p>
     * @return the column number for the tool tip
     */
    protected abstract int getColumnForToolTip();
 
    /**
     * Gets all items of a Tree.<br>
     * Call this method with:<br>
     * <code>getItemsOfTree(tree.getItems(), new TreeItems[0])</code>
     * @param items the first level TreeItems
     * @param allItems the concatenated TreeItems, 
     * start with <code>new TreeItem[0]</code>
     * @return an Array of all TreeItems of the Tree
     */
    public static TreeItem[] getItemsOfTree(TreeItem[] items, 
        TreeItem[] allItems) {
        
        TreeItem[] concItems = new TreeItem[items.length + allItems.length];
        System.arraycopy(items, 0, concItems, 0, items.length);
        System.arraycopy(allItems, 0, concItems, items.length, allItems.length);
        
        for (TreeItem item : items) {
            concItems = getItemsOfTree(item.getItems(), concItems);
        }
        return concItems;
    } 
    
    /**
     * <p>Checks, if the ToolTip should be created or not.</p>
     * <p><b>Should be overwritten from subclass.</b></p>
     * @param item the current item
     * @param point the Point of the Mouse-Cursor
     * @param parent the parent composite of the tooltip.
     * @return true if ToolTip should be created, false otherwise
     */
    protected boolean checkCreateToolTip(Item item, Point point, 
        Composite parent) {
        
        Tree tree = (Tree)getToolTipOwner();
        TreeItem[] items = getItemsOfTree(tree.getItems(), new TreeItem[0]);
        for (TreeItem treeitem : items) {
            for (int i = 0; i < tree.getColumnCount(); i++) {
                Rectangle rect = treeitem.getBounds(i);
                if (rect.contains(point)
                    && rect.equals(getBoundsOfItem(item, 
                        getColumnForToolTip()))) {

                    JBText t = new JBText(parent, SWT.NONE);
                    t.setVisible(false);
                    t.setText(getTextOfItem(item, getColumnForToolTip()));
                    GC gc = new GC(t);
                    int textPixels = gc.textExtent(t.getText() + "  ").x; //$NON-NLS-1$
                    gc.dispose();
                    t.dispose();
                    int textFieldPixels = getBoundsOfItem(
                        item, getColumnForToolTip()).width;
                    if (textPixels <= textFieldPixels) {
                        return false;
                    }
                    return true;                         
                }
            }
        }
        return false;
    }
    
    /**
     * gets the item of the given Control at the given Point.
     * @param control the Control
     * @param point The Point
     * @return an Item at the given Point
     */
    protected Item getItem(Control control, Point point) {
        if (control instanceof Tree) {
            return ((Tree)control).getItem(point);
        }
        if (control instanceof Table) {
            ((Table)control).getItem(point);
        }
        return null;
    }
    
    /**
     * Returns the Bounds of the given Item with the given index.
     * @param item the Item
     * @param index the index
     * @return a Rectangle, the bounds.
     */
    protected Rectangle getBoundsOfItem(Item item, int index) {
        if (item instanceof TableItem) {
            return ((TableItem)item).getBounds(index);
        } 
        if (item instanceof TreeItem) {
            return ((TreeItem)item).getBounds(index);
        }
        return new Rectangle(0, 0, 0, 0);
    }
    
    /**
     * Returns the text of the given Item with the given index.
     * @param item the Item
     * @param index the index
     * @return a String, the text.
     */
    protected String getTextOfItem(Item item, int index) {
        if (item instanceof TableItem) {
            return ((TableItem)item).getText(index);
        } 
        if (item instanceof TreeItem) {
            return ((TreeItem)item).getText(index);
        }
        return StringConstants.EMPTY;
    }
    
    
    /**
     * 
     * 
     *
     * @author BREDEX GmbH
     * @created 13.02.2006
     */
    private class LabelListener implements Listener {

        /**
         * {@inheritDoc}
         * @param event
         */
        public void handleEvent(Event event) {
            Tree tree = (Tree)m_toolTipOwner;
            JBText label = (JBText) event.widget;
            Shell shell = label.getShell();
            switch (event.type) {
                case SWT.MouseDown:
                    Event e = new Event();
                    e.item = (Item) label.getData("_TABLEITEM"); //$NON-NLS-1$
                    // Assuming table is single select, set the selection as if
                    // the mouse down event went through to the table
                    tree.setSelection(new TreeItem[] { (TreeItem) e.item });
                    tree.notifyListeners(SWT.Selection, e);
                  // fall through
                case SWT.MouseExit:
                    shell.dispose();
                    break;
                default: {
                    break;
                }
            }
        }
    }

    /**
     * @return Returns the toolTipOwner.
     */
    protected Control getToolTipOwner() {
        return m_toolTipOwner;
    }
}