/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Suite Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.TestSuiteRef#getTestSuite <em>Test Suite</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getTestSuiteRef()
 * @model
 * @generated
 */
public interface TestSuiteRef extends NamedElement {
    /**
     * Returns the value of the '<em><b>Test Suite</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Test Suite</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Test Suite</em>' reference.
     * @see #setTestSuite(TestSuite)
     * @see org.eclipse.jubula.JubulaPackage#getTestSuiteRef_TestSuite()
     * @model required="true"
     * @generated
     */
    TestSuite getTestSuite();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.TestSuiteRef#getTestSuite <em>Test Suite</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Test Suite</em>' reference.
     * @see #getTestSuite()
     * @generated
     */
    void setTestSuite(TestSuite value);

} // TestSuiteRef
