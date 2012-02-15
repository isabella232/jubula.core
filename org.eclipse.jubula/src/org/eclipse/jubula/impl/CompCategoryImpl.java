/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.CompCategory;
import org.eclipse.jubula.Component;
import org.eclipse.jubula.JubulaPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Comp Category</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.CompCategoryImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.CompCategoryImpl#getComponents <em>Components</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CompCategoryImpl extends NamedElementImpl implements CompCategory {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CompCategoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.COMP_CATEGORY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<CompCategory> getCategories() {
        return (EList<CompCategory>)eGet(JubulaPackage.Literals.COMP_CATEGORY__CATEGORIES, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<Component> getComponents() {
        return (EList<Component>)eGet(JubulaPackage.Literals.COMP_CATEGORY__COMPONENTS, true);
    }

} //CompCategoryImpl
