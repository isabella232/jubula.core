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
import org.eclipse.jubula.UICategory;
import org.eclipse.jubula.UIComponent;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>UI Category</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.UICategoryImpl#getUiComponents <em>Ui Components</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.UICategoryImpl#getCategories <em>Categories</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UICategoryImpl extends NamedElementImpl implements UICategory {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected UICategoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.UI_CATEGORY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<UIComponent> getUiComponents() {
        return (EList<UIComponent>)eGet(JubulaPackage.Literals.UI_CATEGORY__UI_COMPONENTS, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<UICategory> getCategories() {
        return (EList<UICategory>)eGet(JubulaPackage.Literals.UI_CATEGORY__CATEGORIES, true);
    }

} //UICategoryImpl
