/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import com.google.gwt.user.client.rpc.GwtTransient;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.jubula.EventHandler;
import org.eclipse.jubula.EventType;
import org.eclipse.jubula.JubulaPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Handler Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.HandlerEntryImpl#getTypedKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.HandlerEntryImpl#getTypedValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HandlerEntryImpl extends CDOObjectImpl implements BasicEMap.Entry<EventType,EventHandler> {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected HandlerEntryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.HANDLER_ENTRY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected int eStaticFeatureCount() {
        return 0;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventType getTypedKey() {
        return (EventType)eGet(JubulaPackage.Literals.HANDLER_ENTRY__KEY, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTypedKey(EventType newKey) {
        eSet(JubulaPackage.Literals.HANDLER_ENTRY__KEY, newKey);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventHandler getTypedValue() {
        return (EventHandler)eGet(JubulaPackage.Literals.HANDLER_ENTRY__VALUE, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTypedValue(EventHandler newValue) {
        eSet(JubulaPackage.Literals.HANDLER_ENTRY__VALUE, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @GwtTransient
    protected int hash = -1;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getHash() {
        if (hash == -1) {
            Object theKey = getKey();
            hash = (theKey == null ? 0 : theKey.hashCode());
        }
        return hash;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setHash(int hash) {
        this.hash = hash;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventType getKey() {
        return getTypedKey();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setKey(EventType key) {
        setTypedKey(key);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventHandler getValue() {
        return getTypedValue();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventHandler setValue(EventHandler value) {
        EventHandler oldValue = getValue();
        setTypedValue(value);
        return oldValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EMap<EventType, EventHandler> getEMap() {
        EObject container = eContainer();
        return container == null ? null : (EMap<EventType, EventHandler>)container.eGet(eContainmentFeature());
    }

} //HandlerEntryImpl
