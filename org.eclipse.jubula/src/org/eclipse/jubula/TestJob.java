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
 * A representation of the model object '<em><b>Test Job</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.TestJob#getTestSuiteRefs <em>Test Suite Refs</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getTestJob()
 * @model
 * @generated
 */
public interface TestJob extends NamedElement {
    /**
     * Returns the value of the '<em><b>Test Suite Refs</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.TestSuiteRef}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Test Suite Refs</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Test Suite Refs</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getTestJob_TestSuiteRefs()
     * @model containment="true"
     * @generated
     */
    EList<TestSuiteRef> getTestSuiteRefs();

} // TestJob
