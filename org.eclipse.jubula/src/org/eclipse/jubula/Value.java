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
 * A representation of the model object '<em><b>Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.Value#getLanguage <em>Language</em>}</li>
 *   <li>{@link org.eclipse.jubula.Value#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.jubula.Value#getParameter <em>Parameter</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getValue()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Value extends CDOObject {
    /**
     * Returns the value of the '<em><b>Language</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Language</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Language</em>' reference.
     * @see #setLanguage(Language)
     * @see org.eclipse.jubula.JubulaPackage#getValue_Language()
     * @model required="true"
     * @generated
     */
    Language getLanguage();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.Value#getLanguage <em>Language</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Language</em>' reference.
     * @see #getLanguage()
     * @generated
     */
    void setLanguage(Language value);

    /**
     * Returns the value of the '<em><b>Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Text</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Text</em>' attribute.
     * @see #setText(String)
     * @see org.eclipse.jubula.JubulaPackage#getValue_Text()
     * @model required="true"
     * @generated
     */
    String getText();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.Value#getText <em>Text</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Text</em>' attribute.
     * @see #getText()
     * @generated
     */
    void setText(String value);

    /**
     * Returns the value of the '<em><b>Parameter</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameter</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameter</em>' reference.
     * @see #setParameter(Parameter)
     * @see org.eclipse.jubula.JubulaPackage#getValue_Parameter()
     * @model
     * @generated
     */
    Parameter getParameter();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.Value#getParameter <em>Parameter</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parameter</em>' reference.
     * @see #getParameter()
     * @generated
     */
    void setParameter(Parameter value);

} // Value
