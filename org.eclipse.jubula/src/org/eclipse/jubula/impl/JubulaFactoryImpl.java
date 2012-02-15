/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.jubula.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JubulaFactoryImpl extends EFactoryImpl implements JubulaFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static JubulaFactory init() {
        try {
            JubulaFactory theJubulaFactory = (JubulaFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/jubula/2.0"); 
            if (theJubulaFactory != null) {
                return theJubulaFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new JubulaFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JubulaFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case JubulaPackage.PROJECT: return (EObject)createProject();
            case JubulaPackage.TEST_CASE: return (EObject)createTestCase();
            case JubulaPackage.SPEC_CATEGORY: return (EObject)createSpecCategory();
            case JubulaPackage.TEST_CASE_REF: return (EObject)createTestCaseRef();
            case JubulaPackage.TEST_STEP: return (EObject)createTestStep();
            case JubulaPackage.TEST_JOB: return (EObject)createTestJob();
            case JubulaPackage.TEST_SUITE: return (EObject)createTestSuite();
            case JubulaPackage.TEST_SUITE_REF: return (EObject)createTestSuiteRef();
            case JubulaPackage.EXEC_CATEGORY: return (EObject)createExecCategory();
            case JubulaPackage.AUT: return (EObject)createAUT();
            case JubulaPackage.AUT_CONFIG: return (EObject)createAUTConfig();
            case JubulaPackage.UI_COMPONENT: return (EObject)createUIComponent();
            case JubulaPackage.COMP_CATEGORY: return (EObject)createCompCategory();
            case JubulaPackage.COMPONENT: return (EObject)createComponent();
            case JubulaPackage.COMPONENT_REF: return (EObject)createComponentRef();
            case JubulaPackage.UI_CATEGORY: return (EObject)createUICategory();
            case JubulaPackage.PARAMETER: return (EObject)createParameter();
            case JubulaPackage.LANGUAGE: return (EObject)createLanguage();
            case JubulaPackage.LOCAL_VALUE_PROVIDER: return (EObject)createLocalValueProvider();
            case JubulaPackage.VALUE: return (EObject)createValue();
            case JubulaPackage.EXCEL_VALUE_PROVIDER: return (EObject)createExcelValueProvider();
            case JubulaPackage.REF_VALUE_PROVIDER: return (EObject)createRefValueProvider();
            case JubulaPackage.CTD_VALUE_PROVIDER: return (EObject)createCTDValueProvider();
            case JubulaPackage.CTD_SET: return (EObject)createCTDSet();
            case JubulaPackage.CTD_CATEGORY: return (EObject)createCTDCategory();
            case JubulaPackage.EVENT_HANDLER: return (EObject)createEventHandler();
            case JubulaPackage.HANDLER_ENTRY: return (EObject)createHandlerEntry();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case JubulaPackage.EVENT_TYPE:
                return createEventTypeFromString(eDataType, initialValue);
            case JubulaPackage.REENTRY_TYPE:
                return createReentryTypeFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case JubulaPackage.EVENT_TYPE:
                return convertEventTypeToString(eDataType, instanceValue);
            case JubulaPackage.REENTRY_TYPE:
                return convertReentryTypeToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Project createProject() {
        ProjectImpl project = new ProjectImpl();
        return project;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TestCase createTestCase() {
        TestCaseImpl testCase = new TestCaseImpl();
        return testCase;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SpecCategory createSpecCategory() {
        SpecCategoryImpl specCategory = new SpecCategoryImpl();
        return specCategory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TestCaseRef createTestCaseRef() {
        TestCaseRefImpl testCaseRef = new TestCaseRefImpl();
        return testCaseRef;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TestStep createTestStep() {
        TestStepImpl testStep = new TestStepImpl();
        return testStep;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TestJob createTestJob() {
        TestJobImpl testJob = new TestJobImpl();
        return testJob;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TestSuite createTestSuite() {
        TestSuiteImpl testSuite = new TestSuiteImpl();
        return testSuite;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TestSuiteRef createTestSuiteRef() {
        TestSuiteRefImpl testSuiteRef = new TestSuiteRefImpl();
        return testSuiteRef;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ExecCategory createExecCategory() {
        ExecCategoryImpl execCategory = new ExecCategoryImpl();
        return execCategory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AUT createAUT() {
        AUTImpl aut = new AUTImpl();
        return aut;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AUTConfig createAUTConfig() {
        AUTConfigImpl autConfig = new AUTConfigImpl();
        return autConfig;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UIComponent createUIComponent() {
        UIComponentImpl uiComponent = new UIComponentImpl();
        return uiComponent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CompCategory createCompCategory() {
        CompCategoryImpl compCategory = new CompCategoryImpl();
        return compCategory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Component createComponent() {
        ComponentImpl component = new ComponentImpl();
        return component;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ComponentRef createComponentRef() {
        ComponentRefImpl componentRef = new ComponentRefImpl();
        return componentRef;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UICategory createUICategory() {
        UICategoryImpl uiCategory = new UICategoryImpl();
        return uiCategory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Parameter createParameter() {
        ParameterImpl parameter = new ParameterImpl();
        return parameter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Language createLanguage() {
        LanguageImpl language = new LanguageImpl();
        return language;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public LocalValueProvider createLocalValueProvider() {
        LocalValueProviderImpl localValueProvider = new LocalValueProviderImpl();
        return localValueProvider;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Value createValue() {
        ValueImpl value = new ValueImpl();
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ExcelValueProvider createExcelValueProvider() {
        ExcelValueProviderImpl excelValueProvider = new ExcelValueProviderImpl();
        return excelValueProvider;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RefValueProvider createRefValueProvider() {
        RefValueProviderImpl refValueProvider = new RefValueProviderImpl();
        return refValueProvider;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CTDValueProvider createCTDValueProvider() {
        CTDValueProviderImpl ctdValueProvider = new CTDValueProviderImpl();
        return ctdValueProvider;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CTDSet createCTDSet() {
        CTDSetImpl ctdSet = new CTDSetImpl();
        return ctdSet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CTDCategory createCTDCategory() {
        CTDCategoryImpl ctdCategory = new CTDCategoryImpl();
        return ctdCategory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventHandler createEventHandler() {
        EventHandlerImpl eventHandler = new EventHandlerImpl();
        return eventHandler;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Map.Entry<EventType, EventHandler> createHandlerEntry() {
        HandlerEntryImpl handlerEntry = new HandlerEntryImpl();
        return handlerEntry;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventType createEventTypeFromString(EDataType eDataType, String initialValue) {
        EventType result = EventType.get(initialValue);
        if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertEventTypeToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ReentryType createReentryTypeFromString(EDataType eDataType, String initialValue) {
        ReentryType result = ReentryType.get(initialValue);
        if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertReentryTypeToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JubulaPackage getJubulaPackage() {
        return (JubulaPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static JubulaPackage getPackage() {
        return JubulaPackage.eINSTANCE;
    }

} //JubulaFactoryImpl
