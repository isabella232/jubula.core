/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula.impl;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Map;

import org.eclipse.emf.common.util.Reflect;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.jubula.AUTConfig;
import org.eclipse.jubula.CTDCategory;
import org.eclipse.jubula.CTDSet;
import org.eclipse.jubula.CTDValueProvider;
import org.eclipse.jubula.CompCategory;
import org.eclipse.jubula.Component;
import org.eclipse.jubula.ComponentRef;
import org.eclipse.jubula.EventHandler;
import org.eclipse.jubula.EventType;
import org.eclipse.jubula.ExcelValueProvider;
import org.eclipse.jubula.ExecCategory;
import org.eclipse.jubula.JubulaFactory;
import org.eclipse.jubula.JubulaPackage;
import org.eclipse.jubula.Language;
import org.eclipse.jubula.LocalValueProvider;
import org.eclipse.jubula.NamedElement;
import org.eclipse.jubula.Parameter;
import org.eclipse.jubula.Project;
import org.eclipse.jubula.ReentryType;
import org.eclipse.jubula.RefValueProvider;
import org.eclipse.jubula.SpecCategory;
import org.eclipse.jubula.TestCase;
import org.eclipse.jubula.TestCaseChild;
import org.eclipse.jubula.TestCaseRef;
import org.eclipse.jubula.TestJob;
import org.eclipse.jubula.TestStep;
import org.eclipse.jubula.TestSuite;
import org.eclipse.jubula.TestSuiteRef;
import org.eclipse.jubula.UICategory;
import org.eclipse.jubula.UIComponent;
import org.eclipse.jubula.Value;
import org.eclipse.jubula.ValueProvider;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JubulaPackageImpl extends EPackageImpl implements JubulaPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass projectEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass testCaseEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass specCategoryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass testCaseRefEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass testStepEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass testCaseChildEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass testJobEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass testSuiteEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass testSuiteRefEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass execCategoryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass autEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass autConfigEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass uiComponentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass compCategoryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass componentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass componentRefEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass uiCategoryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass parameterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass languageEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass valueProviderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass localValueProviderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass valueEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass excelValueProviderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass refValueProviderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass ctdValueProviderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass ctdSetEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass ctdCategoryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass eventHandlerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass handlerEntryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass namedElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum eventTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum reentryTypeEEnum = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.eclipse.jubula.JubulaPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private JubulaPackageImpl() {
        super(eNS_URI, JubulaFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link JubulaPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static JubulaPackage init() {
        if (isInited) return (JubulaPackage)EPackage.Registry.INSTANCE.getEPackage(JubulaPackage.eNS_URI);

        initializeRegistryHelpers();

        // Obtain or create and register package
        JubulaPackageImpl theJubulaPackage = (JubulaPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof JubulaPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new JubulaPackageImpl());

        isInited = true;

        // Create package meta-data objects
        theJubulaPackage.createPackageContents();

        // Initialize created meta-data
        theJubulaPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theJubulaPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(JubulaPackage.eNS_URI, theJubulaPackage);
        return theJubulaPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void initializeRegistryHelpers() {
        Reflect.register
            (Project.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof Project;
                 }

                 public Object newArrayInstance(int size) {
                     return new Project[size];
                 }
             });
        Reflect.register
            (TestCase.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof TestCase;
                 }

                 public Object newArrayInstance(int size) {
                     return new TestCase[size];
                 }
             });
        Reflect.register
            (SpecCategory.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof SpecCategory;
                 }

                 public Object newArrayInstance(int size) {
                     return new SpecCategory[size];
                 }
             });
        Reflect.register
            (TestCaseRef.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof TestCaseRef;
                 }

                 public Object newArrayInstance(int size) {
                     return new TestCaseRef[size];
                 }
             });
        Reflect.register
            (TestStep.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof TestStep;
                 }

                 public Object newArrayInstance(int size) {
                     return new TestStep[size];
                 }
             });
        Reflect.register
            (TestCaseChild.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof TestCaseChild;
                 }

                 public Object newArrayInstance(int size) {
                     return new TestCaseChild[size];
                 }
             });
        Reflect.register
            (TestJob.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof TestJob;
                 }

                 public Object newArrayInstance(int size) {
                     return new TestJob[size];
                 }
             });
        Reflect.register
            (TestSuite.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof TestSuite;
                 }

                 public Object newArrayInstance(int size) {
                     return new TestSuite[size];
                 }
             });
        Reflect.register
            (TestSuiteRef.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof TestSuiteRef;
                 }

                 public Object newArrayInstance(int size) {
                     return new TestSuiteRef[size];
                 }
             });
        Reflect.register
            (ExecCategory.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof ExecCategory;
                 }

                 public Object newArrayInstance(int size) {
                     return new ExecCategory[size];
                 }
             });
        Reflect.register
            (org.eclipse.jubula.AUT.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof org.eclipse.jubula.AUT;
                 }

                 public Object newArrayInstance(int size) {
                     return new org.eclipse.jubula.AUT[size];
                 }
             });
        Reflect.register
            (AUTConfig.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof AUTConfig;
                 }

                 public Object newArrayInstance(int size) {
                     return new AUTConfig[size];
                 }
             });
        Reflect.register
            (UIComponent.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof UIComponent;
                 }

                 public Object newArrayInstance(int size) {
                     return new UIComponent[size];
                 }
             });
        Reflect.register
            (CompCategory.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof CompCategory;
                 }

                 public Object newArrayInstance(int size) {
                     return new CompCategory[size];
                 }
             });
        Reflect.register
            (Component.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof Component;
                 }

                 public Object newArrayInstance(int size) {
                     return new Component[size];
                 }
             });
        Reflect.register
            (ComponentRef.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof ComponentRef;
                 }

                 public Object newArrayInstance(int size) {
                     return new ComponentRef[size];
                 }
             });
        Reflect.register
            (UICategory.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof UICategory;
                 }

                 public Object newArrayInstance(int size) {
                     return new UICategory[size];
                 }
             });
        Reflect.register
            (Parameter.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof Parameter;
                 }

                 public Object newArrayInstance(int size) {
                     return new Parameter[size];
                 }
             });
        Reflect.register
            (Language.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof Language;
                 }

                 public Object newArrayInstance(int size) {
                     return new Language[size];
                 }
             });
        Reflect.register
            (ValueProvider.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof ValueProvider;
                 }

                 public Object newArrayInstance(int size) {
                     return new ValueProvider[size];
                 }
             });
        Reflect.register
            (LocalValueProvider.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof LocalValueProvider;
                 }

                 public Object newArrayInstance(int size) {
                     return new LocalValueProvider[size];
                 }
             });
        Reflect.register
            (Value.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof Value;
                 }

                 public Object newArrayInstance(int size) {
                     return new Value[size];
                 }
             });
        Reflect.register
            (ExcelValueProvider.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof ExcelValueProvider;
                 }

                 public Object newArrayInstance(int size) {
                     return new ExcelValueProvider[size];
                 }
             });
        Reflect.register
            (RefValueProvider.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof RefValueProvider;
                 }

                 public Object newArrayInstance(int size) {
                     return new RefValueProvider[size];
                 }
             });
        Reflect.register
            (CTDValueProvider.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof CTDValueProvider;
                 }

                 public Object newArrayInstance(int size) {
                     return new CTDValueProvider[size];
                 }
             });
        Reflect.register
            (CTDSet.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof CTDSet;
                 }

                 public Object newArrayInstance(int size) {
                     return new CTDSet[size];
                 }
             });
        Reflect.register
            (CTDCategory.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof CTDCategory;
                 }

                 public Object newArrayInstance(int size) {
                     return new CTDCategory[size];
                 }
             });
        Reflect.register
            (EventHandler.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof EventHandler;
                 }

                 public Object newArrayInstance(int size) {
                     return new EventHandler[size];
                 }
             });
        Reflect.register
            (HandlerEntryImpl.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof HandlerEntryImpl;
                 }

                 public Object newArrayInstance(int size) {
                     return new HandlerEntryImpl[size];
                 }
             });
        Reflect.register
            (NamedElement.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof NamedElement;
                 }

                 public Object newArrayInstance(int size) {
                     return new NamedElement[size];
                 }
             });
        Reflect.register
            (EventType.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof EventType;
                 }

                 public Object newArrayInstance(int size) {
                     return new EventType[size];
                 }
        });
        Reflect.register
            (ReentryType.class, 
             new Reflect.Helper() {
                 public boolean isInstance(Object instance) {
                     return instance instanceof ReentryType;
                 }

                 public Object newArrayInstance(int size) {
                     return new ReentryType[size];
                 }
        });
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static class WhiteList implements IsSerializable, EBasicWhiteList {
        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected Project project;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected TestCase testCase;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected SpecCategory specCategory;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected TestCaseRef testCaseRef;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected TestStep testStep;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected TestCaseChild testCaseChild;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected TestJob testJob;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected TestSuite testSuite;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected TestSuiteRef testSuiteRef;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected ExecCategory execCategory;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected org.eclipse.jubula.AUT aut;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected AUTConfig autConfig;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected UIComponent uiComponent;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected CompCategory compCategory;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected Component component;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected ComponentRef componentRef;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected UICategory uiCategory;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected Parameter parameter;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected Language language;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected ValueProvider valueProvider;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected LocalValueProvider localValueProvider;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected Value value;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected ExcelValueProvider excelValueProvider;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected RefValueProvider refValueProvider;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected CTDValueProvider ctdValueProvider;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected CTDSet ctdSet;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected CTDCategory ctdCategory;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected EventHandler eventHandler;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected HandlerEntryImpl handlerEntry;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected NamedElement namedElement;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected EventType eventType;

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        protected ReentryType reentryType;

    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProject() {
        return projectEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProject_Languages() {
        return (EReference)projectEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProject_CompCategory() {
        return (EReference)projectEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProject_SpecCategory() {
        return (EReference)projectEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProject_ExecCategory() {
        return (EReference)projectEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProject_CtdCategory() {
        return (EReference)projectEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProject_AUTs() {
        return (EReference)projectEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTestCase() {
        return testCaseEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestCase_Children() {
        return (EReference)testCaseEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestCase_Parameters() {
        return (EReference)testCaseEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestCase_DefaultValueProvider() {
        return (EReference)testCaseEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestCase_EventHandlerMap() {
        return (EReference)testCaseEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getSpecCategory() {
        return specCategoryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getSpecCategory_TestCases() {
        return (EReference)specCategoryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getSpecCategory_Categories() {
        return (EReference)specCategoryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTestCaseRef() {
        return testCaseRefEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestCaseRef_TestCase() {
        return (EReference)testCaseRefEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestCaseRef_ComponentRefs() {
        return (EReference)testCaseRefEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTestStep() {
        return testStepEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestStep_Component() {
        return (EReference)testStepEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTestCaseChild() {
        return testCaseChildEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestCaseChild_ValueProvider() {
        return (EReference)testCaseChildEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTestJob() {
        return testJobEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestJob_TestSuiteRefs() {
        return (EReference)testJobEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTestSuite() {
        return testSuiteEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestSuite_TestCaseRefs() {
        return (EReference)testSuiteEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestSuite_AUT() {
        return (EReference)testSuiteEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTestSuiteRef() {
        return testSuiteRefEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTestSuiteRef_TestSuite() {
        return (EReference)testSuiteRefEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getExecCategory() {
        return execCategoryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getExecCategory_Categories() {
        return (EReference)execCategoryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getExecCategory_TestJobs() {
        return (EReference)execCategoryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getExecCategory_TestSuites() {
        return (EReference)execCategoryEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAUT() {
        return autEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAUT_Configs() {
        return (EReference)autEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAUT_UiCategory() {
        return (EReference)autEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAUTConfig() {
        return autConfigEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAUTConfig_Properties() {
        return (EReference)autConfigEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getUIComponent() {
        return uiComponentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getUIComponent_Components() {
        return (EReference)uiComponentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getCompCategory() {
        return compCategoryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCompCategory_Categories() {
        return (EReference)compCategoryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCompCategory_Components() {
        return (EReference)compCategoryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getComponent() {
        return componentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getComponentRef() {
        return componentRefEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getComponentRef_Component() {
        return (EReference)componentRefEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getComponentRef_Override() {
        return (EReference)componentRefEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getUICategory() {
        return uiCategoryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getUICategory_UiComponents() {
        return (EReference)uiCategoryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getUICategory_Categories() {
        return (EReference)uiCategoryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getParameter() {
        return parameterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getLanguage() {
        return languageEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getValueProvider() {
        return valueProviderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getLocalValueProvider() {
        return localValueProviderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLocalValueProvider_Values() {
        return (EReference)localValueProviderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getValue() {
        return valueEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getValue_Language() {
        return (EReference)valueEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getValue_Text() {
        return (EAttribute)valueEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getValue_Parameter() {
        return (EReference)valueEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getExcelValueProvider() {
        return excelValueProviderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getExcelValueProvider_FileName() {
        return (EAttribute)excelValueProviderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRefValueProvider() {
        return refValueProviderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRefValueProvider_TestCase() {
        return (EReference)refValueProviderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getCTDValueProvider() {
        return ctdValueProviderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCTDValueProvider_CtdSet() {
        return (EReference)ctdValueProviderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getCTDSet() {
        return ctdSetEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCTDSet_ValueProvider() {
        return (EReference)ctdSetEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCTDSet_Parameters() {
        return (EReference)ctdSetEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getCTDCategory() {
        return ctdCategoryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCTDCategory_Categories() {
        return (EReference)ctdCategoryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCTDCategory_CtdSets() {
        return (EReference)ctdCategoryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEventHandler() {
        return eventHandlerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEventHandler_ReentryType() {
        return (EAttribute)eventHandlerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getHandlerEntry() {
        return handlerEntryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getHandlerEntry_Key() {
        return (EAttribute)handlerEntryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getHandlerEntry_Value() {
        return (EReference)handlerEntryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getNamedElement() {
        return namedElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getNamedElement_Name() {
        return (EAttribute)namedElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getEventType() {
        return eventTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getReentryType() {
        return reentryTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JubulaFactory getJubulaFactory() {
        return (JubulaFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        projectEClass = createEClass(PROJECT);
        createEReference(projectEClass, PROJECT__LANGUAGES);
        createEReference(projectEClass, PROJECT__COMP_CATEGORY);
        createEReference(projectEClass, PROJECT__SPEC_CATEGORY);
        createEReference(projectEClass, PROJECT__EXEC_CATEGORY);
        createEReference(projectEClass, PROJECT__CTD_CATEGORY);
        createEReference(projectEClass, PROJECT__AU_TS);

        testCaseEClass = createEClass(TEST_CASE);
        createEReference(testCaseEClass, TEST_CASE__CHILDREN);
        createEReference(testCaseEClass, TEST_CASE__PARAMETERS);
        createEReference(testCaseEClass, TEST_CASE__DEFAULT_VALUE_PROVIDER);
        createEReference(testCaseEClass, TEST_CASE__EVENT_HANDLER_MAP);

        specCategoryEClass = createEClass(SPEC_CATEGORY);
        createEReference(specCategoryEClass, SPEC_CATEGORY__TEST_CASES);
        createEReference(specCategoryEClass, SPEC_CATEGORY__CATEGORIES);

        testCaseRefEClass = createEClass(TEST_CASE_REF);
        createEReference(testCaseRefEClass, TEST_CASE_REF__TEST_CASE);
        createEReference(testCaseRefEClass, TEST_CASE_REF__COMPONENT_REFS);

        testStepEClass = createEClass(TEST_STEP);
        createEReference(testStepEClass, TEST_STEP__COMPONENT);

        testCaseChildEClass = createEClass(TEST_CASE_CHILD);
        createEReference(testCaseChildEClass, TEST_CASE_CHILD__VALUE_PROVIDER);

        testJobEClass = createEClass(TEST_JOB);
        createEReference(testJobEClass, TEST_JOB__TEST_SUITE_REFS);

        testSuiteEClass = createEClass(TEST_SUITE);
        createEReference(testSuiteEClass, TEST_SUITE__TEST_CASE_REFS);
        createEReference(testSuiteEClass, TEST_SUITE__AUT);

        testSuiteRefEClass = createEClass(TEST_SUITE_REF);
        createEReference(testSuiteRefEClass, TEST_SUITE_REF__TEST_SUITE);

        execCategoryEClass = createEClass(EXEC_CATEGORY);
        createEReference(execCategoryEClass, EXEC_CATEGORY__CATEGORIES);
        createEReference(execCategoryEClass, EXEC_CATEGORY__TEST_JOBS);
        createEReference(execCategoryEClass, EXEC_CATEGORY__TEST_SUITES);

        autEClass = createEClass(AUT);
        createEReference(autEClass, AUT__CONFIGS);
        createEReference(autEClass, AUT__UI_CATEGORY);

        autConfigEClass = createEClass(AUT_CONFIG);
        createEReference(autConfigEClass, AUT_CONFIG__PROPERTIES);

        uiComponentEClass = createEClass(UI_COMPONENT);
        createEReference(uiComponentEClass, UI_COMPONENT__COMPONENTS);

        compCategoryEClass = createEClass(COMP_CATEGORY);
        createEReference(compCategoryEClass, COMP_CATEGORY__CATEGORIES);
        createEReference(compCategoryEClass, COMP_CATEGORY__COMPONENTS);

        componentEClass = createEClass(COMPONENT);

        componentRefEClass = createEClass(COMPONENT_REF);
        createEReference(componentRefEClass, COMPONENT_REF__COMPONENT);
        createEReference(componentRefEClass, COMPONENT_REF__OVERRIDE);

        uiCategoryEClass = createEClass(UI_CATEGORY);
        createEReference(uiCategoryEClass, UI_CATEGORY__UI_COMPONENTS);
        createEReference(uiCategoryEClass, UI_CATEGORY__CATEGORIES);

        parameterEClass = createEClass(PARAMETER);

        languageEClass = createEClass(LANGUAGE);

        valueProviderEClass = createEClass(VALUE_PROVIDER);

        localValueProviderEClass = createEClass(LOCAL_VALUE_PROVIDER);
        createEReference(localValueProviderEClass, LOCAL_VALUE_PROVIDER__VALUES);

        valueEClass = createEClass(VALUE);
        createEReference(valueEClass, VALUE__LANGUAGE);
        createEAttribute(valueEClass, VALUE__TEXT);
        createEReference(valueEClass, VALUE__PARAMETER);

        excelValueProviderEClass = createEClass(EXCEL_VALUE_PROVIDER);
        createEAttribute(excelValueProviderEClass, EXCEL_VALUE_PROVIDER__FILE_NAME);

        refValueProviderEClass = createEClass(REF_VALUE_PROVIDER);
        createEReference(refValueProviderEClass, REF_VALUE_PROVIDER__TEST_CASE);

        ctdValueProviderEClass = createEClass(CTD_VALUE_PROVIDER);
        createEReference(ctdValueProviderEClass, CTD_VALUE_PROVIDER__CTD_SET);

        ctdSetEClass = createEClass(CTD_SET);
        createEReference(ctdSetEClass, CTD_SET__VALUE_PROVIDER);
        createEReference(ctdSetEClass, CTD_SET__PARAMETERS);

        ctdCategoryEClass = createEClass(CTD_CATEGORY);
        createEReference(ctdCategoryEClass, CTD_CATEGORY__CATEGORIES);
        createEReference(ctdCategoryEClass, CTD_CATEGORY__CTD_SETS);

        eventHandlerEClass = createEClass(EVENT_HANDLER);
        createEAttribute(eventHandlerEClass, EVENT_HANDLER__REENTRY_TYPE);

        handlerEntryEClass = createEClass(HANDLER_ENTRY);
        createEAttribute(handlerEntryEClass, HANDLER_ENTRY__KEY);
        createEReference(handlerEntryEClass, HANDLER_ENTRY__VALUE);

        namedElementEClass = createEClass(NAMED_ELEMENT);
        createEAttribute(namedElementEClass, NAMED_ELEMENT__NAME);

        // Create enums
        eventTypeEEnum = createEEnum(EVENT_TYPE);
        reentryTypeEEnum = createEEnum(REENTRY_TYPE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        projectEClass.getESuperTypes().add(this.getNamedElement());
        testCaseEClass.getESuperTypes().add(this.getNamedElement());
        specCategoryEClass.getESuperTypes().add(this.getNamedElement());
        testCaseRefEClass.getESuperTypes().add(this.getTestCaseChild());
        testStepEClass.getESuperTypes().add(this.getTestCaseChild());
        testCaseChildEClass.getESuperTypes().add(this.getNamedElement());
        testJobEClass.getESuperTypes().add(this.getNamedElement());
        testSuiteEClass.getESuperTypes().add(this.getNamedElement());
        testSuiteRefEClass.getESuperTypes().add(this.getNamedElement());
        execCategoryEClass.getESuperTypes().add(this.getNamedElement());
        autEClass.getESuperTypes().add(this.getNamedElement());
        autConfigEClass.getESuperTypes().add(this.getNamedElement());
        compCategoryEClass.getESuperTypes().add(this.getNamedElement());
        componentEClass.getESuperTypes().add(this.getNamedElement());
        uiCategoryEClass.getESuperTypes().add(this.getNamedElement());
        parameterEClass.getESuperTypes().add(this.getNamedElement());
        localValueProviderEClass.getESuperTypes().add(this.getValueProvider());
        excelValueProviderEClass.getESuperTypes().add(this.getValueProvider());
        refValueProviderEClass.getESuperTypes().add(this.getValueProvider());
        ctdValueProviderEClass.getESuperTypes().add(this.getValueProvider());
        ctdSetEClass.getESuperTypes().add(this.getNamedElement());
        ctdCategoryEClass.getESuperTypes().add(this.getNamedElement());
        eventHandlerEClass.getESuperTypes().add(this.getTestCaseRef());

        // Initialize classes and features; add operations and parameters
        initEClass(projectEClass, Project.class, "Project", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getProject_Languages(), this.getLanguage(), null, "languages", null, 1, -1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getProject_CompCategory(), this.getCompCategory(), null, "compCategory", null, 1, 1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getProject_SpecCategory(), this.getSpecCategory(), null, "specCategory", null, 1, 1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getProject_ExecCategory(), this.getExecCategory(), null, "execCategory", null, 1, 1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getProject_CtdCategory(), this.getCTDCategory(), null, "ctdCategory", null, 1, 1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getProject_AUTs(), this.getAUT(), null, "AUTs", null, 0, -1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(testCaseEClass, TestCase.class, "TestCase", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTestCase_Children(), this.getTestCaseChild(), null, "children", null, 0, -1, TestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTestCase_Parameters(), this.getParameter(), null, "parameters", null, 0, -1, TestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTestCase_DefaultValueProvider(), this.getValueProvider(), null, "defaultValueProvider", null, 0, 1, TestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTestCase_EventHandlerMap(), this.getHandlerEntry(), null, "eventHandlerMap", null, 0, -1, TestCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(specCategoryEClass, SpecCategory.class, "SpecCategory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getSpecCategory_TestCases(), this.getTestCase(), null, "testCases", null, 0, -1, SpecCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getSpecCategory_Categories(), this.getSpecCategory(), null, "categories", null, 0, -1, SpecCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(testCaseRefEClass, TestCaseRef.class, "TestCaseRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTestCaseRef_TestCase(), this.getTestCase(), null, "testCase", null, 1, 1, TestCaseRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTestCaseRef_ComponentRefs(), this.getComponentRef(), null, "componentRefs", null, 0, -1, TestCaseRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(testStepEClass, TestStep.class, "TestStep", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTestStep_Component(), this.getComponent(), null, "component", null, 1, 1, TestStep.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(testCaseChildEClass, TestCaseChild.class, "TestCaseChild", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTestCaseChild_ValueProvider(), this.getValueProvider(), null, "valueProvider", null, 0, 1, TestCaseChild.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(testJobEClass, TestJob.class, "TestJob", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTestJob_TestSuiteRefs(), this.getTestSuiteRef(), null, "testSuiteRefs", null, 0, -1, TestJob.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(testSuiteEClass, TestSuite.class, "TestSuite", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTestSuite_TestCaseRefs(), this.getTestCaseRef(), null, "testCaseRefs", null, 0, -1, TestSuite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTestSuite_AUT(), this.getAUT(), null, "AUT", null, 1, 1, TestSuite.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(testSuiteRefEClass, TestSuiteRef.class, "TestSuiteRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTestSuiteRef_TestSuite(), this.getTestSuite(), null, "testSuite", null, 1, 1, TestSuiteRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(execCategoryEClass, ExecCategory.class, "ExecCategory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getExecCategory_Categories(), this.getExecCategory(), null, "categories", null, 0, -1, ExecCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getExecCategory_TestJobs(), this.getTestJob(), null, "testJobs", null, 0, -1, ExecCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getExecCategory_TestSuites(), this.getTestSuite(), null, "testSuites", null, 0, -1, ExecCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(autEClass, org.eclipse.jubula.AUT.class, "AUT", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getAUT_Configs(), this.getAUTConfig(), null, "configs", null, 0, -1, org.eclipse.jubula.AUT.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getAUT_UiCategory(), this.getUICategory(), null, "uiCategory", null, 1, 1, org.eclipse.jubula.AUT.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(autConfigEClass, AUTConfig.class, "AUTConfig", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getAUTConfig_Properties(), ecorePackage.getEStringToStringMapEntry(), null, "properties", null, 0, -1, AUTConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(uiComponentEClass, UIComponent.class, "UIComponent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getUIComponent_Components(), this.getComponent(), null, "components", null, 0, -1, UIComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(compCategoryEClass, CompCategory.class, "CompCategory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getCompCategory_Categories(), this.getCompCategory(), null, "categories", null, 0, -1, CompCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCompCategory_Components(), this.getComponent(), null, "components", null, 0, -1, CompCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(componentEClass, Component.class, "Component", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(componentRefEClass, ComponentRef.class, "ComponentRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getComponentRef_Component(), this.getComponent(), null, "component", null, 1, 1, ComponentRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getComponentRef_Override(), this.getComponent(), null, "override", null, 0, 1, ComponentRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(uiCategoryEClass, UICategory.class, "UICategory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getUICategory_UiComponents(), this.getUIComponent(), null, "uiComponents", null, 0, -1, UICategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getUICategory_Categories(), this.getUICategory(), null, "categories", null, 0, -1, UICategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(languageEClass, Language.class, "Language", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(valueProviderEClass, ValueProvider.class, "ValueProvider", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        EOperation op = addEOperation(valueProviderEClass, ecorePackage.getEString(), "getValues", 1, -1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getParameter(), "parameter", 1, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getLanguage(), "language", 1, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(localValueProviderEClass, LocalValueProvider.class, "LocalValueProvider", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getLocalValueProvider_Values(), this.getValue(), null, "values", null, 1, -1, LocalValueProvider.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(valueEClass, Value.class, "Value", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getValue_Language(), this.getLanguage(), null, "language", null, 1, 1, Value.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getValue_Text(), ecorePackage.getEString(), "text", null, 1, 1, Value.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getValue_Parameter(), this.getParameter(), null, "parameter", null, 0, 1, Value.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(excelValueProviderEClass, ExcelValueProvider.class, "ExcelValueProvider", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getExcelValueProvider_FileName(), ecorePackage.getEString(), "fileName", null, 0, 1, ExcelValueProvider.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(refValueProviderEClass, RefValueProvider.class, "RefValueProvider", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getRefValueProvider_TestCase(), this.getTestCase(), null, "testCase", null, 1, 1, RefValueProvider.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

        initEClass(ctdValueProviderEClass, CTDValueProvider.class, "CTDValueProvider", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getCTDValueProvider_CtdSet(), this.getCTDSet(), null, "ctdSet", null, 1, 1, CTDValueProvider.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(ctdSetEClass, CTDSet.class, "CTDSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getCTDSet_ValueProvider(), this.getLocalValueProvider(), null, "valueProvider", null, 1, 1, CTDSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCTDSet_Parameters(), this.getParameter(), null, "parameters", null, 0, -1, CTDSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(ctdCategoryEClass, CTDCategory.class, "CTDCategory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getCTDCategory_Categories(), this.getCTDCategory(), null, "categories", null, 0, -1, CTDCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCTDCategory_CtdSets(), this.getCTDSet(), null, "ctdSets", null, 0, -1, CTDCategory.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(eventHandlerEClass, EventHandler.class, "EventHandler", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEventHandler_ReentryType(), this.getReentryType(), "reentryType", null, 1, 1, EventHandler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(handlerEntryEClass, Map.Entry.class, "HandlerEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getHandlerEntry_Key(), this.getEventType(), "key", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getHandlerEntry_Value(), this.getEventHandler(), null, "value", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(namedElementEClass, NamedElement.class, "NamedElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getNamedElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, NamedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize enums and add enum literals
        initEEnum(eventTypeEEnum, EventType.class, "EventType");
        addEEnumLiteral(eventTypeEEnum, EventType.COMPONENT_NOT_FOUND);
        addEEnumLiteral(eventTypeEEnum, EventType.ACTION_ERROR);
        addEEnumLiteral(eventTypeEEnum, EventType.CONFIGURATION_ERROR);
        addEEnumLiteral(eventTypeEEnum, EventType.CHECK_FAILED);

        initEEnum(reentryTypeEEnum, ReentryType.class, "ReentryType");
        addEEnumLiteral(reentryTypeEEnum, ReentryType.PAUSE);
        addEEnumLiteral(reentryTypeEEnum, ReentryType.CONTINUE);
        addEEnumLiteral(reentryTypeEEnum, ReentryType.BREAK);
        addEEnumLiteral(reentryTypeEEnum, ReentryType.RETRY);

        // Create resource
        createResource(eNS_URI);
    }

} //JubulaPackageImpl
