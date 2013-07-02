/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.contributionitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.handlers.ChooseLanguageHandler;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * Populates the dropdown list for the "Choose Language" toolbar item.
 * @author BREDEX GmbH
 *
 */
public class LanguageContributionItem extends CompoundContributionItem {
    
    /**
     * {@inheritDoc}
     */
    protected IContributionItem[] getContributionItems() {
        List <Locale> languages = null;
        IProjectPO project = GeneralStorage.getInstance().getProject();
        List<IContributionItem> contributionItems = 
                new ArrayList<IContributionItem>();
        if (project != null) {            
            languages = project.getLangHelper().getLanguageList();
            List<String> dispList = new ArrayList<String>(); 
            for (Locale locale : languages) {
                dispList.add(locale.getDisplayName());
            }
            Collections.sort(dispList);
            
            for (String lang : dispList) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(ChooseLanguageHandler.LANGUAGE,
                        lang);
                IContributionItem langContribItem = CommandHelper
                        .createContributionItem(RCPCommandIDs.CHOOSE_LANGUAGE,
                                params, lang,
                                CommandContributionItem.STYLE_CHECK);
                contributionItems.add(langContribItem);
                
            }
        }   
        return contributionItems.toArray(
                new IContributionItem [contributionItems.size()]);
    }

}
