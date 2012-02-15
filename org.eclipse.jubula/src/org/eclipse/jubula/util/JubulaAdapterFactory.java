/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.util;

import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jubula.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.jubula.JubulaPackage
 * @generated
 */
public class JubulaAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static JubulaPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JubulaAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = JubulaPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected JubulaSwitch<Adapter> modelSwitch =
        new JubulaSwitch<Adapter>() {
            @Override
            public Adapter caseProject(Project object) {
                return createProjectAdapter();
            }
            @Override
            public Adapter caseTestCase(TestCase object) {
                return createTestCaseAdapter();
            }
            @Override
            public Adapter caseSpecCategory(SpecCategory object) {
                return createSpecCategoryAdapter();
            }
            @Override
            public Adapter caseTestCaseRef(TestCaseRef object) {
                return createTestCaseRefAdapter();
            }
            @Override
            public Adapter caseTestStep(TestStep object) {
                return createTestStepAdapter();
            }
            @Override
            public Adapter caseTestCaseChild(TestCaseChild object) {
                return createTestCaseChildAdapter();
            }
            @Override
            public Adapter caseTestJob(TestJob object) {
                return createTestJobAdapter();
            }
            @Override
            public Adapter caseTestSuite(TestSuite object) {
                return createTestSuiteAdapter();
            }
            @Override
            public Adapter caseTestSuiteRef(TestSuiteRef object) {
                return createTestSuiteRefAdapter();
            }
            @Override
            public Adapter caseExecCategory(ExecCategory object) {
                return createExecCategoryAdapter();
            }
            @Override
            public Adapter caseAUT(AUT object) {
                return createAUTAdapter();
            }
            @Override
            public Adapter caseAUTConfig(AUTConfig object) {
                return createAUTConfigAdapter();
            }
            @Override
            public Adapter caseUIComponent(UIComponent object) {
                return createUIComponentAdapter();
            }
            @Override
            public Adapter caseCompCategory(CompCategory object) {
                return createCompCategoryAdapter();
            }
            @Override
            public Adapter caseComponent(Component object) {
                return createComponentAdapter();
            }
            @Override
            public Adapter caseComponentRef(ComponentRef object) {
                return createComponentRefAdapter();
            }
            @Override
            public Adapter caseUICategory(UICategory object) {
                return createUICategoryAdapter();
            }
            @Override
            public Adapter caseParameter(Parameter object) {
                return createParameterAdapter();
            }
            @Override
            public Adapter caseLanguage(Language object) {
                return createLanguageAdapter();
            }
            @Override
            public Adapter caseValueProvider(ValueProvider object) {
                return createValueProviderAdapter();
            }
            @Override
            public Adapter caseLocalValueProvider(LocalValueProvider object) {
                return createLocalValueProviderAdapter();
            }
            @Override
            public Adapter caseValue(Value object) {
                return createValueAdapter();
            }
            @Override
            public Adapter caseExcelValueProvider(ExcelValueProvider object) {
                return createExcelValueProviderAdapter();
            }
            @Override
            public Adapter caseRefValueProvider(RefValueProvider object) {
                return createRefValueProviderAdapter();
            }
            @Override
            public Adapter caseCTDValueProvider(CTDValueProvider object) {
                return createCTDValueProviderAdapter();
            }
            @Override
            public Adapter caseCTDSet(CTDSet object) {
                return createCTDSetAdapter();
            }
            @Override
            public Adapter caseCTDCategory(CTDCategory object) {
                return createCTDCategoryAdapter();
            }
            @Override
            public Adapter caseEventHandler(EventHandler object) {
                return createEventHandlerAdapter();
            }
            @Override
            public Adapter caseHandlerEntry(Map.Entry<EventType, EventHandler> object) {
                return createHandlerEntryAdapter();
            }
            @Override
            public Adapter caseNamedElement(NamedElement object) {
                return createNamedElementAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.Project <em>Project</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.Project
     * @generated
     */
    public Adapter createProjectAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.TestCase <em>Test Case</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.TestCase
     * @generated
     */
    public Adapter createTestCaseAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.SpecCategory <em>Spec Category</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.SpecCategory
     * @generated
     */
    public Adapter createSpecCategoryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.TestCaseRef <em>Test Case Ref</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.TestCaseRef
     * @generated
     */
    public Adapter createTestCaseRefAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.TestStep <em>Test Step</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.TestStep
     * @generated
     */
    public Adapter createTestStepAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.TestCaseChild <em>Test Case Child</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.TestCaseChild
     * @generated
     */
    public Adapter createTestCaseChildAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.TestJob <em>Test Job</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.TestJob
     * @generated
     */
    public Adapter createTestJobAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.TestSuite <em>Test Suite</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.TestSuite
     * @generated
     */
    public Adapter createTestSuiteAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.TestSuiteRef <em>Test Suite Ref</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.TestSuiteRef
     * @generated
     */
    public Adapter createTestSuiteRefAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.ExecCategory <em>Exec Category</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.ExecCategory
     * @generated
     */
    public Adapter createExecCategoryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.AUT <em>AUT</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.AUT
     * @generated
     */
    public Adapter createAUTAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.AUTConfig <em>AUT Config</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.AUTConfig
     * @generated
     */
    public Adapter createAUTConfigAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.UIComponent <em>UI Component</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.UIComponent
     * @generated
     */
    public Adapter createUIComponentAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.CompCategory <em>Comp Category</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.CompCategory
     * @generated
     */
    public Adapter createCompCategoryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.Component <em>Component</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.Component
     * @generated
     */
    public Adapter createComponentAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.ComponentRef <em>Component Ref</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.ComponentRef
     * @generated
     */
    public Adapter createComponentRefAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.UICategory <em>UI Category</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.UICategory
     * @generated
     */
    public Adapter createUICategoryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.Parameter <em>Parameter</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.Parameter
     * @generated
     */
    public Adapter createParameterAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.Language <em>Language</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.Language
     * @generated
     */
    public Adapter createLanguageAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.ValueProvider <em>Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.ValueProvider
     * @generated
     */
    public Adapter createValueProviderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.LocalValueProvider <em>Local Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.LocalValueProvider
     * @generated
     */
    public Adapter createLocalValueProviderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.Value <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.Value
     * @generated
     */
    public Adapter createValueAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.ExcelValueProvider <em>Excel Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.ExcelValueProvider
     * @generated
     */
    public Adapter createExcelValueProviderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.RefValueProvider <em>Ref Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.RefValueProvider
     * @generated
     */
    public Adapter createRefValueProviderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.CTDValueProvider <em>CTD Value Provider</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.CTDValueProvider
     * @generated
     */
    public Adapter createCTDValueProviderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.CTDSet <em>CTD Set</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.CTDSet
     * @generated
     */
    public Adapter createCTDSetAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.CTDCategory <em>CTD Category</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.CTDCategory
     * @generated
     */
    public Adapter createCTDCategoryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.EventHandler <em>Event Handler</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.EventHandler
     * @generated
     */
    public Adapter createEventHandlerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Handler Entry</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see java.util.Map.Entry
     * @generated
     */
    public Adapter createHandlerEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.jubula.NamedElement <em>Named Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.jubula.NamedElement
     * @generated
     */
    public Adapter createNamedElementAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //JubulaAdapterFactory
