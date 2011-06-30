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
package org.eclipse.jubula.rc.common.components;

/**
 * @author Markus Tiede
 * @created Jun 29, 2011
 */
public class FindComponentResult {
    /**
     * <code>m_technicalComponent</code>
     */
    private Object m_technicalComponent = null;

    /**
     * the <code>m_matchPercentage</code> when this component identifier has
     * been collected
     */
    private double m_matchPercentage = -1d;

    /**
     * the <code>m_numberOfOtherMatchingComponents</code> which may also be
     * likely to be found in future
     */
    private int m_numberOfOtherMatchingComponents = -1;

    /**
     * @param technicalComponent
     *            the technical component
     * @param matchPercentage
     *            the matching percen tage (equivalence)
     * @param noOfOtherComponents
     *            the number of other components which have been higher than the
     *            threshold value - also possible matches in future
     */
    public FindComponentResult(Object technicalComponent,
            double matchPercentage, int noOfOtherComponents) {
        setTechnicalComponent(technicalComponent);
        setMatchPercentage(matchPercentage);
        setNumberOfOtherMatchingComponents(noOfOtherComponents);
    }

    /**
     * @param technicalComponent
     *            the technicalComponent to set
     */
    private void setTechnicalComponent(Object technicalComponent) {
        m_technicalComponent = technicalComponent;
    }

    /**
     * @return the technicalComponent; may be <code>null</code>.
     */
    public Object getTechnicalComponent() {
        return m_technicalComponent;
    }

    /**
     * @param matchPercentage
     *            the matchPercentage to set
     */
    private void setMatchPercentage(double matchPercentage) {
        m_matchPercentage = matchPercentage;
    }

    /**
     * @return the matchPercentage
     */
    public double getMatchPercentage() {
        return m_matchPercentage;
    }

    /**
     * @param numberOfOtherMatchingComponents
     *            the numberOfOtherMatchingComponents to set
     */
    private void setNumberOfOtherMatchingComponents(
            int numberOfOtherMatchingComponents) {
        m_numberOfOtherMatchingComponents = numberOfOtherMatchingComponents;
    }

    /**
     * @return the numberOfOtherMatchingComponents
     */
    public int getNumberOfOtherMatchingComponents() {
        return m_numberOfOtherMatchingComponents;
    }
}
