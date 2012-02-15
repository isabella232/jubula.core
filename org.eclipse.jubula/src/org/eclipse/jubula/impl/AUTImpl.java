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
import org.eclipse.jubula.AUTConfig;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.UICategory;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>AUT</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.AUTImpl#getConfigs <em>Configs</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.AUTImpl#getUiCategory <em>Ui Category</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AUTImpl extends NamedElementImpl implements AUT {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AUTImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.AUT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<AUTConfig> getConfigs() {
        return (EList<AUTConfig>)eGet(JubulaPackage.Literals.AUT__CONFIGS, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UICategory getUiCategory() {
        return (UICategory)eGet(JubulaPackage.Literals.AUT__UI_CATEGORY, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUiCategory(UICategory newUiCategory) {
        eSet(JubulaPackage.Literals.AUT__UI_CATEGORY, newUiCategory);
    }

} //AUTImpl
