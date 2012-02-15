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
 * A representation of the model object '<em><b>AUT</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.AUT#getConfigs <em>Configs</em>}</li>
 *   <li>{@link org.eclipse.jubula.AUT#getUiCategory <em>Ui Category</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getAUT()
 * @model
 * @generated
 */
public interface AUT extends NamedElement {
    /**
     * Returns the value of the '<em><b>Configs</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.AUTConfig}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Configs</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Configs</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getAUT_Configs()
     * @model containment="true"
     * @generated
     */
    EList<AUTConfig> getConfigs();

    /**
     * Returns the value of the '<em><b>Ui Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ui Category</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ui Category</em>' containment reference.
     * @see #setUiCategory(UICategory)
     * @see org.eclipse.jubula.JubulaPackage#getAUT_UiCategory()
     * @model containment="true" required="true"
     * @generated
     */
    UICategory getUiCategory();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.AUT#getUiCategory <em>Ui Category</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ui Category</em>' containment reference.
     * @see #getUiCategory()
     * @generated
     */
    void setUiCategory(UICategory value);

} // AUT
