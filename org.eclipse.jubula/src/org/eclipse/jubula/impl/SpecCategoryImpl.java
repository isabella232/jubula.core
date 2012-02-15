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
import org.eclipse.jubula.SpecCategory;
import org.eclipse.jubula.TestCase;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Spec Category</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.SpecCategoryImpl#getTestCases <em>Test Cases</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.SpecCategoryImpl#getCategories <em>Categories</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SpecCategoryImpl extends NamedElementImpl implements SpecCategory {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SpecCategoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.SPEC_CATEGORY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<TestCase> getTestCases() {
        return (EList<TestCase>)eGet(JubulaPackage.Literals.SPEC_CATEGORY__TEST_CASES, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<SpecCategory> getCategories() {
        return (EList<SpecCategory>)eGet(JubulaPackage.Literals.SPEC_CATEGORY__CATEGORIES, true);
    }

} //SpecCategoryImpl
