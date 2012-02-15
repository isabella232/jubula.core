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
 * A representation of the model object '<em><b>Test Case Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.TestCaseRef#getTestCase <em>Test Case</em>}</li>
 *   <li>{@link org.eclipse.jubula.TestCaseRef#getComponentRefs <em>Component Refs</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getTestCaseRef()
 * @model
 * @generated
 */
public interface TestCaseRef extends TestCaseChild {
    /**
     * Returns the value of the '<em><b>Test Case</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Test Case</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Test Case</em>' reference.
     * @see #setTestCase(TestCase)
     * @see org.eclipse.jubula.JubulaPackage#getTestCaseRef_TestCase()
     * @model required="true"
     * @generated
     */
    TestCase getTestCase();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.TestCaseRef#getTestCase <em>Test Case</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Test Case</em>' reference.
     * @see #getTestCase()
     * @generated
     */
    void setTestCase(TestCase value);

    /**
     * Returns the value of the '<em><b>Component Refs</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.jubula.ComponentRef}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Component Refs</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Component Refs</em>' reference list.
     * @see org.eclipse.jubula.JubulaPackage#getTestCaseRef_ComponentRefs()
     * @model
     * @generated
     */
    EList<ComponentRef> getComponentRefs();

} // TestCaseRef
