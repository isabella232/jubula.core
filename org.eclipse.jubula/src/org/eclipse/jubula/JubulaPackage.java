/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.jubula.JubulaFactory
 * @model kind="package"
 * @generated
 */
public interface JubulaPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "jubula";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.eclipse.org/jubula/2.0";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "jb";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    JubulaPackage eINSTANCE = org.eclipse.jubula.impl.JubulaPackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.NamedElementImpl <em>Named Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.NamedElementImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getNamedElement()
     * @generated
     */
    int NAMED_ELEMENT = 29;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_ELEMENT__NAME = 0;

    /**
     * The number of structural features of the '<em>Named Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_ELEMENT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.ProjectImpl <em>Project</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.ProjectImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getProject()
     * @generated
     */
    int PROJECT = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Languages</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT__LANGUAGES = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Comp Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT__COMP_CATEGORY = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Spec Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT__SPEC_CATEGORY = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Exec Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT__EXEC_CATEGORY = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Ctd Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT__CTD_CATEGORY = NAMED_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>AU Ts</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT__AU_TS = NAMED_ELEMENT_FEATURE_COUNT + 5;

    /**
     * The number of structural features of the '<em>Project</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECT_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 6;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.TestCaseImpl <em>Test Case</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.TestCaseImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestCase()
     * @generated
     */
    int TEST_CASE = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE__CHILDREN = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE__PARAMETERS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Default Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE__DEFAULT_VALUE_PROVIDER = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Event Handler Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE__EVENT_HANDLER_MAP = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>Test Case</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.SpecCategoryImpl <em>Spec Category</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.SpecCategoryImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getSpecCategory()
     * @generated
     */
    int SPEC_CATEGORY = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPEC_CATEGORY__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Test Cases</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPEC_CATEGORY__TEST_CASES = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Categories</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPEC_CATEGORY__CATEGORIES = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Spec Category</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPEC_CATEGORY_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.TestCaseChildImpl <em>Test Case Child</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.TestCaseChildImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestCaseChild()
     * @generated
     */
    int TEST_CASE_CHILD = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_CHILD__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_CHILD__VALUE_PROVIDER = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Test Case Child</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_CHILD_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.TestCaseRefImpl <em>Test Case Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.TestCaseRefImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestCaseRef()
     * @generated
     */
    int TEST_CASE_REF = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_REF__NAME = TEST_CASE_CHILD__NAME;

    /**
     * The feature id for the '<em><b>Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_REF__VALUE_PROVIDER = TEST_CASE_CHILD__VALUE_PROVIDER;

    /**
     * The feature id for the '<em><b>Test Case</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_REF__TEST_CASE = TEST_CASE_CHILD_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Component Refs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_REF__COMPONENT_REFS = TEST_CASE_CHILD_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Test Case Ref</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_CASE_REF_FEATURE_COUNT = TEST_CASE_CHILD_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.TestStepImpl <em>Test Step</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.TestStepImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestStep()
     * @generated
     */
    int TEST_STEP = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_STEP__NAME = TEST_CASE_CHILD__NAME;

    /**
     * The feature id for the '<em><b>Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_STEP__VALUE_PROVIDER = TEST_CASE_CHILD__VALUE_PROVIDER;

    /**
     * The feature id for the '<em><b>Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_STEP__COMPONENT = TEST_CASE_CHILD_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Test Step</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_STEP_FEATURE_COUNT = TEST_CASE_CHILD_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.TestJobImpl <em>Test Job</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.TestJobImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestJob()
     * @generated
     */
    int TEST_JOB = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_JOB__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Test Suite Refs</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_JOB__TEST_SUITE_REFS = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Test Job</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_JOB_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.TestSuiteImpl <em>Test Suite</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.TestSuiteImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestSuite()
     * @generated
     */
    int TEST_SUITE = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_SUITE__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Test Case Refs</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_SUITE__TEST_CASE_REFS = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>AUT</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_SUITE__AUT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Test Suite</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_SUITE_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.TestSuiteRefImpl <em>Test Suite Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.TestSuiteRefImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestSuiteRef()
     * @generated
     */
    int TEST_SUITE_REF = 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_SUITE_REF__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Test Suite</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_SUITE_REF__TEST_SUITE = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Test Suite Ref</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TEST_SUITE_REF_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.ExecCategoryImpl <em>Exec Category</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.ExecCategoryImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getExecCategory()
     * @generated
     */
    int EXEC_CATEGORY = 9;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXEC_CATEGORY__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Categories</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXEC_CATEGORY__CATEGORIES = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Test Jobs</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXEC_CATEGORY__TEST_JOBS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Test Suites</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXEC_CATEGORY__TEST_SUITES = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Exec Category</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXEC_CATEGORY_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.AUTImpl <em>AUT</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.AUTImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getAUT()
     * @generated
     */
    int AUT = 10;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUT__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Configs</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUT__CONFIGS = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Ui Category</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUT__UI_CATEGORY = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>AUT</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUT_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.AUTConfigImpl <em>AUT Config</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.AUTConfigImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getAUTConfig()
     * @generated
     */
    int AUT_CONFIG = 11;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUT_CONFIG__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUT_CONFIG__PROPERTIES = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>AUT Config</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUT_CONFIG_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.UIComponentImpl <em>UI Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.UIComponentImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getUIComponent()
     * @generated
     */
    int UI_COMPONENT = 12;

    /**
     * The feature id for the '<em><b>Components</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UI_COMPONENT__COMPONENTS = 0;

    /**
     * The number of structural features of the '<em>UI Component</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UI_COMPONENT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.CompCategoryImpl <em>Comp Category</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.CompCategoryImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getCompCategory()
     * @generated
     */
    int COMP_CATEGORY = 13;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMP_CATEGORY__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Categories</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMP_CATEGORY__CATEGORIES = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Components</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMP_CATEGORY__COMPONENTS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Comp Category</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMP_CATEGORY_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.ComponentImpl <em>Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.ComponentImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getComponent()
     * @generated
     */
    int COMPONENT = 14;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT__NAME = NAMED_ELEMENT__NAME;

    /**
     * The number of structural features of the '<em>Component</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.ComponentRefImpl <em>Component Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.ComponentRefImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getComponentRef()
     * @generated
     */
    int COMPONENT_REF = 15;

    /**
     * The feature id for the '<em><b>Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_REF__COMPONENT = 0;

    /**
     * The feature id for the '<em><b>Override</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_REF__OVERRIDE = 1;

    /**
     * The number of structural features of the '<em>Component Ref</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPONENT_REF_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.UICategoryImpl <em>UI Category</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.UICategoryImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getUICategory()
     * @generated
     */
    int UI_CATEGORY = 16;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UI_CATEGORY__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Ui Components</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UI_CATEGORY__UI_COMPONENTS = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Categories</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UI_CATEGORY__CATEGORIES = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>UI Category</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UI_CATEGORY_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.ParameterImpl <em>Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.ParameterImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getParameter()
     * @generated
     */
    int PARAMETER = 17;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARAMETER__NAME = NAMED_ELEMENT__NAME;

    /**
     * The number of structural features of the '<em>Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARAMETER_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.LanguageImpl <em>Language</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.LanguageImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getLanguage()
     * @generated
     */
    int LANGUAGE = 18;

    /**
     * The number of structural features of the '<em>Language</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LANGUAGE_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.ValueProvider <em>Value Provider</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.ValueProvider
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getValueProvider()
     * @generated
     */
    int VALUE_PROVIDER = 19;

    /**
     * The number of structural features of the '<em>Value Provider</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_PROVIDER_FEATURE_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.LocalValueProviderImpl <em>Local Value Provider</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.LocalValueProviderImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getLocalValueProvider()
     * @generated
     */
    int LOCAL_VALUE_PROVIDER = 20;

    /**
     * The feature id for the '<em><b>Values</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOCAL_VALUE_PROVIDER__VALUES = VALUE_PROVIDER_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Local Value Provider</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOCAL_VALUE_PROVIDER_FEATURE_COUNT = VALUE_PROVIDER_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.ValueImpl <em>Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.ValueImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getValue()
     * @generated
     */
    int VALUE = 21;

    /**
     * The feature id for the '<em><b>Language</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE__LANGUAGE = 0;

    /**
     * The feature id for the '<em><b>Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE__TEXT = 1;

    /**
     * The feature id for the '<em><b>Parameter</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE__PARAMETER = 2;

    /**
     * The number of structural features of the '<em>Value</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.ExcelValueProviderImpl <em>Excel Value Provider</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.ExcelValueProviderImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getExcelValueProvider()
     * @generated
     */
    int EXCEL_VALUE_PROVIDER = 22;

    /**
     * The feature id for the '<em><b>File Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXCEL_VALUE_PROVIDER__FILE_NAME = VALUE_PROVIDER_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Excel Value Provider</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXCEL_VALUE_PROVIDER_FEATURE_COUNT = VALUE_PROVIDER_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.RefValueProviderImpl <em>Ref Value Provider</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.RefValueProviderImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getRefValueProvider()
     * @generated
     */
    int REF_VALUE_PROVIDER = 23;

    /**
     * The feature id for the '<em><b>Test Case</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REF_VALUE_PROVIDER__TEST_CASE = VALUE_PROVIDER_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Ref Value Provider</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REF_VALUE_PROVIDER_FEATURE_COUNT = VALUE_PROVIDER_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.CTDValueProviderImpl <em>CTD Value Provider</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.CTDValueProviderImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getCTDValueProvider()
     * @generated
     */
    int CTD_VALUE_PROVIDER = 24;

    /**
     * The feature id for the '<em><b>Ctd Set</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_VALUE_PROVIDER__CTD_SET = VALUE_PROVIDER_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>CTD Value Provider</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_VALUE_PROVIDER_FEATURE_COUNT = VALUE_PROVIDER_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.CTDSetImpl <em>CTD Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.CTDSetImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getCTDSet()
     * @generated
     */
    int CTD_SET = 25;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_SET__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_SET__VALUE_PROVIDER = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_SET__PARAMETERS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>CTD Set</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_SET_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.CTDCategoryImpl <em>CTD Category</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.CTDCategoryImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getCTDCategory()
     * @generated
     */
    int CTD_CATEGORY = 26;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_CATEGORY__NAME = NAMED_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Categories</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_CATEGORY__CATEGORIES = NAMED_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Ctd Sets</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_CATEGORY__CTD_SETS = NAMED_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>CTD Category</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CTD_CATEGORY_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.EventHandlerImpl <em>Event Handler</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.EventHandlerImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getEventHandler()
     * @generated
     */
    int EVENT_HANDLER = 27;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_HANDLER__NAME = TEST_CASE_REF__NAME;

    /**
     * The feature id for the '<em><b>Value Provider</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_HANDLER__VALUE_PROVIDER = TEST_CASE_REF__VALUE_PROVIDER;

    /**
     * The feature id for the '<em><b>Test Case</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_HANDLER__TEST_CASE = TEST_CASE_REF__TEST_CASE;

    /**
     * The feature id for the '<em><b>Component Refs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_HANDLER__COMPONENT_REFS = TEST_CASE_REF__COMPONENT_REFS;

    /**
     * The feature id for the '<em><b>Reentry Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_HANDLER__REENTRY_TYPE = TEST_CASE_REF_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Event Handler</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_HANDLER_FEATURE_COUNT = TEST_CASE_REF_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.impl.HandlerEntryImpl <em>Handler Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.impl.HandlerEntryImpl
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getHandlerEntry()
     * @generated
     */
    int HANDLER_ENTRY = 28;

    /**
     * The feature id for the '<em><b>Key</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HANDLER_ENTRY__KEY = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HANDLER_ENTRY__VALUE = 1;

    /**
     * The number of structural features of the '<em>Handler Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HANDLER_ENTRY_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.EventType <em>Event Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.EventType
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getEventType()
     * @generated
     */
    int EVENT_TYPE = 30;

    /**
     * The meta object id for the '{@link org.eclipse.jubula.ReentryType <em>Reentry Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.jubula.ReentryType
     * @see org.eclipse.jubula.impl.JubulaPackageImpl#getReentryType()
     * @generated
     */
    int REENTRY_TYPE = 31;


    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.Project <em>Project</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Project</em>'.
     * @see org.eclipse.jubula.Project
     * @generated
     */
    EClass getProject();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.Project#getLanguages <em>Languages</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Languages</em>'.
     * @see org.eclipse.jubula.Project#getLanguages()
     * @see #getProject()
     * @generated
     */
    EReference getProject_Languages();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.jubula.Project#getCompCategory <em>Comp Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Comp Category</em>'.
     * @see org.eclipse.jubula.Project#getCompCategory()
     * @see #getProject()
     * @generated
     */
    EReference getProject_CompCategory();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.jubula.Project#getSpecCategory <em>Spec Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Spec Category</em>'.
     * @see org.eclipse.jubula.Project#getSpecCategory()
     * @see #getProject()
     * @generated
     */
    EReference getProject_SpecCategory();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.jubula.Project#getExecCategory <em>Exec Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Exec Category</em>'.
     * @see org.eclipse.jubula.Project#getExecCategory()
     * @see #getProject()
     * @generated
     */
    EReference getProject_ExecCategory();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.jubula.Project#getCtdCategory <em>Ctd Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Ctd Category</em>'.
     * @see org.eclipse.jubula.Project#getCtdCategory()
     * @see #getProject()
     * @generated
     */
    EReference getProject_CtdCategory();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.Project#getAUTs <em>AU Ts</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>AU Ts</em>'.
     * @see org.eclipse.jubula.Project#getAUTs()
     * @see #getProject()
     * @generated
     */
    EReference getProject_AUTs();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.TestCase <em>Test Case</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Test Case</em>'.
     * @see org.eclipse.jubula.TestCase
     * @generated
     */
    EClass getTestCase();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.TestCase#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Children</em>'.
     * @see org.eclipse.jubula.TestCase#getChildren()
     * @see #getTestCase()
     * @generated
     */
    EReference getTestCase_Children();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.TestCase#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Parameters</em>'.
     * @see org.eclipse.jubula.TestCase#getParameters()
     * @see #getTestCase()
     * @generated
     */
    EReference getTestCase_Parameters();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.jubula.TestCase#getDefaultValueProvider <em>Default Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Default Value Provider</em>'.
     * @see org.eclipse.jubula.TestCase#getDefaultValueProvider()
     * @see #getTestCase()
     * @generated
     */
    EReference getTestCase_DefaultValueProvider();

    /**
     * Returns the meta object for the map '{@link org.eclipse.jubula.TestCase#getEventHandlerMap <em>Event Handler Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Event Handler Map</em>'.
     * @see org.eclipse.jubula.TestCase#getEventHandlerMap()
     * @see #getTestCase()
     * @generated
     */
    EReference getTestCase_EventHandlerMap();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.SpecCategory <em>Spec Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Spec Category</em>'.
     * @see org.eclipse.jubula.SpecCategory
     * @generated
     */
    EClass getSpecCategory();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.SpecCategory#getTestCases <em>Test Cases</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Test Cases</em>'.
     * @see org.eclipse.jubula.SpecCategory#getTestCases()
     * @see #getSpecCategory()
     * @generated
     */
    EReference getSpecCategory_TestCases();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.SpecCategory#getCategories <em>Categories</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Categories</em>'.
     * @see org.eclipse.jubula.SpecCategory#getCategories()
     * @see #getSpecCategory()
     * @generated
     */
    EReference getSpecCategory_Categories();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.TestCaseRef <em>Test Case Ref</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Test Case Ref</em>'.
     * @see org.eclipse.jubula.TestCaseRef
     * @generated
     */
    EClass getTestCaseRef();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.TestCaseRef#getTestCase <em>Test Case</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Test Case</em>'.
     * @see org.eclipse.jubula.TestCaseRef#getTestCase()
     * @see #getTestCaseRef()
     * @generated
     */
    EReference getTestCaseRef_TestCase();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.jubula.TestCaseRef#getComponentRefs <em>Component Refs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Component Refs</em>'.
     * @see org.eclipse.jubula.TestCaseRef#getComponentRefs()
     * @see #getTestCaseRef()
     * @generated
     */
    EReference getTestCaseRef_ComponentRefs();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.TestStep <em>Test Step</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Test Step</em>'.
     * @see org.eclipse.jubula.TestStep
     * @generated
     */
    EClass getTestStep();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.TestStep#getComponent <em>Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Component</em>'.
     * @see org.eclipse.jubula.TestStep#getComponent()
     * @see #getTestStep()
     * @generated
     */
    EReference getTestStep_Component();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.TestCaseChild <em>Test Case Child</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Test Case Child</em>'.
     * @see org.eclipse.jubula.TestCaseChild
     * @generated
     */
    EClass getTestCaseChild();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.jubula.TestCaseChild#getValueProvider <em>Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Value Provider</em>'.
     * @see org.eclipse.jubula.TestCaseChild#getValueProvider()
     * @see #getTestCaseChild()
     * @generated
     */
    EReference getTestCaseChild_ValueProvider();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.TestJob <em>Test Job</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Test Job</em>'.
     * @see org.eclipse.jubula.TestJob
     * @generated
     */
    EClass getTestJob();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.TestJob#getTestSuiteRefs <em>Test Suite Refs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Test Suite Refs</em>'.
     * @see org.eclipse.jubula.TestJob#getTestSuiteRefs()
     * @see #getTestJob()
     * @generated
     */
    EReference getTestJob_TestSuiteRefs();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.TestSuite <em>Test Suite</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Test Suite</em>'.
     * @see org.eclipse.jubula.TestSuite
     * @generated
     */
    EClass getTestSuite();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.TestSuite#getTestCaseRefs <em>Test Case Refs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Test Case Refs</em>'.
     * @see org.eclipse.jubula.TestSuite#getTestCaseRefs()
     * @see #getTestSuite()
     * @generated
     */
    EReference getTestSuite_TestCaseRefs();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.TestSuite#getAUT <em>AUT</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>AUT</em>'.
     * @see org.eclipse.jubula.TestSuite#getAUT()
     * @see #getTestSuite()
     * @generated
     */
    EReference getTestSuite_AUT();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.TestSuiteRef <em>Test Suite Ref</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Test Suite Ref</em>'.
     * @see org.eclipse.jubula.TestSuiteRef
     * @generated
     */
    EClass getTestSuiteRef();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.TestSuiteRef#getTestSuite <em>Test Suite</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Test Suite</em>'.
     * @see org.eclipse.jubula.TestSuiteRef#getTestSuite()
     * @see #getTestSuiteRef()
     * @generated
     */
    EReference getTestSuiteRef_TestSuite();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.ExecCategory <em>Exec Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Exec Category</em>'.
     * @see org.eclipse.jubula.ExecCategory
     * @generated
     */
    EClass getExecCategory();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.ExecCategory#getCategories <em>Categories</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Categories</em>'.
     * @see org.eclipse.jubula.ExecCategory#getCategories()
     * @see #getExecCategory()
     * @generated
     */
    EReference getExecCategory_Categories();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.ExecCategory#getTestJobs <em>Test Jobs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Test Jobs</em>'.
     * @see org.eclipse.jubula.ExecCategory#getTestJobs()
     * @see #getExecCategory()
     * @generated
     */
    EReference getExecCategory_TestJobs();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.ExecCategory#getTestSuites <em>Test Suites</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Test Suites</em>'.
     * @see org.eclipse.jubula.ExecCategory#getTestSuites()
     * @see #getExecCategory()
     * @generated
     */
    EReference getExecCategory_TestSuites();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.AUT <em>AUT</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>AUT</em>'.
     * @see org.eclipse.jubula.AUT
     * @generated
     */
    EClass getAUT();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.AUT#getConfigs <em>Configs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Configs</em>'.
     * @see org.eclipse.jubula.AUT#getConfigs()
     * @see #getAUT()
     * @generated
     */
    EReference getAUT_Configs();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.jubula.AUT#getUiCategory <em>Ui Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Ui Category</em>'.
     * @see org.eclipse.jubula.AUT#getUiCategory()
     * @see #getAUT()
     * @generated
     */
    EReference getAUT_UiCategory();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.AUTConfig <em>AUT Config</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>AUT Config</em>'.
     * @see org.eclipse.jubula.AUTConfig
     * @generated
     */
    EClass getAUTConfig();

    /**
     * Returns the meta object for the map '{@link org.eclipse.jubula.AUTConfig#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Properties</em>'.
     * @see org.eclipse.jubula.AUTConfig#getProperties()
     * @see #getAUTConfig()
     * @generated
     */
    EReference getAUTConfig_Properties();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.UIComponent <em>UI Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>UI Component</em>'.
     * @see org.eclipse.jubula.UIComponent
     * @generated
     */
    EClass getUIComponent();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.jubula.UIComponent#getComponents <em>Components</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Components</em>'.
     * @see org.eclipse.jubula.UIComponent#getComponents()
     * @see #getUIComponent()
     * @generated
     */
    EReference getUIComponent_Components();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.CompCategory <em>Comp Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Comp Category</em>'.
     * @see org.eclipse.jubula.CompCategory
     * @generated
     */
    EClass getCompCategory();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.CompCategory#getCategories <em>Categories</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Categories</em>'.
     * @see org.eclipse.jubula.CompCategory#getCategories()
     * @see #getCompCategory()
     * @generated
     */
    EReference getCompCategory_Categories();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.CompCategory#getComponents <em>Components</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Components</em>'.
     * @see org.eclipse.jubula.CompCategory#getComponents()
     * @see #getCompCategory()
     * @generated
     */
    EReference getCompCategory_Components();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.Component <em>Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Component</em>'.
     * @see org.eclipse.jubula.Component
     * @generated
     */
    EClass getComponent();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.ComponentRef <em>Component Ref</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Component Ref</em>'.
     * @see org.eclipse.jubula.ComponentRef
     * @generated
     */
    EClass getComponentRef();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.ComponentRef#getComponent <em>Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Component</em>'.
     * @see org.eclipse.jubula.ComponentRef#getComponent()
     * @see #getComponentRef()
     * @generated
     */
    EReference getComponentRef_Component();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.ComponentRef#getOverride <em>Override</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Override</em>'.
     * @see org.eclipse.jubula.ComponentRef#getOverride()
     * @see #getComponentRef()
     * @generated
     */
    EReference getComponentRef_Override();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.UICategory <em>UI Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>UI Category</em>'.
     * @see org.eclipse.jubula.UICategory
     * @generated
     */
    EClass getUICategory();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.UICategory#getUiComponents <em>Ui Components</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Ui Components</em>'.
     * @see org.eclipse.jubula.UICategory#getUiComponents()
     * @see #getUICategory()
     * @generated
     */
    EReference getUICategory_UiComponents();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.UICategory#getCategories <em>Categories</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Categories</em>'.
     * @see org.eclipse.jubula.UICategory#getCategories()
     * @see #getUICategory()
     * @generated
     */
    EReference getUICategory_Categories();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.Parameter <em>Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Parameter</em>'.
     * @see org.eclipse.jubula.Parameter
     * @generated
     */
    EClass getParameter();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.Language <em>Language</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Language</em>'.
     * @see org.eclipse.jubula.Language
     * @generated
     */
    EClass getLanguage();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.ValueProvider <em>Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Value Provider</em>'.
     * @see org.eclipse.jubula.ValueProvider
     * @generated
     */
    EClass getValueProvider();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.LocalValueProvider <em>Local Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Local Value Provider</em>'.
     * @see org.eclipse.jubula.LocalValueProvider
     * @generated
     */
    EClass getLocalValueProvider();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.LocalValueProvider#getValues <em>Values</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Values</em>'.
     * @see org.eclipse.jubula.LocalValueProvider#getValues()
     * @see #getLocalValueProvider()
     * @generated
     */
    EReference getLocalValueProvider_Values();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.Value <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Value</em>'.
     * @see org.eclipse.jubula.Value
     * @generated
     */
    EClass getValue();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.Value#getLanguage <em>Language</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Language</em>'.
     * @see org.eclipse.jubula.Value#getLanguage()
     * @see #getValue()
     * @generated
     */
    EReference getValue_Language();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.jubula.Value#getText <em>Text</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Text</em>'.
     * @see org.eclipse.jubula.Value#getText()
     * @see #getValue()
     * @generated
     */
    EAttribute getValue_Text();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.Value#getParameter <em>Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Parameter</em>'.
     * @see org.eclipse.jubula.Value#getParameter()
     * @see #getValue()
     * @generated
     */
    EReference getValue_Parameter();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.ExcelValueProvider <em>Excel Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Excel Value Provider</em>'.
     * @see org.eclipse.jubula.ExcelValueProvider
     * @generated
     */
    EClass getExcelValueProvider();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.jubula.ExcelValueProvider#getFileName <em>File Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>File Name</em>'.
     * @see org.eclipse.jubula.ExcelValueProvider#getFileName()
     * @see #getExcelValueProvider()
     * @generated
     */
    EAttribute getExcelValueProvider_FileName();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.RefValueProvider <em>Ref Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Ref Value Provider</em>'.
     * @see org.eclipse.jubula.RefValueProvider
     * @generated
     */
    EClass getRefValueProvider();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.RefValueProvider#getTestCase <em>Test Case</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Test Case</em>'.
     * @see org.eclipse.jubula.RefValueProvider#getTestCase()
     * @see #getRefValueProvider()
     * @generated
     */
    EReference getRefValueProvider_TestCase();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.CTDValueProvider <em>CTD Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>CTD Value Provider</em>'.
     * @see org.eclipse.jubula.CTDValueProvider
     * @generated
     */
    EClass getCTDValueProvider();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.jubula.CTDValueProvider#getCtdSet <em>Ctd Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Ctd Set</em>'.
     * @see org.eclipse.jubula.CTDValueProvider#getCtdSet()
     * @see #getCTDValueProvider()
     * @generated
     */
    EReference getCTDValueProvider_CtdSet();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.CTDSet <em>CTD Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>CTD Set</em>'.
     * @see org.eclipse.jubula.CTDSet
     * @generated
     */
    EClass getCTDSet();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.jubula.CTDSet#getValueProvider <em>Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Value Provider</em>'.
     * @see org.eclipse.jubula.CTDSet#getValueProvider()
     * @see #getCTDSet()
     * @generated
     */
    EReference getCTDSet_ValueProvider();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.CTDSet#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Parameters</em>'.
     * @see org.eclipse.jubula.CTDSet#getParameters()
     * @see #getCTDSet()
     * @generated
     */
    EReference getCTDSet_Parameters();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.CTDCategory <em>CTD Category</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>CTD Category</em>'.
     * @see org.eclipse.jubula.CTDCategory
     * @generated
     */
    EClass getCTDCategory();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.CTDCategory#getCategories <em>Categories</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Categories</em>'.
     * @see org.eclipse.jubula.CTDCategory#getCategories()
     * @see #getCTDCategory()
     * @generated
     */
    EReference getCTDCategory_Categories();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.jubula.CTDCategory#getCtdSets <em>Ctd Sets</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Ctd Sets</em>'.
     * @see org.eclipse.jubula.CTDCategory#getCtdSets()
     * @see #getCTDCategory()
     * @generated
     */
    EReference getCTDCategory_CtdSets();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.EventHandler <em>Event Handler</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Event Handler</em>'.
     * @see org.eclipse.jubula.EventHandler
     * @generated
     */
    EClass getEventHandler();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.jubula.EventHandler#getReentryType <em>Reentry Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Reentry Type</em>'.
     * @see org.eclipse.jubula.EventHandler#getReentryType()
     * @see #getEventHandler()
     * @generated
     */
    EAttribute getEventHandler_ReentryType();

    /**
     * Returns the meta object for class '{@link java.util.Map.Entry <em>Handler Entry</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Handler Entry</em>'.
     * @see java.util.Map.Entry
     * @model keyDataType="org.eclipse.jubula.EventType" keyRequired="true"
     *        valueType="org.eclipse.jubula.EventHandler" valueRequired="true"
     * @generated
     */
    EClass getHandlerEntry();

    /**
     * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Key</em>'.
     * @see java.util.Map.Entry
     * @see #getHandlerEntry()
     * @generated
     */
    EAttribute getHandlerEntry_Key();

    /**
     * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Value</em>'.
     * @see java.util.Map.Entry
     * @see #getHandlerEntry()
     * @generated
     */
    EReference getHandlerEntry_Value();

    /**
     * Returns the meta object for class '{@link org.eclipse.jubula.NamedElement <em>Named Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Named Element</em>'.
     * @see org.eclipse.jubula.NamedElement
     * @generated
     */
    EClass getNamedElement();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.jubula.NamedElement#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.eclipse.jubula.NamedElement#getName()
     * @see #getNamedElement()
     * @generated
     */
    EAttribute getNamedElement_Name();

    /**
     * Returns the meta object for enum '{@link org.eclipse.jubula.EventType <em>Event Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Event Type</em>'.
     * @see org.eclipse.jubula.EventType
     * @generated
     */
    EEnum getEventType();

    /**
     * Returns the meta object for enum '{@link org.eclipse.jubula.ReentryType <em>Reentry Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Reentry Type</em>'.
     * @see org.eclipse.jubula.ReentryType
     * @generated
     */
    EEnum getReentryType();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    JubulaFactory getJubulaFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.ProjectImpl <em>Project</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.ProjectImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getProject()
         * @generated
         */
        EClass PROJECT = eINSTANCE.getProject();

        /**
         * The meta object literal for the '<em><b>Languages</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT__LANGUAGES = eINSTANCE.getProject_Languages();

        /**
         * The meta object literal for the '<em><b>Comp Category</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT__COMP_CATEGORY = eINSTANCE.getProject_CompCategory();

        /**
         * The meta object literal for the '<em><b>Spec Category</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT__SPEC_CATEGORY = eINSTANCE.getProject_SpecCategory();

        /**
         * The meta object literal for the '<em><b>Exec Category</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT__EXEC_CATEGORY = eINSTANCE.getProject_ExecCategory();

        /**
         * The meta object literal for the '<em><b>Ctd Category</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT__CTD_CATEGORY = eINSTANCE.getProject_CtdCategory();

        /**
         * The meta object literal for the '<em><b>AU Ts</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PROJECT__AU_TS = eINSTANCE.getProject_AUTs();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.TestCaseImpl <em>Test Case</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.TestCaseImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestCase()
         * @generated
         */
        EClass TEST_CASE = eINSTANCE.getTestCase();

        /**
         * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_CASE__CHILDREN = eINSTANCE.getTestCase_Children();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_CASE__PARAMETERS = eINSTANCE.getTestCase_Parameters();

        /**
         * The meta object literal for the '<em><b>Default Value Provider</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_CASE__DEFAULT_VALUE_PROVIDER = eINSTANCE.getTestCase_DefaultValueProvider();

        /**
         * The meta object literal for the '<em><b>Event Handler Map</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_CASE__EVENT_HANDLER_MAP = eINSTANCE.getTestCase_EventHandlerMap();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.SpecCategoryImpl <em>Spec Category</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.SpecCategoryImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getSpecCategory()
         * @generated
         */
        EClass SPEC_CATEGORY = eINSTANCE.getSpecCategory();

        /**
         * The meta object literal for the '<em><b>Test Cases</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SPEC_CATEGORY__TEST_CASES = eINSTANCE.getSpecCategory_TestCases();

        /**
         * The meta object literal for the '<em><b>Categories</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SPEC_CATEGORY__CATEGORIES = eINSTANCE.getSpecCategory_Categories();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.TestCaseRefImpl <em>Test Case Ref</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.TestCaseRefImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestCaseRef()
         * @generated
         */
        EClass TEST_CASE_REF = eINSTANCE.getTestCaseRef();

        /**
         * The meta object literal for the '<em><b>Test Case</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_CASE_REF__TEST_CASE = eINSTANCE.getTestCaseRef_TestCase();

        /**
         * The meta object literal for the '<em><b>Component Refs</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_CASE_REF__COMPONENT_REFS = eINSTANCE.getTestCaseRef_ComponentRefs();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.TestStepImpl <em>Test Step</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.TestStepImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestStep()
         * @generated
         */
        EClass TEST_STEP = eINSTANCE.getTestStep();

        /**
         * The meta object literal for the '<em><b>Component</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_STEP__COMPONENT = eINSTANCE.getTestStep_Component();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.TestCaseChildImpl <em>Test Case Child</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.TestCaseChildImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestCaseChild()
         * @generated
         */
        EClass TEST_CASE_CHILD = eINSTANCE.getTestCaseChild();

        /**
         * The meta object literal for the '<em><b>Value Provider</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_CASE_CHILD__VALUE_PROVIDER = eINSTANCE.getTestCaseChild_ValueProvider();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.TestJobImpl <em>Test Job</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.TestJobImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestJob()
         * @generated
         */
        EClass TEST_JOB = eINSTANCE.getTestJob();

        /**
         * The meta object literal for the '<em><b>Test Suite Refs</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_JOB__TEST_SUITE_REFS = eINSTANCE.getTestJob_TestSuiteRefs();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.TestSuiteImpl <em>Test Suite</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.TestSuiteImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestSuite()
         * @generated
         */
        EClass TEST_SUITE = eINSTANCE.getTestSuite();

        /**
         * The meta object literal for the '<em><b>Test Case Refs</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_SUITE__TEST_CASE_REFS = eINSTANCE.getTestSuite_TestCaseRefs();

        /**
         * The meta object literal for the '<em><b>AUT</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_SUITE__AUT = eINSTANCE.getTestSuite_AUT();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.TestSuiteRefImpl <em>Test Suite Ref</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.TestSuiteRefImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getTestSuiteRef()
         * @generated
         */
        EClass TEST_SUITE_REF = eINSTANCE.getTestSuiteRef();

        /**
         * The meta object literal for the '<em><b>Test Suite</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TEST_SUITE_REF__TEST_SUITE = eINSTANCE.getTestSuiteRef_TestSuite();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.ExecCategoryImpl <em>Exec Category</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.ExecCategoryImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getExecCategory()
         * @generated
         */
        EClass EXEC_CATEGORY = eINSTANCE.getExecCategory();

        /**
         * The meta object literal for the '<em><b>Categories</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EXEC_CATEGORY__CATEGORIES = eINSTANCE.getExecCategory_Categories();

        /**
         * The meta object literal for the '<em><b>Test Jobs</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EXEC_CATEGORY__TEST_JOBS = eINSTANCE.getExecCategory_TestJobs();

        /**
         * The meta object literal for the '<em><b>Test Suites</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EXEC_CATEGORY__TEST_SUITES = eINSTANCE.getExecCategory_TestSuites();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.AUTImpl <em>AUT</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.AUTImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getAUT()
         * @generated
         */
        EClass AUT = eINSTANCE.getAUT();

        /**
         * The meta object literal for the '<em><b>Configs</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference AUT__CONFIGS = eINSTANCE.getAUT_Configs();

        /**
         * The meta object literal for the '<em><b>Ui Category</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference AUT__UI_CATEGORY = eINSTANCE.getAUT_UiCategory();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.AUTConfigImpl <em>AUT Config</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.AUTConfigImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getAUTConfig()
         * @generated
         */
        EClass AUT_CONFIG = eINSTANCE.getAUTConfig();

        /**
         * The meta object literal for the '<em><b>Properties</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference AUT_CONFIG__PROPERTIES = eINSTANCE.getAUTConfig_Properties();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.UIComponentImpl <em>UI Component</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.UIComponentImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getUIComponent()
         * @generated
         */
        EClass UI_COMPONENT = eINSTANCE.getUIComponent();

        /**
         * The meta object literal for the '<em><b>Components</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference UI_COMPONENT__COMPONENTS = eINSTANCE.getUIComponent_Components();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.CompCategoryImpl <em>Comp Category</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.CompCategoryImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getCompCategory()
         * @generated
         */
        EClass COMP_CATEGORY = eINSTANCE.getCompCategory();

        /**
         * The meta object literal for the '<em><b>Categories</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMP_CATEGORY__CATEGORIES = eINSTANCE.getCompCategory_Categories();

        /**
         * The meta object literal for the '<em><b>Components</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMP_CATEGORY__COMPONENTS = eINSTANCE.getCompCategory_Components();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.ComponentImpl <em>Component</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.ComponentImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getComponent()
         * @generated
         */
        EClass COMPONENT = eINSTANCE.getComponent();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.ComponentRefImpl <em>Component Ref</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.ComponentRefImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getComponentRef()
         * @generated
         */
        EClass COMPONENT_REF = eINSTANCE.getComponentRef();

        /**
         * The meta object literal for the '<em><b>Component</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPONENT_REF__COMPONENT = eINSTANCE.getComponentRef_Component();

        /**
         * The meta object literal for the '<em><b>Override</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPONENT_REF__OVERRIDE = eINSTANCE.getComponentRef_Override();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.UICategoryImpl <em>UI Category</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.UICategoryImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getUICategory()
         * @generated
         */
        EClass UI_CATEGORY = eINSTANCE.getUICategory();

        /**
         * The meta object literal for the '<em><b>Ui Components</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference UI_CATEGORY__UI_COMPONENTS = eINSTANCE.getUICategory_UiComponents();

        /**
         * The meta object literal for the '<em><b>Categories</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference UI_CATEGORY__CATEGORIES = eINSTANCE.getUICategory_Categories();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.ParameterImpl <em>Parameter</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.ParameterImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getParameter()
         * @generated
         */
        EClass PARAMETER = eINSTANCE.getParameter();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.LanguageImpl <em>Language</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.LanguageImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getLanguage()
         * @generated
         */
        EClass LANGUAGE = eINSTANCE.getLanguage();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.ValueProvider <em>Value Provider</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.ValueProvider
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getValueProvider()
         * @generated
         */
        EClass VALUE_PROVIDER = eINSTANCE.getValueProvider();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.LocalValueProviderImpl <em>Local Value Provider</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.LocalValueProviderImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getLocalValueProvider()
         * @generated
         */
        EClass LOCAL_VALUE_PROVIDER = eINSTANCE.getLocalValueProvider();

        /**
         * The meta object literal for the '<em><b>Values</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference LOCAL_VALUE_PROVIDER__VALUES = eINSTANCE.getLocalValueProvider_Values();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.ValueImpl <em>Value</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.ValueImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getValue()
         * @generated
         */
        EClass VALUE = eINSTANCE.getValue();

        /**
         * The meta object literal for the '<em><b>Language</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference VALUE__LANGUAGE = eINSTANCE.getValue_Language();

        /**
         * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VALUE__TEXT = eINSTANCE.getValue_Text();

        /**
         * The meta object literal for the '<em><b>Parameter</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference VALUE__PARAMETER = eINSTANCE.getValue_Parameter();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.ExcelValueProviderImpl <em>Excel Value Provider</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.ExcelValueProviderImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getExcelValueProvider()
         * @generated
         */
        EClass EXCEL_VALUE_PROVIDER = eINSTANCE.getExcelValueProvider();

        /**
         * The meta object literal for the '<em><b>File Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EXCEL_VALUE_PROVIDER__FILE_NAME = eINSTANCE.getExcelValueProvider_FileName();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.RefValueProviderImpl <em>Ref Value Provider</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.RefValueProviderImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getRefValueProvider()
         * @generated
         */
        EClass REF_VALUE_PROVIDER = eINSTANCE.getRefValueProvider();

        /**
         * The meta object literal for the '<em><b>Test Case</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference REF_VALUE_PROVIDER__TEST_CASE = eINSTANCE.getRefValueProvider_TestCase();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.CTDValueProviderImpl <em>CTD Value Provider</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.CTDValueProviderImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getCTDValueProvider()
         * @generated
         */
        EClass CTD_VALUE_PROVIDER = eINSTANCE.getCTDValueProvider();

        /**
         * The meta object literal for the '<em><b>Ctd Set</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CTD_VALUE_PROVIDER__CTD_SET = eINSTANCE.getCTDValueProvider_CtdSet();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.CTDSetImpl <em>CTD Set</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.CTDSetImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getCTDSet()
         * @generated
         */
        EClass CTD_SET = eINSTANCE.getCTDSet();

        /**
         * The meta object literal for the '<em><b>Value Provider</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CTD_SET__VALUE_PROVIDER = eINSTANCE.getCTDSet_ValueProvider();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CTD_SET__PARAMETERS = eINSTANCE.getCTDSet_Parameters();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.CTDCategoryImpl <em>CTD Category</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.CTDCategoryImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getCTDCategory()
         * @generated
         */
        EClass CTD_CATEGORY = eINSTANCE.getCTDCategory();

        /**
         * The meta object literal for the '<em><b>Categories</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CTD_CATEGORY__CATEGORIES = eINSTANCE.getCTDCategory_Categories();

        /**
         * The meta object literal for the '<em><b>Ctd Sets</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CTD_CATEGORY__CTD_SETS = eINSTANCE.getCTDCategory_CtdSets();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.EventHandlerImpl <em>Event Handler</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.EventHandlerImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getEventHandler()
         * @generated
         */
        EClass EVENT_HANDLER = eINSTANCE.getEventHandler();

        /**
         * The meta object literal for the '<em><b>Reentry Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EVENT_HANDLER__REENTRY_TYPE = eINSTANCE.getEventHandler_ReentryType();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.HandlerEntryImpl <em>Handler Entry</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.HandlerEntryImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getHandlerEntry()
         * @generated
         */
        EClass HANDLER_ENTRY = eINSTANCE.getHandlerEntry();

        /**
         * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute HANDLER_ENTRY__KEY = eINSTANCE.getHandlerEntry_Key();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference HANDLER_ENTRY__VALUE = eINSTANCE.getHandlerEntry_Value();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.impl.NamedElementImpl <em>Named Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.impl.NamedElementImpl
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getNamedElement()
         * @generated
         */
        EClass NAMED_ELEMENT = eINSTANCE.getNamedElement();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute NAMED_ELEMENT__NAME = eINSTANCE.getNamedElement_Name();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.EventType <em>Event Type</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.EventType
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getEventType()
         * @generated
         */
        EEnum EVENT_TYPE = eINSTANCE.getEventType();

        /**
         * The meta object literal for the '{@link org.eclipse.jubula.ReentryType <em>Reentry Type</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.jubula.ReentryType
         * @see org.eclipse.jubula.impl.JubulaPackageImpl#getReentryType()
         * @generated
         */
        EEnum REENTRY_TYPE = eINSTANCE.getReentryType();

    }

} //JubulaPackage
