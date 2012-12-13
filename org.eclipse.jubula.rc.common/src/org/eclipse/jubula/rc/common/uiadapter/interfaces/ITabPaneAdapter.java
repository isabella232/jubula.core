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
package org.eclipse.jubula.rc.common.uiadapter.interfaces;

/**
 * Interface for all necessary methods to test TabbedPanes.
 * It extends the <code>IWidgetAdapter</code> to add Tab Pane specific methods.
 * @author BREDEX GmbH
 *
 */
public interface ITabPaneAdapter extends IWidgetAdapter {
    /**
     * 
     * @return the number of the tabs
     */
    public int getTabCount();
    
    /**
     * 
     * @param index of the tab
     * @return the title of the wanted tab.
     */
    public String getTitleofTab(int index);

    /**
     * 
     * @param index of the tab
     * @return the bounds of the tabs.
     */
    public Object getBoundsAt(int index);

    /**
     * 
     * @param index index of the tab
     * @return the enablement of the specific tab.
     */
    public boolean isEnabledAt(int index);

    /**
     * 
     * @return the index of the selected tab.
     */
    public int getSelectedIndex();
}
