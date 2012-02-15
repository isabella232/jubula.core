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
 * A representation of the model object '<em><b>Exec Category</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.ExecCategory#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.jubula.ExecCategory#getTestJobs <em>Test Jobs</em>}</li>
 *   <li>{@link org.eclipse.jubula.ExecCategory#getTestSuites <em>Test Suites</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getExecCategory()
 * @model
 * @generated
 */
public interface ExecCategory extends NamedElement {
    /**
     * Returns the value of the '<em><b>Categories</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.ExecCategory}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Categories</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Categories</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getExecCategory_Categories()
     * @model containment="true"
     * @generated
     */
    EList<ExecCategory> getCategories();

    /**
     * Returns the value of the '<em><b>Test Jobs</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.TestJob}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Test Jobs</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Test Jobs</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getExecCategory_TestJobs()
     * @model containment="true"
     * @generated
     */
    EList<TestJob> getTestJobs();

    /**
     * Returns the value of the '<em><b>Test Suites</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.TestSuite}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Test Suites</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Test Suites</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getExecCategory_TestSuites()
     * @model containment="true"
     * @generated
     */
    EList<TestSuite> getTestSuites();

} // ExecCategory
