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

import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTMainPO;

/**
 * Factory to create common problems and generic ones for external usage.
 * 
 * @author BREDEX GmbH
 * @created 24.01.2011
 */
public final class ProblemFactory {

    /** 
     * private constructor because its a utility class 
     */
    private ProblemFactory() {
        // no-op
    }

    /**
     * @param loc
     *            Locale for which the test data are incomplete.
     * @return A Problem that is representing missing test data for this local.
     */
    public static IProblem createIncompleteTestDataProblem(Locale loc) {
        return new Problem(Messages.ProblemIncompleteTestDataMarkerText, 
                new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
                        Messages.ProblemIncompleteTestDataMarkerTooltip),
                loc, ProblemType.REASON_TD_INCOMPLETE);
    }

    /**
     * 
     * @param aut
     *            AUT where the object mapping is incomplete
     * @return A problem which represents incomplete object mapping of this
     *         aut.
     */
    public static IProblem createIncompleteObjectMappingProblem(
            IAUTMainPO aut) {
        return new Problem(Messages.ProblemIncompleteObjectMappingMarkerText,
                new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
                        Messages.ProblemIncompleteObjectMappingMarkerTooltip), 
                aut, ProblemType.REASON_OM_INCOMPLETE);
    }

    /**
     * @return Problem that represents missing spectestcases.
     */
    public static IProblem createMissingReferencedSpecTestCasesProblem() {
        return new Problem(Messages.ProblemMissingReferencedTestCaseMarkerText,
                new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
                        Messages.ProblemMissingReferencedTestCaseMarkerTooltip),
                null, ProblemType.REASON_MISSING_SPEC_TC);
    }

    /**
     * @param status
     *            Status with which the problem will be intialized.
     * @return An instance of this problem
     */
    public static IProblem createExternalProblem(IStatus status) {
        return new Problem(null, status, null, 
                ProblemType.EXTERNAL);
    }

    /**
     * @param status
     *            Status with which the problem will be intialized.
     * @param markerMessage
     *            message of the marker
     * @return An instance of this problem which will create an marker when
     *         attached to a INodePO.
     */
    public static IProblem createExternalProblemWithMarker(
            IStatus status, String markerMessage) {
        return new Problem(markerMessage, status, null, 
                ProblemType.EXTERNAL);
    }

}
