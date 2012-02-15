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
import org.eclipse.jubula.CTDCategory;
import org.eclipse.jubula.CompCategory;
import org.eclipse.jubula.ExecCategory;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.Language;
import org.eclipse.jubula.Project;
import org.eclipse.jubula.SpecCategory;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Project</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.ProjectImpl#getLanguages <em>Languages</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ProjectImpl#getCompCategory <em>Comp Category</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ProjectImpl#getSpecCategory <em>Spec Category</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ProjectImpl#getExecCategory <em>Exec Category</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ProjectImpl#getCtdCategory <em>Ctd Category</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ProjectImpl#getAUTs <em>AU Ts</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProjectImpl extends NamedElementImpl implements Project {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProjectImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.PROJECT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<Language> getLanguages() {
        return (EList<Language>)eGet(JubulaPackage.Literals.PROJECT__LANGUAGES, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CompCategory getCompCategory() {
        return (CompCategory)eGet(JubulaPackage.Literals.PROJECT__COMP_CATEGORY, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCompCategory(CompCategory newCompCategory) {
        eSet(JubulaPackage.Literals.PROJECT__COMP_CATEGORY, newCompCategory);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SpecCategory getSpecCategory() {
        return (SpecCategory)eGet(JubulaPackage.Literals.PROJECT__SPEC_CATEGORY, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSpecCategory(SpecCategory newSpecCategory) {
        eSet(JubulaPackage.Literals.PROJECT__SPEC_CATEGORY, newSpecCategory);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ExecCategory getExecCategory() {
        return (ExecCategory)eGet(JubulaPackage.Literals.PROJECT__EXEC_CATEGORY, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setExecCategory(ExecCategory newExecCategory) {
        eSet(JubulaPackage.Literals.PROJECT__EXEC_CATEGORY, newExecCategory);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CTDCategory getCtdCategory() {
        return (CTDCategory)eGet(JubulaPackage.Literals.PROJECT__CTD_CATEGORY, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCtdCategory(CTDCategory newCtdCategory) {
        eSet(JubulaPackage.Literals.PROJECT__CTD_CATEGORY, newCtdCategory);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<AUT> getAUTs() {
        return (EList<AUT>)eGet(JubulaPackage.Literals.PROJECT__AU_TS, true);
    }

} //ProjectImpl
