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

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IndexConverter;
import org.eclipse.jubula.rc.common.implclasses.MatchUtil;
import org.eclipse.jubula.rc.common.implclasses.Verifier;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.objects.event.EventFactory;
import org.eclipse.jubula.tools.objects.event.TestErrorEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;


/**
 * Implementation class for SWT-CTabFolder
 *
 * @author BREDEX GmbH
 * @created Oct 30, 2006
 */
public class CTabFolderImplClass extends AbstractTabFolderImplClass {

    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(CTabFolderImplClass.class);

    /** the CTabFolder from the AUT */
    private CTabFolder m_ctabFolder;

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        m_ctabFolder = (CTabFolder)graphicsComponent;
    }
    
    /**
     * {@inheritDoc}
     */
    public Control getComponent() {
        return m_ctabFolder;
    }
    
    /**
     * @param title The tab title
     * @param operator The matching operator
     * @return The tab index
     */
    protected int getIndexOfTab(final String title, final String operator) {

        int index = ((Integer)getEventThreadQueuer().invokeAndWait(
                "indexOfTab", //$NON-NLS-1$
                new IRunnable() {
             
                public Object run() throws StepExecutionException {
                    for (int i = 0; i < m_ctabFolder.getItemCount(); i++) {
                        if (MatchUtil.getInstance().match(
                            SwtUtils.removeMnemonics(
                                m_ctabFolder.getItem(i).getText()), 
                            title, 
                            operator)) {

                            int j = m_ctabFolder.indexOf(m_ctabFolder.
                                getItem(i));
                            return new Integer(j);
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
     * {@inheritDoc}
     */
    protected String getTitleOfTab(final int index) {
        String tabTitle = (String)getEventThreadQueuer().invokeAndWait(
                "verifyTextOfTabByIndex", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() throws StepExecutionException {
                        verifyIndexExists(index);
                        return m_ctabFolder.getItem(index).getText();
                    }
                });
        return tabTitle;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected Rectangle getBoundsAt(final int index) {
        return (Rectangle)getEventThreadQueuer().invokeAndWait("getBoundsAt", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    return SwtUtils.getRelativeWidgetBounds(
                            m_ctabFolder.getItem(index), m_ctabFolder);
                }
            });
    }
    
    /**
     * checks if the given tab index is valid
     * @param index a tab index
     * @throws StepExecutionException if the tab index does not exist
     */
    protected void verifyIndexExists(final int index) 
        throws StepExecutionException {
        Boolean exists = (Boolean)getEventThreadQueuer().invokeAndWait(
            "indexOfTab", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    int i = m_ctabFolder.getItemCount();
                    return ((index >= 0) && (index < i)) 
                        ? Boolean.TRUE : Boolean.FALSE;
                }
            });
        if (!exists.booleanValue()) {
            throw new StepExecutionException(
                "The tab index doesn't exist: " + index, EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.INVALID_INDEX));
        }
    }

    /**
     * Verifies the selection of the tab with the passed index.
     * @param index The tab index
     * @param selected Should the tab be selected?
     * @throws StepExecutionException If the tab index is invalid.
     */
    public void gdVerifySelectedTabByIndex(int index, boolean selected)
        throws StepExecutionException {
        int implIdx = IndexConverter.toImplementationIndex(index);
        int selIndex = ((Integer)getEventThreadQueuer().invokeAndWait(
                "getSelectedIndex", //$NON-NLS-1$
                new IRunnable() {

                public Object run() throws StepExecutionException {
                    return new Integer(m_ctabFolder.getSelectionIndex());
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
     * @param title The tab title
     * @param operator Operator to be executed
     * @param selected Should the tab be selected?
     * @throws StepExecutionException If the tab title is invalid.
     */
    public void gdVerifySelectedTab(String title,  String operator, 
            boolean selected) throws StepExecutionException {
        
        String selectedTabTitle = (String)getEventThreadQueuer().invokeAndWait(
            "getSelectedTitle", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    CTabItem selectedItem = m_ctabFolder.getSelection();
                    if (selectedItem == null) {
                        // No selection
                        return null;
                    }
                    return SwtUtils.removeMnemonics(selectedItem.getText());
                }
            });

        if (selectedTabTitle == null) {
            // No tab is selected, so the given tab is definitely 
            // not selected
            if (!selected) {
                return;
            }
            throw new StepExecutionException(
                    I18n.getString(TestErrorEvent.NO_SELECTION),
                    EventFactory.createActionError(
                        TestErrorEvent.NO_SELECTION));
        }

        Verifier.match(selectedTabTitle, title, operator, selected);

    }
    /**
     * Verifies if the tab with the passed title is enabled.
     * @param title The tab title
     * @param operator operation to be executed
     * @param isEnabled wether to test if the tab  is enabled or not
     * @throws StepExecutionException If the tab title is invalid.
     */
    public void gdVerifyEnabled(String title, String operator, 
        final boolean isEnabled)

        throws StepExecutionException {

        final int tabIndex = getIndexOfTab(title, operator);

        verify(isEnabled, "isEnabledAt", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    Control control = 
                        m_ctabFolder.getItem(tabIndex).getControl();
                    if (control == null) {
                        // FIXME zeb: Strange workaround for GUIdancer CTabFolders,
                        //            which somehow never seem to have an associated
                        //            Control.
                        log.debug(this + ".getControl() returned null."); //$NON-NLS-1$
                        return Boolean.TRUE;
                    }

                    return control.isEnabled() ? Boolean.TRUE : Boolean.FALSE;
                }
            
            });
    }

    /**
     * Verifies if the tab with the passed index is enabled.
     * @param index The tab index
     * @param enabled Should the tab be enabled?
     * @throws StepExecutionException If the tab index is invalid.
     */
    public void gdVerifyEnabledByIndex(int index, boolean enabled)
        throws StepExecutionException {
        
        final int implIdx = IndexConverter.toImplementationIndex(index);
        verifyIndexExists(implIdx);
        verify(enabled, "isEnabledAt", //$NON-NLS-1$
            new IRunnable() {
                public Object run() throws StepExecutionException {
                    Control control = 
                        m_ctabFolder.getItem(implIdx).getControl();
                    if (control == null) {
                        // FIXME zeb: Strange workaround for GUIdancer CTabFolders,
                        //            which somehow never seem to have an associated
                        //            Control.
                        log.debug(this + ".getControl() returned null."); //$NON-NLS-1$
                        return Boolean.TRUE;
                    }
                    return control.isEnabled() ? Boolean.TRUE : Boolean.FALSE;
                }
            });
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        final String[] componentTextArray;
        Item[] itemArray = m_ctabFolder.getItems();
        componentTextArray = getTextArrayFromItemArray(itemArray);         
        return componentTextArray;
    }

    /**
     * {@inheritDoc}
     */
    protected void ensureTabIsShowing(final int index) {
        getEventThreadQueuer().invokeAndWait(
                "showTab", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() throws StepExecutionException {
                        m_ctabFolder.showItem(m_ctabFolder.getItem(index));

                        return null;
                    }
                });

        Boolean isShowing = (Boolean)getEventThreadQueuer().invokeAndWait(
                "ensureTabIsShowing", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() throws StepExecutionException {
                        return m_ctabFolder.getItem(index).isShowing()
                            ? Boolean.TRUE : Boolean.FALSE;
                    }
                });
        
        if (!isShowing.booleanValue()) {
            throw new StepExecutionException(
                "The chosen tab could not be shown.", //$NON-NLS-1$ 
                EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
    }

    /**
     * {@inheritDoc}
     */
    protected boolean indexOrTitleOfTabExists(final String tab,
            final String operator) {
        int tabIndex = -1;
        try {
            tabIndex = IndexConverter.toImplementationIndex(
                    Integer.parseInt(tab));
        } catch (NumberFormatException nfe) {
            tabIndex = ((Integer)getEventThreadQueuer().invokeAndWait(
                    "indexOrTitleOfTabExists", //$NON-NLS-1$
                    new IRunnable() {
                    public Object run() throws StepExecutionException {
                        for (int i = 0; i < m_ctabFolder.getItemCount(); i++) {
                            if (MatchUtil.getInstance().match(
                                SwtUtils.removeMnemonics(
                                    m_ctabFolder.getItem(i).getText()), 
                                tab, operator)) {
                                int j = m_ctabFolder.indexOf(
                                        m_ctabFolder.getItem(i));
                                return new Integer(j);
                            }
                        }
                        return new Integer(-1);
                    }
                })).intValue();            
        }
        final int index = tabIndex;
        Boolean tabExists = (Boolean)getEventThreadQueuer().invokeAndWait(
                "verifyExistenceOfTab", //$NON-NLS-1$
                new IRunnable() {
                    public Object run() throws StepExecutionException {
                        if (index < 0 || index >= m_ctabFolder.getItemCount()) {
                            return new Boolean(false);
                        }
                        return new Boolean(true);
                    }
                });
        return tabExists.booleanValue();
    }
}