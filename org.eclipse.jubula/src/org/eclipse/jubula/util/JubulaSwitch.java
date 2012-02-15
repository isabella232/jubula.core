/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.util;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.jubula.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.jubula.JubulaPackage
 * @generated
 */
public class JubulaSwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static JubulaPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JubulaSwitch() {
        if (modelPackage == null) {
            modelPackage = JubulaPackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @parameter ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case JubulaPackage.PROJECT: {
                Project project = (Project)theEObject;
                T result = caseProject(project);
                if (result == null) result = caseNamedElement(project);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.TEST_CASE: {
                TestCase testCase = (TestCase)theEObject;
                T result = caseTestCase(testCase);
                if (result == null) result = caseNamedElement(testCase);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.SPEC_CATEGORY: {
                SpecCategory specCategory = (SpecCategory)theEObject;
                T result = caseSpecCategory(specCategory);
                if (result == null) result = caseNamedElement(specCategory);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.TEST_CASE_REF: {
                TestCaseRef testCaseRef = (TestCaseRef)theEObject;
                T result = caseTestCaseRef(testCaseRef);
                if (result == null) result = caseTestCaseChild(testCaseRef);
                if (result == null) result = caseNamedElement(testCaseRef);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.TEST_STEP: {
                TestStep testStep = (TestStep)theEObject;
                T result = caseTestStep(testStep);
                if (result == null) result = caseTestCaseChild(testStep);
                if (result == null) result = caseNamedElement(testStep);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.TEST_CASE_CHILD: {
                TestCaseChild testCaseChild = (TestCaseChild)theEObject;
                T result = caseTestCaseChild(testCaseChild);
                if (result == null) result = caseNamedElement(testCaseChild);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.TEST_JOB: {
                TestJob testJob = (TestJob)theEObject;
                T result = caseTestJob(testJob);
                if (result == null) result = caseNamedElement(testJob);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.TEST_SUITE: {
                TestSuite testSuite = (TestSuite)theEObject;
                T result = caseTestSuite(testSuite);
                if (result == null) result = caseNamedElement(testSuite);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.TEST_SUITE_REF: {
                TestSuiteRef testSuiteRef = (TestSuiteRef)theEObject;
                T result = caseTestSuiteRef(testSuiteRef);
                if (result == null) result = caseNamedElement(testSuiteRef);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.EXEC_CATEGORY: {
                ExecCategory execCategory = (ExecCategory)theEObject;
                T result = caseExecCategory(execCategory);
                if (result == null) result = caseNamedElement(execCategory);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.AUT: {
                AUT aut = (AUT)theEObject;
                T result = caseAUT(aut);
                if (result == null) result = caseNamedElement(aut);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.AUT_CONFIG: {
                AUTConfig autConfig = (AUTConfig)theEObject;
                T result = caseAUTConfig(autConfig);
                if (result == null) result = caseNamedElement(autConfig);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.UI_COMPONENT: {
                UIComponent uiComponent = (UIComponent)theEObject;
                T result = caseUIComponent(uiComponent);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.COMP_CATEGORY: {
                CompCategory compCategory = (CompCategory)theEObject;
                T result = caseCompCategory(compCategory);
                if (result == null) result = caseNamedElement(compCategory);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.COMPONENT: {
                Component component = (Component)theEObject;
                T result = caseComponent(component);
                if (result == null) result = caseNamedElement(component);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.COMPONENT_REF: {
                ComponentRef componentRef = (ComponentRef)theEObject;
                T result = caseComponentRef(componentRef);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.UI_CATEGORY: {
                UICategory uiCategory = (UICategory)theEObject;
                T result = caseUICategory(uiCategory);
                if (result == null) result = caseNamedElement(uiCategory);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.PARAMETER: {
                Parameter parameter = (Parameter)theEObject;
                T result = caseParameter(parameter);
                if (result == null) result = caseNamedElement(parameter);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.LANGUAGE: {
                Language language = (Language)theEObject;
                T result = caseLanguage(language);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.VALUE_PROVIDER: {
                ValueProvider valueProvider = (ValueProvider)theEObject;
                T result = caseValueProvider(valueProvider);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.LOCAL_VALUE_PROVIDER: {
                LocalValueProvider localValueProvider = (LocalValueProvider)theEObject;
                T result = caseLocalValueProvider(localValueProvider);
                if (result == null) result = caseValueProvider(localValueProvider);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.VALUE: {
                Value value = (Value)theEObject;
                T result = caseValue(value);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.EXCEL_VALUE_PROVIDER: {
                ExcelValueProvider excelValueProvider = (ExcelValueProvider)theEObject;
                T result = caseExcelValueProvider(excelValueProvider);
                if (result == null) result = caseValueProvider(excelValueProvider);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.REF_VALUE_PROVIDER: {
                RefValueProvider refValueProvider = (RefValueProvider)theEObject;
                T result = caseRefValueProvider(refValueProvider);
                if (result == null) result = caseValueProvider(refValueProvider);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.CTD_VALUE_PROVIDER: {
                CTDValueProvider ctdValueProvider = (CTDValueProvider)theEObject;
                T result = caseCTDValueProvider(ctdValueProvider);
                if (result == null) result = caseValueProvider(ctdValueProvider);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.CTD_SET: {
                CTDSet ctdSet = (CTDSet)theEObject;
                T result = caseCTDSet(ctdSet);
                if (result == null) result = caseNamedElement(ctdSet);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.CTD_CATEGORY: {
                CTDCategory ctdCategory = (CTDCategory)theEObject;
                T result = caseCTDCategory(ctdCategory);
                if (result == null) result = caseNamedElement(ctdCategory);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.EVENT_HANDLER: {
                EventHandler eventHandler = (EventHandler)theEObject;
                T result = caseEventHandler(eventHandler);
                if (result == null) result = caseTestCaseRef(eventHandler);
                if (result == null) result = caseTestCaseChild(eventHandler);
                if (result == null) result = caseNamedElement(eventHandler);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.HANDLER_ENTRY: {
                @SuppressWarnings("unchecked") Map.Entry<EventType, EventHandler> handlerEntry = (Map.Entry<EventType, EventHandler>)theEObject;
                T result = caseHandlerEntry(handlerEntry);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JubulaPackage.NAMED_ELEMENT: {
                NamedElement namedElement = (NamedElement)theEObject;
                T result = caseNamedElement(namedElement);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Project</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Project</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseProject(Project object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Test Case</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Test Case</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTestCase(TestCase object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Spec Category</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Spec Category</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseSpecCategory(SpecCategory object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Test Case Ref</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Test Case Ref</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTestCaseRef(TestCaseRef object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Test Step</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Test Step</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTestStep(TestStep object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Test Case Child</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Test Case Child</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTestCaseChild(TestCaseChild object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Test Job</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Test Job</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTestJob(TestJob object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Test Suite</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Test Suite</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTestSuite(TestSuite object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Test Suite Ref</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Test Suite Ref</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTestSuiteRef(TestSuiteRef object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Exec Category</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Exec Category</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseExecCategory(ExecCategory object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>AUT</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>AUT</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseAUT(AUT object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>AUT Config</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>AUT Config</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseAUTConfig(AUTConfig object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>UI Component</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>UI Component</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseUIComponent(UIComponent object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Comp Category</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Comp Category</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCompCategory(CompCategory object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Component</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Component</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseComponent(Component object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Component Ref</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Component Ref</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseComponentRef(ComponentRef object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>UI Category</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>UI Category</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseUICategory(UICategory object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Parameter</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Parameter</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseParameter(Parameter object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Language</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Language</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseLanguage(Language object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Value Provider</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Value Provider</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseValueProvider(ValueProvider object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Local Value Provider</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Local Value Provider</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseLocalValueProvider(LocalValueProvider object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Value</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Value</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseValue(Value object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Excel Value Provider</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Excel Value Provider</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseExcelValueProvider(ExcelValueProvider object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Ref Value Provider</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Ref Value Provider</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseRefValueProvider(RefValueProvider object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>CTD Value Provider</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>CTD Value Provider</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCTDValueProvider(CTDValueProvider object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>CTD Set</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>CTD Set</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCTDSet(CTDSet object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>CTD Category</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>CTD Category</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCTDCategory(CTDCategory object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Event Handler</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Event Handler</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseEventHandler(EventHandler object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Handler Entry</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Handler Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseHandlerEntry(Map.Entry<EventType, EventHandler> object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Named Element</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Named Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseNamedElement(NamedElement object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }

} //JubulaSwitch
