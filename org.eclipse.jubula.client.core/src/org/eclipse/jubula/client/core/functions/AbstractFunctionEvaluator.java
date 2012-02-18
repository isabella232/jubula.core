package org.eclipse.jubula.client.core.functions;

import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;

public abstract class AbstractFunctionEvaluator 
    implements IFunctionEvaluator {

    /**
     * @param arguments
     * @param numParamsExpected
     * @throws InvalidDataException
     */
    protected void validateParamCount(String[] arguments, 
        int numParamsExpected)
        throws InvalidDataException {
        if (arguments.length != 2) {
            throw new InvalidDataException("expected " + numParamsExpected
                    + " arguments, got " + arguments.length,
                    MessageIDs.E_WRONG_NUM_FUNCTION_ARGS);
        }
    }

}