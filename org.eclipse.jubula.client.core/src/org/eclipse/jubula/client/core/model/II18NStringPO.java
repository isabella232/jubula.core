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

import java.util.Locale;
import java.util.Set;

/**
 * @author BREDEX GmbH
 * @created 20.12.2005
 *
 *
 *
 *
 */
public interface II18NStringPO extends IPersistentObject {
    /**
     * set the value for the given language
     * @param lang language, for which to set the value
     * @param value value
     * @param project associated project
     */
    public abstract void setValue(Locale lang, String value, 
        IProjectPO project);

    /**
     * get the value for a given locale
     * @param lang language, for which to get the value
     * @return value
     */
    public abstract String getValue(Locale lang);

    /**
     * @return a set of all Locale's used in this I18NString
     */
    public abstract Set<Locale> getLanguages();
    
    /**
     * Compares this I18nString object to the given object.
     * Returns true if the values  for all locales are equal.
     * @param obj the object to compare.
     * @return a <code>boolean</code> value. <br>
     * true if the two objects are equal, otherwise false. 
     */
    public abstract boolean equals(Object obj);

    /**
     * @return a deep copy of this
     */
    public abstract II18NStringPO deepCopy();

    /**
     * @return empty string
     */
    public abstract String getName();
}