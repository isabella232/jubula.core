/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.jubula.JubulaPackage
 * @generated
 */
public interface JubulaFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    JubulaFactory eINSTANCE = org.eclipse.jubula.impl.JubulaFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Project</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Project</em>'.
     * @generated
     */
    Project createProject();

    /**
     * Returns a new object of class '<em>Test Case</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Test Case</em>'.
     * @generated
     */
    TestCase createTestCase();

    /**
     * Returns a new object of class '<em>Spec Category</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Spec Category</em>'.
     * @generated
     */
    SpecCategory createSpecCategory();

    /**
     * Returns a new object of class '<em>Test Case Ref</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Test Case Ref</em>'.
     * @generated
     */
    TestCaseRef createTestCaseRef();

    /**
     * Returns a new object of class '<em>Test Step</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Test Step</em>'.
     * @generated
     */
    TestStep createTestStep();

    /**
     * Returns a new object of class '<em>Test Job</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Test Job</em>'.
     * @generated
     */
    TestJob createTestJob();

    /**
     * Returns a new object of class '<em>Test Suite</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Test Suite</em>'.
     * @generated
     */
    TestSuite createTestSuite();

    /**
     * Returns a new object of class '<em>Test Suite Ref</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Test Suite Ref</em>'.
     * @generated
     */
    TestSuiteRef createTestSuiteRef();

    /**
     * Returns a new object of class '<em>Exec Category</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Exec Category</em>'.
     * @generated
     */
    ExecCategory createExecCategory();

    /**
     * Returns a new object of class '<em>AUT</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>AUT</em>'.
     * @generated
     */
    AUT createAUT();

    /**
     * Returns a new object of class '<em>AUT Config</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>AUT Config</em>'.
     * @generated
     */
    AUTConfig createAUTConfig();

    /**
     * Returns a new object of class '<em>UI Component</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>UI Component</em>'.
     * @generated
     */
    UIComponent createUIComponent();

    /**
     * Returns a new object of class '<em>Comp Category</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Comp Category</em>'.
     * @generated
     */
    CompCategory createCompCategory();

    /**
     * Returns a new object of class '<em>Component</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Component</em>'.
     * @generated
     */
    Component createComponent();

    /**
     * Returns a new object of class '<em>Component Ref</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Component Ref</em>'.
     * @generated
     */
    ComponentRef createComponentRef();

    /**
     * Returns a new object of class '<em>UI Category</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>UI Category</em>'.
     * @generated
     */
    UICategory createUICategory();

    /**
     * Returns a new object of class '<em>Parameter</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Parameter</em>'.
     * @generated
     */
    Parameter createParameter();

    /**
     * Returns a new object of class '<em>Language</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Language</em>'.
     * @generated
     */
    Language createLanguage();

    /**
     * Returns a new object of class '<em>Local Value Provider</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Local Value Provider</em>'.
     * @generated
     */
    LocalValueProvider createLocalValueProvider();

    /**
     * Returns a new object of class '<em>Value</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Value</em>'.
     * @generated
     */
    Value createValue();

    /**
     * Returns a new object of class '<em>Excel Value Provider</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Excel Value Provider</em>'.
     * @generated
     */
    ExcelValueProvider createExcelValueProvider();

    /**
     * Returns a new object of class '<em>Ref Value Provider</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Ref Value Provider</em>'.
     * @generated
     */
    RefValueProvider createRefValueProvider();

    /**
     * Returns a new object of class '<em>CTD Value Provider</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>CTD Value Provider</em>'.
     * @generated
     */
    CTDValueProvider createCTDValueProvider();

    /**
     * Returns a new object of class '<em>CTD Set</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>CTD Set</em>'.
     * @generated
     */
    CTDSet createCTDSet();

    /**
     * Returns a new object of class '<em>CTD Category</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>CTD Category</em>'.
     * @generated
     */
    CTDCategory createCTDCategory();

    /**
     * Returns a new object of class '<em>Event Handler</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Event Handler</em>'.
     * @generated
     */
    EventHandler createEventHandler();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    JubulaPackage getJubulaPackage();

} //JubulaFactory
