/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.ComponentRef;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.TestCase;
import org.eclipse.jubula.TestCaseRef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Case Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.TestCaseRefImpl#getTestCase <em>Test Case</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.TestCaseRefImpl#getComponentRefs <em>Component Refs</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestCaseRefImpl extends TestCaseChildImpl implements TestCaseRef {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TestCaseRefImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.TEST_CASE_REF;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TestCase getTestCase() {
        return (TestCase)eGet(JubulaPackage.Literals.TEST_CASE_REF__TEST_CASE, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTestCase(TestCase newTestCase) {
        eSet(JubulaPackage.Literals.TEST_CASE_REF__TEST_CASE, newTestCase);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<ComponentRef> getComponentRefs() {
        return (EList<ComponentRef>)eGet(JubulaPackage.Literals.TEST_CASE_REF__COMPONENT_REFS, true);
    }

} //TestCaseRefImpl
