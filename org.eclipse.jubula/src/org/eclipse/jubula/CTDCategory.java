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
 * A representation of the model object '<em><b>CTD Category</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.CTDCategory#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.eclipse.jubula.CTDCategory#getCtdSets <em>Ctd Sets</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getCTDCategory()
 * @model
 * @generated
 */
public interface CTDCategory extends NamedElement {
    /**
     * Returns the value of the '<em><b>Categories</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.CTDCategory}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Categories</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Categories</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getCTDCategory_Categories()
     * @model containment="true"
     * @generated
     */
    EList<CTDCategory> getCategories();

    /**
     * Returns the value of the '<em><b>Ctd Sets</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.CTDSet}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ctd Sets</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ctd Sets</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getCTDCategory_CtdSets()
     * @model containment="true"
     * @generated
     */
    EList<CTDSet> getCtdSets();

} // CTDCategory
