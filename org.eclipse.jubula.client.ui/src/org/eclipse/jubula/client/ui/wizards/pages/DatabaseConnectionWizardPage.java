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
package org.eclipse.jubula.client.ui.wizards.pages;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.persistence.DatabaseConnectionInfo;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnection;
import org.eclipse.jubula.client.core.preferences.database.H2ConnectionInfo;
import org.eclipse.jubula.client.core.preferences.database.OracleConnectionInfo;
import org.eclipse.jubula.client.ui.databinding.SimpleIntegerToStringConverter;
import org.eclipse.jubula.client.ui.databinding.SimpleStringToIntegerConverter;
import org.eclipse.jubula.client.ui.databinding.validators.StringToPortValidator;
import org.eclipse.jubula.client.ui.widgets.JBText;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


/**
 * This wizard page consists of the following for configuring a database 
 * connection:<ul>
 * <li>a general area for configuring the name and type</li>
 * <li>a detail area for configuring specific properties (the contents of 
 *     which depend on the selected type)</li>
 * <li>a concatenated connection URL display area that summarizes the 
 *     information provided in the other areas</li>
 * </ul>
 * @author BREDEX GmbH
 * @created May 19, 2010
 */
public class DatabaseConnectionWizardPage extends WizardPage {

    /** text to append to each label that corresponds to a text input */
    private static final String LABEL_TERMINATOR = ":"; //$NON-NLS-1$
    
    /**
     * Responsible for creating the detail area.
     * 
     * @author BREDEX GmbH
     */
    private static interface IDetailAreaBuilder {
        
        /**
         * Creates a detail area for the given parameters.
         * 
         * @param parent The parent that will contain the detail area.
         * @param dbc The data binding context for the detail area.
         */
        public void createDetailArea(Composite parent, 
                DataBindingContext dbc);

        /**
         * 
         * @return a String-representation of the receiver that can be shown 
         *         to the user. 
         */
        public String getTypeName();
        
        /**
         * Attempts to initialize the receiver with the given parameter. If the
         * provided information is not valid for the receiver, then this method
         * will do nothing.
         * 
         * @param sourceInfo The info to use for initialization.
         */
        public void initializeInfo(DatabaseConnectionInfo sourceInfo);
        
        /**
         * 
         * @return the connection info managed by the receiver.
         */
        public DatabaseConnectionInfo getConnectionInfo();
    }

    /**
     * Creates detail area for, and manages, an {@link H2ConnectionInfo}.
     * 
     * @author BREDEX GmbH
     */
    private static final class H2DetailBuilder 
        implements IDetailAreaBuilder {

        /** the managed connection info */
        private H2ConnectionInfo m_connInfo = new H2ConnectionInfo();

        /**
         * 
         * {@inheritDoc}
         */
        public void initializeInfo(DatabaseConnectionInfo sourceInfo) {
            if (sourceInfo instanceof H2ConnectionInfo) {
                m_connInfo = (H2ConnectionInfo)sourceInfo;
            }
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void createDetailArea(Composite parent, 
                DataBindingContext dbc) {

            createLabel(parent, I18n.getString("DatabaseConnection.H2.Location")); //$NON-NLS-1$
            final JBText locationText = new JBText(parent, SWT.BORDER);
            dbc.bindValue(SWTObservables.observeText(locationText, SWT.Modify), 
                    PojoObservables.observeValue(
                            m_connInfo, H2ConnectionInfo.PROP_NAME_LOCATION));
        }

        /**
         * 
         * {@inheritDoc}
         */
        public String getTypeName() {
            return "H2"; //$NON-NLS-1$
        }

        /**
         * 
         * {@inheritDoc}
         */
        public DatabaseConnectionInfo getConnectionInfo() {
            return m_connInfo;
        }
    
    }

    /**
     * Creates detail area for, and manages, an {@link OracleConnectionInfo}.
     * 
     * @author BREDEX GmbH
     */
    private static final class OracleDetailBuilder 
        implements IDetailAreaBuilder {

        /** the managed connection info */
        private OracleConnectionInfo m_connInfo = new OracleConnectionInfo();
        
        /**
         * 
         * {@inheritDoc}
         */
        public void createDetailArea(Composite parent, 
                DataBindingContext dbc) {

            createLabel(parent, 
                    I18n.getString("DatabaseConnection.Oracle.Hostname")); //$NON-NLS-1$
            final JBText hostnameText = new JBText(parent, SWT.BORDER);
            dbc.bindValue(SWTObservables.observeText(hostnameText, SWT.Modify), 
                    BeansObservables.observeValue(m_connInfo, 
                            OracleConnectionInfo.PROP_NAME_HOSTNAME));

            createLabel(parent, 
                    I18n.getString("DatabaseConnection.Oracle.Port")); //$NON-NLS-1$
            final JBText portText = new JBText(parent, SWT.BORDER);
            UpdateValueStrategy portTargetToModelUpdateStrategy =
                new UpdateValueStrategy();
            portTargetToModelUpdateStrategy
                .setConverter(new SimpleStringToIntegerConverter())
                .setAfterGetValidator(new StringToPortValidator(
                        I18n.getString("DatabaseConnection.Oracle.Port"))); //$NON-NLS-1$
            dbc.bindValue(SWTObservables.observeText(portText, SWT.Modify), 
                    BeansObservables.observeValue(m_connInfo, 
                            OracleConnectionInfo.PROP_NAME_PORT),
                    portTargetToModelUpdateStrategy,
                    new UpdateValueStrategy().setConverter(
                            new SimpleIntegerToStringConverter()));
            
            createLabel(parent, 
                    I18n.getString("DatabaseConnection.Oracle.Schema")); //$NON-NLS-1$
            final JBText schemaText = new JBText(parent, SWT.BORDER);
            dbc.bindValue(SWTObservables.observeText(schemaText, SWT.Modify), 
                    BeansObservables.observeValue(m_connInfo, 
                            OracleConnectionInfo.PROP_NAME_SCHEMA));
        }

        /**
         * 
         * {@inheritDoc}
         */
        public String getTypeName() {
            return "Oracle"; //$NON-NLS-1$
        }

        /**
         * 
         * {@inheritDoc}
         */
        public void initializeInfo(DatabaseConnectionInfo sourceInfo) {
            if (sourceInfo instanceof OracleConnectionInfo) {
                m_connInfo = (OracleConnectionInfo)sourceInfo;
            }
        }

        /**
         * 
         * {@inheritDoc}
         */
        public DatabaseConnectionInfo getConnectionInfo() {
            return m_connInfo;
        }
        
    }

    /** all available detail area builders */
    private IDetailAreaBuilder[] m_detailAreaBuilders = 
            new IDetailAreaBuilder [] {
                new H2DetailBuilder(),
                new OracleDetailBuilder()
            };

    /** the connection to edit within this page */
    private DatabaseConnection m_connectionToEdit;
    
    /**
     * Constructor
     * 
     * @param pageName The name of the page.
     */
    public DatabaseConnectionWizardPage(String pageName) {
        super(pageName);
    }

    /**
     * Constructor
     * 
     * @param pageName The name of the page.
     * @param connectionToEdit The object that will be modified based on the
     *                         data entered on this page.
     */
    public DatabaseConnectionWizardPage(String pageName, 
            DatabaseConnection connectionToEdit) {
        
        super(pageName);
        m_connectionToEdit = connectionToEdit;
        for (IDetailAreaBuilder builder : m_detailAreaBuilders) {
            builder.initializeInfo(m_connectionToEdit.getConnectionInfo());
        }
    }

    
    
    /**
     * 
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        setTitle(I18n.getString("DatabaseConnectionWizardPage.title")); //$NON-NLS-1$
        setDescription(I18n.getString("DatabaseConnectionWizardPage.description")); //$NON-NLS-1$
        
        final DataBindingContext dbc = new DataBindingContext();
        WizardPageSupport.create(this, dbc);
        GridDataFactory textGridDataFactory =
            GridDataFactory.fillDefaults().grab(true, false);
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(
                GridLayoutFactory.fillDefaults().numColumns(2).create());
        setControl(composite);
        
        createLabel(composite, I18n.getString("DatabaseConnection.Name")); //$NON-NLS-1$
        JBText nameText = new JBText(composite, SWT.BORDER);
        nameText.setLayoutData(textGridDataFactory.create());
        dbc.bindValue(SWTObservables.observeText(nameText, SWT.Modify), 
                BeansObservables.observeValue(m_connectionToEdit, 
                    DatabaseConnection.PROP_NAME_NAME), 
                new UpdateValueStrategy().setAfterGetValidator(
                        new IValidator() {
                            public IStatus validate(Object value) {
                                if (StringUtils.isEmpty((String)value)) {
                                    return ValidationStatus.error(
                                            I18n.getString("DatabaseConnectionWizardPage.Error.emptyName")); //$NON-NLS-1$
                                }
                                return ValidationStatus.ok();
                            }
                        }), 
                new UpdateValueStrategy());
        nameText.setFocus();
        nameText.selectAll();
        
        createLabel(composite, I18n.getString("DatabaseConnection.Type")); //$NON-NLS-1$
        ComboViewer typeComboViewer = new ComboViewer(composite);
        typeComboViewer.setContentProvider(new ArrayContentProvider());
        typeComboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((IDetailAreaBuilder)element).getTypeName();
            } 
        });
        typeComboViewer.setInput(m_detailAreaBuilders);
        typeComboViewer.getControl().setLayoutData(
                textGridDataFactory.create());

        final Group detailArea = new Group(composite, SWT.NONE);
        detailArea.setText(I18n.getString("DatabaseConnectionWizardPage.DetailArea.title")); //$NON-NLS-1$
        detailArea.setLayoutData(
                GridDataFactory.fillDefaults().grab(true, true)
                    .span(2, 1).create());
        detailArea.setLayout(new GridLayout(2, false));

        IObservableValue connectionInfoObservable = 
            BeansObservables.observeValue(m_connectionToEdit, 
                    DatabaseConnection.PROP_NAME_CONN_INFO);
        
        bindComboViewer(dbc, typeComboViewer, detailArea,
                connectionInfoObservable);
        
        JBText url = new JBText(composite, SWT.BORDER);
        url.setEditable(false);
        url.setBackground(composite.getBackground());
        url.setLayoutData(
                GridDataFactory.fillDefaults().grab(true, false)
                    .span(2, 1).create());
        dbc.bindValue(SWTObservables.observeText(url), 
                BeansObservables.observeDetailValue(
                        connectionInfoObservable, 
                        DatabaseConnectionInfo.PROP_NAME_CONN_URL, 
                        null));
        
    }

    /**
     * 
     * @param dbc The data binding context.
     * @param typeComboViewer The combo viewer.
     * @param detailArea The detail area.
     * @param connectionInfoObservable The connection info observable.
     */
    private void bindComboViewer(final DataBindingContext dbc,
            ComboViewer typeComboViewer, final Composite detailArea,
            IObservableValue connectionInfoObservable) {
        dbc.bindValue(
                ViewersObservables.observeSingleSelection(typeComboViewer), 
                connectionInfoObservable, 
                new UpdateValueStrategy().setConverter(new IConverter() {
                    public Object getToType() {
                        return DatabaseConnectionInfo.class;
                    }
                    public Object getFromType() {
                        return IDetailAreaBuilder.class;
                    }
                    public Object convert(Object fromObject) {
                        for (Control child : detailArea.getChildren()) {
                            child.dispose();
                        }
                        IDetailAreaBuilder fromBuilder = 
                            (IDetailAreaBuilder)fromObject;
                        fromBuilder.createDetailArea(detailArea, dbc);
                        detailArea.layout();
                        ((Composite)getControl()).layout();
                        return fromBuilder.getConnectionInfo();
                    }
                }), new UpdateValueStrategy().setConverter(new IConverter() {
                    
                    public Object getToType() {
                        return IDetailAreaBuilder.class;
                    }
                    
                    public Object getFromType() {
                        return DatabaseConnectionInfo.class;
                    }
                    
                    public Object convert(Object fromObject) {
                        DatabaseConnectionInfo fromInfo =
                            (DatabaseConnectionInfo)fromObject;
                        for (IDetailAreaBuilder builder 
                                : m_detailAreaBuilders) {
                            if (builder.getConnectionInfo() == fromInfo) {
                                for (Control child : detailArea.getChildren()) {
                                    child.dispose();
                                }
                                
                                builder.createDetailArea(detailArea, dbc);
                                
                                detailArea.layout();
                                ((Composite)getControl()).layout();
                                return builder;
                            }
                        }

                        return null;
                    }
                }));
    }

    /**
     * Creates a label with appropriate text in the given composite.
     * 
     * @param parent The parent composite for the label.
     * @param fieldName The internationalized name of the text input field for 
     *                  which to create a label.
     */
    private static void createLabel(Composite parent, String fieldName) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(fieldName + LABEL_TERMINATOR);
    }
    
}
