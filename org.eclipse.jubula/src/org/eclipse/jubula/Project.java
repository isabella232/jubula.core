/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Project</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.Project#getLanguages <em>Languages</em>}</li>
 *   <li>{@link org.eclipse.jubula.Project#getCompCategory <em>Comp Category</em>}</li>
 *   <li>{@link org.eclipse.jubula.Project#getSpecCategory <em>Spec Category</em>}</li>
 *   <li>{@link org.eclipse.jubula.Project#getExecCategory <em>Exec Category</em>}</li>
 *   <li>{@link org.eclipse.jubula.Project#getCtdCategory <em>Ctd Category</em>}</li>
 *   <li>{@link org.eclipse.jubula.Project#getAUTs <em>AU Ts</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getProject()
 * @model
 * @generated
 */
public interface Project extends NamedElement {
    /**
     * Returns the value of the '<em><b>Languages</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.Language}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Languages</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Languages</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getProject_Languages()
     * @model containment="true" required="true"
     * @generated
     */
    EList<Language> getLanguages();

    /**
     * Returns the value of the '<em><b>Comp Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Comp Category</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Comp Category</em>' containment reference.
     * @see #setCompCategory(CompCategory)
     * @see org.eclipse.jubula.JubulaPackage#getProject_CompCategory()
     * @model containment="true" required="true"
     * @generated
     */
    CompCategory getCompCategory();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.Project#getCompCategory <em>Comp Category</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Comp Category</em>' containment reference.
     * @see #getCompCategory()
     * @generated
     */
    void setCompCategory(CompCategory value);

    /**
     * Returns the value of the '<em><b>Spec Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Spec Category</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Spec Category</em>' containment reference.
     * @see #setSpecCategory(SpecCategory)
     * @see org.eclipse.jubula.JubulaPackage#getProject_SpecCategory()
     * @model containment="true" required="true"
     * @generated
     */
    SpecCategory getSpecCategory();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.Project#getSpecCategory <em>Spec Category</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Spec Category</em>' containment reference.
     * @see #getSpecCategory()
     * @generated
     */
    void setSpecCategory(SpecCategory value);

    /**
     * Returns the value of the '<em><b>Exec Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Exec Category</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Exec Category</em>' containment reference.
     * @see #setExecCategory(ExecCategory)
     * @see org.eclipse.jubula.JubulaPackage#getProject_ExecCategory()
     * @model containment="true" required="true"
     * @generated
     */
    ExecCategory getExecCategory();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.Project#getExecCategory <em>Exec Category</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Exec Category</em>' containment reference.
     * @see #getExecCategory()
     * @generated
     */
    void setExecCategory(ExecCategory value);

    /**
     * Returns the value of the '<em><b>Ctd Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ctd Category</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ctd Category</em>' containment reference.
     * @see #setCtdCategory(CTDCategory)
     * @see org.eclipse.jubula.JubulaPackage#getProject_CtdCategory()
     * @model containment="true" required="true"
     * @generated
     */
    CTDCategory getCtdCategory();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.Project#getCtdCategory <em>Ctd Category</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ctd Category</em>' containment reference.
     * @see #getCtdCategory()
     * @generated
     */
    void setCtdCategory(CTDCategory value);

    /**
     * Returns the value of the '<em><b>AU Ts</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.AUT}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>AU Ts</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>AU Ts</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getProject_AUTs()
     * @model containment="true"
     * @generated
     */
    EList<AUT> getAUTs();

} // Project
