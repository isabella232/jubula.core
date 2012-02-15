/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Value Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.jubula.JubulaPackage#getValueProvider()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface ValueProvider extends CDOObject {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model required="true" parameterRequired="true" languageRequired="true"
     * @generated
     */
    EList<String> getValues(Parameter parameter, Language language);

} // ValueProvider
