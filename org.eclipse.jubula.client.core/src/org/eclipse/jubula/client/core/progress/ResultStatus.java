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
package org.eclipse.jubula.client.core.progress;

import org.eclipse.core.runtime.Status;

/**
 * @author BREDEX GmbH
 * @created Apr 1, 2011
 * @param <T>
 *            The type of the return result.
 */
public class ResultStatus<T> extends Status {
    /** the return result */
    private T m_result;

    /**
     * @param severity
     *            the severity
     * @param pluginId
     *            the plugin id
     * @param message
     *            the message
     */
    public ResultStatus(int severity, String pluginId, String message) {
        super(severity, pluginId, message);
    }

    /**
     * @param severity
     *            the severity
     * @param pluginId
     *            the plugin id
     * @param message
     *            the message
     * @param result
     *            the result
     */
    public ResultStatus(int severity, String pluginId, String message, 
        T result) {
        this(severity, pluginId, message);
        setResult(result);
    }

    /**
     * @param result
     *            the result to use.
     */
    protected void setResult(T result) {
        m_result = result;
    }

    /**
     * @return the result.
     */
    public T getResult() {
        return m_result;
    }
}
