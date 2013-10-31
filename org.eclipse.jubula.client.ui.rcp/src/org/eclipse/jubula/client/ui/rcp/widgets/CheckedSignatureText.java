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
package org.eclipse.jubula.client.ui.rcp.widgets;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;


/**
 * @author BREDEX GmbH
 * @created 29.10.2013
 */
public class CheckedSignatureText extends CheckedText {

    /**
     * Check for valid names
     */
    public static class SignatureValidator implements IValidator {
       /**
         * 
         */
        public SignatureValidator() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        public ValidationState validateInput(VerifyEvent e) {
            return ValidationState.OK;
        }
        
    }

    /**
     * 
     * @param parent SWT
     * @param style SWT 
     */
    public CheckedSignatureText(Composite parent, int style) {
        super(parent, style, new SignatureValidator());
    }
}
