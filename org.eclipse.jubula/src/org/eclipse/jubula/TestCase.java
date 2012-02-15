/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Case</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jubula.TestCase#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.jubula.TestCase#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.jubula.TestCase#getDefaultValueProvider <em>Default Value Provider</em>}</li>
 *   <li>{@link org.eclipse.jubula.TestCase#getEventHandlerMap <em>Event Handler Map</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jubula.JubulaPackage#getTestCase()
 * @model
 * @generated
 */
public interface TestCase extends NamedElement {
    /**
     * Returns the value of the '<em><b>Children</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.TestCaseChild}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Children</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getTestCase_Children()
     * @model containment="true"
     * @generated
     */
    EList<TestCaseChild> getChildren();

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jubula.Parameter}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference list.
     * @see org.eclipse.jubula.JubulaPackage#getTestCase_Parameters()
     * @model containment="true"
     * @generated
     */
    EList<Parameter> getParameters();

    /**
     * Returns the value of the '<em><b>Default Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Value Provider</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default Value Provider</em>' containment reference.
     * @see #setDefaultValueProvider(ValueProvider)
     * @see org.eclipse.jubula.JubulaPackage#getTestCase_DefaultValueProvider()
     * @model containment="true"
     * @generated
     */
    ValueProvider getDefaultValueProvider();

    /**
     * Sets the value of the '{@link org.eclipse.jubula.TestCase#getDefaultValueProvider <em>Default Value Provider</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default Value Provider</em>' containment reference.
     * @see #getDefaultValueProvider()
     * @generated
     */
    void setDefaultValueProvider(ValueProvider value);

    /**
     * Returns the value of the '<em><b>Event Handler Map</b></em>' map.
     * The key is of type {@link org.eclipse.jubula.EventType},
     * and the value is of type {@link org.eclipse.jubula.EventHandler},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Event Handler Map</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Event Handler Map</em>' map.
     * @see org.eclipse.jubula.JubulaPackage#getTestCase_EventHandlerMap()
     * @model mapType="org.eclipse.jubula.HandlerEntry<org.eclipse.jubula.EventType, org.eclipse.jubula.EventHandler>"
     * @generated
     */
    EMap<EventType, EventHandler> getEventHandlerMap();

} // TestCase
