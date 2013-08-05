/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.core.functions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.alm.core.i18n.Messages;
import org.eclipse.jubula.client.alm.core.utils.ALMAccess;
import org.eclipse.jubula.client.core.functions.AbstractFunctionEvaluator;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 */
public class GetALMTaskAttribute extends AbstractFunctionEvaluator {
    /** the supported attributes */
    private static Map<String, String> mappedAttributes = 
            new HashMap<String, String>();

    static {
        mappedAttributes.put("assignee", TaskAttribute.USER_ASSIGNED);
        mappedAttributes.put("component", TaskAttribute.COMPONENT);
        mappedAttributes.put("description", TaskAttribute.DESCRIPTION);
        mappedAttributes.put("priority", TaskAttribute.PRIORITY);
        mappedAttributes.put("product", TaskAttribute.PRODUCT);
        mappedAttributes.put("rank", TaskAttribute.RANK);
        mappedAttributes.put("reporter", TaskAttribute.USER_REPORTER);
        mappedAttributes.put("resolution", TaskAttribute.RESOLUTION);
        mappedAttributes.put("summary", TaskAttribute.SUMMARY);
        mappedAttributes.put("status", TaskAttribute.STATUS);
    }

    /** {@inheritDoc} */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 3);
        String repo = arguments[0];
        String taskId = arguments[1];
        String attributeId = arguments[2];

        if (!mappedAttributes.containsKey(attributeId)) {
            throw new InvalidDataException(NLS.bind(
                    Messages.UnsupportedTaskAttribute, attributeId),
                    MessageIDs.E_FUNCTION_EVAL_ERROR);
        }

        String mappedAttributeId = mappedAttributes.get(attributeId);

        String taskAttributeValue = ALMAccess.getTaskAttributeValue(repo,
                taskId, mappedAttributeId);
        if (taskAttributeValue == null) {
            throw new InvalidDataException(NLS.bind(
                    Messages.TaskAttributeNotFound, attributeId),
                    MessageIDs.E_FUNCTION_EVAL_ERROR);
        }

        return taskAttributeValue;
    }
}
