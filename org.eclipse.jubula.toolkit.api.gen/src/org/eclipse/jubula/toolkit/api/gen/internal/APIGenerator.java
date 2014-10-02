/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.api.gen.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jubula.toolkit.api.gen.ClassGenerator;
import org.eclipse.jubula.toolkit.api.gen.FactoryGenerator;
import org.eclipse.jubula.tools.internal.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitConfig;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitInfo;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;

/**
 * Generates classes for components from comp system
 */
public class APIGenerator {
    
    /** class generator */
    private static ClassGenerator classGenerator =
            new ClassGenerator();
    
    /** factory generator */
    private static FactoryGenerator factoryGenerator =
            new FactoryGenerator();
    
    /** list containing all components with information for factory generation.
     *  will be reseted for each toolkit */
    private static List<FactoryInfo> componentList =
            new ArrayList<FactoryInfo>();
    
    /**
     * Constructor
     */
    private APIGenerator() {
        // hidden
    }

    /** 
     * main
     * @param args args
     */
    public static void main(String[] args) {
        ConfigLoader loader = ConfigLoader.getInstance();
        String generationBaseDir = loader.getGenerationDir();
        ToolkitConfig config = loader.getToolkitConfig();
        CompSystemProcessor processor = new CompSystemProcessor(config);
                
        List<ToolkitInfo> toolkitInfos = processor.getToolkitInfos();

        // Clean up
        for (ToolkitInfo tkInfo : toolkitInfos) {
            cleanUp(tkInfo, generationBaseDir);
        }
        
        // Generate classes and interfaces toolkit by toolkit
        for (ToolkitInfo tkInfo : toolkitInfos) {
            componentList.clear();
            List<ComponentInfo> compInfos = processor.getCompInfos(
                    tkInfo.getType(), tkInfo.getShortType(), false);
            for (ComponentInfo compInfo : compInfos) {
                Component component = compInfo.getComponent();
                // generate interface
                createClass(component, generationBaseDir, true);
                //generate implementation class
                createClass(component, generationBaseDir, false);
            }
            // Generate a component factory for each toolkit
            CompSystem compSystem = processor.getCompSystem();
            ToolkitDescriptor toolkitDesriptor = compSystem
                    .getToolkitDescriptor(tkInfo.getType());
            GenerationInfo tkGenInfo = new GenerationInfo(toolkitDesriptor,
                    componentList, compSystem);
            createFactory(tkGenInfo, generationBaseDir);
        }
    }

    /**
     * Deletes all generated content of a given toolkit
     * @param tkInfo the toolkit
     * @param generationBaseDirTemplate location of generated content
     */
    private static void cleanUp(ToolkitInfo tkInfo,
            String generationBaseDirTemplate) {
        
        String name = tkInfo.getShortType().toLowerCase()
                .replace("abstract", "base"); //$NON-NLS-1$ //$NON-NLS-2$
        String generationBaseDir = MessageFormat.format(
                generationBaseDirTemplate,
                new Object[] {name});
        File dir = new File(generationBaseDir);
        emptyDirectory(dir);
    }

    /**
     * Empties a directory recursively
     * @param dir the directory
     */
    private static void emptyDirectory(File dir) {
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
    }

    /** 
     * creates class for component
     * @param component the component
     * @param generationBaseDirTemplate directory for generation
     * @param generateInterface whether an interface should be generated
     */
    private static void createClass(Component component,
            String generationBaseDirTemplate, Boolean generateInterface) {
        GenerationInfo genInfo = new GenerationInfo(component,
                generateInterface);
        String path = genInfo.getDirectoryPath();
        String className = genInfo.getClassName();
        String generationBaseDir = MessageFormat.format(
                generationBaseDirTemplate,
                new Object[] {genInfo.getToolkitName()});
        File dir = new File(generationBaseDir + path);
        File file = new File(dir, className + ".java"); //$NON-NLS-1$
        String content = classGenerator.generate(genInfo);

        createFile(dir, file, content);
        
        if (!generateInterface && component.isVisible()) {
            componentList.add(new FactoryInfo(
                    genInfo.getClassName(),
                    genInfo.getFqClassName(),
                    genInfo.getFqInterfaceName(),
                    genInfo.hasDefaultMapping()));
        }
    }

    /** 
     * creates factory for toolkit
     * @param tkGenInfo the generation information for the toolkit
     * @param generationBaseDirTemplate directory for generation
     */
    private static void createFactory(GenerationInfo tkGenInfo,
            String generationBaseDirTemplate) {
        String path = tkGenInfo.getDirectoryPath();
        String className = tkGenInfo.getClassName();
        String generationBaseDir = MessageFormat.format(
                generationBaseDirTemplate,
                new Object[] {tkGenInfo.getToolkitName()});
        File dir = new File(generationBaseDir + path);
        File file = new File(dir, className + ".java"); //$NON-NLS-1$
        String content = factoryGenerator.generate(tkGenInfo);

        createFile(dir, file, content); 
    }

    /** creates a file with given content in a given directory
     * @param dir the directory
     * @param file the file
     * @param content the content
     */
    private static void createFile(File dir, File file, String content) {
        if (!file.exists()) {
            try {
                dir.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            try (FileOutputStream fop = new FileOutputStream(file)) {
                byte[] contentInBytes = content.getBytes();
                IOUtils.write(contentInBytes, fop);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: " + file.getName() + " already exists!"); //$NON-NLS-1$ //$NON-NLS-2$
            System.exit(1);
        }
    }
}