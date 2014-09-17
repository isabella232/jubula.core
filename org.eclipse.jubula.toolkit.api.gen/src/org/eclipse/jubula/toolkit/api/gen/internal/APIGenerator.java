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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jubula.toolkit.api.gen.ClassGenerator;
import org.eclipse.jubula.tools.internal.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitConfig;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitInfo;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;

/**
 * Generates classes for components from comp system
 */
public class APIGenerator {
    
    /** component generator */
    private static ClassGenerator classGenerator =
            new ClassGenerator();
    
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
        
        // Clean up
        for (ToolkitInfo tkInfo : processor.getToolkitInfos()) {
            cleanUp(tkInfo, generationBaseDir);
        }
        
        // Generate classes
        List<ComponentInfo> compInfos = processor.getCompInfos(false);
        for (ComponentInfo compInfo : compInfos) {
            Component component = compInfo.getComponent();
            createClass(component, generationBaseDir, true);
            createClass(component, generationBaseDir, false);
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