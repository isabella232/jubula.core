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
package org.eclipse.jubula.client.analyze.constants;
/**
 * This class includes the constants for the ANALYZE framework.
 * @author volker
 */
public class AnalyzeConstants {

    
    /*
     * Constants for the ExtensionPoint Analyze
     */
    /** ID of the extension point definition */
    public static final String EPDEF_ID = "org.eclipse.jubula.client.analyze.definition"; //$NON-NLS-1$
    /** ID of the analyze Plugin */
    public static final String PLUGIN_ID = "org.eclipse.jubula.analyze"; //$NON-NLS-1$
    
    /*
     * Constants for the analyze-Element in the Extension
     */
    /** The analyze */
    public static final String ANA = "Analyze"; //$NON-NLS-1$ 
    /** The name */
    public static final String ANA_NAME = "name"; //$NON-NLS-1$ 
    /** The id */
    public static final String ANA_ID = "id"; //$NON-NLS-1$ 
    /** The class*/
    public static final String ANA_CLASS = "class"; //$NON-NLS-1$ 
    /** The CategoryID */
    public static final String ANA_CAT_ID = "CategoryID"; //$NON-NLS-1$ 
    /** The ContextType */
    public static final String ANA_CONTEXT_TYPE = "ContextType"; //$NON-NLS-1$ 
    /** The ResultType */
    public static final String ANA_RESULT_TYPE = "ResultType"; //$NON-NLS-1$ 
    
    /*
     * Constants for the AnalyzeParameter-Element in the Extension
     */
    /** The AnalyzeParameter */
    public static final String PARAM = "AnalyzeParameter"; //$NON-NLS-1$ 
    /** The AnalyzeParameterID */
    public static final String PARAM_ID = "id"; //$NON-NLS-1$
    /** The AnalyzeParamName */
    public static final String PARAM_NAME = "name"; //$NON-NLS-1$
    /** The AnalyzeParamDescription */
    public static final String PARAM_DESCRIPTION = "description"; //$NON-NLS-1$
    /** The AnalyzeParamDefaultVaule */
    public static final String PARAM_DEFAULT_VALUE = "defaultValue"; //$NON-NLS-1$
    
    /*
     * Constants for the Context-Element in the Extension
     */
    /** The Context */
    public static final String CONTEXT = "Context"; //$NON-NLS-1$
    /** The ContextID*/
    public static final String CONTEXT_ID = "id"; //$NON-NLS-1$
    /** The ContextName */
    public static final String CONTEXT_NAME = "name"; //$NON-NLS-1$
    /** The ContextClass */
    public static final String CONTEXT_CLASS = "class"; //$NON-NLS-1$

    /*
     * Constants for the ResultRenderer-Element in the Extension
     */
    /** The ResultRenderer */
    public static final String RENDERER = "ResultRenderer"; //$NON-NLS-1$ 
    /** The id */
    public static final String RENDERER_ID = "id"; //$NON-NLS-1$ 
    /** The ResultType */
    public static final String RENDERER_RESULT_TYPE = "ResultType"; //$NON-NLS-1$ 
    /** The class*/
    public static final String RENDERER_CLASS = "rendererClass"; //$NON-NLS-1$ 
    
    /*
     * Constants for the Category-Element in the Extension
     */
    /** The Category */
    public static final String CATEGORY = "Category"; //$NON-NLS-1
    /** The id */
    public static final String CATEGORY_ID = "id"; //$NON-NLS-1
    /** The name */
    public static final String CATEGORY_NAME = "name"; //$NON-NLS-1
    /** The id of the topLevelCategory */
    public static final String CATEGORY_TOP_CAT_ID = "categoryid"; //$NON-NLS-1
    
    /**
     * Empty constructor
     */
    private AnalyzeConstants() {
        
    }

}
