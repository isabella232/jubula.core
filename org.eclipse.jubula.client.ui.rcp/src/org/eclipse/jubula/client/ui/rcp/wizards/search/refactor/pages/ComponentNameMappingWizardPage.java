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
package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.i18n.CompSystemI18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class ComponentNameMappingWizardPage extends WizardPage {

    /** */
    private ScrolledComposite m_scroll;
    /** */
    private Composite m_composite;
    /** */
    private Set<IExecTestCasePO> m_listOfExecs;
    /** */
    private ISpecTestCasePO m_newSpec;
    
    /**
     * 
     * @param pageName
     *            the page name
     * @param execTCList
     *            the selected exec Test Cases which should be replaced
     */
    public ComponentNameMappingWizardPage(String pageName,
            Set<IExecTestCasePO> execTCList) {
        super(pageName, Messages.ReplaceTCRWizard_matchComponentNames_title,
                null);
        m_listOfExecs = execTCList;
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        m_scroll = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        
        m_scroll.setLayout(GridLayoutFactory.fillDefaults()
                .numColumns(1).create());
        m_scroll.setMinSize(parent.getSize());
        m_composite = new Composite(m_scroll,
                SWT.NONE);
        setControl(m_scroll);
    }

    /**
     * 
     * @param newSpec
     *            new Spec Test case which should be used for replacement
     */
    public void setNewSpec(ISpecTestCasePO newSpec) {
        m_newSpec = newSpec;
        setPageComplete(false);
        
        IExecTestCasePO newExec = NodeMaker.createExecTestCasePO(newSpec);
        Collection<ICompNamesPairPO> compNamePairs = 
                new CompNamesBP().getAllCompNamesPairs(newExec);        
        
        m_composite.dispose(); // Disposing old and generating new composite

        Composite mappingGrid = new Composite(m_scroll, SWT.NONE);
        mappingGrid.setLayout(GridLayoutFactory.fillDefaults()
                .numColumns(3).spacing(10, 10).create());
        GridData tableGridData = GridDataFactory.fillDefaults()
                .grab(false, false).align(SWT.CENTER, SWT.CENTER).create();
        mappingGrid.setLayoutData(tableGridData);
        
        createLayoutWithData(compNamePairs, mappingGrid);
        
        m_scroll.setContent(mappingGrid);
        m_composite = mappingGrid;
        setPageComplete(true);
    }

    /**
     * 
     * @param compNamePairs
     *            the component name pairs of the new Spec Test Case
     * @param parent
     *            the parent in which the data should be rendered
     */
    private void createLayoutWithData(
            Collection<ICompNamesPairPO> compNamePairs, Composite parent) {

        new Label(parent, SWT.NONE).setText(
                Messages.ReplaceTCRWizard_ComponentNameMapping_newTC);
        new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
        new Label(parent, SWT.NONE).setText(
                Messages.ReplaceTCRWizard_ComponentNameMapping_oldTC);

        Label seperatorHorizontal = new Label(parent, SWT.HORIZONTAL
                | SWT.SEPARATOR);
        
        GridData seperatorGridHorizontal = new GridData(GridData.FILL,
                GridData.CENTER, true, false);
        seperatorGridHorizontal.horizontalSpan = 3;
        seperatorHorizontal.setLayoutData(seperatorGridHorizontal);
        GridData seperatorVertical = new GridData(
                GridData.CENTER, GridData.FILL, false, true);
        seperatorVertical.verticalSpan = compNamePairs.size();
        
        boolean first = true;
        for (Iterator compIterator = compNamePairs.iterator(); compIterator
                .hasNext();) {
            GridData leftGridData = new GridData();
            leftGridData.horizontalAlignment = SWT.LEFT;
            leftGridData.verticalAlignment = SWT.BEGINNING;
            GridData rightGridData = new GridData();
            rightGridData.horizontalAlignment = SWT.LEFT;
            rightGridData.verticalAlignment = SWT.BEGINNING;
            ICompNamesPairPO compNamesPair = (ICompNamesPairPO) compIterator
                    .next();
            String type = CompSystemI18n.getString(compNamesPair.getType());
            String name = ComponentNamesBP.getInstance().getName(
                    compNamesPair.getName());
            Label compname = new Label(parent, NONE);
            compname.setText(name + " [" + type + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            compname.setLayoutData(leftGridData);
            if (first) {
                Label seperator = new Label(parent, 
                        SWT.VERTICAL | SWT.SEPARATOR);
                seperator.setLayoutData(seperatorVertical);
                first = false;
            }
            
            Combo oldCompNames = new Combo(parent, NONE);
            oldCompNames.setLayoutData(rightGridData);
        }
        parent.pack();
        
    }

}
