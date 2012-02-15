/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Local Value Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.LocalValueProvider#getValues <em>Values</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getLocalValueProvider()
 * @model
 * @generated
 */
public interface LocalValueProvider extends ValueProvider {
    /**
     * Returns the value of the '<em><b>Values</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.Value}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Values</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Values</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getLocalValueProvider_Values()
     * @model containment="true" required="true"
     * @generated
     */
    EList<Value> getValues();

} // LocalValueProvider
