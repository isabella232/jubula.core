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
 * A representation of the model object '<em><b>CTD Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.CTDSet#getValueProvider <em>Value Provider</em>}</li>
 *   <li>{@link org.eclipse.jubula.CTDSet#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getCTDSet()
 * @model
 * @generated
 */
public interface CTDSet extends NamedElement {
    /**
     * Returns the value of the '<em><b>Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value Provider</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value Provider</em>' containment reference.
     * @see #setValueProvider(LocalValueProvider)
     * @see org.eclipse.jubula.JubulaPackage#getCTDSet_ValueProvider()
     * @model containment="true" required="true"
     * @generated
     */
    LocalValueProvider getValueProvider();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.CTDSet#getValueProvider <em>Value Provider</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value Provider</em>' containment reference.
     * @see #getValueProvider()
     * @generated
     */
    void setValueProvider(LocalValueProvider value);

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.Parameter}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getCTDSet_Parameters()
     * @model containment="true"
     * @generated
     */
    EList<Parameter> getParameters();

} // CTDSet
