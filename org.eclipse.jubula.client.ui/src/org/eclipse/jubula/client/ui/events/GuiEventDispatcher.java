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
package org.eclipse.jubula.client.ui.events;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jubula.client.ui.editors.IGDEditor;
import org.eclipse.jubula.tools.constants.DebugConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Jun 7, 2010
 */
public class GuiEventDispatcher {

    /** to notify clients about change of the dirty state of an editor */
    public interface IEditorDirtyStateListener {
        /**
         * callback method
         * @param editor The editor whose dirty state changed
         * @param isDirty The new dirty state
         */
        public void handleEditorDirtyStateChanged(IGDEditor editor, 
                boolean isDirty);
    }
    
    /** logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(GuiEventDispatcher.class);
    

    /**
     * <code>m_instance</code> singleton
     */
    private static GuiEventDispatcher instance = null;

    /**
     * <code>m_editorDirtyStateListeners</code> listener for notification 
     * about change of the dirty state of an editor
     */
    private Set<IEditorDirtyStateListener> m_editorDirtyStateListeners =
        new HashSet<IEditorDirtyStateListener>();
    /**
     * <code>m_editorDirtyStateListenersPost</code> listener for notification 
     * about change of the dirty state of an editor
     *  POST-Event for gui updates 
     */
    private Set<IEditorDirtyStateListener> m_editorDirtyStateListenersPost =
        new HashSet<IEditorDirtyStateListener>();
    
    /**
     * private constructor
     */
    private GuiEventDispatcher() {
        // Nothing to initialize
    }
    
    /**
     * @return the single instance
     */
    public static synchronized GuiEventDispatcher getInstance() {
        if (instance == null) {
            instance = new GuiEventDispatcher();
        }
        return instance;
    }

    /**
     * @param l The listener to add as new listener for notification 
     * about change of the dirty state of an editor
     * @param guiMode
     *      should this listener be called after the model listener 
     */
    public void addEditorDirtyStateListener(
        IEditorDirtyStateListener l, boolean guiMode) {
        if (guiMode) {
            m_editorDirtyStateListenersPost.add(l);
        } else {
            m_editorDirtyStateListeners.add(l);
        }
    }
    
    /**
     * @param l The listener to be deleted as listener for notification 
     * about change of the dirty state of an editor
     */
    public void removeEditorDirtyStateListener(IEditorDirtyStateListener l) {
        m_editorDirtyStateListeners.remove(l);
        m_editorDirtyStateListenersPost.remove(l);
    }
    
    /**
     * notify listener about change of the dirty state of an editor
     * @param editor The editor whose dirty state changed
     * @param isDirty The new dirty state
     */
    public void fireEditorDirtyStateListener(IGDEditor editor, 
            boolean isDirty) {
        long start = System.currentTimeMillis();
        
        // model updates
        final Set<IEditorDirtyStateListener> stableListeners = 
            new HashSet<IEditorDirtyStateListener>(m_editorDirtyStateListeners);
        for (IEditorDirtyStateListener l : stableListeners) {
            try {
                l.handleEditorDirtyStateChanged(editor, isDirty);
            } catch (Throwable t) {
                LOG.error("Unhandled exception while calling listeners", t); //$NON-NLS-1$
            }
        }

        // gui updates
        final Set<IEditorDirtyStateListener> stableListenersPost = 
            new HashSet<IEditorDirtyStateListener>
            (m_editorDirtyStateListenersPost);
        for (IEditorDirtyStateListener l : stableListenersPost) {
            try {
                l.handleEditorDirtyStateChanged(editor, isDirty);
            } catch (Throwable t) {
                LOG.error("Unhandled exception while calling listeners", t); //$NON-NLS-1$
            }
        }
        
        if (System.getProperty(DebugConstants.VM_DEBUG) != null
            && System.getProperty(DebugConstants.VM_DEBUG).equals("true")) { //$NON-NLS-1$
            System.out.println("fireEditorDirtyStateListener():"  //$NON-NLS-1$
                + (System.currentTimeMillis() - start));
        }
    }
    
}
