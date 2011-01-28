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
package org.eclipse.jubula.client.core.businessprocess.problems;

/**
 * Describes a Problem of a Resource.
 * 
 * @author BREDEX GmbH
 * @created 24.01.2011
 */
public interface IProblem {

    /**
     * @return the message to display in the marker for this problem, or 
     *         <code>null</code> if no marker should be displayed.
     * @see #isWithMarker() 
     */
    String getMarkerMessage();

    /**
     * @return an internationalized message suitable for displaying as a 
     *         tooltip.
     */
    String getTooltipMessage();
    
    /**
     * @return the unique identifier of the plugin associated with this problem.
     */
    String getPlugin();

    /**
     * @return the severity of this problem, as defined in {@link IStatus}.
     */
    int getSeverity();

    /**
     * @return additional data associated with this problem, or 
     *         <code>null</code> if no additional data is associated with 
     *         this problem.
     */
    Object getData();
    
    /**
     * @return the type of this problem.
     */
    ProblemType getProblemType();
    
    /**
     * @return <code>true</code> when the problem should result in a marker in 
     *         the Problems View.
     * @see #getMarkerMessage()
     */
    boolean isWithMarker();
}
