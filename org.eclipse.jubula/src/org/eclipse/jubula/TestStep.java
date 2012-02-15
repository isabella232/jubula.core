/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Step</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.TestStep#getComponent <em>Component</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getTestStep()
 * @model
 * @generated
 */
public interface TestStep extends TestCaseChild {
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
     * @see org.eclipse.jubula.JubulaPackage#getTestStep_Component()
     * @model required="true"
     * @generated
     */
    Component getComponent();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.TestStep#getComponent <em>Component</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Component</em>' reference.
     * @see #getComponent()
     * @generated
     */
    void setComponent(Component value);

} // TestStep
