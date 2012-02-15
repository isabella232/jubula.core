/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.TestSuite;
import org.eclipse.jubula.TestSuiteRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Suite Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.TestSuiteRefImpl#getTestSuite <em>Test Suite</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestSuiteRefImpl extends NamedElementImpl implements TestSuiteRef {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TestSuiteRefImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.TEST_SUITE_REF;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TestSuite getTestSuite() {
        return (TestSuite)eGet(JubulaPackage.Literals.TEST_SUITE_REF__TEST_SUITE, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTestSuite(TestSuite newTestSuite) {
        eSet(JubulaPackage.Literals.TEST_SUITE_REF__TEST_SUITE, newTestSuite);
    }

} //TestSuiteRefImpl
