/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Case Child</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.TestCaseChild#getValueProvider <em>Value Provider</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getTestCaseChild()
 * @model abstract="true"
 * @generated
 */
public interface TestCaseChild extends NamedElement {
    /**
     * Returns the value of the '<em><b>Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value Provider</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value Provider</em>' containment reference.
     * @see #setValueProvider(ValueProvider)
     * @see org.eclipse.jubula.JubulaPackage#getTestCaseChild_ValueProvider()
     * @model containment="true"
     * @generated
     */
    ValueProvider getValueProvider();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.TestCaseChild#getValueProvider <em>Value Provider</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value Provider</em>' containment reference.
     * @see #getValueProvider()
     * @generated
     */
    void setValueProvider(ValueProvider value);

} // TestCaseChild
