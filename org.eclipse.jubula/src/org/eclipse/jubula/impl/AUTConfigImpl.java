/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.AUTConfig;
import org.eclipse.jubula.JubulaPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>AUT Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.AUTConfigImpl#getProperties <em>Properties</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AUTConfigImpl extends NamedElementImpl implements AUTConfig {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AUTConfigImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.AUT_CONFIG;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EMap<String, String> getProperties() {
        return (EMap<String, String>)eGet(JubulaPackage.Literals.AUT_CONFIG__PROPERTIES, true);
    }

} //AUTConfigImpl
