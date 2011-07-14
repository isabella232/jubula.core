/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.provider.contentprovider.objectmapping.OMEditorTreeContentProvider;
import org.eclipse.jubula.client.ui.search.query.ShowResponsibleNodeForComponentName;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

/**
 * @author Markus Tiede
 * @created Jul 13, 2011
 */
public class OMEShowUnusedComponentNamesHandler extends AbstractHandler {
    /** standard logging */
    static final Log LOG = LogFactory
            .getLog(OMEShowUnusedComponentNamesHandler.class);

    /**
     * @author Markus Tiede
     * @created Jul 13, 2011
     */
    public class OMEUsedComponentNameViewerFilter extends ViewerFilter {
        /**
         * <code>m_editor</code> the current editor
         */
        private ObjectMappingMultiPageEditor m_editor = null;
        
        /**
         * <code>m_cache</code>
         */
        private Map<Object, Boolean> m_cache = new HashMap<Object, Boolean>();
        
        /**
         * <code>m_useCache</code>
         */
        private boolean m_useCache = true;

        /**
         * @param editor
         *            the current editor
         */
        public OMEUsedComponentNameViewerFilter(
                ObjectMappingMultiPageEditor editor) {
            m_editor = editor;
        }
        
        /**
         * @param elements the elements to init the cache with
         * @param monitor
         *            the progress monitor to use
         */
        public void initCache(Object[] elements, IProgressMonitor monitor) {
            enableCache();
            int noOfUnused = 0;
            for (Object o : elements) {
                if (monitor.isCanceled()) {
                    disableCache();
                    return;
                }
                if (select(null, null, o) && o instanceof IComponentNamePO
                        && m_cache.containsKey(o)) {
                    noOfUnused++;
                    monitor.subTask(NLS.bind(
                            Messages.FilteringUsedComponentNames, noOfUnused));
                }
                monitor.worked(1);
            }
        }

        /** {@inheritDoc} */
        public boolean select(Viewer viewer, Object parentElement,
                Object element) {
            if (m_useCache) {
                if (m_cache.containsKey(element)) {
                    return m_cache.get(element);
                }
            }
            boolean select = true;
            if (element instanceof IComponentNamePO) {
                IComponentNamePO compName = (IComponentNamePO)element;
                select = ShowResponsibleNodeForComponentName
                        .calculateListOfCompNameUsingNodes(compName.getGuid(),
                                m_editor.getAut()).isEmpty();
            }
            if (m_useCache) {
                m_cache.put(element, select);
            }
            return select;
        }
        
        /**
         * disable the cache
         */
        public void disableCache() {
            m_useCache = false;
            m_cache.clear();
        }
        
        /**
         * enable the cache
         */
        public void enableCache() {
            m_useCache = true;
            m_cache.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ObjectMappingMultiPageEditor editor = 
            ((ObjectMappingMultiPageEditor)HandlerUtil
                .getActivePartChecked(event));
        boolean found = false;
        for (TreeViewer tv : editor.getTreeViewers()) {
            for (ViewerFilter vf : tv.getFilters()) {
                if (vf instanceof OMEUsedComponentNameViewerFilter) {
                    tv.removeFilter(vf);
                    updateCommandState(false);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            // run in UI thread
            List<Object> allTreeElements = new ArrayList<Object>();
            for (final TreeViewer tv : editor.getTreeViewers()) {
                OMEditorTreeContentProvider ometcp = 
                    (OMEditorTreeContentProvider)tv.getContentProvider();
                for (Object o : ometcp.getElements(tv.getInput())) {
                    allTreeElements.addAll(getAllElements(o, ometcp));
                }
            }
            final Object[] aObjects = allTreeElements.toArray();
            // run in background
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(HandlerUtil
                    .getActiveShell(event).getShell());
            try {
                dialog.run(true, true, new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor) {
                        monitor.beginTask(Messages.Filtering,
                                aObjects.length);
                        final OMEUsedComponentNameViewerFilter vf = 
                            new OMEUsedComponentNameViewerFilter(
                                editor);
                        vf.initCache(aObjects, monitor);
                        if (!monitor.isCanceled()) {
                            Plugin.getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    // run in UI thread
                                    for (final TreeViewer tv : editor
                                            .getTreeViewers()) {
                                        tv.expandAll();
                                        tv.addFilter(vf);
                                    }
                                    vf.disableCache();
                                    updateCommandState(true);
                                }

                            });
                        }
                        monitor.done();
                    }
                });
            } catch (InvocationTargetException e) {
                LOG.error(e);
            } catch (InterruptedException e) {
                LOG.error(e);
            }
        }
        return null;
    }

    /**
     * @param toggleState
     *            the toggle state
     */
    public static void updateCommandState(boolean toggleState) {
        CommandHelper
                .getCommandService()
                .getCommand(
                        CommandIDs.OME_SHOW_UNUSED_COMPONENT_NAME_COMMAND_ID)
                .getState(RegistryToggleState.STATE_ID).setValue(toggleState);
    }

    /**
     * @param o
     *            the object to begin traversing at
     * @param ometcp
     *            the OMEditorTreeContentProvider
     * @return all elements of the tree
     */
    private List<Object> getAllElements(Object o,
            OMEditorTreeContentProvider ometcp) {
        List<Object> treeElements = new ArrayList<Object>();
        treeElements.add(o);
        if (ometcp.hasChildren(o)) {
            for (Object child : ometcp.getChildren(o)) {
                treeElements.addAll(getAllElements(child, ometcp));
            }
        }
        return treeElements;
    }
}
