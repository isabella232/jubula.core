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

import org.apache.commons.lang.Validate;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.persistence.IEntityManagerProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label decorator that wraps another label decorator. All corresponding method 
 * calls are forwarded to the wrapped decorator, but , where appropriate, the 
 * <code>element</code> to be decorated will be retrieved from the session to 
 * which this decorator corresponds.
 */
public class SessionBasedLabelProviderDecoratorWrapper implements
    ILabelDecorator, ILabelProvider {

    /** 
     * provides the EntityManager from which elements to decorate can 
     * be retrieved 
     */
    private IEntityManagerProvider m_sessionProvider;

    /** the wrapped decorator */
    private ILabelDecorator m_wrappedDecorator;
    
    /** the wrapped provider */
    private ILabelProvider m_wrappedProvider;

    /**
     * Constructor
     * 
     * @param sessionProvider Provides the EntityManager from which elements to 
     *                        decorate can be retrieved. Must not be 
     *                        <code>null</code>.
     * @param baseDecorator The wrapped decorator. Must not be 
     *                      <code>null</code>.
     * @param baseProvider The wrapped provider. Must not be 
     *                      <code>null</code>.
     */
    public SessionBasedLabelProviderDecoratorWrapper(
            IEntityManagerProvider sessionProvider, 
            ILabelDecorator baseDecorator,
            ILabelProvider baseProvider) {

        Validate.notNull(sessionProvider);
        Validate.notNull(baseDecorator);
        Validate.notNull(baseProvider);
        m_sessionProvider = sessionProvider;
        m_wrappedDecorator = baseDecorator;
        m_wrappedProvider = baseProvider;
    }
    
    /** {@inheritDoc} */
    public boolean isLabelProperty(Object element, String property) {
        Object o = getPOFromSession(element);
        if (o != null) {
            return m_wrappedProvider.isLabelProperty(o, property);
        }
        return false;
    }

    /** {@inheritDoc} */
    public Image decorateImage(Image image, Object element) {
        Object o = getPOFromSession(element);
        if (o != null) {
            return m_wrappedDecorator.decorateImage(image, o);
        }
        return null;
    }

    /** {@inheritDoc} */
    public String decorateText(String text, Object element) {
        Object o = getPOFromSession(element);
        if (o != null) {
            return m_wrappedDecorator.decorateText(text, o);
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public void addListener(ILabelProviderListener listener) {
        m_wrappedProvider.addListener(listener);
    }

    /** {@inheritDoc} */
    public void dispose() {
        m_wrappedProvider.dispose();
    }

    /** {@inheritDoc} */
    public void removeListener(ILabelProviderListener listener) {
        m_wrappedProvider.removeListener(listener);
    }

    /** {@inheritDoc} */
    public Image getImage(Object element) {
        Object o = getPOFromSession(element);
        if (o != null) {
            return m_wrappedProvider.getImage(o);
        }
        return null;
    }

    /** {@inheritDoc} */
    public String getText(Object element) {
        Object o = getPOFromSession(element);
        if (o != null) {
            return m_wrappedProvider.getText(o);
        }
        return null;
    }

    /**
     * @param object
     *            the object to search for in session if it's an
     *            IPersistentObject, otherwise the object itself will be returned
     * @return the PO from the current session provider or <code>null</code> if
     *         no such po could be found; or if object was a
     *         non-persistent-object or not-yet-persisted object this method
     *         will return the object itself
     */
    private Object getPOFromSession(Object object) {
        Object sessionPO = object;
        if (object instanceof IPersistentObject
                && ((IPersistentObject) object).getId() != null) {
            IPersistentObject po = (IPersistentObject) object;
            sessionPO = m_sessionProvider.getEntityManager().find(
                    po.getClass(), po.getId());
        }
        return sessionPO;
    }
}
