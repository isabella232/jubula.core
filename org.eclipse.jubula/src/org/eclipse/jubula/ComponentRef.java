/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Component Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.ComponentRef#getComponent <em>Component</em>}</li>
 *   <li>{@link org.eclipse.jubula.ComponentRef#getOverride <em>Override</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getComponentRef()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface ComponentRef extends CDOObject {
    /**
     * Returns the value of the '<em><b>Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Component</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Component</em>' reference.
     * @see #setComponent(Component)
     * @see org.eclipse.jubula.JubulaPackage#getComponentRef_Component()
     * @model required="true"
     * @generated
     */
    Component getComponent();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.ComponentRef#getComponent <em>Component</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Component</em>' reference.
     * @see #getComponent()
     * @generated
     */
    void setComponent(Component value);

    /**
     * Returns the value of the '<em><b>Override</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Override</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Override</em>' reference.
     * @see #setOverride(Component)
     * @see org.eclipse.jubula.JubulaPackage#getComponentRef_Override()
     * @model
     * @generated
     */
    Component getOverride();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.ComponentRef#getOverride <em>Override</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Override</em>' reference.
     * @see #getOverride()
     * @generated
     */
    void setOverride(Component value);

} // ComponentRef
