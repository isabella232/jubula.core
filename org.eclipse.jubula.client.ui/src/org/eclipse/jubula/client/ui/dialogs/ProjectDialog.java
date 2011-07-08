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
package org.eclipse.jubula.client.ui.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Layout;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog containing all information necessary to uniquely identify a project.
 *
 * @author BREDEX GmbH
 * @created Jun 21, 2007
 */
public class ProjectDialog extends TitleAreaDialog {
    /** standard logging */
    private static Log log = LogFactory.getLog(TitleAreaDialog.class);

    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;

    /** number of columns = 4 */
    private static final int NUM_COLUMNS_4 = 4;

    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;

    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 10;

    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 10;

    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;

    /** horizontal span = 3 */
    private static final int HORIZONTAL_SPAN = 3;
    
    /** The message m_text */
    private String m_message;
    
    /** the name textfield */
    private DirectCombo<String> m_nameComboBox;

    /** the version textfield */
    private DirectCombo<String> m_versionComboBox;

    /**
     * Mapping from project name to project versions 
     */
    private Map<String, List<String>> m_nameToVersionMap;

    /**
     * Mapping from project data to project 
     */
    private Map<ProjectData, IProjectPO> m_projectMap;
    
    /** List of project names */
    private List<String> m_nameList;
    
    /** Current list of versions */
    private List<String> m_versionList;
    
    /** result of dialog, null if nothing was selected */
    private IProjectPO m_selection = null;

    /**
     * label of name combo
     */
    private String m_label = Messages.OpenProjectActionLabel;

    /**
     * label of the version combo
     */
    private String m_label2 = Messages.OpenProjectActionLabel2; 

    /**
     * <code>m_title</code> title
     */
    private String m_title;

    /**
     * <code>m_image</code> associated image
     */
    private Image m_image;

    /**
     * <code>m_shellTitle</code> the shell title
     */
    private String m_shellTitle;
    
    /**
     * <code>m_isDeleteAction</code> true if dialog is "delete project"-dialog
     */
    private boolean m_isDeleteAction = false;
    
    /**
     * check box to define if test result summary should not be deleted, when project is deleted
     */
    private Button m_keepTestresultSummaryButton;
    
    /**
     * true if test result summary should not be deleted, when project is deleted
     */
    private boolean m_keepTestresultSummary = false;

    /**
     * Value class to hold name and version info for a project.
     * @author BREDEX GmbH
     * @created Jun 21, 2007
     */
    private class ProjectData {
        /** project name */
        private String m_name;
        /** project version */
        private String m_versionString;

        /**
         * Constructor
         * @param name project name
         * @param versionString project version string
         */
        public ProjectData(String name, String versionString) {
            m_name = name;
            m_versionString = versionString;
        }

        /**
         * 
         * @return version string
         */
        private String getVersionString() {
            return m_versionString;
        }

        /**
         * 
         * @return name
         */
        private String getName() {
            return m_name;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ProjectData)) {
                return false;
            }
            
            ProjectData otherData = (ProjectData)obj;
            return new EqualsBuilder().append(getName(), otherData.getName())
                .append(getVersionString(), otherData.getVersionString())
                .isEquals();
        }

        /**
         * 
         * {@inheritDoc}
         */
        public int hashCode() {
            return new HashCodeBuilder().append(getName())
                .append(getVersionString())
                .toHashCode();
        }

    }

    /**
     * @param parentShell The parent shell.
     * @param projectList list of available projects
     * @param message message m_text
     * @param title title
     * @param image name of image
     * @param shellTitle shell title
     * @param isDeleteAction true if dialog is "delete project"-dialog
     */
    public ProjectDialog(
        Shell parentShell, List<IProjectPO> projectList, String message,
        String title, Image image, String shellTitle, boolean isDeleteAction) {
        
        super(parentShell);
        
        m_nameToVersionMap = 
            new HashMap<String, List<String>>();
        m_projectMap = new HashMap<ProjectData, IProjectPO>();
        m_nameList = new ArrayList<String>();
        m_versionList = new ArrayList<String>();
        for (IProjectPO proj : projectList) {
            String projName = proj.getName();
            String projVersion = proj.getVersionString();
            if ((projName != null) && (projVersion != null)) { // protect
                                                               // against racing
                                                               // conditions in
                                                               // DB
                if (!m_nameToVersionMap.containsKey(projName)) {
                    m_nameList.add(projName);
                    m_nameToVersionMap.put(projName, new ArrayList<String>());
                }
                m_nameToVersionMap.get(projName).add(projVersion);
                m_projectMap.put(new ProjectData(projName, projVersion), proj);
            } else  {
                log.warn(Messages.ProjectWithGUID + StringConstants.SPACE 
                        + proj.getGuid() + StringConstants.SPACE 
                        + Messages.HasNoName + StringConstants.DOT);
            }
        }

        m_message = message;
        m_title = title;
        m_image = image;
        m_shellTitle = shellTitle;
        m_isDeleteAction = isDeleteAction;
    }
    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setMessage(m_message);
        setTitle(m_title); 
        setTitleImage(m_image);
        getShell().setText(m_shellTitle); 
        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);

        Plugin.createSeparator(parent);

        Composite area = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = NUM_COLUMNS_4;
        area.setLayout(gridLayout);

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;

        area.setLayoutData(gridData);

        createComboBoxes(area);
        
        if (m_isDeleteAction) {
            createDeleteTestresultsCheckbox(area);
        }

        Plugin.createSeparator(parent);
        
        return area;
    }
    
    /**
     * @param composite the parent composite
     */
    private void createDeleteTestresultsCheckbox(Composite composite) {
        m_keepTestresultSummaryButton = new Button(composite, SWT.CHECK);
        m_keepTestresultSummaryButton.setText(
                Messages.DeleteProjectActionKeepTestresultSummaryCheckbox);
        m_keepTestresultSummaryButton.setSelection(false);
        GridData data = new GridData();
        data.horizontalSpan = HORIZONTAL_SPAN;
        m_keepTestresultSummaryButton.setLayoutData(data);
        ControlDecorator.decorateInfo(m_keepTestresultSummaryButton,  
                "GDControlDecorator.KeepTestresultSummary", false); //$NON-NLS-1$
    }

    /**
     * @param parent
     *            The parent composite.
     */
    private void createComboBoxes(Composite parent) {
        new Label(parent, SWT.NONE).setLayoutData(new GridData(GridData.FILL, 
            GridData.CENTER, false, false, HORIZONTAL_SPAN + 1, 1));
        new Label(parent, SWT.NONE).setText(m_label);
        m_nameComboBox = new DirectCombo<String>(parent, SWT.SINGLE | SWT.BORDER
                | SWT.READ_ONLY, m_nameList, m_nameList, false, true);
        GridData gridData = newGridData();
        Layout.addToolTipAndMaxWidth(gridData, m_nameComboBox);
        m_nameComboBox.setLayoutData(gridData);
        m_nameComboBox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                m_versionList = m_nameToVersionMap.get(
                    m_nameComboBox.getSelectedObject());
                m_versionComboBox.setItems(m_versionList, m_versionList);
                m_versionComboBox.select(m_versionComboBox.getItemCount() - 1);
                enableOKButton();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing                
            }           
        });     
        if (m_nameComboBox.getItemCount() > 0) {
            m_nameComboBox.select(0);
        }
        m_versionList = m_nameToVersionMap.get(
            m_nameComboBox.getSelectedObject());
        new Label(parent, SWT.NONE).setLayoutData(new GridData(GridData.FILL, 
            GridData.CENTER, false, false, HORIZONTAL_SPAN + 1, 1));
        new Label(parent, SWT.NONE).setText(m_label2);
        m_versionComboBox = 
            new DirectCombo<String>(parent, SWT.SINGLE | SWT.BORDER
                | SWT.READ_ONLY, m_versionList, m_versionList, false,
                new Comparator<String>() {

                    public int compare(String s1, String s2) {
                        Float f1 = Float.parseFloat(s1);
                        Float f2 = Float.parseFloat(s2);
                        
                        return f1.compareTo(f2);
                    }
                
                });
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.horizontalSpan = HORIZONTAL_SPAN;
        m_versionComboBox.setLayoutData(gridData);
        m_versionComboBox.select(m_versionComboBox.getItemCount() - 1);
        m_versionComboBox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                enableOKButton();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing                
            }           
        });     
    }

    /**
     * enables the OK button
     */
    public void enableOKButton() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        setMessage(m_message); 
    }

    /**
     * This method is called, when the OK button was pressed
     */
    protected void okPressed() {
        m_selection = m_projectMap.get(
                new ProjectData(m_nameComboBox.getSelectedObject(), 
                    m_versionComboBox.getSelectedObject()));
        if (m_isDeleteAction) {
            m_keepTestresultSummary = 
                m_keepTestresultSummaryButton.getSelection();
        }
        setReturnCode(OK);
        close();
    }

    /**
     * Creates a new GridData.
     * @return grid data
     */
    private GridData newGridData() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = HORIZONTAL_SPAN;
        return gridData;
    }

    /**
     * @return Returns the selection.
     */
    public IProjectPO getSelection() {
        return m_selection;
    }
    
    /**
     * @return Returns true, if test result summary should not be deleted.
     */
    public boolean keepTestresultSummary() {
        return m_keepTestresultSummary;
    }

}
