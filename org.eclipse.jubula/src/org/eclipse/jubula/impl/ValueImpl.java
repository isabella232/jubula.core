/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.Language;
import org.eclipse.jubula.Parameter;
import org.eclipse.jubula.Value;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jubula.impl.ValueImpl#getLanguage <em>Language</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ValueImpl#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.jubula.impl.ValueImpl#getParameter <em>Parameter</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ValueImpl extends CDOObjectImpl implements Value {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ValueImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JubulaPackage.Literals.VALUE;
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
    public Language getLanguage() {
        return (Language)eGet(JubulaPackage.Literals.VALUE__LANGUAGE, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLanguage(Language newLanguage) {
        eSet(JubulaPackage.Literals.VALUE__LANGUAGE, newLanguage);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getText() {
        return (String)eGet(JubulaPackage.Literals.VALUE__TEXT, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setText(String newText) {
        eSet(JubulaPackage.Literals.VALUE__TEXT, newText);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Parameter getParameter() {
        return (Parameter)eGet(JubulaPackage.Literals.VALUE__PARAMETER, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setParameter(Parameter newParameter) {
        eSet(JubulaPackage.Literals.VALUE__PARAMETER, newParameter);
    }

} //ValueImpl
