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

import java.util.Comparator;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.model.IPersistentObject;


/**
 * Comparator for IPersistensObject
 * @author BREDEX GmbH
 * @created Nov 17, 2005
 *
 */
public class PersistenceObjectComparator 
    implements Comparator<IPersistentObject> {
    /**
     * {@inheritDoc}
     */
    public int compare(IPersistentObject o1, IPersistentObject o2) {
        Validate.noNullElements(new Object[]{o1, o2}, "The persistant objects must be not null!"); //$NON-NLS-1$
        String pers1 = o1.getName().toLowerCase();
        String pers2 = o2.getName().toLowerCase();
        if (pers1 == null && pers2 == null) {
            return 0;
        } else if (pers2 == null) {
            return -1;
        } else if (pers1 == null) {
            return 1;
        }
        return pers1.compareTo(pers2);
    }
}