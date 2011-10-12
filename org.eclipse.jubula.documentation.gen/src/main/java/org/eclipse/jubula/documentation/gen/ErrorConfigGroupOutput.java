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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Performs the ouput operations for a given Tex generation group. The class
 * reads the template file, calls the Tex generators, and writes the generated
 * Tex code into the output files.
 * 
 * @author BREDEX GmbH
 * @created 23.09.2005
 * @version $Revision: 4085 $
 */
public class ErrorConfigGroupOutput {
    /**
     * The configuration group for Tex generation.
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
     * <code>m_processor</code>
     */
    private ErrorProcessor m_processor;

    /**
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
    public ErrorConfigGroupOutput(ConfigGroup group, String templateDir,
        String outputDir) throws FileNotFoundException, IOException {

        m_group = group;
        m_outputDir = outputDir;
        m_processor = new ErrorProcessor();

        FileReader reader = new FileReader(templateDir + File.separator
            + m_group.getTemplate());
        try {
            m_templateContent = ConfigGroupOutput.readFile(reader);
        } finally {
            ConfigGroupOutput.dropReader(reader);
        }
    }

    /**
     * Creates the output for all components for the given generation group
     * passed to the constructor.
     */
    public void createOutput() {
       // get the list from the processor
        List<ErrorInfo> infos = m_processor.getErrorInfos();
        // sort the list
        Collections.sort(infos);

        List<String> filenames = new ArrayList<String>(infos.size());

        for (ErrorInfo info : infos) {
            Generator generator = GeneratorFactory.create(m_group
                .getGeneratorClass(), m_processor, info, m_group);

                 /*
             * String ecFormatted = info.getErrorCode(); //Get rid of the
             * underscores in the filenames ecFormatted =
             * ecFormatted.replace('_', '-'); String outputFile =
             * MessageFormat.format(m_group.getOutput(), new Object[] {
             * ecFormatted });
             */
            int errorNumber = info.getKey();
            String outputFile = MessageFormat.format(m_group.getOutput(),
                new Object[] { String.valueOf(errorNumber) });

            // collect the filenames so that we can input them all from one
            // file.
            filenames.add(outputFile);

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(m_outputDir + File.separator
                    + outputFile);
                String content = MessageFormat.format(m_templateContent,
                    new Object[] { generator.generate() });
                fileWriter.write(content);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                ConfigGroupOutput.dropWriter(fileWriter);
            }

        }

        // output one file which will \input all error messages.
        String inputCommand = m_group.getProp("input"); //$NON-NLS-1$

        String outputAllFile = MessageFormat.format(m_group.getOutput(),
            new Object[] { "all" }); //$NON-NLS-1$
        FileWriter allWriter = null;
        String completeFilename = m_outputDir + File.separator + outputAllFile;
        try {
            allWriter = new FileWriter(completeFilename);
            for (String filename : filenames) {
                String content = MessageFormat.format(inputCommand,
                    new Object[] { filename });
                allWriter.write(content + '\n');
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            ConfigGroupOutput.dropWriter(allWriter);
        }
    }
}
