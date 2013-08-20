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
package org.eclipse.jubula.client.ui.rcp.propertytester;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.propertytester.AbstractBooleanPropertyTester;


/**
 * PropertyTester for INodePO objects.
 *
 * @author BREDEX GmbH
 * @created Jan 13, 2009
 */
public class NodePropertyTester extends AbstractBooleanPropertyTester {
    /** the id of the "isEditable" property */
    public static final String EDITABLE_PROP = "isEditable"; //$NON-NLS-1$
    /** the id of the "hasTaskId" property */
    public static final String HAS_TASK_ID_PROP = "hasTaskId"; //$NON-NLS-1$
    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { EDITABLE_PROP,
        HAS_TASK_ID_PROP};
    
    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        IPersistentObject po = (IPersistentObject)receiver;
        if (property.equals(EDITABLE_PROP)) {
            return testIsEditable(po);
        } else if (property.equals(HAS_TASK_ID_PROP)) {
            return hasTaskIdSet(po);
        }
        return false;
    }

    /**
     * @param node The node for which to check the editabilty.
     * @return the results of <code>guiNode.isEditable()</code>.
     */
    private boolean testIsEditable(IPersistentObject node) {
        if (node instanceof IObjectMappingCategoryPO) {
            return true;
        }
        return NodeBP.isEditable(node);
    }
    
    /**
     * @param node The node for which to check the task id.
     * @return whether a task id is set or not
     */
    private boolean hasTaskIdSet(IPersistentObject node) {
        if (node instanceof INodePO) {
            return StringUtils.isNotEmpty(((INodePO)node).getTaskId());
        }
        return false;
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return IPersistentObject.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
