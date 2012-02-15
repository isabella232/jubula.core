/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.EventHandler;
import org.eclipse.jubula.EventType;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.Parameter;
import org.eclipse.jubula.TestCase;
import org.eclipse.jubula.TestCaseChild;
import org.eclipse.jubula.ValueProvider;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Case</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.TestCaseImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.TestCaseImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.TestCaseImpl#getDefaultValueProvider <em>Default Value Provider</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.TestCaseImpl#getEventHandlerMap <em>Event Handler Map</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestCaseImpl extends NamedElementImpl implements TestCase {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TestCaseImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.TEST_CASE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<TestCaseChild> getChildren() {
        return (EList<TestCaseChild>)eGet(JubulaPackage.Literals.TEST_CASE__CHILDREN, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<Parameter> getParameters() {
        return (EList<Parameter>)eGet(JubulaPackage.Literals.TEST_CASE__PARAMETERS, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ValueProvider getDefaultValueProvider() {
        return (ValueProvider)eGet(JubulaPackage.Literals.TEST_CASE__DEFAULT_VALUE_PROVIDER, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDefaultValueProvider(ValueProvider newDefaultValueProvider) {
        eSet(JubulaPackage.Literals.TEST_CASE__DEFAULT_VALUE_PROVIDER, newDefaultValueProvider);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EMap<EventType, EventHandler> getEventHandlerMap() {
        return (EMap<EventType, EventHandler>)eGet(JubulaPackage.Literals.TEST_CASE__EVENT_HANDLER_MAP, true);
    }

} //TestCaseImpl
