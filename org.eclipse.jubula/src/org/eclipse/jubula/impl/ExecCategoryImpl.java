/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.ExecCategory;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.TestJob;
import org.eclipse.jubula.TestSuite;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Exec Category</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.ExecCategoryImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ExecCategoryImpl#getTestJobs <em>Test Jobs</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ExecCategoryImpl#getTestSuites <em>Test Suites</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExecCategoryImpl extends NamedElementImpl implements ExecCategory {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ExecCategoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.EXEC_CATEGORY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<ExecCategory> getCategories() {
        return (EList<ExecCategory>)eGet(JubulaPackage.Literals.EXEC_CATEGORY__CATEGORIES, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<TestJob> getTestJobs() {
        return (EList<TestJob>)eGet(JubulaPackage.Literals.EXEC_CATEGORY__TEST_JOBS, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<TestSuite> getTestSuites() {
        return (EList<TestSuite>)eGet(JubulaPackage.Literals.EXEC_CATEGORY__TEST_SUITES, true);
    }

} //ExecCategoryImpl
