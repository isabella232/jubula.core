/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.editors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IObjectMappingProfilePO;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.databinding.InverseBooleanConverter;
import org.eclipse.jubula.client.ui.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.tools.i18n.I18n;
import org.eclipse.jubula.tools.xml.businessmodell.Profile;
import org.eclipse.jubula.tools.xml.businessprocess.ProfileBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;


/**
 * Component for selecting/modifying object mapping profiles.
 *
 * @author BREDEX GmbH
 * @created Nov 5, 2008
 */
public class ObjectMappingConfigComponent {

    /** the display string to represent a non-named profile */
    private static final String CUSTOM_NAME = "Custom"; //$NON-NLS-1$
    
    /**
     * Takes care of IJBEditor-specific steps when changing a value via a 
     * databinding. This includes requesting an editable state and marking the
     * editor as dirty, if necessary.
     *
     * @author BREDEX GmbH
     * @created Nov 24, 2008
     */
    private class JBEditorUpdateValueStrategy 
            extends UpdateValueStrategy {
        /** 
         * the editor within which this update value strategy does its work 
         */
        private IJBEditor m_editor; 
        
        /**
         * Constructor
         * 
         * @param editor The editor within which this update value strategy 
         *               does its work.
         */
        public JBEditorUpdateValueStrategy(IJBEditor editor) {
            super();
            m_editor = editor;
        }

        /**
         * Constructor
         * 
         * @param updatePolicy The updatePolicy to use.
         * @param editor The editor within which this update value strategy 
         *               does its work.
         */
        public JBEditorUpdateValueStrategy(int updatePolicy, IJBEditor editor) {
            super(updatePolicy);
            m_editor = editor;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        protected IStatus doSet(
                IObservableValue observableValue, Object value) {
            
            if (m_editor.getEditorHelper().requestEditableState() 
                    == EditableState.OK) {
                IStatus status = super.doSet(observableValue, value);
                if (!m_editor.isDirty()) {
                    m_editor.getEditorHelper().setDirty(true);
                }
                setComboValue();
                return status;
            }
            
            return Status.CANCEL_STATUS;
        }
        
        
    }
    
    /** 1 column */
    private static final int NUM_COLUMNS = 1;

    /** 10 horizontal spaces */
    private static final int HORIZONTAL_SPACING_10 = 10;

    /** 10 vertical spaces */
    private static final int VERTICAL_SPACING_10 = 10;

    /**
    * 100 %
    */
    private static final int HUNDRED_PERCENT = 200;
    
    /**
     * step
     */
    private static final int STEP = 1;

    /** 
     * the maximum number of digits displayable in labels corresponding to 
     * sliders
     */
    private static final int MAX_NUM_DIGITS = 5;

    /**
     * Databinding converter: Model => Label text
     *
     * @author BREDEX GmbH
     * @created Nov 19, 2008
     */
    private static class ModelToLabelConverter extends Converter {

        /** the string to use for formatting percentage label text */
        private static final String FORMAT_STRING = "%3.1f"; //$NON-NLS-1$
        
        /** 
         * the factor for converting from model percentage values to label
         * text values 
         */
        private static final int CONVERSION_FACTOR = 100;

        /**
         * Constructor
         */
        public ModelToLabelConverter() {
            super(double.class, String.class);
        }

        /**
         * 
         * {@inheritDoc}
         */
        public Object convert(Object fromObject) {
            Double fromDouble = (Double)fromObject;
            return String.format(FORMAT_STRING, 
                    fromDouble * CONVERSION_FACTOR);
        }
    }

    /**
     * Databinding converter: IObjectMappingProfilePO => UI component enablement
     *
     * @author BREDEX GmbH
     * @created Nov 24, 2008
     */
    private static class ModelToEnablementConverter extends Converter {

        /**
         * Constructor
         */
        public ModelToEnablementConverter() {
            super(IObjectMappingProfilePO.class, boolean.class);
        }

        /**
         * {@inheritDoc}
         */
        public Object convert(Object fromObject) {
            if (fromObject 
                    instanceof IObjectMappingProfilePO) {

                return ((IObjectMappingProfilePO)fromObject).getName() 
                    == null;
            }
            return true;
        }
        
    }

    /** 
     * reusable instance of converter, in order to avoid having to instantiate 
     * multiple converters
     */
    private ModelToEnablementConverter m_modelToEnablementConverter =
        new ModelToEnablementConverter();

    /** 
     * reusable instance of converter, in order to avoid having to instantiate 
     * multiple converters
     */
    private InverseBooleanConverter m_inverseBooleanConverter =
        new InverseBooleanConverter();
    /**
     * Databinding converter: Slider value (int) => Model value (double)
     *
     * @author BREDEX GmbH
     * @created Nov 24, 2008
     */
    private static class SliderToModelConverter extends Converter {

        /**
         * Constructor
         */
        public SliderToModelConverter() {
            super(0, 0.0);
        }

        /**
         * {@inheritDoc}
         */
        public Object convert(Object fromObject) {
            Object retVal = fromObject;
            if (retVal instanceof Integer) {
                retVal =
                    ((Integer)fromObject).doubleValue() / HUNDRED_PERCENT;
            }
            
            return retVal;
        }
        
    }
    
    /** 
     * reusable instance of converter, in order to avoid having to instantiate 
     * multiple converters
     */
    private SliderToModelConverter m_sliderToModelConverter =
        new SliderToModelConverter();

    /**
     * Databinding converter: Model value (double) => Slider value (int)
     *
     * @author BREDEX GmbH
     * @created Nov 24, 2008
     */
    private static class ModelToSliderConverter extends Converter {

        /**
         * @param fromType
         * @param toType
         */
        public ModelToSliderConverter() {
            super(double.class, int.class);
        }

        /**
         * {@inheritDoc}
         */
        public Object convert(Object fromObject) {
            Integer retVal = null;
            if (fromObject instanceof Double) {
                retVal =
                    (int)(((Double)fromObject) * HUNDRED_PERCENT);
            }
            
            return retVal;
        }
        
    }

    /** 
     * reusable instance of converter, in order to avoid having to instantiate 
     * multiple converters
     */
    private ModelToSliderConverter m_modelToSliderConverter = 
        new ModelToSliderConverter();
    
    /**
     * Textfield for m_nameFactor
     */
    private Label m_nameMalusText = null;

    /**
     * slider for recognitionParameter
     */
    private Scale m_threshold = null;
    /**
     * Textfield for m_nameFactor
     */
    private Label m_thresholdText = null;
    
    /**
     * combo for profiles
     */
    private Combo m_profileCombo = null;

    /**
     * composite for slider
     */
    private Composite m_sliderComposite;

    /** 
     * reusable instance of converter, in order to avoid having to instantiate 
     * multiple converters
     */
    private ModelToLabelConverter m_modelToLabelConverter = 
        new ModelToLabelConverter();

    /** Model */
    private IObjectMappingPO m_input;

    /** editor reference */
    private IJBEditor m_editor;

    /** used for triggered updates */
    private DataBindingContext m_bindingContext;

    /** observable for the editor input */
    private IObservableValue m_profileObservable; 
    
    /**
     * Constructor
     * 
     * @param parent The parent for the created controls.
     * @param input The initial value being edited by this component.
     * @param editor The editor supplying the data for this component.
     */
    public ObjectMappingConfigComponent(Composite parent, 
            IObjectMappingPO input, IJBEditor editor) {
        
        m_input = input;
        m_editor = editor;
        createAndInitControl(parent, input, editor);
    }
    
    /**
     * Creates all controls for this component and initializes data
     * binding.
     * 
     * @param parent The parent for the created controls.
     * @param input The initial value being edited by this component.
     * @param editor The editor supplying the data for this component.
     */
    private void createAndInitControl(
            Composite parent, IObjectMappingPO input, IJBEditor editor) {
        
        /** Add layer to parent widget */
        final ScrolledComposite scrollComposite = 
            new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gridData = 
            new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_BOTH);
        scrollComposite.setLayoutData(gridData);
        final Composite composite = new Composite(scrollComposite, SWT.NONE);

        m_bindingContext = new DataBindingContext();
        /** Define layout rules for widget placement */
        compositeGridData(composite);
        // add widgets to composite
        createProfileCombo(composite);

        m_sliderComposite = createSliderComposite(composite);
        Set<Scale> factorSliders = new HashSet<Scale>();
        m_profileObservable = PojoObservables.observeValue(input,
                IObjectMappingPO.PROP_PROFILE);
        factorSliders.add(createFactorSlider(m_sliderComposite,
                I18n.getString("ObjectMappingPreferencePage.pathFactor"), //$NON-NLS-1$
                IObjectMappingProfilePO.PROP_PATH_FACTOR, m_bindingContext,
                m_profileObservable, editor));
        factorSliders.add(createFactorSlider(m_sliderComposite,
                I18n.getString("ObjectMappingPreferencePage.nameFactor"), //$NON-NLS-1$
                IObjectMappingProfilePO.PROP_NAME_FACTOR, m_bindingContext,
                m_profileObservable, editor));
        factorSliders.add(createFactorSlider(m_sliderComposite,
                I18n.getString("ObjectMappingPreferencePage.contextFactor"), //$NON-NLS-1$
                IObjectMappingProfilePO.PROP_CONTEXT_FACTOR, m_bindingContext,
                m_profileObservable, editor));

        linkFactorSliders(m_bindingContext, factorSliders, editor);

        createThresholdSlider(m_sliderComposite, m_bindingContext,
                m_profileObservable, editor);

        m_bindingContext.updateTargets();

        // context sensitive help
        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.PREFPAGE_OBJECT_MAP);
        /** return the widget used as the base for the user interface */
        m_profileCombo.setSize(factorSliders.iterator().next().getSize().x,
                m_profileCombo.getSize().y);
        scrollComposite.setContent(composite);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.setMinSize(composite.computeSize(
                SWT.DEFAULT, SWT.DEFAULT));
        scrollComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                scrollComposite.setMinSize(composite.computeSize(
                        SWT.DEFAULT, SWT.DEFAULT));
            }
        });
    }

    /**
     * @param composite The parent for the slider composite.
     * @return The slider composite. This contains the sliders and related
     *         UI components for the editor.
     */
    private Composite createSliderComposite(final Composite composite) {
        Composite sliderComposite = new Composite(composite, SWT.BORDER);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = 4;
        GridData compositeData = new GridData(GridData.GRAB_HORIZONTAL 
            | GridData.FILL_BOTH
            | GridData.GRAB_VERTICAL);
        compositeLayout.horizontalSpacing = 5;
        compositeLayout.verticalSpacing = 5;
        compositeData.grabExcessHorizontalSpace = true;
        sliderComposite.setLayout(compositeLayout);
        sliderComposite.setLayoutData(compositeData);
      
        Label label = new Label(sliderComposite, SWT.CENTER);
        label = new Label(sliderComposite, SWT.CENTER);
        label = new Label(sliderComposite, SWT.CENTER);
        label.setText(" %");  //$NON-NLS-1$
        label = new Label(sliderComposite, SWT.NONE);
        label.setText(I18n.
            getString("ObjectMappingPreferencePage.lock"));  //$NON-NLS-1$
        
        return sliderComposite;
    }

    /**
     * 
     * @param bindingContext The context to use for databinding for all factor
     *                       slider.
     * @param factorSliders All sliders in the UI that correspond to an object
     *                      mapping profile "factor".
     * @param editor The editor containing the sliders.
     */
    private void linkFactorSliders(final DataBindingContext bindingContext, 
            final Set<Scale> factorSliders, final IJBEditor editor) {
        for (final Scale slider : factorSliders) {
            slider.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                    // Do nothing
                }

                public void widgetSelected(SelectionEvent e) {
                    if (editor.getEditorHelper().requestEditableState() 
                            == EditableState.OK) {
                        
                        checkSum(slider, factorSliders);
                        bindingContext.updateModels();
                        if (!editor.isDirty()) {
                            editor.getEditorHelper().setDirty(true);
                        }
                    }
                }
                
            });
        }
    }

    /**
     * @param parent The parent for the created UI elements.
     * @param labelText Text to use for the label for the created slider.
     * @param boundProperty The model property to use for databinding for
     *                      the created UI elements.
     * @param bindingContext The context to use for databinding for the created
     *                       UI elements.
     * @param masterObservable Observable for the master element for which these
     *                         UI elements serve as detail. Essentially, the
     *                         created UI elements represent detailed
     *                         information about this element.
     * @param editor The editor containing the slider.
     * @return the created slider.
     */
    private Scale createFactorSlider(Composite parent, String labelText, 
            String boundProperty, final DataBindingContext bindingContext,
            IObservableValue masterObservable, IJBEditor editor) {
        
        // create Widget
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        final Scale factorScale = new Scale(parent, SWT.NONE);
        factorScale.setMinimum(0);
        factorScale.setMaximum(HUNDRED_PERCENT);
        factorScale.setIncrement(STEP);
        factorScale.setPageIncrement(STEP);
        Label factorText = new Label(parent, SWT.NONE);
        setLabelWidth(factorText);
        Button lockCheckbox = new Button(parent, SWT.CHECK);
        bindFactor(boundProperty, bindingContext, factorScale, 
                factorText, lockCheckbox, masterObservable, editor);
        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);
        new Label(parent, SWT.NONE);
        
        return factorScale;
    }

    /**
     * 
     * @param boundProperty The model property to use for databinding for
     *                      the created UI elements.
     * @param bindingContext The context to use for databinding for the created
     *                       UI elements.
     * @param factorScale Slider to bind.
     * @param factorText Label to bind.
     * @param lockCheckbox Checkbox to bind.
     * @param masterObservable Observable for the master element for which these
     *                         UI elements serve as detail. Essentially, the
     *                         created UI elements represent detailed
     *                         information about this element.
     * @param editor The editor containing the factor UI elements.
     */
    private void bindFactor(String boundProperty,
            final DataBindingContext bindingContext, final Scale factorScale,
            Label factorText, Button lockCheckbox, 
            IObservableValue masterObservable, IJBEditor editor) {
        
        IObservableValue uiElement = 
            SWTObservables.observeSelection(factorScale);
        IObservableValue modelElement = 
            BeansObservables.observeDetailValue(masterObservable, 
                    boundProperty, 
                    double.class);

        bindingContext.bindValue(uiElement, modelElement, 
                new JBEditorUpdateValueStrategy(
                        UpdateValueStrategy.POLICY_ON_REQUEST, editor)
                            .setConverter(m_sliderToModelConverter),
                new UpdateValueStrategy()
                    .setConverter(m_modelToSliderConverter));
        uiElement = 
            SWTObservables.observeText(factorText);
        bindingContext.bindValue(uiElement, modelElement, 
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), 
                new UpdateValueStrategy().setConverter(
                        m_modelToLabelConverter));

        IObservableValue checkboxSelection = 
            SWTObservables.observeSelection(lockCheckbox);

        uiElement = SWTObservables.observeEnabled(factorScale);
        bindingContext.bindValue(uiElement, masterObservable, 
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), 
                new UpdateValueStrategy().setConverter(
                        m_modelToEnablementConverter));
        bindingContext.bindValue(uiElement, checkboxSelection, 
                new UpdateValueStrategy().setConverter(
                        m_inverseBooleanConverter), 
                new UpdateValueStrategy().setConverter(
                        m_inverseBooleanConverter));

        uiElement = SWTObservables.observeEnabled(factorText);
        bindingContext.bindValue(uiElement, masterObservable, 
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), 
                new UpdateValueStrategy().setConverter(
                        m_modelToEnablementConverter));
        bindingContext.bindValue(uiElement, checkboxSelection, 
                new UpdateValueStrategy().setConverter(
                        m_inverseBooleanConverter), 
                new UpdateValueStrategy().setConverter(
                        m_inverseBooleanConverter));

        uiElement = SWTObservables.observeEnabled(lockCheckbox);
        bindingContext.bindValue(uiElement, masterObservable, 
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), 
                new UpdateValueStrategy().setConverter(
                        m_modelToEnablementConverter));
        
    }

    /**
     * @param composite The composite.
     */
    private void compositeGridData(Composite composite) {
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS;
        compositeLayout.horizontalSpacing = HORIZONTAL_SPACING_10;
        compositeLayout.verticalSpacing = VERTICAL_SPACING_10;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = false;
        composite.setLayoutData(compositeData);
    }

    /**
     * @param parent Parent of this Combo.
     */
    private void createProfileCombo(Composite parent) {
        
        // create Widget
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = 2;
        composite.setLayout(compositeLayout);
        
        Label label = new Label(composite, SWT.NONE);
        label.setText(I18n.getString("ObjectMappingPreferencePage.profile")); //$NON-NLS-1$
        
        m_profileCombo = new Combo(composite, SWT.CHECK | SWT.READ_ONLY);
        m_profileCombo.setTextLimit(20);
        m_profileCombo.setItems(ProfileBuilder.getProfileNames());
        m_profileCombo.add(CUSTOM_NAME);
        
        setComboValue();
        
        m_profileCombo.addModifyListener(new ModifyListener() {
            
            @SuppressWarnings("synthetic-access")
            public void modifyText(ModifyEvent e) {
                if (m_editor.getEditorHelper().requestEditableState() 
                        == EditableState.OK) {
                    if (!m_editor.isDirty()) {
                        m_editor.getEditorHelper().setDirty(true);
                    }
                    String name = m_profileCombo.getText();
                    if (name != null) {
                        Profile p = ProfileBuilder.getProfile(name);
                        if (p != null) {
                            m_input.getProfile().useTemplate(p);
                        }
                    }
                }
            }
        });
        
    }

    /**
     * selects a combo entry depending on the profile values
     */
    @SuppressWarnings("unchecked")
    private void setComboValue() {
        List<Profile> profiles = ProfileBuilder.getProfiles();
        for (Profile profile : profiles) {
            if (m_input.getProfile().matchesTemplate(profile)) {
                int index = m_profileCombo.indexOf(profile.getName());
                if (index != -1) {
                    if (index != m_profileCombo.getSelectionIndex()) {
                        m_profileCombo.select(index);
                    }
                    return;
                }
            }
        }
        int index = m_profileCombo.indexOf("Custom"); //$NON-NLS-1$
        if (index != -1) {
            if (index != m_profileCombo.getSelectionIndex()) {
                m_profileCombo.select(index);
            }
            return;
        }
    }

    /**
     * Creates the labels and slider for the Threshold property as
     * well as the corresponding data bindings.
     * 
     * @param parent Parent of the created components.
     * @param bindingContext The data binding context.
     * @param masterObservable Observable value used to determine
     *                         which model object is currently
     *                         being observed in detail.
     * @param editor The editor that contains this slider.
     */
    private void createThresholdSlider(Composite parent,
            DataBindingContext bindingContext, 
            IObservableValue masterObservable, IJBEditor editor) {
        
        String boundProperty = 
            IObjectMappingProfilePO.PROP_THRESHOLD;
        // create Widget
        Label label = new Label(parent, SWT.NONE);
        label.setText(I18n.
            getString("ObjectMappingPreferencePage.threshold"));  //$NON-NLS-1$
        m_threshold = new Scale(parent, SWT.NONE);
        m_threshold.setMinimum(0);
        m_threshold.setMaximum(HUNDRED_PERCENT);
        m_threshold.setIncrement(STEP);
        m_threshold.setPageIncrement(STEP);
        m_thresholdText = new Label(parent, SWT.NONE);
        setLabelWidth(m_thresholdText);
        new Label(parent, SWT.NONE);

        ISWTObservableValue uiElement = 
            SWTObservables.observeSelection(m_threshold);
        IObservableValue modelElement = 
            BeansObservables.observeDetailValue(masterObservable, 
                    boundProperty, 
                    double.class);
        bindingContext.bindValue(uiElement, modelElement, 
                new JBEditorUpdateValueStrategy(editor)
                    .setConverter(m_sliderToModelConverter),
                new UpdateValueStrategy()
                    .setConverter(m_modelToSliderConverter));

        uiElement = 
            SWTObservables.observeText(m_thresholdText);
        bindingContext.bindValue(uiElement, modelElement, 
                new UpdateValueStrategy(
                        UpdateValueStrategy.POLICY_NEVER), 
                new UpdateValueStrategy().setConverter(
                        m_modelToLabelConverter));

        uiElement = SWTObservables.observeEnabled(m_threshold);
        bindingContext.bindValue(uiElement, masterObservable, 
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), 
                new UpdateValueStrategy().setConverter(
                        m_modelToEnablementConverter));

        uiElement = SWTObservables.observeEnabled(m_thresholdText);
        bindingContext.bindValue(uiElement, masterObservable, 
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), 
                new UpdateValueStrategy().setConverter(
                        m_modelToEnablementConverter));
    }

    /**
     * Sets the correct width and layout data for the given label. Assumes that
     * the parent uses a grid layout.
     * 
     * @param label The label for which to set the width and layout data.
     */
    private void setLabelWidth(Label label) {
        GC gc = new GC(label);
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gd.minimumWidth = 
            gc.getFontMetrics().getAverageCharWidth() * MAX_NUM_DIGITS;
        label.setLayoutData(gd);
        gc.dispose();
    }
    
    /**
     * checks and corrects the scales to not get over 100%
     * @param s
     *      Scale
     * @param factorSliders all sliders in the UI that correspond to an object
     *                      mapping profile "factor".
     */
    void checkSum(Scale s, Set<Scale> factorSliders) {
        while (getSum(factorSliders) > HUNDRED_PERCENT) {
            boolean didDecrement = false;
            for (Scale toDecrement : factorSliders) {
                if (getSum(factorSliders) > HUNDRED_PERCENT 
                        && toDecrement.isEnabled()
                        && !toDecrement.equals(s)
                        && toDecrement.getSelection() > 0) {
                    toDecrement.setSelection(toDecrement.getSelection() - STEP);
                    didDecrement = true;
                }
            }
            if (!didDecrement) {
                s.setSelection(s.getSelection() - STEP);
            }
        }
        while (getSum(factorSliders) < HUNDRED_PERCENT) {
            boolean didIncrement = false;
            for (Scale toIncrement : factorSliders) {
                if (getSum(factorSliders) < HUNDRED_PERCENT
                        && !s.equals(toIncrement) 
                    && toIncrement.isEnabled()
                    && toIncrement.getSelection() < HUNDRED_PERCENT) {
                    toIncrement.setSelection(toIncrement.getSelection() + STEP);
                    didIncrement = true;
                }
            }
            if (!didIncrement) {
                s.setSelection(s.getSelection() + STEP);
            }
        }
    }

    /**
     * 
     * @param factorSliders all sliders in the UI that correspond to an object
     *                      mapping profile "factor".
     * @return the sum of all factor scales 
     */
    public double getSum(Set<Scale> factorSliders) {
        double sum = 0;
        for (Scale slider : factorSliders) {
            if (slider != null && !slider.isDisposed()) {
                sum += slider.getSelection();
            }
        }

        return sum;
    }

    /**
     * sets the correct value into a scale
     * @param s
     *      Scale
     * @param d
     *      double
     */
    public void setValue(Scale s, double d) {
        s.setSelection((int)(HUNDRED_PERCENT * d));
    }

    /**
     * Sets the input for this component, updating all data bindings
     * to reflect this change.
     * 
     * @param input The new input to use for this component.
     */
    public void setInput(IObjectMappingPO input) {
        m_input = input;
        m_profileObservable.setValue(input.getProfile());
        m_bindingContext.updateTargets();
        setComboValue();
    }
}
