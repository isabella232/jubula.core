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
 * A representation of the model object '<em><b>Spec Category</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.SpecCategory#getTestCases <em>Test Cases</em>}</li>
 *   <li>{@link org.eclipse.jubula.SpecCategory#getCategories <em>Categories</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getSpecCategory()
 * @model
 * @generated
 */
public interface SpecCategory extends NamedElement {
    /**
     * Returns the value of the '<em><b>Test Cases</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.TestCase}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Test Cases</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Test Cases</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getSpecCategory_TestCases()
     * @model containment="true"
     * @generated
     */
    EList<TestCase> getTestCases();

    /**
     * Returns the value of the '<em><b>Categories</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.SpecCategory}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Categories</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Categories</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getSpecCategory_Categories()
     * @model containment="true"
     * @generated
     */
    EList<SpecCategory> getCategories();

} // SpecCategory
