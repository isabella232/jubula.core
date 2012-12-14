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

import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.swing.swing.interfaces.IJTabbedPane;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;


/**
 * This class implements actions on the Swing <code>JTabbedPane</code>.
 * @author BREDEX GmbH
 * @created 20.05.2005
 */
public class JTabbedPaneImplClass extends AbstractSwingImplClass 
    implements IJTabbedPane {

    /** The JTabbedPane on which the actions are performed. */
    private JTabbedPane m_pane;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_pane = (JTabbedPane)graphicsComponent;
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return m_pane;
    }

    /**
     * @param title The tab title
     * @param operator The matching operator
     * @return The tab index
     */
    private int getIndexOfTab(final String title, final String operator) {

        int index = ((Integer)getEventThreadQueuer().invokeAndWait(
                "indexOfTab", //$NON-NLS-1$
                new IRunnable() {

                public Object run() throws StepExecutionException {

                    int tabs = m_pane.getTabCount();
                    for (int a = 0; a < tabs; a++) {
                        if (MatchUtil.getInstance().match(
                            m_pane.getTitleAt(a),
                            title,
                            operator)) {

                            return new Integer(a);
                        }
                    }

                    return new Integer(-1);
                }
            })).intValue();

        if (index == -1) {
            throw new StepExecutionException(
                "Can not find tab: '" + title + "' using operator: '"  //$NON-NLS-1$ //$NON-NLS-2$
                + operator + "'", EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.NOT_FOUND));
        }
        return index;

    }

    /**
     * Selects the tab with the passed index. The method doesn't care if the tab is enabled or not.
     * @param index The tab index
     */
    private void selectTabByImplIndex(int index) {
        verifyIndexExists(index);

        // FIXME zeb: We currently ignore the possibility of needing to scroll
        //            or use a pulldown menu to find the tab item. This means
        //            that the user must know when this type of action is
        //            necessary and specify their tests accordingly. We may wish
        //            to change this later so that it is "smarter" (i.e. can
        //            scroll or use a pulldown menu to find tab items in a crowded
        //            tab folder).

        // Some tab  items have a close button embedded in them.
        // In order to reduce the chance of clicking this close button, we click
        // at x-coordinate 25% rather than 50%.
        getRobot().click(m_pane, getBoundsAt(index), 
            ClickOptions.create().left(), 25, false, 50, false);

    }

    /**
     * @param index The tab index
     * @return The bounds of the tab title area
     * {@inheritDoc}
     */
    private Rectangle getBoundsAt(final int index) {
        return (Rectangle)getEventThreadQueuer().invokeAndWait("getBoundsAt", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    return m_pane.getBoundsAt(index);
                }
            });
    }

    /**
     * checks if the given tab index is valid
     * @param index a tab index
     * @throws StepExecutionException if the tab index does not exist
     */
    private void verifyIndexExists(final int index)
        throws StepExecutionException {
        Boolean exists = (Boolean)getEventThreadQueuer().invokeAndWait(
            "indexOfTab", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    int i = m_pane.getTabCount();
                    return ((index >= 0) && (index < i))
                        ? Boolean.TRUE : Boolean.FALSE;
                }
            });
        if (!exists.booleanValue()) {
            throw new StepExecutionException(
                "The tab index doesn't exist: " + index, EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.NOT_FOUND));
        }
    }

    /**
     * Selects the tab with the passed index.
     * The method doesn't care if the tab is enabled or not.
     *
     * @param index
     *            The tab index
     * @throws StepExecutionException
     *             If the tab index is invalid.
     */
    public void gdSelectTabByIndex(int index)
        throws StepExecutionException {
        int implIdx = IndexConverter.toImplementationIndex(index);

        selectTabByImplIndex(implIdx);
    }
    /**
     * Selects the tab with the passed title. The method doesn't care if the tab
     * is enabled or not.
     *
     * @param title
     *            The tab title
     * @param operator
     *      using regex
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void gdSelectTab(final String title, String operator)
        throws StepExecutionException {

        selectTabByImplIndex(getIndexOfTab(title, operator));

    }

    /**
     * Verifies the selection of the tab with the passed index.
     *
     * @param index
     *            The tab index
     * @param selected
     *            Should the tab be selected?
     * @throws StepExecutionException
     *             If the tab index is invalid.
     */
    public void gdVerifySelectedTabByIndex(int index, boolean selected)
        throws StepExecutionException {
        int implIdx = IndexConverter.toImplementationIndex(index);
        int selIndex = ((Integer)getEventThreadQueuer().invokeAndWait(
                "getSelectedIndex", //$NON-NLS-1$
                new IRunnable() {

                public Object run() throws StepExecutionException {
                    return new Integer(m_pane.getSelectedIndex());
                }
            })).intValue();

        if (selIndex == -1) {
            if (!selected) {
                return;
            }
            throw new StepExecutionException(
                I18n.getString(TestErrorEvent.NO_SELECTION),
                EventFactory.createActionError(TestErrorEvent.NO_SELECTION));
        }

        Verifier.equals(selected, selIndex == implIdx);
    }

    /**
     * Verifies the selection of the tab with the passed title.
     *
     * @param tabTitlePattern
     *            The tab title pattern to use for checking
     * @param operator
     *            Operator to be executed
     * @param selected
     *            Should the tab be selected?
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void gdVerifySelectedTab(String tabTitlePattern, String operator,
            boolean selected)
        throws StepExecutionException {
        String selectedTabTitle = (String)getEventThreadQueuer().invokeAndWait(
            "getSelectedTitle", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    int selectedIndex = m_pane.getSelectedIndex();
                    if (selectedIndex == -1) {
                        // No selection
                        return null;
                    }

                    return m_pane.getTitleAt(selectedIndex);
                }
            });

        if (selectedTabTitle == null) {
            if (!selected) {
                return;
            }
            throw new StepExecutionException(
                I18n.getString(TestErrorEvent.NO_SELECTION),
                EventFactory.createActionError(TestErrorEvent.NO_SELECTION));
        }
        Verifier.match(selectedTabTitle, tabTitlePattern, operator, selected);
    }
    /**
     * Verifies if the tab with the passed title is enabled.
     *
     * @param title The tab title
     * @param operator operation to be executed
     * @param isEnabled wether to test if the tab  is enabled or not
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void gdVerifyEnabled(String title, String operator,
        final boolean isEnabled)

        throws StepExecutionException {
        final int tabIndex = getIndexOfTab(title, operator);

        verify(isEnabled, "isEnabledAt", //$NON-NLS-1$
            new IRunnable() {

                public Object run() throws StepExecutionException {
                    return m_pane.isEnabledAt(tabIndex) ? Boolean.TRUE
                        : Boolean.FALSE;
                }
            });
    }

    /**
     * Verifies if the tab with the passed index is enabled.
     *
     * @param index
     *            The tab index
     * @param enabled
     *            Should the tab be enabled?
     * @throws StepExecutionException
     *             If the tab index is invalid.
     */
    public void gdVerifyEnabledByIndex(int index, boolean enabled)
        throws StepExecutionException {
        final int implIdx = IndexConverter.toImplementationIndex(index);
        verifyIndexExists(implIdx);
        verify(enabled, "isEnabledAt", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    return m_pane.isEnabledAt(implIdx) ? Boolean.TRUE
                        : Boolean.FALSE;
                }
            });
    }
    
    /**
     * Verifies the text of the tab by index
     *
     * @param index index of tab
     * @param text The tab title
     * @param operator Operator to be executed
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void gdVerifyTextOfTabByIndex(final int index, final String text,
            final String operator)
        throws StepExecutionException {        
        final int tabIndex = IndexConverter.toImplementationIndex(index);
        String tabTitle = (String)getEventThreadQueuer().invokeAndWait(
            "verifyTextOfTabByIndex", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    checkTabIndex(tabIndex, m_pane.getTabCount());
                    return m_pane.getTitleAt(tabIndex);
                }
            });
        Verifier.match(tabTitle, text, operator);
    }
    
    /**
     * Verifies existence of tab by index/value
     *
     * @param tab index/value of tab
     * @param operator Operator to be executed
     * @param exists boolean, tab exists
     * @throws StepExecutionException if tab does not exist.
     */
    public void gdVerifyExistenceOfTab(String tab, String operator,
            boolean exists)
        throws StepExecutionException {
        final int tabIdx = getTabIndexFromString(tab, operator);
        Boolean tabExists = (Boolean)getEventThreadQueuer().invokeAndWait(
            "verifyExistenceOfTab", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    if (tabIdx < 0 || tabIdx >= m_pane.getTabCount()) {
                        return new Boolean(false);
                    }
                    return new Boolean(true);
                }
            });
        Verifier.equals(exists, tabExists.booleanValue());
    }

    /**
     * @param tab index or title of tab
     * @param operator Operator to be executed
     * @return returns index of tab if exists, -1 otherwise
     */
    private int getTabIndexFromString(String tab, String operator) {
        int tabIndex = -1;
        try {
            tabIndex = IndexConverter.toImplementationIndex(
                    Integer.parseInt(tab));
        } catch (NumberFormatException nfe) {
            for (int i = 0; i < m_pane.getTabCount(); i++) {
                String text = m_pane.getTitleAt(i);
                if (MatchUtil.getInstance().match(text, tab, operator)) {
                    return i;
                }
            }
            
        }
        return tabIndex;
    }
    
    /**
     * Checks if index exists in tabbedpane
     *
     * @param index The index of tab.
     * @param count the tab count.
     */
    private void checkTabIndex(int index, int count) {
        if ((index < 0) || (index >= count)) {
            throw new StepExecutionException(
                    "Invalid Tab Index/Title: " + index, //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.INVALID_INDEX));
        }
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        final String[] componentTextArray;
        componentTextArray = new String[m_pane.getTabCount()];
        for (int i = 0; i < componentTextArray.length; i++) {
            componentTextArray[i] = m_pane.getTitleAt(i);
        }
        return componentTextArray;
    }

    /**
     * @see org.eclipse.jubula.rc.swing.swing.implclasses.AbstractSwingImplClass#getText()
     * @return always null
     */
    protected String getText() {
        return null;
    }

}
