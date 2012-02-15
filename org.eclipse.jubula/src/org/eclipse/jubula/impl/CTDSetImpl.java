/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.CTDSet;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.LocalValueProvider;
import org.eclipse.jubula.Parameter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CTD Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.CTDSetImpl#getValueProvider <em>Value Provider</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.CTDSetImpl#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CTDSetImpl extends NamedElementImpl implements CTDSet {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CTDSetImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.CTD_SET;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public LocalValueProvider getValueProvider() {
        return (LocalValueProvider)eGet(JubulaPackage.Literals.CTD_SET__VALUE_PROVIDER, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValueProvider(LocalValueProvider newValueProvider) {
        eSet(JubulaPackage.Literals.CTD_SET__VALUE_PROVIDER, newValueProvider);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<Parameter> getParameters() {
        return (EList<Parameter>)eGet(JubulaPackage.Literals.CTD_SET__PARAMETERS, true);
    }

} //CTDSetImpl
