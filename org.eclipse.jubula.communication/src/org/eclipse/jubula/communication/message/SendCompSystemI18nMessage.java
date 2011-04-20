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
package org.eclipse.jubula.communication.message;

/**
 * @author BREDEX GmbH
 * @created Oct 31, 2007
 */
public class SendCompSystemI18nMessage extends Message {
    /** The ResourceBundles */
    private String m_resourceBundles;

    /**  */
    public SendCompSystemI18nMessage() {
        super();
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return "org.eclipse.jubula.communication.commands.SetCompSystemI18nCommand"; //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    public double getVersion() {
        return 1.0;
    }

    /** @return the ResourceBundles */
    public String getResourceBundles() {
        return m_resourceBundles;
    }

    /**
     * @param resourceBundles
     *            a List of ResourceBundles
     */
    public void setResourceBundles(String resourceBundles) {
        m_resourceBundles = resourceBundles;
    }
}