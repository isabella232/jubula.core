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
package org.eclipse.jubula.rc.rcp.e3.accessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.tester.adapter.factory.GUIAdapterFactoryRegistry;
import org.eclipse.jubula.rc.rcp.e3.gef.inspector.GefInspectorListenerAppender;
import org.eclipse.jubula.rc.rcp.e3.gef.listener.GefPartListener;
import org.eclipse.jubula.rc.rcp.swt.aut.RcpSwtComponentNamer;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.tester.adapter.factory.SWTAdapterFactory;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.constants.CommandConstants;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPartReference;


/**
 * Initializes an AUT Server in a plug-in context.
 *
 * It is very important to avoid referencing GEF (org.eclipse.gef.*) classes
 * directly from this class, as this will cause Class Not Found errors if the
 * AUT does not contain the GEF plug-in.
 *
 * @author BREDEX GmbH
 * @created Oct 5, 2007
 */
public abstract class E3Startup implements IStartup {

    /** bundle ID for Eclipse Graphical Editing Framework (GEF) */
    private static final String GEF_BUNDLE_ID = "org.eclipse.gef"; //$NON-NLS-1$

    /** Single listener instance */
    private IPartListener2 m_partNamingListener =
            new PartNamingListener();

    /**
     * This listener
     */
    private IPartListener2 m_gefListener = null;

    /**
     * Assigns the controls (Composites) of Parts unique names based on
     * their partId.
     *
     * @author BREDEX GmbH
     * @created Oct 5, 2007
     */
    protected class PartNamingListener implements IPartListener2 {

        /**
         *
         * {@inheritDoc}
         */
        public void partActivated(IWorkbenchPartReference partRef) {
            // Do nothing
        }

        /**
         *
         * {@inheritDoc}
         */
        public void partBroughtToTop(IWorkbenchPartReference partRef) {
            // Do nothing
        }

        /**
         *
         * {@inheritDoc}
         */
        public void partClosed(IWorkbenchPartReference partRef) {
            // Do nothing
        }

        /**
         *
         * {@inheritDoc}
         */
        public void partDeactivated(IWorkbenchPartReference partRef) {
            // Do nothing
        }

        /**
         *
         * {@inheritDoc}
         */
        public void partHidden(IWorkbenchPartReference partRef) {
            // Do nothing
        }

        /**
         *
         * {@inheritDoc}
         */
        public void partInputChanged(IWorkbenchPartReference partRef) {
            // Do nothing
        }

        /**
         *
         * {@inheritDoc}
         */
        public void partOpened(IWorkbenchPartReference partRef) {
            if (partRef instanceof WorkbenchPartReference) {
                WorkbenchPartReference workbenchPartRef =
                    (WorkbenchPartReference)partRef;
                // Get pane contents and part id
                Control partContent =
                    workbenchPartRef.getPane().getControl();

                if (RcpSwtComponentNamer.hasWidgetToBeNamed(partContent)) {

                    // Name pane control based on part
                    String partId = workbenchPartRef.getId();

                    // Append secondary id, if necessary
                    if (partRef instanceof IViewReference) {
                        String secondaryId =
                            ((IViewReference)partRef).getSecondaryId();
                        if (secondaryId != null) {
                            partId += "_" + secondaryId; //$NON-NLS-1$
                        }
                    }

                    if (partId == null || partId.trim().length() == 0) {
                        // Don't assign a name if the id is unusable
                        return;
                    }
                    RcpSwtComponentNamer.setComponentName(partContent, partId);

                    // Assign a corresponding id to the part's toolbar, if
                    // possible/usable.
                    final Control partToolbar =
                            getToolBarFromWorkbenchPartRef(workbenchPartRef);
                    final String finalPartId = partId;
                    if (partToolbar != null) {
                        RcpSwtComponentNamer.setToolbarComponentName(
                                partToolbar, finalPartId);
                    }

                    // A repaint is required in order for the aut component
                    // hierarchy to notice the change.
                    Shell shell = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell();
                    repaintToolbars(shell);
                }
            }

        }

        /**
         *
         * {@inheritDoc}
         */
        public void partVisible(IWorkbenchPartReference partRef) {
            partOpened(partRef);
        }

    }

    /**
     * This abstract method is a place holder for different implementations
     * depending on Eclipse e3.x and e4.x running in compatibility mode
     * to get the SWT control form a given workbench part reference.
     * @param workbenchPartRef The workbench part reference.
     * @return The control of the tool bar from the given workbench part reference
     *         or null, if it does not exist.
     * @see org.eclipse.jubula.rc.rcp.e3.specific#Startup
     * @see org.eclipse.jubula.rc.rcp.e4.compat#Startup
     */
    public abstract Control getToolBarFromWorkbenchPartRef(
            WorkbenchPartReference workbenchPartRef);

    /**
     * {@inheritDoc}
     */
    public void earlyStartup() {
        final Properties envVars =
            EnvironmentUtils.getProcessEnvironment();

        if (getValue(AutConfigConstants.AUT_AGENT_HOST, envVars) != null) {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final Display display = workbench.getDisplay();
            initAutServer(display, envVars);

            display.syncExec(new Runnable() {
                public void run() {
                    // add GEF listeners (and listener appenders) for GEF, if available
                    if (Platform.getBundle(E3Startup.GEF_BUNDLE_ID) != null) {
                        m_gefListener = new GefPartListener();
                        AUTServer.getInstance().addInspectorListenerAppender(
                                new GefInspectorListenerAppender());
                    }

                    // add naming listener
                    E3ComponentNamer namer = new E3ComponentNamer();
                    display.addFilter(SWT.Paint, namer);
                    display.addFilter(SWT.Activate, namer);

                    // Add window listener
                    addWindowListener(workbench);

                    IWorkbenchWindow window =
                        workbench.getActiveWorkbenchWindow();
                    if (window != null) {
                        // Add part listeners
                        addPartListeners(window);

                        // Handle existing parts
                        IWorkbenchPage [] pages = window.getPages();
                        for (int i = 0; i < pages.length; i++) {
                            IEditorReference[] editorRefs =
                                pages[i].getEditorReferences();
                            IViewReference[] viewRefs =
                                pages[i].getViewReferences();
                            for (int j = 0; j < editorRefs.length; j++) {
                                m_partNamingListener.partOpened(editorRefs[j]);
                                if (m_gefListener != null) {
                                    m_gefListener.partOpened(editorRefs[j]);
                                }
                            }
                            for (int k = 0; k < viewRefs.length; k++) {
                                m_partNamingListener.partOpened(viewRefs[k]);
                                if (m_gefListener != null) {
                                    m_gefListener.partOpened(viewRefs[k]);
                                }
                            }
                        }

                        // If a shell already exists, make sure that we get another
                        // chance to immediately add/use our naming listeners.
                        Shell mainShell = window.getShell();
                        if (mainShell != null && !mainShell.isDisposed()) {
                            repaintToolbars(mainShell);
                        }
                    }
                }

            });
            // Registering the AdapterFactory for SWT at the registry
            GUIAdapterFactoryRegistry.getInstance()
                .registerFactory(new SWTAdapterFactory());
            // add listener to AUT
            AUTServer.getInstance().addToolKitEventListenerToAUT();

        }

    }

    /**
     * Initializes the AUT Server for the host application.
     *
     * @param display The Display to use for the AUT Server.
     * @param envVars Environment variables to consult in configuring the
     *                AUT Server.
     */
    private void initAutServer(Display display, Properties envVars) {
        ((SwtAUTServer)AUTServer.getInstance(CommandConstants
                .AUT_SWT_SERVER)).setDisplay(display);
        AUTServer.getInstance().setAutAgentHost(getValue(
                AutConfigConstants.AUT_AGENT_HOST, envVars));
        AUTServer.getInstance().setAutAgentPort(getValue(
                AutConfigConstants.AUT_AGENT_PORT, envVars));
        AUTServer.getInstance().setAutID(getValue(
                AutConfigConstants.AUT_NAME, envVars));

        AUTServer.getInstance().start(true);
    }

    /**
     * Adds a window listener to the given workbench. This listener adds a
     * part naming listener to opening windows.
     *
     * @param workbench The workbench to which the listener will be added.
     */
    private void addWindowListener(IWorkbench workbench) {
        workbench.addWindowListener(new IWindowListener() {

            public void windowActivated(IWorkbenchWindow window) {
                addPartListeners(window);
            }

            public void windowClosed(IWorkbenchWindow window) {
                // Do nothing
            }

            public void windowDeactivated(IWorkbenchWindow window) {
                // Do nothing
            }

            public void windowOpened(IWorkbenchWindow window) {
                addPartListeners(window);
            }

        });
    }

    /**
     * Fires a paint event on all Toolbars and Coolbars within the given shell.
     *
     * @param mainShell The shell to search for Coolbars and Toolbars.
     */
    public static void repaintToolbars(Shell mainShell) {
        List toolbarList = new ArrayList();
        getToolbars(mainShell, toolbarList);
        Iterator iter = toolbarList.iterator();
        while (iter.hasNext()) {
            Control toolbar = (Control)iter.next();
            toolbar.update();
            toolbar.redraw();
            toolbar.update();
        }
    }

    /**
     * Adds all Coolbars and Toolbars within the given composite to the given
     * list. The search is is also performed recursively on children of the
     * given composite.
     *
     * @param composite The composite to search.
     * @param toolbarList The list to which found Toolbars and Coolbars will
     * be added.
     */
    public static void getToolbars(Composite composite,
        List toolbarList) {

        if (composite != null && !composite.isDisposed()) {
            Control [] children = composite.getChildren();
            for (int i = 0; i < children.length; i++) {
                if (children[i] instanceof Composite) {
                    getToolbars((Composite)children[i], toolbarList);
                }
                try {
                    if (children[i] instanceof ToolBar
                            || children[i] instanceof CoolBar) {

                        toolbarList.add(children[i]);
                    }
                } catch (NoClassDefFoundError e) {
                    // we may be running in eRCP which doesn't know about
                    // toolbars, so we just ignore this
                }
            }
        }
    }

    /**
     * Add part listeners to the given window.
     *
     * @param window The window to which the listeners will be added.
     */
    private void addPartListeners(IWorkbenchWindow window) {
        window.getPartService().addPartListener(m_partNamingListener);
        if (m_gefListener != null) {
            window.getPartService().addPartListener(m_gefListener);
        }
    }

    /**
     * Returns the value for a given property. First, <code>envVars</code>
     * is checked for the given property. If this
     * property cannot be found there, the
     * Java System Properties will be checked. If the property is not
     * found there, <code>null</code> will be returned.
     *
     * @param envVars The first source to check for the given property.
     * @param propName The name of the property for which to find the value.
     * @return The value for the given property name, or <code>null</code> if
     *         given property name cannot be found.
     */
    private String getValue(String propName, Properties envVars) {
        String value =
            envVars.getProperty(propName);
        if (value == null) {
            value = System.getProperty(propName);
        }
        return value;
    }
}
