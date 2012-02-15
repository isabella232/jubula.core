/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Excel Value Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.ExcelValueProvider#getFileName <em>File Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getExcelValueProvider()
 * @model
 * @generated
 */
public interface ExcelValueProvider extends ValueProvider {
    /**
     * Returns the value of the '<em><b>File Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>File Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>File Name</em>' attribute.
     * @see #setFileName(String)
     * @see org.eclipse.jubula.JubulaPackage#getExcelValueProvider_FileName()
     * @model
     * @generated
     */
    String getFileName();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.ExcelValueProvider#getFileName <em>File Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>File Name</em>' attribute.
     * @see #getFileName()
     * @generated
     */
    void setFileName(String value);

} // ExcelValueProvider
