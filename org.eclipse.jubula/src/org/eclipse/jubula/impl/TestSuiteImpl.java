/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.AUT;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.TestCaseRef;
import org.eclipse.jubula.TestSuite;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Suite</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.TestSuiteImpl#getTestCaseRefs <em>Test Case Refs</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.TestSuiteImpl#getAUT <em>AUT</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestSuiteImpl extends NamedElementImpl implements TestSuite {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TestSuiteImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.TEST_SUITE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<TestCaseRef> getTestCaseRefs() {
        return (EList<TestCaseRef>)eGet(JubulaPackage.Literals.TEST_SUITE__TEST_CASE_REFS, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AUT getAUT() {
        return (AUT)eGet(JubulaPackage.Literals.TEST_SUITE__AUT, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAUT(AUT newAUT) {
        eSet(JubulaPackage.Literals.TEST_SUITE__AUT, newAUT);
    }

} //TestSuiteImpl
