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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;



/**
 * Function that truncates a given string after a certain number of characters.
 */
public class TruncateFunctionEvaluator implements IFunctionEvaluator {

    /**
     * 
     * {@inheritDoc}
     */
    public String evaluate(String[] arguments) {
        BigDecimal toTruncate = new BigDecimal(arguments[0]);
        int precision = Integer.parseInt(arguments[1]);

        BigDecimal truncated = 
                toTruncate.setScale(precision, RoundingMode.DOWN);
        
        DecimalFormat format = new DecimalFormat();
        format.setGroupingUsed(false);

        return format.format(truncated.doubleValue());
    }

}
