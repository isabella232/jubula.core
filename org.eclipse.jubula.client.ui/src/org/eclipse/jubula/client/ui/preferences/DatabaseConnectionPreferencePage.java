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
package org.eclipse.jubula.client.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.dialogs.DatabaseConnectionDialog;
import org.eclipse.jubula.client.ui.model.DatabaseConnection;
import org.eclipse.jubula.client.ui.model.H2ConnectionInfo;
import org.eclipse.jubula.client.ui.model.OracleConnectionInfo;
import org.eclipse.jubula.client.ui.widgets.JBText;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for configuring Database Connections.
 * 
 * @author BREDEX GmbH
 * @created 10.01.2005
 */
public class DatabaseConnectionPreferencePage extends PreferencePage 
    implements IWorkbenchPreferencePage {

    /** ID of preference containing configured database connections */
    public static final String PREF_DATABASE_CONNECTIONS = 
        "org.eclipse.jubula.client.preference.databaseConnections"; //$NON-NLS-1$

    /** 
     * global listener to select/deselect text in text fields during widget 
     * traversal 
     */
    private static final Listener SELECT_ALL_LISTENER = 
        new Listener() {
            private boolean m_isTraversing = false;
        
            public void handleEvent(Event event) {
                switch (event.type) {
                    case SWT.FocusIn:
                        if (m_isTraversing) {
                            m_isTraversing = false;
                            if (event.widget instanceof JBText) {
                                ((JBText)event.widget).selectAll();
                            }
                        }
                        break;
                    case SWT.FocusOut:
                        if (event.widget instanceof Text) {
                            ((Text)event.widget).clearSelection();
                        }
                        break;
                    case SWT.Traverse:
                        if (event.doit) {
                            m_isTraversing = true;
                        }
                        break;
    
                    default:
                        break;
                }
            }
        };
    
    /** list of managed connections */
    private List<DatabaseConnection> m_connectionList = 
        new ArrayList<DatabaseConnection>();

    /**
     * 
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        setPreferenceStore(Plugin.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        final IObservableList existingConnections = 
            parsePreferences(getPreferenceStore());
        final ListViewer connectionViewer = new ListViewer(parent);
        ViewerSupport.bind(connectionViewer, existingConnections, 
                BeanProperties.value(DatabaseConnection.PROP_NAME_NAME));
        
        createAddButton(parent, existingConnections);
        
        createRemoveButton(parent, existingConnections, connectionViewer);
        
        createEditButton(parent, connectionViewer);
        
        return null;
    }

    /**
     * 
     * @param parent The parent composite.
     * @param connectionViewer The viewer containing the elements affected by 
     *                         pressing the created button.
     */
    private void createEditButton(Composite parent,
            final ListViewer connectionViewer) {
        final Button editButton = new Button(parent, SWT.NONE);
        editButton.setEnabled(false);
        connectionViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        IStructuredSelection sel =
                            (IStructuredSelection)event.getSelection();
                        editButton.setEnabled(sel.size() == 1);
                    }
                });
        editButton.setText(
                I18n.getString("DatabaseConnectionPreferencePage.EditButton.label")); //$NON-NLS-1$
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Object selectedObj = 
                    ((IStructuredSelection)connectionViewer.getSelection())
                        .getFirstElement();
                if (selectedObj instanceof DatabaseConnection) {
                    DatabaseConnection selectedConn = 
                        (DatabaseConnection)selectedObj;

                    try {
                        DatabaseConnectionDialog databaseConnectionWizard = 
                            new DatabaseConnectionDialog(
                                    new DatabaseConnection(selectedConn));
                            
                        if (showDialog(databaseConnectionWizard, event.display) 
                                == Window.OK) {
                            DatabaseConnection modifiedConn = 
                                databaseConnectionWizard
                                    .getEditedConnection();
                            selectedConn.setName(modifiedConn.getName());
                            selectedConn.setConnectionInfo(
                                    modifiedConn.getConnectionInfo());
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                } else if (selectedObj != null) {
                    throw new RuntimeException(I18n.getString(
                            "DatabaseConnectionPreferencePage.Error.selectedObjectIsOfIncorrectType")); //$NON-NLS-1$
                }

            }

        });
    }

    /**
     * Displays a dialog for the given wizard.
     * 
     * @param databaseConnectionWizard The wizard to display in the dialog.
     * @param display Used for registering/deregistering global listeners.
     * @return the result of {@link Window#open()}.
     */
    private static int showDialog(
            DatabaseConnectionDialog databaseConnectionWizard,
            Display display) {
        WizardDialog dialog = 
            new WizardDialog(Plugin.getShell(), 
                    databaseConnectionWizard) {
                protected void createButtonsForButtonBar(
                        Composite parent) {
                    super.createButtonsForButtonBar(parent);
                    Button finishButton = 
                        getButton(IDialogConstants.FINISH_ID);
                    finishButton.setText(
                            IDialogConstants.OK_LABEL);
                }
            };
        databaseConnectionWizard.setWindowTitle(
                I18n.getString("DatabaseConnectionDialog.title")); //$NON-NLS-1$
        dialog.setHelpAvailable(true);
        
        
        display.addFilter(
                SWT.FocusIn, SELECT_ALL_LISTENER);
        display.addFilter(
                SWT.FocusOut, SELECT_ALL_LISTENER);
        display.addFilter(
                SWT.Traverse, SELECT_ALL_LISTENER);

        try {
            return dialog.open();
        } finally {
            display.removeFilter(
                    SWT.FocusIn, SELECT_ALL_LISTENER);
            display.removeFilter(
                    SWT.FocusOut, SELECT_ALL_LISTENER);
            display.removeFilter(
                    SWT.Traverse, SELECT_ALL_LISTENER);
        }
    }

    /**
     * 
     * @param parent The parent composite.
     * @param existingConnections List of connections contained in the viewer.
     * @param connectionViewer The viewer containing the elements affected by 
     *                         pressing the created button.
     */
    private void createRemoveButton(Composite parent,
            final IObservableList existingConnections,
            final ListViewer connectionViewer) {
        final Button removeButton = new Button(parent, SWT.NONE);
        removeButton.setEnabled(false);
        connectionViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        removeButton.setEnabled(
                                !event.getSelection().isEmpty());
                    }
                });
        removeButton.setText(
                I18n.getString("DatabaseConnectionPreferencePage.RemoveButton.label")); //$NON-NLS-1$
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                existingConnections.removeAll(
                        ((IStructuredSelection)connectionViewer.getSelection())
                            .toList());
            }
        });
    }

    /**
     * 
     * @param parent The parent composite.
     * @param existingConnections List of connections contained in the viewer.
     */
    private void createAddButton(Composite parent,
            final IObservableList existingConnections) {
        Button addButton = new Button(parent, SWT.NONE);
        addButton.setText(
                I18n.getString("DatabaseConnectionPreferencePage.AddButton.label")); //$NON-NLS-1$
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                DatabaseConnectionDialog databaseConnectionWizard = 
                    new DatabaseConnectionDialog();

                if (showDialog(databaseConnectionWizard, event.display) 
                        == Window.OK) {
                    existingConnections.add(
                            databaseConnectionWizard.getEditedConnection());
                }
            }
        });
    }

    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(PREF_DATABASE_CONNECTIONS, 
                serializeDatabaseList(m_connectionList));
        storeDatabasePreferences(getPreferenceStore());
        
        return super.performOk();
    }

    /**
     * Parses the configured database connections from the information 
     * contained in the given preference store.
     * 
     * @param prefStore The preference store that contains the database 
     *                  connection information.
     * @return the list of configured database connections found in the given
     *         preference store.
     */
    private static IObservableList parsePreferences(
            IPreferenceStore prefStore) {
        // FIXME implement parsing
        IObservableList dummyList = new WritableList();
        dummyList.add(new DatabaseConnection(
                "Example H2 Connection", new H2ConnectionInfo()));
        dummyList.add(new DatabaseConnection(
                "Example Oracle Connection", new OracleConnectionInfo()));
        return dummyList;
    }

    /**
     * Persists the information from the provided preference store.
     * 
     * @param prefStore The information to persist.
     */
    private static void storeDatabasePreferences(
            IPreferenceStore prefStore) {
        // FIXME implement storing
    }
    
    /**
     * 
     * @param connections The connections to serialize.
     * @return a String containing all of the information provided in the 
     *         method argument. 
     */
    private static String serializeDatabaseList(
            List<DatabaseConnection> connections) {
        
        StringBuilder sb = new StringBuilder();
        for (DatabaseConnection conn : connections) {
            // FIXME implement serialization
        }
        
        return sb.toString();
    }
}
