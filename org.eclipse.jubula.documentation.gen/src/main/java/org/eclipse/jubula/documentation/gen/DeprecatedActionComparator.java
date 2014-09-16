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

import java.util.Comparator;

import org.eclipse.jubula.tools.internal.utils.generator.ActionInfo;
import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitInfo;


/**
 * @author BREDEX GmbH
 * @created Aug 8, 2007
 * @version $Revision: 12986 $
 */
public class DeprecatedActionComparator implements Comparator<ActionInfo> {

    /**
     * {@inheritDoc}
     */
    public int compare(ActionInfo ai1, ActionInfo ai2) {
        int result;
        ComponentInfo ci1 = ai1.getContainerComp();
        ComponentInfo ci2 = ai2.getContainerComp();
        ToolkitInfo ti1 = ci1.getTkInfo();
        ToolkitInfo ti2 = ci2.getTkInfo();
        int tkResult = ti1.compareTo(ti2);
        if (tkResult == 0) {
            int cResult = ci1.compareTo(ci2);
            if (cResult == 0) {
                int aResult = ai1.compareTo(ai2);
                result = aResult;
            } else {
                result = cResult;
            }
        } else {
            result = tkResult;
        }
        return result;
    }


}
