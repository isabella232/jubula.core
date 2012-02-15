/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.Component;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.TestStep;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Step</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.TestStepImpl#getComponent <em>Component</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestStepImpl extends TestCaseChildImpl implements TestStep {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TestStepImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.TEST_STEP;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Component getComponent() {
        return (Component)eGet(JubulaPackage.Literals.TEST_STEP__COMPONENT, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setComponent(Component newComponent) {
        eSet(JubulaPackage.Literals.TEST_STEP__COMPONENT, newComponent);
    }

} //TestStepImpl
