/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event Handler</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.EventHandler#getReentryType <em>Reentry Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getEventHandler()
 * @model
 * @generated
 */
public interface EventHandler extends TestCaseRef {
    /**
     * Returns the value of the '<em><b>Reentry Type</b></em>' attribute.
     * The literals are from the enumeration {@link org.eclipse.jubula.ReentryType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Reentry Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Reentry Type</em>' attribute.
     * @see org.eclipse.jubula.ReentryType
     * @see #setReentryType(ReentryType)
     * @see org.eclipse.jubula.JubulaPackage#getEventHandler_ReentryType()
     * @model required="true"
     * @generated
     */
    ReentryType getReentryType();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.EventHandler#getReentryType <em>Reentry Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Reentry Type</em>' attribute.
     * @see org.eclipse.jubula.ReentryType
     * @see #getReentryType()
     * @generated
     */
    void setReentryType(ReentryType value);

} // EventHandler
