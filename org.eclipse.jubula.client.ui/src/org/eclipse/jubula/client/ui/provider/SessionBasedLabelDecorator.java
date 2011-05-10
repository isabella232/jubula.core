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
package org.eclipse.jubula.client.ui.provider;

import javax.persistence.EntityManager;

import org.apache.commons.lang.Validate;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.IEntityManagerProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label decorator that wraps another label decorator. All corresponding method 
 * calls are forwarded to the wrapped decorator, but , where appropriate, the 
 * <code>element</code> to be decorated will be retrieved from the session to 
 * which this decorator corresponds.
 */
public class SessionBasedLabelDecorator extends BaseLabelProvider 
        implements ILabelDecorator {

    /** 
     * provides the EntityManager from which elements to decorate can 
     * be retrieved 
     */
    private IEntityManagerProvider m_sessionProvider;

    /** the wrapped decorator */
    private ILabelDecorator m_wrappedDecorator;

    /**
     * Constructor
     * 
     * @param sessionProvider Provides the EntityManager from which elements to 
     *                        decorate can be retrieved. Must not be 
     *                        <code>null</code>.
     * @param baseDecorator The wrapped decorator. Must not be 
     *                      <code>null</code>.
     */
    public SessionBasedLabelDecorator(
            IEntityManagerProvider sessionProvider, 
            ILabelDecorator baseDecorator) {

        Validate.notNull(sessionProvider);
        Validate.notNull(baseDecorator);
        m_sessionProvider = sessionProvider;
        m_wrappedDecorator = baseDecorator;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public boolean isLabelProperty(Object element, String property) {
        if (element instanceof IPersistentObject
                && ((IPersistentObject)element).getId() != null) {
            IPersistentObject po = (IPersistentObject)element;
            EntityManager session = m_sessionProvider.getEntityManager();
            if (session.isOpen()) {
                Object inSession = session.find(po.getClass(), po.getId());
                if (inSession != null) {
                    return m_wrappedDecorator.isLabelProperty(
                            inSession, property);
                }
            }
            return false;
        }

        return m_wrappedDecorator.isLabelProperty(element, property);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Image decorateImage(Image image, Object element) {
        if (element instanceof IPersistentObject
                && ((IPersistentObject)element).getId() != null) {
            IPersistentObject po = (IPersistentObject)element;
            EntityManager session = m_sessionProvider.getEntityManager();
            if (session.isOpen()) {
                Object inSession = session.find(po.getClass(), po.getId());
                if (inSession != null) {
                    return m_wrappedDecorator.decorateImage(image, inSession);
                }
            }
            return null;
        }

        return m_wrappedDecorator.decorateImage(image, element);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String decorateText(String text, Object element) {
        if (element instanceof IPersistentObject
                && ((IPersistentObject)element).getId() != null) {
            IPersistentObject po = (IPersistentObject)element;
            EntityManager session = m_sessionProvider.getEntityManager();
            if (session.isOpen()) {
                Object inSession = session.find(po.getClass(), po.getId());
                if (inSession != null) {
                    return m_wrappedDecorator.decorateText(text, inSession);
                }
            }
            return null;
        }

        return m_wrappedDecorator.decorateText(text, element);
    }

}
