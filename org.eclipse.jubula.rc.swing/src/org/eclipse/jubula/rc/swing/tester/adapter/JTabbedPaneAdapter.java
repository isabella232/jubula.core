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
package org.eclipse.jubula.rc.swing.tester.adapter;

import javax.swing.JTabbedPane;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITabPaneAdapter;
/**
 * Implementation of the Interface <code>ITabPaneAdapter</code> as a
 * adapter for the <code>JTabbedPane</code> component.
 * @author BREDEX GmbH
 *
 */
public class JTabbedPaneAdapter extends WidgetAdapter 
    implements ITabPaneAdapter {

    /** The JTabbedPane on which the actions are performed. */
    private JTabbedPane m_pane;
    /**
     * 
     * @param objectToAdapt 
     */
    public JTabbedPaneAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_pane = (JTabbedPane) objectToAdapt;
    }

    /**
     * {@inheritDoc}
     */
    public int getTabCount() {
        return ((Integer) getEventThreadQueuer().invokeAndWait(
                "getTabCount", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return new Integer(m_pane.getTabCount());
                    }
                })).intValue(); 
    }
    
    /**
     * {@inheritDoc}
     */
    public String getTitleofTab(final int index) {
        return (String) getEventThreadQueuer().invokeAndWait(
                "getTitleOfTab", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return m_pane.getTitleAt(index);
                    }
                });        
    }

    /**
     * {@inheritDoc}
     */
    public Object getBoundsAt(final int index) {
        
        return getEventThreadQueuer().invokeAndWait(
                "getBoundsAt", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return m_pane.getBoundsAt(index);
                    }
                }); 
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabledAt(final int index) {
        return ((Boolean) getEventThreadQueuer().invokeAndWait(
                "isEnabledAt", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return Boolean.valueOf(m_pane.isEnabledAt(index));
                    }
                })).booleanValue(); 
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {        
        return ((Integer) getEventThreadQueuer().invokeAndWait(
                "getSelectedIndex", new IRunnable() { //$NON-NLS-1$
                    public Object run() {
                        return new Integer(m_pane.getSelectedIndex());
                    }
                })).intValue();
    }

}
