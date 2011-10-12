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
package org.eclipse.jubula.documentation.gen;

import org.eclipse.jubula.tools.utils.generator.Info;

/**
 * @author BREDEX GmbH
 * @created Nov 19, 2005
 * @version $Revision: 12986 $
 */
public class ErrorInfo extends Info implements Comparable<ErrorInfo> {

    /**
     * <code>m_key</code>
     */
    private Integer m_key;
    
    /**
     * <code>m_errorCode</code>
     */
    private String m_errorCode;
    
    /**
     * @param key
     *        the Integer key
     * @param errorCode
     *        The String error code
     */
    protected ErrorInfo(Integer key, String errorCode) {
//        super(I18nErrorProvider.getInstance().getErrorMessage(errorCode));
        super(errorCode);
        m_key = key;
        //errorcode has a "ErrorMessage." in it, get rid of that.
        //i.e. take everything after the first '.'
//        m_errorCode = errorCode.substring(errorCode.indexOf('.') + 1);
        m_errorCode = errorCode;
    }
    
    /**
     * @return Returns the Integer key corresponding to the error message
     */
    public Integer getKey() {
        return m_key;
    }
    
    /**
     * @return Returns the error code corresponding to the error message
     */
    public String getErrorCode() {
        return m_errorCode;
    }

    /**
     * {@inheritDoc}
     * @param o
     * @return
     */
    public int compareTo(ErrorInfo other) {
/*        // We want to compare by error code (for now)
        return m_errorCode.compareTo(other.m_errorCode);
 */   
        return m_key.compareTo(other.m_key);
    }

}
