/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;

/**
 * @author BREDEX GmbH
 */ 
public class NodeAttributeEvaluator extends AbstractFunctionEvaluator {
    /**
     * the comment attribute name
     */
    private static final String COMMENT_ATTRIBUTE = "comment"; //$NON-NLS-1$
    
    /**
     * the name attribute name
     */
    private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 1);
        String arg0 = arguments[0].toLowerCase();
        String attributeValue = null;
        INodePO node = getContext().getNode();
        if (NAME_ATTRIBUTE.equals(arg0)) {
            attributeValue = node.getName();
        } else if (COMMENT_ATTRIBUTE.equals(arg0)) {
            attributeValue = node.getComment();
        } else {
            throw new InvalidDataException("Unkown attribute: " //$NON-NLS-1$
                    + arg0, MessageIDs.E_WRONG_PARAMETER_VALUE);
        }
        return attributeValue;
    }
}
