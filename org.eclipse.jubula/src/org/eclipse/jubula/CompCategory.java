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
 * A representation of the model object '<em><b>Comp Category</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.CompCategory#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.jubula.CompCategory#getComponents <em>Components</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getCompCategory()
 * @model
 * @generated
 */
public interface CompCategory extends NamedElement {
    /**
     * Returns the value of the '<em><b>Categories</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.CompCategory}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Categories</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Categories</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getCompCategory_Categories()
     * @model containment="true"
     * @generated
     */
    EList<CompCategory> getCategories();

    /**
     * Returns the value of the '<em><b>Components</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.Component}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Components</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Components</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getCompCategory_Components()
     * @model containment="true"
     * @generated
     */
    EList<Component> getComponents();

} // CompCategory
