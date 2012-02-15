/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>CTD Value Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.CTDValueProvider#getCtdSet <em>Ctd Set</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getCTDValueProvider()
 * @model
 * @generated
 */
public interface CTDValueProvider extends ValueProvider {
    /**
     * Returns the value of the '<em><b>Ctd Set</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ctd Set</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ctd Set</em>' reference.
     * @see #setCtdSet(CTDSet)
     * @see org.eclipse.jubula.JubulaPackage#getCTDValueProvider_CtdSet()
     * @model required="true"
     * @generated
     */
    CTDSet getCtdSet();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.CTDValueProvider#getCtdSet <em>Ctd Set</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ctd Set</em>' reference.
     * @see #getCtdSet()
     * @generated
     */
    void setCtdSet(CTDSet value);

} // CTDValueProvider
