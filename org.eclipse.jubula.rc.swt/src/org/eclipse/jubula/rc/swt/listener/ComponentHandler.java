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
package org.eclipse.jubula.rc.swt.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.communication.message.ChangeAUTModeMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.GuiDancerComponentNotFoundException;
import org.eclipse.jubula.rc.common.exception.GuiDancerComponentNotManagedException;
import org.eclipse.jubula.rc.common.exception.GuiDancerNoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.exception.GuiDancerUnsupportedComponentException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.IComponentFactory;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.components.SwtAUTHierarchy;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.tools.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.objects.IComponentIdentifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


/**
 * This class is responsible for handling the components of the AUT. <br>
 * This class implements the SWTEventListener interface, listening to
 * <code>ShellEvent.Activated</code>. 
 *  
 * An instance of <code>AUTSWTHierarchy</code> is notified for WindowEvents. <br>
 * 
 * The static methods for fetching an identifier for a component and getting the
 * component for an identifer delegates to this AUTSWTHierarchy.
 * 
 * @author BREDEX GmbH
 * @created 19.04.2006
 */
public class ComponentHandler extends BaseSwtEventListener 
    implements BaseAUTListener { 

    /** the logger */
    private static Log log = LogFactory.getLog(ComponentHandler.class);

    /** the Container hierarchy of the AUT*/
    private static SwtAUTHierarchy autHierarchy = new SwtAUTHierarchy();
    
    /**
     * private constructor
     */
    public ComponentHandler() {
        super();

        EventThreadQueuerSwtImpl etQueuer = new EventThreadQueuerSwtImpl();
        etQueuer.invokeAndWait(this.getClass().getName() 
                + "Add active shell to AUT Hierarchy", new IRunnable() { //$NON-NLS-1$
                    public Object run() throws StepExecutionException {
                        Shell activeShell = 
                            ((SwtAUTServer)AUTServer.getInstance())
                                .getAutDisplay().getActiveShell();
                        if (activeShell != null) {
                            autHierarchy.add(activeShell);
                        }
                        
                        // Return value not used
                        return null;
                    }
                });

    }
    
    /**
     * Investigates the given <code>component</code> for an identifier. It
     * must be distinct for the whole AUT. To obtain this identifier the
     * AUTSWTHierarchy is queried. 
     * @param component the component to get an identifier for
     * @throws GuiDancerNoIdentifierForComponentException if an identifer could not created for <code>component</code>.
     * @return the identifier, containing the identification 
     */
    public static IComponentIdentifier getIdentifier(Widget component) 
        throws GuiDancerNoIdentifierForComponentException {
        
        try {
            return autHierarchy.getComponentIdentifier(component);
        } catch (GuiDancerComponentNotManagedException cnme) {
            log.warn(cnme);
            throw new GuiDancerNoIdentifierForComponentException(
                    "unable to create an identifier for '" //$NON-NLS-1$
                    + component + "'", //$NON-NLS-1$
                    MessageIDs.E_COMPONENT_ID_CREATION); 
        }
    }
    
    /**
     * dummy doc see ComponentHandler
     * @param factory factory
     * @param componentName componentName
     * @param technicalName technicalName
     * @throws GuiDancerUnsupportedComponentException
     */
    public static void addToHierarchy(IComponentFactory factory,
        String componentName, String technicalName)
        throws GuiDancerUnsupportedComponentException {
        
        autHierarchy.addToHierarchy(factory, componentName, technicalName);
    }
    
    /**
     * returns an array of all componentIdentifier of (supported) components,
     * which are currently instantiated by the AUT. <br>
     * delegate to AUTSWTHierarchy.getAllComponentId() 
     * @return array with componentIdentifier, never null
     */
    public static IComponentIdentifier[] getAllComponentId() {
        return autHierarchy.getAllComponentId();
    }
    
    /**
     * Searchs the component in the AUT, which belongs to the given
     * <code>componentIdentifier</code>. 
     * @param componentIdentifier the identifier of the component to search for
     * @param retry number of tries to get object
     * @param timeout timeout for retries
     * @throws GuiDancerComponentNotFoundException if no component is found for the given identifier.
     * @throws IllegalArgumentException if the identifier is null or contains invalid data
     * {@inheritDoc}
     * @return the found component
     */
    public static Widget findComponent(
            final IComponentIdentifier componentIdentifier, boolean retry, 
            int timeout)
        throws GuiDancerComponentNotFoundException, IllegalArgumentException {
        
        long start = System.currentTimeMillis();

        // FIXME Dennis : waitForComponent
        
        try {
            return autHierarchy.findComponent(componentIdentifier);
        } catch (GuiDancerComponentNotManagedException cnme) {
            log.warn(cnme);
            if (retry) {

                while (System.currentTimeMillis() - start < timeout) {
                    try {
                        Thread.sleep(TimingConstantsServer
                                .POLLING_DELAY_FIND_COMPONENT);
                        
                        // Execute the search synchronously on the event queue.
                        // This prevents threading issues, as the AUT hierarchy
                        // is only modified from the event thread.
                        IEventThreadQueuer queuer = 
                            new EventThreadQueuerSwtImpl();
                        Widget component = (Widget)queuer.invokeAndWait("findComponent", new IRunnable() { //$NON-NLS-1$

                            public Object run() throws StepExecutionException {
                                try {
                                    return autHierarchy.
                                        findComponent(componentIdentifier); 
                                }  catch (GuiDancerComponentNotManagedException e) { // NOPMD by zeb on 10.04.07 15:26
                                    // OK, we will throw a corresponding exception later
                                    // if we really can't find the component
                                } catch (InvalidDataException ide) { // NOPMD by zeb on 10.04.07 15:26
                                    // OK, we will throw a corresponding exception later
                                    // if we really can't find the component
                                }
                                
                                return null;
                            }
                            
                        });
                        
                        if (component != null) {
                            return component;
                        }
                    } catch (InterruptedException e) {
                        // ok
                    }
                }
            }
            throw new GuiDancerComponentNotFoundException(
                        cnme.getMessage(), MessageIDs.E_COMPONENT_NOT_FOUND);
        } catch (IllegalArgumentException iae) {
            log.error(iae);
            throw iae;
        } catch (InvalidDataException ide) {
            log.error(ide);
            throw new GuiDancerComponentNotFoundException(
                    ide.getMessage(), MessageIDs.E_COMPONENT_NOT_FOUND);
        }
    }

    /**
     * {@inheritDoc}
     */
    public long[] getEventMask() {
        return new long[]{SWT.Activate, SWT.Show, SWT.Paint, SWT.Hide};
    }

    /**
     * @param event event
     */
    private void eventDispatched(Event event) {
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
            this.getClass().getClassLoader());
        try {
            if (log.isDebugEnabled()) {
                log.debug(event.toString());
            }
            int id = event.type;
            switch (id) {
                case SWT.Activate:
                case SWT.Show:
                case SWT.Paint:
                    // add recursivly all components to AUTSWTHierarchy 
                    // and create names for unnamed components
                    if (event.widget instanceof Shell) {
                        Shell window = (Shell)event.widget;
                        autHierarchy.refreshShell(window);
                    } else {
                        refreshComponent(event.widget);
                    }
                    break;
                case SWT.Hide:
                    autHierarchy.componentRemoved(event.widget);
                    break;
                default:
                    // do nothing
            }
            if (AUTServer.getInstance().getMode() 
                == ChangeAUTModeMessage.OBJECT_MAPPING) {
                AUTServer.getInstance().updateHighLighter();
            }
        } catch (Throwable t) {
            log.error("exception during ComponentHandler", t); //$NON-NLS-1$
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }

    /**
     * Refreshes the component within the AUTSWTHierarchy.
     * 
     * @param toRefresh the component to refresh.
     */
    private void refreshComponent(Widget toRefresh) {
        // Refresh the component entry in the AutHierarchy
        if (toRefresh != null && !toRefresh.isDisposed()) {
            autHierarchy.refreshComponent(toRefresh);
        }
    }
    
    /**
     * 
     * @return the AUT Hierarchy
     */
    public static SwtAUTHierarchy getAutHierarchy() {
        return autHierarchy;
    }

    /**
     * {@inheritDoc}
     */
    public void handleEvent(Event event) {
        eventDispatched(event);
    }
}