/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.TestCaseChild;
import org.eclipse.jubula.ValueProvider;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Case Child</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.TestCaseChildImpl#getValueProvider <em>Value Provider</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class TestCaseChildImpl extends NamedElementImpl implements TestCaseChild {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TestCaseChildImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.TEST_CASE_CHILD;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ValueProvider getValueProvider() {
        return (ValueProvider)eGet(JubulaPackage.Literals.TEST_CASE_CHILD__VALUE_PROVIDER, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValueProvider(ValueProvider newValueProvider) {
        eSet(JubulaPackage.Literals.TEST_CASE_CHILD__VALUE_PROVIDER, newValueProvider);
    }

} //TestCaseChildImpl
