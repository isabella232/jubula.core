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
package org.eclipse.jubula.client.ui.search.page;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.search.data.AbstractSearchData;
import org.eclipse.jubula.client.ui.search.data.AbstractSearchData.SearchableType;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


/**
 * @author BREDEX GmbH
 * @created Jul 26, 2010
 */
public abstract class AbstractSearchPage extends DialogPage implements
    ISearchPage {
    /** number of columns = 4 */
    protected static final int NUM_COLUMNS = 4;  
    /** vertical spacing */
    protected static final int VERTICAL_SPACING = 10;
    
    /** CheckbBox to select use regular expression */
    private Button m_useRegExCheck;
    /** CheckbBox to select use search case sensitiv */
    private Button m_caseSensitivCheck;
    /** search Text Field */
    private Combo m_searchStringCombo;

    /** {@inheritDoc} */
    public void createControl(Composite parent) {
        Composite pageContent = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = NUM_COLUMNS;
        layout.verticalSpacing = VERTICAL_SPACING;
        layout.marginWidth = Layout.MARGIN_WIDTH;
        layout.marginHeight = Layout.MARGIN_HEIGHT;
        pageContent.setLayout(layout);
        
        Label findLabel = new Label(pageContent, SWT.NONE);
        findLabel.setText(I18n.getString("SimpleSearchPage.Search")); //$NON-NLS-1$

        setSearchStringCombo(new Combo(pageContent, SWT.BORDER));
        getSearchStringCombo().setLayoutData(
                getGridData(NUM_COLUMNS - 1, true));
        getSearchStringCombo().setItems(
                getSearchData().getRecent().toArray(
                        new String[getSearchData().getRecent().size()]));
        if (getSearchStringCombo().getItemCount() == 0) {
            getSearchStringCombo().setText(I18n.getString("SimpleSearchPage.Phrase")); //$NON-NLS-1$
        } else {
            getSearchStringCombo().select(0);
        }
        
        createSearchOptionsGroup(pageContent);
        createAdditionalGUI(pageContent);
        setControl(pageContent);
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.FIND_DIALOG);
    }
    
    /**
     * @param horizontalSpan
     *            the horizontal column span
     * @param grabHorizontal
     *            set to true to grabExcessHorizontalSpace
     * @return a valid grid data
     */
    protected GridData getGridData(int horizontalSpan, boolean grabHorizontal) {
        GridData gd = GridDataFactory.fillDefaults().create();
        gd.grabExcessHorizontalSpace = grabHorizontal;
        gd.horizontalSpan = horizontalSpan;
        return gd;
    }
    
    /**
     * calls find method on callback object
     */
    private void doCallBack() {
        if (getSearchData().getRecent().contains(
                getSearchStringCombo().getText())) {
            getSearchData().getRecent()
                    .remove(getSearchStringCombo().getText());
        }
        if (getSearchData().getRecent().size() > 4) {
            getSearchData().getRecent().remove(
                    getSearchData().getRecent().size() - 1);
        }
        getSearchData().getRecent().add(0, m_searchStringCombo.getText());

        getSearchStringCombo().setItems(
                getSearchData().getRecent().toArray(
                        new String[getSearchData().getRecent().size()]));
        getSearchStringCombo().select(0);
        getSearchStringCombo().setFocus();
        
        getSearchData().setCaseSensitive(getCaseSensitivCheck().getSelection());
        getSearchData().setUseRegex(getUseRegExCheck().getSelection());
        
        NewSearchUI.runQueryInBackground(newQuery());
    }

    /**
     * creates the Direction Group
     * @param parent the parent to use
     */
    private void createSearchOptionsGroup(Composite parent) {
        Group optionsGroup = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = VERTICAL_SPACING;
        layout.marginWidth = Layout.MARGIN_WIDTH;
        layout.marginHeight = Layout.MARGIN_HEIGHT;
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(getGridData(4, true));
        optionsGroup.setText(I18n.getString("SimpleSearchPage.OptionGroupHeader")); //$NON-NLS-1$

        setCaseSensitivCheck(new Button(optionsGroup, SWT.CHECK));
        getCaseSensitivCheck().setText(I18n.getString("SimpleSearchPage.CaseSen")); //$NON-NLS-1$
        getCaseSensitivCheck().setSelection(getSearchData().isCaseSensitive());
        getCaseSensitivCheck().setLayoutData(getGridData(1, true));
        setUseRegExCheck(new Button(optionsGroup, SWT.CHECK));
        getUseRegExCheck().setText(I18n.getString("SimpleSearchPage.RegEx")); //$NON-NLS-1$
        getUseRegExCheck().setSelection(getSearchData().isUseRegex());
        getUseRegExCheck().setLayoutData(getGridData(1, true));
    }

    /** {@inheritDoc} */
    public boolean performAction() {
        if (GeneralStorage.getInstance().getProject() != null) {
            doCallBack();
        }
        return true;
    }

    /** {@inheritDoc} */
    public void setContainer(ISearchPageContainer container) {
        // no container support yet
    }

    /**
     * @return a new search query
     */
    protected abstract ISearchQuery newQuery();
    
    /**
     * @return the useRegExCheck
     */
    private Button getUseRegExCheck() {
        return m_useRegExCheck;
    }

    /**
     * @param useRegExCheck the useRegExCheck to set
     */
    private void setUseRegExCheck(Button useRegExCheck) {
        m_useRegExCheck = useRegExCheck;
    }

    /**
     * @return the caseSensitivCheck
     */
    private Button getCaseSensitivCheck() {
        return m_caseSensitivCheck;
    }

    /**
     * @param caseSensitivCheck the caseSensitivCheck to set
     */
    private void setCaseSensitivCheck(Button caseSensitivCheck) {
        m_caseSensitivCheck = caseSensitivCheck;
    }

    /**
     * @return the searchStringCombo
     */
    protected Combo getSearchStringCombo() {
        return m_searchStringCombo;
    }

    /**
     * @param searchStringCombo the searchStringCombo to set
     */
    private void setSearchStringCombo(Combo searchStringCombo) {
        m_searchStringCombo = searchStringCombo;
    }
    
    /**
     * subclasses may override
     * @param parent the parent
     */
    protected void createAdditionalGUI(Composite parent) {
        Group optionsGroup = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.verticalSpacing = VERTICAL_SPACING;
        layout.marginWidth = Layout.MARGIN_WIDTH;
        layout.marginHeight = Layout.MARGIN_HEIGHT;
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(getGridData(3, true));
        optionsGroup.setText(I18n
                .getString("SimpleSearchPage.StructureToSearchGroupHeader")); //$NON-NLS-1$

        DataBindingContext dbc = new DataBindingContext();
        for (SearchableType searchableType : getSearchData()
                .getTypesToSearchFor()) {
            createTypeChoice(dbc, optionsGroup, searchableType);
        }
    }
    
    /**
     * @return a list of searchable types for this page
     */
    protected abstract AbstractSearchData getSearchData();

    /**
     * @param dbc
     *            the data binding context
     * @param parent
     *            the parent
     * @param searchableType
     *            the type to search for
     */
    private void createTypeChoice(DataBindingContext dbc, Group parent, 
        SearchableType searchableType) {
        Button choiceButton = new Button(parent, SWT.CHECK);
        
        IObservableValue guiElement = SWTObservables
                .observeSelection(choiceButton);
        IObservableValue modelElement = PojoObservables.observeValue(
                searchableType, "enabled"); //$NON-NLS-1$

        dbc.bindValue(guiElement, modelElement);
        dbc.updateTargets();

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);

        choiceButton.setLayoutData(gd);
        choiceButton.setText(searchableType.getName());
    }
}
