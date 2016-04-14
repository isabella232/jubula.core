/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.tester.adapter;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JSlider;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ISliderComponent;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
/**
 * Implementation of the Interface <code>ISliderAdapter</code> as a
 * adapter for the <code>JSlider</code> component.
 * @author BREDEX GmbH
 */
public class JSliderAdapter extends JComponentAdapter implements
        ISliderComponent {
    
    /** the actual slider */
    private JSlider m_slider;
    
    /**
     * @param objectToAdapt 
     */
    public JSliderAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_slider = (JSlider) objectToAdapt;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPosition(final String units) {
        return getEventThreadQueuer().invokeAndWait(
                "getPosition", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        double value;
                        int absValue = m_slider.getValue();
                        if (units.equalsIgnoreCase(
                                ValueSets.Measure.percent.rcValue())) {
                            value = 100 * absValue
                                    / (m_slider.getMaximum() - m_slider
                                            .getMinimum());
                        } else {
                            Dictionary labelFormatter =
                                    m_slider.getLabelTable();
                            if (labelFormatter != null) {
                                Object obj = labelFormatter.get(absValue);
                                if (obj != null && obj instanceof JLabel) {
                                    return ((JLabel) obj).getText();
                                }
                            }
                            value = absValue;
                        }
                        return String.valueOf(value);
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public void setPosition(final String position, final String operator,
            final String units) {
        getEventThreadQueuer().invokeAndWait(
                "setPosition", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() {
                        if (!m_slider.isEnabled()) {
                            throw new StepExecutionException(
                                    "The slider is not enabled", EventFactory //$NON-NLS-1$
                                    .createActionError("The slider is not enabled")); //$NON-NLS-1$
                        }
                        Integer value = null;
                        Dictionary<Integer, ?> labelTable =
                                m_slider.getLabelTable();
                        if (labelTable != null) {
                            Enumeration<Integer> keys = labelTable.keys();
                            MatchUtil matcher = MatchUtil.getInstance();
                            for (Integer k : Collections.list(keys)) {
                                Object o = labelTable.get(k);
                                String stringToMatch;
                                if (o instanceof JLabel) {
                                    stringToMatch = ((JLabel) o).getText();
                                } else {
                                    stringToMatch = String.valueOf(position);
                                }
                                if (matcher.match(stringToMatch,
                                        position, operator)) {
                                    value = k;
                                    break;
                                }
                            }
                            if (value == null) {
                                throw new StepExecutionException("Value not found", //$NON-NLS-1$
                                        EventFactory.createActionError(
                                                TestErrorEvent.NOT_FOUND));
                            }
                        } else {
                            try {
                                value = Integer.valueOf(position);
                            } catch (NumberFormatException nfe) {
                                throwInvalidInputMessage();
                            }
                        }
                        setValueProgrammatically(units, value);
                        return null;
                    }
                });
    }
    
    /**
     * @param units the units
     * @param value the value
     */
    private void setValueProgrammatically(final String units,
            int value) {
        final int valueToSet;
        if (units.equalsIgnoreCase(
                ValueSets.Measure.percent.rcValue())) {
            if (value < 0 || 100 < value) {
                throwInvalidInputMessage();
            }
            valueToSet = (int) (m_slider.getMinimum() + value
                    * ((m_slider.getMaximum() - m_slider.getMinimum())) * 0.01);
        } else {
            valueToSet = value;
        }
        m_slider.setValue(valueToSet);
    }
    
    /** throws invalid input message */
    private void throwInvalidInputMessage() {
        throw new StepExecutionException("Invalid input for slider", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_INPUT));
    }

}
