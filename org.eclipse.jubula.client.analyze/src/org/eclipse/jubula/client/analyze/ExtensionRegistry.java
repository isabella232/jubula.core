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
package org.eclipse.jubula.client.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.client.analyze.constants.AnalyzeConstants;
import org.eclipse.jubula.client.analyze.internal.Analyze;
import org.eclipse.jubula.client.analyze.internal.AnalyzeParameter;
import org.eclipse.jubula.client.analyze.internal.Category;
import org.eclipse.jubula.client.analyze.internal.Context;
import org.eclipse.jubula.client.analyze.internal.Renderer;
import org.eclipse.jubula.tools.constants.DebugConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author volker
 * 
 */
public class ExtensionRegistry {

    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(ExtensionRegistry.class);

    /** singleton */
    private static ExtensionRegistry plugin;

    /** HashMap of loaded Analyzes */
    private static HashMap<String, Analyze> analyzeMap = 
            new HashMap<String, Analyze>();

    /** HashMap of registered Renderer */
    private static HashMap<String, Renderer> rendererMap = 
            new HashMap<String, Renderer>();

    /** HashMap of registered Categories */
    private static HashMap<String, Category> categoryMap = 
            new HashMap<String, Category>();

    /** HashMap of registered Contexts */
    private static HashMap<String, Context> contextMap = 
            new HashMap<String, Context>();

    /** Constructor */
    private ExtensionRegistry() {
    }

    /**
     * @return unmodifiable Map of registered Analyzes
     */
    public static Map<String, Analyze> getAnalyze() {
        return Collections.unmodifiableMap(analyzeMap);
    }

    /**
     * 
     * @return unmodifiable Map of registered Renderer
     */
    public static Map<String, Renderer> getRenderer() {
        return Collections.unmodifiableMap(rendererMap);
    }

    /**
     * @return unmodifiable Map of registered Categories
     */
    public static Map<String, Category> getCategory() {
        return Collections.unmodifiableMap(categoryMap);
    }

    /**
     * @return unmodifiable Map of registered Contexts
     */
    public static Map<String, Context> getContexts() {
        return Collections.unmodifiableMap(contextMap);
    }

    /**
     * @return The Instance of the Plugin
     */
    public static ExtensionRegistry getInstance() {
        if (plugin == null) {
            plugin = new ExtensionRegistry();
        }
        return plugin;
    }

    /**
     * @return all of the extensions my extension point provides
     */
    private static IExtension[] getExtensions() {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = extensionRegistry
                .getExtensionPoint(AnalyzeConstants.EPDEF_ID);
        return extensionPoint.getExtensions();
    }

    /**
     * Initialization of all declared Analyzes
     */
    private static void initializeAnalyze() {
        for (IExtension extension : getExtensions()) {
            for (IConfigurationElement analyze : extension
                    .getConfigurationElements()) {
                if (AnalyzeConstants.ANA.equals(analyze.getName())) {
                    handleAnalyze(analyze);
                }
            }
        }
    }

    /**
     * Initialization of all declared ResultRenderer
     */
    private static void initializeResultRenderer() {
        for (IExtension extension : getExtensions()) {
            for (IConfigurationElement renderer : extension
                    .getConfigurationElements()) {
                if (AnalyzeConstants.RENDERER.equals(renderer.getName())) {
                    handleResultRenderer(renderer);
                }
            }
        }
    }

    /**
     * Initialization of all declared Categories
     */
    private static void initializeCategories() {
        for (IExtension extension : getExtensions()) {
            for (IConfigurationElement category : extension
                    .getConfigurationElements()) {
                if (AnalyzeConstants.CATEGORY.equals(category.getName())) {
                    handleCategories(category);
                }
            }
        }
    }

    /**
     * Initialization of all declared Contexts
     */
    private static void initializeContexts() {
        for (IExtension extension : getExtensions()) {
            for (IConfigurationElement context : extension
                    .getConfigurationElements()) {
                if (AnalyzeConstants.CONTEXT.equals(context.getName())) {
                    handleContexts(context);
                }
            }
        }
    }

    /**
     * loads all declared Analyzes with their AnalyzeParameters and fills the
     * different HashMaps
     * 
     * @param analyzeConfig
     *            the Analyze that is going to be handled
     */
    private static void handleAnalyze(IConfigurationElement analyzeConfig) {
        try {
            ArrayList<AnalyzeParameter> parameterList = 
                    new ArrayList<AnalyzeParameter>();
            // run over the AnalyzeParameter-children of the Analyze and write
            // them in the AnalyzeParameterMap
            for (IConfigurationElement param : analyzeConfig.getChildren()) {

                AnalyzeParameter anaParam = new AnalyzeParameter(
                        param.getAttribute(AnalyzeConstants.PARAM_ID),
                        param.getAttribute(
                                AnalyzeConstants.PARAM_DEFAULT_VALUE),
                        param.getAttribute(AnalyzeConstants.PARAM_NAME),
                        param.getAttribute(AnalyzeConstants.PARAM_DESCRIPTION),
                        "");
                parameterList.add(anaParam);
            }

            // write the just created createExecutableExtension and its id in
            //  the HashMap of the AnalzeCommandHandler
            Analyze ana = new Analyze(
                    analyzeConfig.getAttribute(AnalyzeConstants.ANA_ID),
                    analyzeConfig.getAttribute(AnalyzeConstants.ANA_NAME),
                    analyzeConfig.getAttribute(AnalyzeConstants.ANA_CLASS),
                    analyzeConfig.getAttribute(AnalyzeConstants.ANA_CAT_ID),
                    analyzeConfig
                            .getAttribute(AnalyzeConstants.ANA_CONTEXT_TYPE),
                    analyzeConfig
                            .getAttribute(AnalyzeConstants.ANA_RESULT_TYPE));

            ana.setExecutableExtension(analyzeConfig
                    .createExecutableExtension(AnalyzeConstants.ANA_CLASS));
            ana.setAnalyzeParameter(parameterList);

            // put the registered Analyze in the AnalyzeMap
            analyzeMap.put(analyzeConfig.getAttribute(AnalyzeConstants.ANA_ID),
                    ana);
        } catch (CoreException e) {
            log.error(DebugConstants.ERROR, e);
        }
    }

    /**
     * loads all declared ResultRenderer and fills the HashMap
     * 
     * @param rendererConfig
     *            the ResultRenderer that is going to be handled
     */
    private static void handleResultRenderer(
            IConfigurationElement rendererConfig) {

        try {
            Renderer renderer = new Renderer(
                    rendererConfig.getAttribute(AnalyzeConstants.RENDERER_ID),
                    rendererConfig
                            .getAttribute(AnalyzeConstants.RENDERER_CLASS),
                    rendererConfig
                            .getAttribute(
                                    AnalyzeConstants.RENDERER_RESULT_TYPE),
                    rendererConfig
                            .createExecutableExtension(
                                    AnalyzeConstants.RENDERER_CLASS));
            // put the registered Renderer in the RendererMap
            rendererMap.put(
                    rendererConfig.getAttribute(AnalyzeConstants.RENDERER_ID),
                    renderer);
        } catch (CoreException e) {
            log.error(DebugConstants.ERROR, e);
        }
    }

    /**
     * loads all declared Categories and fills the HashMap
     * 
     * @param categoryConfig
     *            the Category that is going to be handled
     */
    private static void handleCategories(IConfigurationElement categoryConfig) {

        Category category = new Category(
                categoryConfig.getAttribute(AnalyzeConstants.CATEGORY_ID),
                categoryConfig.getAttribute(AnalyzeConstants.CATEGORY_NAME),
                categoryConfig
                        .getAttribute(AnalyzeConstants.CATEGORY_TOP_CAT_ID));
        // put the registered Category in the CategoryMap
        categoryMap.put(
                categoryConfig.getAttribute(AnalyzeConstants.CATEGORY_ID),
                category);
    }

    /**
     * loads all declared Contexts and fills the Context HashMap
     * 
     * @param contextConfig
     *            the Category that is going to be handled
     */
    private static void handleContexts(IConfigurationElement contextConfig) {

        Context context;
        try {
            context = new Context(
                    contextConfig.getAttribute(AnalyzeConstants.CONTEXT_ID),
                    contextConfig.getAttribute(AnalyzeConstants.CONTEXT_NAME),
                    contextConfig.createExecutableExtension(
                                    AnalyzeConstants.CONTEXT_CLASS));
            // put the registered Context in the ContextMap
            contextMap.put(
                    contextConfig.getAttribute(AnalyzeConstants.CONTEXT_ID),
                    context);
        } catch (CoreException e) {
            log.error(DebugConstants.ERROR, e);
        }
    }

    /**
     * This method is called when the Bundle starts.
     */
    public void start() {

        initializeAnalyze();
        initializeResultRenderer();
        initializeCategories();
        initializeContexts();
    }
}
