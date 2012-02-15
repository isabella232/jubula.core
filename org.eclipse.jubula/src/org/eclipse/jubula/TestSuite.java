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
 * A representation of the model object '<em><b>Test Suite</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.TestSuite#getTestCaseRefs <em>Test Case Refs</em>}</li>
 *   <li>{@link org.eclipse.jubula.TestSuite#getAUT <em>AUT</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getTestSuite()
 * @model
 * @generated
 */
public interface TestSuite extends NamedElement {
    /**
     * Returns the value of the '<em><b>Test Case Refs</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.TestCaseRef}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Test Case Refs</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Test Case Refs</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getTestSuite_TestCaseRefs()
     * @model containment="true"
     * @generated
     */
    EList<TestCaseRef> getTestCaseRefs();

    /**
     * Returns the value of the '<em><b>AUT</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>AUT</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>AUT</em>' reference.
     * @see #setAUT(AUT)
     * @see org.eclipse.jubula.JubulaPackage#getTestSuite_AUT()
     * @model required="true"
     * @generated
     */
    AUT getAUT();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.TestSuite#getAUT <em>AUT</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>AUT</em>' reference.
     * @see #getAUT()
     * @generated
     */
    void setAUT(AUT value);

} // TestSuite
