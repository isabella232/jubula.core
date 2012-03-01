/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.client.analyze.impl.standard.analyze.helper;

import org.eclipse.jubula.client.core.model.INodePO;

/**
 * A Helperclass to manage the Input for an Analyze
 * @author volker
 * 
 */
public class AnalyzeInputHelper {
    
    /** The node which is going to be the "RootNode" */
    private static INodePO root;
    
    /** Empty Constructor */
    private AnalyzeInputHelper() {
    }

    /**
     * @return The Node which is set as the "RootNode"
     */
    public static INodePO getNode() {
        return root;
    }

    /**
     * @param node The Node which is going to be set as the "RootNode"
     */
    public static void setNode(INodePO node) {
        root = node;
    }
}
