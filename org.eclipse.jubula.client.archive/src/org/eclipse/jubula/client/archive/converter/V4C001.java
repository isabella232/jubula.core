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
package org.eclipse.jubula.client.archive.converter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jubula.client.archive.schema.Aut;
import org.eclipse.jubula.client.archive.schema.ComponentName;
import org.eclipse.jubula.client.archive.schema.Project;

/**
 * Converter for ticket #2820
 * 
 * @author BREDEX GmbH
 * @created Nov 13, 2009
 */
public class V4C001 extends AbstractXmlConverter {

    /**
     * <code>OLD_WEB_TOOLKIT_ID</code>
     */
    private static final String OLD_WEB_TOOLKIT_ID = "com.bredexsw.guidancer.WebToolkitPlugin"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    protected boolean conversionIsNecessary(Project xml) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void convertImpl(Project xml) {
        cleanComponentNameTypes(xml);
        removeWebAUTs(xml);
    }

    /**
     * @param xml
     *            the project
     */
    private void removeWebAUTs(Project xml) {
        List<Integer> positionsToRemove = new LinkedList<Integer>();
        for (Aut a : xml.getAutList()) {
            if (a.getAutToolkit().equals(OLD_WEB_TOOLKIT_ID)) {
                for (int i = 0; i < xml.getAutList().size(); i++) {
                    if (xml.getAutArray(i) == a) {
                        positionsToRemove.add(new Integer(i));
                    }
                }
            }
        }

        Collections.sort(positionsToRemove);
        Collections.reverse(positionsToRemove);

        for (Integer i : positionsToRemove) {
            xml.removeAut(i.intValue());
        }
    }

    /**
     * @param xml
     *            the project xml
     */
    private void cleanComponentNameTypes(Project xml) {
        for (ComponentName cn : xml.getComponentNamesList()) {
            if (cn.getCompType()
                    .equals("com.bredexsw.guidancer.autieserver.implclasses.GraphicApplication")) { //$NON-NLS-1$
                cn.setCompType("guidancer.concrete.GraphicApplication"); //$NON-NLS-1$
            }
        }
    }
}
