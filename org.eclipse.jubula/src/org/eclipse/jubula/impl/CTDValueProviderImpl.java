/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.jubula.CTDSet;
import org.eclipse.jubula.CTDValueProvider;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.Language;
import org.eclipse.jubula.Parameter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CTD Value Provider</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.CTDValueProviderImpl#getCtdSet <em>Ctd Set</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CTDValueProviderImpl extends CDOObjectImpl implements CTDValueProvider {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CTDValueProviderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.CTD_VALUE_PROVIDER;
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
    public CTDSet getCtdSet() {
        return (CTDSet)eGet(JubulaPackage.Literals.CTD_VALUE_PROVIDER__CTD_SET, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCtdSet(CTDSet newCtdSet) {
        eSet(JubulaPackage.Literals.CTD_VALUE_PROVIDER__CTD_SET, newCtdSet);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getValues(Parameter parameter, Language language) {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

} //CTDValueProviderImpl
