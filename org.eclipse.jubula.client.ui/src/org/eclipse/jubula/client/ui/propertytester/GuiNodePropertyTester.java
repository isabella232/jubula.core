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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.osgi.util.NLS;


/**
 * PropertyTester for INodePO objects.
 *
 * @author BREDEX GmbH
 * @created Jan 13, 2009
 */
public class GuiNodePropertyTester extends PropertyTester {
    
    /** the id of the "isEditable" property */
    public static final String EDITABLE_PROP = "isEditable"; //$NON-NLS-1$

    /** the logger */
    private static final Log LOG = 
        LogFactory.getLog(GuiNodePropertyTester.class);
    
    /**
     * 
     * {@inheritDoc}
     */
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {

        if (receiver instanceof INodePO) {
            INodePO guiNode = (INodePO)receiver;
            if (property.equals(EDITABLE_PROP)) {
                boolean isEditable = testIsEditable(guiNode);
                boolean expectedBoolean = expectedValue instanceof Boolean 
                    ? ((Boolean)expectedValue).booleanValue() : true;
                return isEditable == expectedBoolean;
            }

            LOG.warn(NLS.bind(Messages.PropertyTesterPropertyNotSupported,
                    new String [] {property}));
            return false;
        }

        String receiverClass = 
            receiver != null ? receiver.getClass().getName() : "null"; //$NON-NLS-1$
        LOG.warn(NLS.bind(Messages.PropertyTesterTypeNotSupported,
                new String [] {receiverClass}));
        return false;
    }

    /**
     * 
     * @param node The node for which to check the editabilty.
     * @return the results of <code>guiNode.isEditable()</code>.
     */
    private boolean testIsEditable(INodePO node) {
        return NodeBP.isEditable(node);
    }
}
