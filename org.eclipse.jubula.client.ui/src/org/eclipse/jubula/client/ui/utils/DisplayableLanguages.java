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
package org.eclipse.jubula.client.ui.utils;

import java.util.List;
import java.util.Locale;

/**
 * Class for exchanging displayable Languages
 *
 * @author BREDEX GmbH
 * @created 03.04.2006
 */
public class DisplayableLanguages {

    /** The Locales */
    private List<Locale> m_locales;
    
    
    /**
     * @param locales the Locales
     */
    public DisplayableLanguages(List<Locale> locales) {
        m_locales = locales;
    }

    /**
     * @return The Locales
     */
    public List<Locale> getLocales() {
        return m_locales;
    }

}
