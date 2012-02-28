/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.analyze.ui.contributionitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jubula.client.analyze.ExtensionRegistry;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.Category;
import org.eclipse.jubula.client.analyze.ui.internal.helper.ContextHelper;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;


/**
 * ContextMenuContributionItem is used to handle the creation of the ContributionItems ContextMenu
 * @author volker
 * 
 */
public class ContextMenuContributionItem extends CompoundContributionItem {
    
    /** the label shown in the menu */
    private String m_label;
    
    /** the attribute id from the actual Analyze */
    private String m_currentID;
    
    /**  */
    public ContextMenuContributionItem() {
    }
    /**
     * @param id
     *            The id
     */
    public ContextMenuContributionItem(String id) {
        super(id);
    }

    @Override
    protected IContributionItem[] getContributionItems() {

        List<IContributionItem> contributionItems = 
                new ArrayList<IContributionItem>();

        for (Map.Entry<String, Category> e : ExtensionRegistry.getCategory()
                .entrySet()) {

            Category c = (Category) e.getValue();

            createMenuEntry(c, contributionItems, null);

        }
        return contributionItems
                .toArray(new IContributionItem[contributionItems.size()]);
    }

    /**
     * creates a ContributionItem which represents a Category calls the
     * createSubMenuEntry to create the ContributionItem which represents the
     * Analyze
     * 
     * @param c
     *            the given category
     * @param contributionItems
     *            the contributionItems list
     * @param mm
     *            MenuManager
     */
    private void createMenuEntry(Category c,
            List<IContributionItem> contributionItems, MenuManager mm) {

        // check if there is a ToplevelCategory
        if (c.getParentCatID() == null || c.getParentCatID().length() == 0) {
            contributionItems.add(createSubMenuEntry(c));
        } else {
            for (Map.Entry<String, Category> e : ExtensionRegistry.getCategory()
                    .entrySet()) {
                Category cat = (Category) e.getValue();
                if (cat.getID().equals(c.getParentCatID())) {
                    MenuManager mgr = new MenuManager(cat.getName());
                    mgr.add(createSubMenuEntry(c));
                    contributionItems.add(mgr);
                    contributionItems.add(new Separator());
                }
            }
        }
    }

    /**
     * creates the ContributionItem which represents the Analyze in the contextMenu
     * @param c
     *            current Category
     * @return mgr MenuManager
     */
    private MenuManager createSubMenuEntry(Category c) {
        // Set the Command and the Command Parameters
        String command = "org.eclipse.jubula.client.analyze.ui.RunSelection";                //$NON-NLS-1$
        String idParam = "org.eclipse.jubula.client.analyze.ui.RunSelection.IDParam";        //$NON-NLS-1$

        MenuManager mgr = new MenuManager(c.getName());
        for (Map.Entry<String, Analyze> f : ExtensionRegistry.getAnalyze()
                .entrySet()) {

            Analyze analyze = f.getValue();

            if (analyze.getCategoryID().equals(c.getID())
                    && ContextHelper.isEnabled(analyze)) {

                // shown label
                m_label = analyze.getName();
                // current ID
                m_currentID = analyze.getID();

                Map<String, String> params = new HashMap<String, String>();

                params.put(idParam, m_currentID);
                IContributionItem item = CommandHelper.createContributionItem(
                        command, params, m_label,
                        CommandContributionItem.STYLE_PUSH);
                mgr.add(item);
            }
        }
        return mgr;
    }
}
