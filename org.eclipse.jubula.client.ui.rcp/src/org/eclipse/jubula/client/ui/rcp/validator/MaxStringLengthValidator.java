package org.eclipse.jubula.client.ui.rcp.validator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.model.INodePO;

/**
 * @author BREDEX GmbH
 */
public class MaxStringLengthValidator implements IValidator {
    /** {@inheritDoc} */
    public IStatus validate(Object value) {
        if (value instanceof String) {
            return (((String) value).length() < INodePO.MAX_STRING_LENGTH) 
                    ? Status.OK_STATUS : Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }
}
