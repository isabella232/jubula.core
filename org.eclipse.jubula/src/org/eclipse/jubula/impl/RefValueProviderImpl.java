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

import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.Language;
import org.eclipse.jubula.Parameter;
import org.eclipse.jubula.RefValueProvider;
import org.eclipse.jubula.TestCase;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ref Value Provider</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.RefValueProviderImpl#getTestCase <em>Test Case</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RefValueProviderImpl extends CDOObjectImpl implements RefValueProvider {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RefValueProviderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.REF_VALUE_PROVIDER;
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
    public TestCase getTestCase() {
        return (TestCase)eGet(JubulaPackage.Literals.REF_VALUE_PROVIDER__TEST_CASE, true);
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

} //RefValueProviderImpl
