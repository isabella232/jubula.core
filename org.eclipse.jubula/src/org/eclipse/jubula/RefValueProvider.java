/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ref Value Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.RefValueProvider#getTestCase <em>Test Case</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getRefValueProvider()
 * @model
 * @generated
 */
public interface RefValueProvider extends ValueProvider {
    /**
     * Returns the value of the '<em><b>Test Case</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Test Case</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Test Case</em>' reference.
     * @see org.eclipse.jubula.JubulaPackage#getRefValueProvider_TestCase()
     * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
     * @generated
     */
    TestCase getTestCase();

} // RefValueProvider
