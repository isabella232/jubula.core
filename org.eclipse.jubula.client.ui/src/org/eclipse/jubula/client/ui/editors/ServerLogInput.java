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
package org.eclipse.jubula.client.ui.editors;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.ui.IPersistableElement;


/**
 * @author BREDEX GmbH
 * @created Feb 8, 2007
 */
public class ServerLogInput extends PlatformObject implements
    ISimpleEditorInput {

    /** the log string */
    private String m_logString;
    
    /**
     * 
     * @param logString the log string
     */
    public ServerLogInput(String logString) {
        m_logString = logString;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor() {
        return ImageDescriptor.getMissingImageDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return I18n.getString("ServerLogViewer.Name"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText() {
        return StringConstants.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    public String getContent() {
        return m_logString;
    }

}
