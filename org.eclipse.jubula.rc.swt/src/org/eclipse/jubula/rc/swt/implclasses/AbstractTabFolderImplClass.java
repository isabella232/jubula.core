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
package org.eclipse.jubula.rc.swt.implclasses;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.swt.graphics.Rectangle;


/**
 * @author BREDEX GmbH
 * @created 23.04.2007
 */
public abstract class AbstractTabFolderImplClass 
    extends AbstractControlImplClass {

    /**
     * Selects the tab with the passed index. The method doesn't care if the tab is enabled or not. 
     * @param index The tab index
     * @throws StepExecutionException If the tab index is invalid.
     */
    public void gdSelectTabByIndex(int index)
        throws StepExecutionException {
        int implIdx = IndexConverter.toImplementationIndex(index);
        
        selectTabByImplIndex(implIdx);
    }
    
    /**
     * Selects the tab with the passed index. The method doesn't care if the tab is enabled or not. 
     * @param index The tab index
     */
    private void selectTabByImplIndex(int index) {
        verifyIndexExists(index);
        ensureTabIsShowing(index);
        
        // Some tab items (like in Eclipse) have a close button embedded in them.
        // In order to reduce the chance of clicking this close button, we click
        // at x-coordinate 25% rather than 50%.
        getRobot().click(
                        getComponent(),
                        getBoundsAt(index),
                        ClickOptions.create().left(), 25, false,
                        50, false);
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
                    return getTitleOfTab(tabIndex);
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
     * @throws StepExecutionException
     *             If the tab title is invalid.
     */
    public void gdVerifyExistenceOfTab(final String tab, final String operator,
            boolean exists)
        throws StepExecutionException {
        Boolean tabExists = (Boolean)getEventThreadQueuer().invokeAndWait(
            "verifyExistenceOfTab", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    return new Boolean(indexOrTitleOfTabExists(tab, operator));
                }
            });
        Verifier.equals(exists, tabExists.booleanValue());
    }

    /**
     * Removes the shortcut sign (&) of the given tab name.
     * @param tabName a tab name
     * @return the tab name without shortcut sign or the given tabName
     *         if there is no shortcut sign.
     */
    protected String removeShortcutSign(String tabName) {
        int shortCutIdx = tabName.indexOf("&"); //$NON-NLS-1$
        StringBuffer buf = new StringBuffer(tabName);
        if (shortCutIdx > -1) {
            buf.deleteCharAt(shortCutIdx);
            return buf.toString();
        }
        return tabName;
    }

    /**
     * checks if the given tab index is valid
     * @param index a tab index
     */
    protected abstract void verifyIndexExists(final int index);

    /**
     * Ensures that the tab at the given index is visible (i.e. 
     * can be selected).
     * 
     * @param index a tab index
     */
    protected abstract void ensureTabIsShowing(final int index);

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
     * @param index The tab index
     * @return The relative bounds of the tab title area 
     */
    protected abstract Rectangle getBoundsAt(final int index);

    /**
     * @param title The tab title
     * @param operator The matching operator
     * @return The tab index
     */
    protected abstract int getIndexOfTab(
        final String title, final String operator);
    
    /**
     * @param index The tab index
     * @return The tab title
     */
    protected abstract String getTitleOfTab(final int index);
    
    /**
     * @param tab The tab index/title
     * @param operator The matching operator
     * @return true if tab exists, false otherwise
     */
    protected abstract boolean indexOrTitleOfTabExists(final String tab,
            final String operator);

}
