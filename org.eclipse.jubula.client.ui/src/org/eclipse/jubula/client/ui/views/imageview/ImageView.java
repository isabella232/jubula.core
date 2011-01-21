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
package org.eclipse.jubula.client.ui.views.imageview;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;


/**
 * @author BREDEX GmbH
 * @created Apr 14, 2010
 */
public class ImageView extends ViewPart {
    /**
     * <code>viewer</code>
     */
    private ImageViewer m_viewer;

    /**
     * <code>provider</code>
     */
    private ImageProvider m_provider;

    /**
     * <code>image</code>
     */
    private Image m_image;

    /**
     * The selectionListener listens for changes in the workbench's selection
     * service.
     */
    private ISelectionListener m_selectionListener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart part, 
                ISelection selection) {
            disposeImage();
            m_viewer.redraw();
            handleSelection(selection);
        }
    };

    /**
     * @param selection
     *            the selection
     */
    private void handleSelection(ISelection selection) {
        if (selection == null) {
            return;
        }
        if (selection instanceof IStructuredSelection) {
            handleSelection((IStructuredSelection)selection);
        }
    }

    /**
     * @param selection
     *            the selection
     */
    private void handleSelection(IStructuredSelection selection) {
        if (selection.size() == 0) {
            return;
        }
        handleSelection(selection.getFirstElement());
    }

    /**
     * @param object
     *            the object
     */
    private void handleSelection(Object object) {
        ImageProvider provider = null;

        // First, if the object is adaptable, ask it to get an adapter.
        if (object instanceof IAdaptable) {
            provider = (ImageProvider)((IAdaptable)object)
                    .getAdapter(ImageProvider.class);
        }

        // If we haven't found an adapter yet, try asking the AdapterManager.
        if (provider == null) {
            provider = (ImageProvider)Platform.getAdapterManager().getAdapter(
                    object, ImageProvider.class);
        }

        handleSelection(provider);
    }

    /**
     * @param provider
     *            the provider
     */
    private void handleSelection(final ImageProvider provider) {
        if (provider == null) {
            return;
        }
        final String jobName = Messages.UIJobLoadingImage;
        Job job = new Job(jobName) {
            public IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                setImageProvider(provider);
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        JobUtils.executeJob(job, null);
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout());
        m_viewer = new ImageViewer(parent, SWT.NONE);
        getSelectionService().addSelectionListener(m_selectionListener);
        handleSelection(getSelectionService().getSelection());
    }

    /**
     * @param provider the provider
     */
    protected void setImageProvider(ImageProvider provider) {
        if (provider == null) {
            return;
        }
        final Image image = provider.getImage(m_viewer.getDisplay());
        Plugin.getDisplay().syncExec(new Runnable() {
            public void run() {
                if (image != null) {
                    m_viewer.setImage(image);
                }
                disposeImage();
            }
        });
        this.m_provider = provider;
        this.m_image = image;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        super.dispose();
        getSelectionService().removeSelectionListener(m_selectionListener);
        disposeImage();
    }

    /**
     * dispose the image
     */
    private void disposeImage() {
        if (m_provider == null) {
            return;
        }
        if (m_image == null) {
            return;
        }
        m_provider.disposeImage(m_image);
        m_provider = null;
        m_image = null;
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        m_viewer.setFocus();
    }

    /**
     * @return the selection service
     */
    private ISelectionService getSelectionService() {
        return getSite().getWorkbenchWindow().getSelectionService();
    }
}
