/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>UI Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.UIComponent#getComponents <em>Components</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getUIComponent()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface UIComponent extends CDOObject {
    /**
     * Returns the value of the '<em><b>Components</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.jubula.Component}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Components</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Components</em>' reference list.
     * @see org.eclipse.jubula.JubulaPackage#getUIComponent_Components()
     * @model
     * @generated
     */
    EList<Component> getComponents();

} // UIComponent
