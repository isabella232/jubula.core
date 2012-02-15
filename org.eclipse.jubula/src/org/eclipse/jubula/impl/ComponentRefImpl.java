/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.jubula.Component;
import org.eclipse.jubula.ComponentRef;
import org.eclipse.jubula.JubulaPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.ComponentRefImpl#getComponent <em>Component</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ComponentRefImpl#getOverride <em>Override</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentRefImpl extends CDOObjectImpl implements ComponentRef {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ComponentRefImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.COMPONENT_REF;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected int eStaticFeatureCount() {
        return 0;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Component getComponent() {
        return (Component)eGet(JubulaPackage.Literals.COMPONENT_REF__COMPONENT, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setComponent(Component newComponent) {
        eSet(JubulaPackage.Literals.COMPONENT_REF__COMPONENT, newComponent);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Component getOverride() {
        return (Component)eGet(JubulaPackage.Literals.COMPONENT_REF__OVERRIDE, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOverride(Component newOverride) {
        eSet(JubulaPackage.Literals.COMPONENT_REF__OVERRIDE, newOverride);
    }

} //ComponentRefImpl
