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
package org.eclipse.jubula.documentation.gen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.eclipse.jubula.tools.internal.utils.generator.ActionInfo;
import org.eclipse.jubula.tools.internal.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ComponentInfo;
import org.eclipse.jubula.tools.internal.utils.generator.IProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ParamInfo;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitInfo;


/**
 * Performs the output operations for a given Tex generation group. The class
 * reads the template file, calls the Tex generators, and writes the generated
 * Tex code into the output files.
 * 
 * @author BREDEX GmbH
 * @created 23.09.2005
 * @version $Revision: 12986 $
 */
public class ConfigGroupOutput {
    /**
     * The processor.
     */
    private IProcessor m_processor;

    /**
     * The configuration group of the Tex generation.
     */
    private ConfigGroup m_group;

    /**
     * The content of the read template file.
     */
    private String m_templateContent;

    /**
     * The output directory.
     */
    private String m_outputDir;

    /**
     * The directory for cap descriptions
     */
    private String m_descrDir;

    /**
     * @param processor
     *            The processor
     * @param group
     *            The Tex generation group
     * @param templateDir
     *            The directory of the template files
     * @param outputDir
     *            The output directory
     * @throws FileNotFoundException
     *             If a template file cannot be found
     * @throws IOException
     *             If reading a template file fails
     */
    public ConfigGroupOutput(CompSystemProcessor processor, ConfigGroup group,
            String templateDir, String outputDir) throws FileNotFoundException,
            IOException {

        m_processor = processor;
        m_group = group;
        m_outputDir = outputDir;
        m_descrDir = m_outputDir + File.separator
                + ".." + File.separator + "Descriptions"; //$NON-NLS-1$//$NON-NLS-2$

        FileReader reader = new FileReader(templateDir + File.separator
                + m_group.getTemplate());
        try {
            m_templateContent = readFile(reader);
        } finally {
            dropReader(reader);
        }
    }

    /**
     * @param reader Input source
     * @return a String with the content of reader
     * @throws IOException in case of errors
     */
    public static String readFile(FileReader reader) throws IOException {
        char buffer[] = new char[100];
        StringBuilder res = new StringBuilder();
        int len;
        while ((len = reader.read(buffer)) > 0) {
            res.append(buffer, 0, len);
        }
        return res.toString();
    }

    /**
     * Closes a FileReader without checking.
     * @param reader The FileReader to drop.
     */
    public static void dropReader(FileReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            // just ignore any error
        }
        
    }


    /**
     * Closes a FileWriter without checking.
     * @param writer The FileWrite to drop.
     */
    public static void dropWriter(FileWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            // just ignore any error
        }
        
    }

    /**
     * Creates the output for all components for the given generation group
     * passed to the constructor.
     */
    @SuppressWarnings("unchecked")
    public void createOutput() {
        if (m_group.getName().equals("CAPDescription")) { //$NON-NLS-1$
            // output for all CAP elements, recursuve generation
            handleCAPDescription();
        } else if (m_group.getName().equals("ComponentList")) { //$NON-NLS-1$
            // output for all components, single file.
            List<ToolkitInfo> toolkits = ((CompSystemProcessor)m_processor)
                    .getToolkitInfos();
            for (ToolkitInfo toolkit : toolkits) {
                String outputFile = m_group.getOutput();
                StringBuilder sb = new StringBuilder();
                List<ComponentInfo> comps = ((CompSystemProcessor)m_processor)
                        .getCompInfos(toolkit.getType(), toolkit.getI18nName());
                Collections.sort(comps);
                for (ComponentInfo info : comps) {
                    Generator generator = GeneratorFactory.create(m_group
                            .getGeneratorClass(), m_processor, info, m_group);
                    sb.append(generator.generate());
                }
                String content = MessageFormat.format(m_templateContent, sb
                        .toString());
                String toolkitOutputDir = getToolkitOutputDir(toolkit
                        .getShortType());
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(toolkitOutputDir
                            + File.separator + outputFile);
                    fileWriter.write(content);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } finally {
                    dropWriter(fileWriter);
                }
            }
        } else if (m_group.getName().equals("Toolkit")) { //$NON-NLS-1$
            List<ToolkitInfo> toolkits = ((CompSystemProcessor)m_processor)
                    .getToolkitInfos();
            for (ToolkitInfo info : toolkits) {
                Generator generator = GeneratorFactory.create(m_group
                        .getGeneratorClass(), m_processor, info, m_group);
                String shortType = info.getShortType();
                String outputFile = MessageFormat.format(m_group.getOutput(),
                        new Object[] { shortType });
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(m_outputDir + File.separator
                            + outputFile);
                    String content = MessageFormat.format(m_templateContent,
                            info.getI18nName(), generator.generate());
                    fileWriter.write(content);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } finally {
                    dropWriter(fileWriter);
                }
            }
        } else if (m_group.getName().equals("ToolkitList")) { //$NON-NLS-1$
            handleToolkitList();
        } else if (m_group.getName().equals("DeprecatedAction")) { //$NON-NLS-1$
            handleDeprecated();
        } else if (m_group.getName().equals("DeprecatedDescription")) { //$NON-NLS-1$
            handleDeprecatedDescription();
        } else {
            handleDefault();
        }
    }

    /**
     * 
     */
    private void handleDeprecated() {
        final String gdrefdepbegin = m_group.getProp("gdrefdepbegin"); //$NON-NLS-1$
        final String gdrefdepend = m_group.getProp("gdrefdepend"); //$NON-NLS-1$
        final String outputFile = m_group.getOutput();
        StringBuilder sb = new StringBuilder();
        List<ActionInfo> deprecated = ((CompSystemProcessor)m_processor)
            .getDeprecatedActions();
        // sort the actions
        Collections.sort(deprecated, new DeprecatedActionComparator());
        sb.append(gdrefdepbegin);
        for (ActionInfo dep : deprecated) {
            Generator generator = GeneratorFactory.create(m_group
                    .getGeneratorClass(), m_processor, dep, m_group);
            sb.append(generator.generate());
        }
        sb.append(gdrefdepend);
        String content = MessageFormat.format(m_templateContent, sb.toString());
        String filepath = m_outputDir + File.separator + outputFile;
        writeFile(filepath, content);        
    }
    


    /**
     * 
     */
    private void handleDeprecatedDescription() {
        String outputdir = m_descrDir + File.separator
            + m_group.getProp("outputdir"); //$NON-NLS-1$
        handleDir(outputdir);
        String output = m_group.getOutput();
        List<ActionInfo> deprecated = ((CompSystemProcessor)m_processor)
            .getDeprecatedActions();
        for (ActionInfo dep : deprecated) {
            ComponentInfo ci = dep.getContainerComp();
            ToolkitInfo ti = ci.getTkInfo();
            String filename = MessageFormat.format(output, ti.getShortType(),
                    ci.getShortType(), dep.getShortName());
            String filepath = outputdir
                + File.separator + filename;
            String content = MessageFormat.format(m_templateContent,
                    dep.getI18nName());
            writeFile(filepath, content, true);
        }
    }


    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private void handleToolkitList() {
        String outputFile = m_outputDir + File.separator + m_group.getOutput();
        List<ToolkitInfo> toolkits = ((CompSystemProcessor)m_processor)
            .getToolkitInfos();
        Collections.sort(toolkits);
        StringBuilder sb = new StringBuilder();
        for (ToolkitInfo info : toolkits) {
            Generator generator = GeneratorFactory.create(m_group
                    .getGeneratorClass(), m_processor, info, m_group);
            sb.append(generator.generate());
        }
        String content = MessageFormat.format(m_templateContent, sb
                .toString());
        writeFile(outputFile, content);
    }
    

    /**
     * @param filepath the file to output (including directory!)
     * @param content the content of the file
     * @param checkExistence whether to check for existence of file first
     */
    private void writeFile(String filepath, String content,
            boolean checkExistence) {
        File file = new File(filepath);
        // Only write this description file if it does not already exist,
        // otherwise documentor changes could be lost.
        if (!(checkExistence && file.exists())) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(filepath);
                fileWriter.write(content);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                dropWriter(fileWriter);
            }
        }

    }

    /**
     * @param outputFile the name of the file to be written
     * @param content the contents of the file to be written
     */
    private void writeFile(String outputFile, String content) {
        writeFile(outputFile, content, false);
    }

    /**
     * 
     */
    private void handleDefault() {
        // output for all components, multiple files.
        CompSystemProcessor processor = (CompSystemProcessor)m_processor;
        List<ToolkitInfo> toolkits = processor.getToolkitInfos();
        for (ToolkitInfo toolkit : toolkits) {
            List<ComponentInfo> comps = processor.getCompInfos(toolkit
                    .getType(), toolkit.getI18nName());
            for (ComponentInfo info : comps) {
                Generator generator = GeneratorFactory.create(m_group
                        .getGeneratorClass(), processor, info, m_group);
                String shortName = info.getShortType();
                String outputFile = MessageFormat.format(m_group
                        .getOutput(), new Object[] { shortName });
                FileWriter fileWriter = null;
                String toolkitOutputDir = getToolkitOutputDir(
                        toolkit.getShortType());
                try {
                    fileWriter = new FileWriter(toolkitOutputDir 
                            + File.separator + outputFile);
                    String content = MessageFormat.format(
                            m_templateContent, info.getI18nName(),
                            generator.generate());
                    fileWriter.write(content);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } finally {
                    dropWriter(fileWriter);
                }
            }
        }
    }

    /**
     * @param shortType
     *            the toolkit's short name
     * @return the output directory for the given toolkit
     */
    private String getToolkitOutputDir(String shortType) {
        String tOutputDir = ConfigLoader.getInstance().getToolkitConfig()
                .getOutputdir();
        String outputDir = m_outputDir + File.separator
                + MessageFormat.format(tOutputDir, shortType);
        handleDir(outputDir);
        return outputDir;
    }
    

    /**
     * Calls the generator for each CAP element and outputs to file.
     */
    @SuppressWarnings("unchecked")
    private void handleCAPDescription() {
        CompSystemProcessor processor = (CompSystemProcessor)m_processor;
        List<ToolkitInfo> tInfos = processor.getToolkitInfos();
        for (ToolkitInfo tInfo : tInfos) {
            String tkDir = m_descrDir + File.separator + tInfo.getShortType();
            String tkFilePart = "toolkit-" + tInfo.getShortType(); //$NON-NLS-1$
            String tkTemplParam = tInfo.getI18nName();
            
            handleDir(tkDir);
            
            capOutput(tkDir, tkFilePart, tkTemplParam);
            List<ComponentInfo> cInfos = processor
                .getCompInfos(tInfo.getType(), tInfo.getI18nName());
            for (ComponentInfo compInf : cInfos) {
                String filenamePart = "comp-" + compInf.getShortType(); //$NON-NLS-1$
                String compTemplParam = compInf.getI18nName();
                String compDir = tkDir + File.separator
                    + compInf.getShortType();

                handleDir(compDir);

                capOutput(compDir, filenamePart, compTemplParam);
                List<ActionInfo> actions = processor.getActions(compInf, true);
                for (ActionInfo ainfo : actions) {
                    String actionFilenamePart = "action-" //$NON-NLS-1$
                        + ainfo.getShortName();
                    String actionTemplParam = compTemplParam + " --> " //$NON-NLS-1$
                        + ainfo.getI18nName();
                    String actionDir = compDir + File.separator
                        + ainfo.getShortName();
                    handleDir(actionDir);
                    capOutput(actionDir, actionFilenamePart, actionTemplParam);
                    List<ParamInfo> params = ainfo.getParams();
                    for (ParamInfo pinfo : params) {
                        String paramFilenamePart = "param-" //$NON-NLS-1$
                            + pinfo.getShortName();
                        String paramTemplParam = actionTemplParam + " --> " //$NON-NLS-1$
                            + pinfo.getI18nName();
                        capOutput(actionDir, paramFilenamePart, 
                                paramTemplParam);
                    }
                }
            }
        }
    }

    /**
     * Creates directory if it doesn't exist yet. If a file exists with the name
     * of the desired dir, it is deleted.
     * 
     * @param strDir
     *            the directory to test
     */
    private void handleDir(String strDir) {
        File dir = new File(strDir);
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                System.err.println("File \"" + strDir //$NON-NLS-1$
                        + " should not be needed and will be deleted."); //$NON-NLS-1$
                dir.delete();
            }
        } else {
            dir.mkdir();
        }
    }

    /**
     * @param dirPath
     *            the directory to be written to
     * @param helpid
     *            the helpid of the cap element, used for filename
     * @param templParam
     *            parameter data used to format the template
     */
    private void capOutput(String dirPath, String helpid, String templParam) {
        String outputFile = MessageFormat.format(m_group.getOutput(), helpid);
        String filePath = dirPath + File.separator + outputFile;
        File file = new File(filePath);
        // Only write this description file if it does not already exist,
        // otherwise documentor changes could be lost.
        if (!file.exists()) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(dirPath + File.separator
                        + outputFile);
                String content = MessageFormat.format(m_templateContent,
                        templParam);
                fileWriter.write(content);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                dropWriter(fileWriter);
            }
        }
    }
}