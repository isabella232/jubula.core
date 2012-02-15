/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jubula.EventHandler;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.ReentryType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event Handler</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.EventHandlerImpl#getReentryType <em>Reentry Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EventHandlerImpl extends TestCaseRefImpl implements EventHandler {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EventHandlerImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.EVENT_HANDLER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ReentryType getReentryType() {
        return (ReentryType)eGet(JubulaPackage.Literals.EVENT_HANDLER__REENTRY_TYPE, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReentryType(ReentryType newReentryType) {
        eSet(JubulaPackage.Literals.EVENT_HANDLER__REENTRY_TYPE, newReentryType);
    }

} //EventHandlerImpl
