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
package org.eclipse.jubula.client.core.model;

import java.util.Set;


/**
 * interface to manage language lists of persistent objects
 *
 * @author BREDEX GmbH
 * @created 10.10.2005
 *
 */
public interface ILangSupport {
    
    /**
     * @param lang Locale convert to String
     */
    public void addLangToList(String lang);
    
    /**
     * deletes content of language list
     */
    public void clearLangList();
    
    /**
     * @return Returns the isModified.     
     */
    public boolean isModified();
    
    /**
     * @param isModified The isModified to set.
     */
    public void setModified(boolean isModified);
    
    /**
     * @return language list
     */
    public Set<String> getHbmLanguageList();

}
