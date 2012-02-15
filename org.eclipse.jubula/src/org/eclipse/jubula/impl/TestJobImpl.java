/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.TestJob;
import org.eclipse.jubula.TestSuiteRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Job</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.TestJobImpl#getTestSuiteRefs <em>Test Suite Refs</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestJobImpl extends NamedElementImpl implements TestJob {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TestJobImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.TEST_JOB;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<TestSuiteRef> getTestSuiteRefs() {
        return (EList<TestSuiteRef>)eGet(JubulaPackage.Literals.TEST_JOB__TEST_SUITE_REFS, true);
    }

} //TestJobImpl
