/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.CTDCategory;
import org.eclipse.jubula.CTDSet;
import org.eclipse.jubula.JubulaPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CTD Category</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.CTDCategoryImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.CTDCategoryImpl#getCtdSets <em>Ctd Sets</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CTDCategoryImpl extends NamedElementImpl implements CTDCategory {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CTDCategoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.CTD_CATEGORY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<CTDCategory> getCategories() {
        return (EList<CTDCategory>)eGet(JubulaPackage.Literals.CTD_CATEGORY__CATEGORIES, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<CTDSet> getCtdSets() {
        return (EList<CTDSet>)eGet(JubulaPackage.Literals.CTD_CATEGORY__CTD_SETS, true);
    }

} //CTDCategoryImpl
