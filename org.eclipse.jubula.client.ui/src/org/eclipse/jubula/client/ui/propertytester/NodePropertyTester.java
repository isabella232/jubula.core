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
package org.eclipse.jubula.client.ui.propertytester;

import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.INodePO;


/**
 * PropertyTester for INodePO objects.
 *
 * @author BREDEX GmbH
 * @created Jan 13, 2009
 */
public class NodePropertyTester extends AbstractBooleanPropertyTester {
    /** the id of the "isEditable" property */
    public static final String EDITABLE_PROP = "isEditable"; //$NON-NLS-1$
    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { EDITABLE_PROP };
    
    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        INodePO guiNode = (INodePO)receiver;
        if (property.equals(EDITABLE_PROP)) {
            return testIsEditable(guiNode);
        }
        return false;
    }

    /**
     * @param node The node for which to check the editabilty.
     * @return the results of <code>guiNode.isEditable()</code>.
     */
    private boolean testIsEditable(INodePO node) {
        return NodeBP.isEditable(node);
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return INodePO.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
