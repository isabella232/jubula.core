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
package org.eclipse.jubula.rc.swing.swing.implclasses;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;


/**
 * Implementation class for <code>javax.swing.JMenuItem</code>.
 *
 * @author BREDEX GmbH
 * @created 17.08.2005
 */
public class JMenuItemImplClass extends AbstractSwingImplClass {
    /**
     * The menu item.
     */
    private JMenuItem m_menuItem;
    /**
     * The helper to control default button operations.
     */
    private AbstractButtonHelper m_buttonHelper;
    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_menuItem = (JMenuItem)graphicsComponent;
        m_buttonHelper = new AbstractButtonHelper(this);
    }
    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return m_menuItem;
    }
    /**
     * Convenience method.
     *
     * @return The menut item
     */
    private JMenuItem getItem() {
        return (JMenuItem)getComponent();
    }
    /**
     * Finds the AWT components on top of the menu item component hierarchy and
     * returns them in a list.
     *
     * @return The list of components that point to the menu item
     */
    private List findMenuPath() {
        Container parent = null;
        Container current = getItem();

        List path = new ArrayList();
        path.add(current);

        do {
            parent = current.getParent();
            if (parent instanceof JPopupMenu) {
                JPopupMenu popupMenu = (JPopupMenu)parent;
                current = (Container)popupMenu.getInvoker();
            } else {
                current = parent;
            }
            if (((current instanceof JMenuItem) || (current instanceof JMenu))
                    && current.isShowing()) {
                path.add(current);
            }
        } while (current != null);

        Collections.reverse(path);
        return path;
    }
    /**
     * Performs a click on the menu item.
     */
    public void gdClick() {
        List path = findMenuPath();
        for (Iterator it = path.iterator(); it.hasNext();) {
            Component comp = (Component)it.next();
            getRobot().click(
                comp,
                null,
                ClickOptions.create().setClickType(
                    ClickOptions.ClickType.RELEASED));
        }
    }
    /**
     * Verifies the selected property.
     *
     * @param selected
     *            The selected property value to verify.
     */
    public void gdVerifySelected(boolean selected) {
        m_buttonHelper.verifySelected(selected);
    }
    /**
     * Verifies the passed text.
     *
     * @param text
     *            The text to verify
     * @param operator
     *            The operator used to verify
     */
    public void gdVerifyText(String text, String operator) {
        m_buttonHelper.verifyText(text, operator);
    }
    /**
     * Verifies the passed text.
     *
     * @param text
     *            The text to verify
     */
    public void gdVerifyText(String text) {
        m_buttonHelper.verifyText(text, MatchUtil.DEFAULT_OPERATOR);
    }

    /**
     * {@inheritDoc}
     */
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return new String[] {m_menuItem.getText()};
    }

    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return always null
     */
    protected String getText() {
        return null;
    }

}
