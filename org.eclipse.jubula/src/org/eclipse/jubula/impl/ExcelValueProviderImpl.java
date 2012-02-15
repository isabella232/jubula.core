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

import org.eclipse.jubula.ExcelValueProvider;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.Language;
import org.eclipse.jubula.Parameter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Excel Value Provider</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.ExcelValueProviderImpl#getFileName <em>File Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExcelValueProviderImpl extends CDOObjectImpl implements ExcelValueProvider {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ExcelValueProviderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.EXCEL_VALUE_PROVIDER;
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
    public String getFileName() {
        return (String)eGet(JubulaPackage.Literals.EXCEL_VALUE_PROVIDER__FILE_NAME, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFileName(String newFileName) {
        eSet(JubulaPackage.Literals.EXCEL_VALUE_PROVIDER__FILE_NAME, newFileName);
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

} //ExcelValueProviderImpl
