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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jubula.tools.internal.utils.generator.CompSystemProcessor;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitConfig;


/**
 * Starts the Tex generation.
 * @author BREDEX GmbH
 * @created 16.09.2005
 * @version $Revision: 13372 $
 */
public class TexGen {
    
    /**
     * Constructor.
     */
    private TexGen() {
        // private utility constructor
    }

    /**
     * @return The command line options
     */
    private static Options createOptions() {
        Options options = new Options();

        OptionBuilder.withArgName("generate type");  //$NON-NLS-1$
        OptionBuilder.withDescription("The type of output to generate (i.e. 'actions', 'errors'). Default: 'actions'"); //$NON-NLS-1$
        OptionBuilder.hasArg();
        // Not required. Default to type 'actions'.
        options.addOption(OptionBuilder.create("gt")); //$NON-NLS-1$
        
        OptionBuilder.withArgName("template directory"); //$NON-NLS-1$
        OptionBuilder
                .withDescription("The directory which contains the template files"); //$NON-NLS-1$
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        options.addOption(OptionBuilder.create("td")); //$NON-NLS-1$

        OptionBuilder.withArgName("output directory"); //$NON-NLS-1$
        OptionBuilder.withDescription("The output directory"); //$NON-NLS-1$
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        options.addOption(OptionBuilder.create("od")); //$NON-NLS-1$

        OptionBuilder.withArgName("language"); //$NON-NLS-1$
        OptionBuilder.withDescription("The language, e.g. 'en' or 'de'"); //$NON-NLS-1$
        OptionBuilder.hasArg();
        OptionBuilder.isRequired(false);
        options.addOption(OptionBuilder.create("nl")); //$NON-NLS-1$
        return options;
    }

    /**
     * @param args The program arguments
     * @throws Exception If an error occurs
     */
    public static void main(String[] args) throws Exception {
        Options options = createOptions();
        CommandLine cl = null;
        try {
            CommandLineParser parser = new GnuParser();
            cl = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar texgen.jar", options, true); //$NON-NLS-1$
            return;
        }
        String templateDir = cl.getOptionValue("td"); //$NON-NLS-1$
        String outputDir = cl.getOptionValue("od"); //$NON-NLS-1$
        
        if (cl.hasOption("nl")) { //$NON-NLS-1$
            Locale.setDefault(new Locale(cl.getOptionValue("nl"))); //$NON-NLS-1$
        }
        
        String gType = "actions"; //$NON-NLS-1$
        if (cl.hasOption("gt")) { //$NON-NLS-1$
            gType = cl.getOptionValue("gt"); //$NON-NLS-1$
        }
        if (gType.equals("actions")) { //$NON-NLS-1$
            handleActions(templateDir, outputDir);
        } else if (gType.equals("errors")) { //$NON-NLS-1$
            handleErrors(templateDir, outputDir);
        }
    }

    /**
     * @param templateDir The directory containing the TeX templates
     * @param outputDir The directory where the output should go
     * @throws FileNotFoundException When a directory is not found
     * @throws IOException If there's a general input/output error
     */
    private static void handleErrors(String templateDir,
        String outputDir) throws FileNotFoundException, IOException {
        
        ErrorConfigLoader loader = new ErrorConfigLoader();
        for (ConfigGroup group : loader.getGroups()) {
            ErrorConfigGroupOutput output = new ErrorConfigGroupOutput(group,
                templateDir, outputDir);
            output.createOutput();
        } 
    }

    /**
     * @param templateDir The TeX templates
     * @param outputDir The output directory
     * @throws FileNotFoundException If the files are not found
     * @throws IOException For general i/o errors
     */
    private static void handleActions(String templateDir,
        String outputDir) 
        throws FileNotFoundException, IOException {

        ConfigLoader loader = ConfigLoader.getInstance();
        ToolkitConfig config = loader.getToolkitConfig();
        CompSystemProcessor processor = new CompSystemProcessor(config);
        for (ConfigGroup group : loader.getGroups()) {
            ConfigGroupOutput output = new ConfigGroupOutput(processor, group,
                    templateDir, outputDir);
            output.createOutput();
        }
    }
}