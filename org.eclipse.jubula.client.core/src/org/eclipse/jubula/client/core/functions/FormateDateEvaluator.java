/**
 * 
 */
package org.eclipse.jubula.client.core.functions;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.eclipse.jubula.tools.exception.InvalidDataException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;

/**
 * @author al
 * 
 */
public final class FormateDateEvaluator extends AbstractFunctionEvaluator {

    /**
     * 
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) throws InvalidDataException {
        validateParamCount(arguments, 2);
        try {
            Long dateTime = Long.valueOf(arguments[0]);
            if (dateTime < 0) {
                throw new InvalidDataException("value to small: " + dateTime, //$NON-NLS-1$
                        MessageIDs.E_TOO_SMALL_VALUE);
            }
            Date date = new Date(dateTime);

            return DateFormatUtils.format(date, arguments[1]);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("not an integer: " + arguments[0], //$NON-NLS-1$
                    MessageIDs.E_BAD_INT);

        }
    }

}
