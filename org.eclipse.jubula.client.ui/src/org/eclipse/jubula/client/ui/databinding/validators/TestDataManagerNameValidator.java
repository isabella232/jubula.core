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
package org.eclipse.jubula.client.ui.databinding.validators;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.tools.i18n.I18n;


/**
 * Validates a test data cube name.
 *
 * @author BREDEX GmbH
 * @created Jun 29, 2010
 */
public class TestDataManagerNameValidator implements IValidator {
    /**
     * <code>m_alreadyUsedNames</code>
     */
    private Set<String> m_alreadyUsedNames;

    /**
     * Constructor
     * 
     * @param oldName if not null allow this name the support rename
     * @param usedNames a set of already used names
     */
    public TestDataManagerNameValidator(String oldName, Set<String> usedNames) {
        m_alreadyUsedNames = usedNames;
    }
    
    /**
     * {@inheritDoc}
     */
    public IStatus validate(Object value) {
        return isTestDataCubeName(String.valueOf(value));
    }

    /**
     * @param stringValue component name
     * @return IStatus.OK if this is a valid component name, error status
     * otherwise
     */
    public IStatus isTestDataCubeName(String stringValue) {
        IStatus is = isValidTestDataCubeString(stringValue);
        if (!is.isOK()) {
            return is;
        }
        if (!m_alreadyUsedNames.contains(stringValue)) {
            return ValidationStatus.ok();
        }

        return ValidationStatus.error(
                I18n.getString("TestDataCube.Error.Exists")); //$NON-NLS-1$
    }

    /**
     * @param stringValue name to check
     * @return IStatus.OK if this is a valid component name, error status
     * otherwise
     */
    public static IStatus isValidTestDataCubeString(String stringValue) {
        if (StringUtils.isEmpty(stringValue)) {
            return ValidationStatus.error(
                    I18n.getString("TestDataCube.Error.Empty")); //$NON-NLS-1$
        }
        if (stringValue.startsWith(" ")  //$NON-NLS-1$
            || stringValue.charAt(
                    stringValue.length() - 1) == ' ') {

            return ValidationStatus.error(
                    I18n.getString("TestDataCube.Error.NoSpaceAtStartOrEnd")); //$NON-NLS-1$
        }
        for (char ch : stringValue.toCharArray()) {
            if (Character.isISOControl(ch)) {
                return ValidationStatus.error(I18n
                        .getString("TestDataCube.Error.InvalidChar")); //$NON-NLS-1$
            }
        }
        return ValidationStatus.ok();
    }

}
