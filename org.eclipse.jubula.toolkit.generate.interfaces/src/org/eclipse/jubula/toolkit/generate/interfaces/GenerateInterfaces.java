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
package org.eclipse.jubula.toolkit.generate.interfaces;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jubula.toolkit.generate.interfaces.createinterfaces.BuildCompSystem;
import org.eclipse.jubula.toolkit.generate.interfaces.createinterfaces.LoadToolkitConfig;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.xml.businessmodell.Action;
import org.eclipse.jubula.tools.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.xml.businessmodell.Component;
import org.eclipse.jubula.tools.xml.businessmodell.ConcreteComponent;
import org.eclipse.jubula.tools.xml.businessmodell.Param;


/**
 * @author BREDEX GmbH
 * @created Nov 24, 2009
 */
public class GenerateInterfaces {
    /**
     * The Location for the Interfaces
     */
    private static String location = "interfaces"; //$NON-NLS-1$

    /**
     * The chosen Toolkit for the Interfaces
     */
    private static String chosenToolkit = StringConstants.EMPTY;

    /**
     * The Packagestructre for the interfaces
     */
    private static String packagestr = StringConstants.EMPTY;

    /**
     * choosed option for toolkitgeneration
     */
    private static Boolean alltoolkits = false;
    
    /**
     * The Propertyfile for the toolkit-specific interfaces 
     */
    private static final String PROPERTY_FILE = "interface.properties"; //$NON-NLS-1$
    
    /**
     * lists the generatable toolkits from the propertyfile
     */
    private static List<String> generatableTKList;

    /**
     * private constructor
     */
    private GenerateInterfaces() {
    // empty
    }

    /**
     * @param args
     *            java application arguments
     * @throws IOException 
     */
    @SuppressWarnings("nls")
    public static void main(String[] args) throws IOException {
        chooseToolkit(args);
        if (alltoolkits) {
            Configuration conf;
            try {
                URL prop = ClassLoader.getSystemResource(PROPERTY_FILE);
                conf = new PropertiesConfiguration(prop);
            } catch (ConfigurationException e) {
                throw new IllegalArgumentException(e);
            }
            generatableTKList = conf.getList("generatableToolkits");
            String[] possibleToolkits = new String[generatableTKList.size()];
            for (int i = 0; i < generatableTKList.size(); i++) {
                possibleToolkits[i] = generatableTKList.get(i).toLowerCase();
                getInterfaces(possibleToolkits[i]);
            }
        } else {
            System.out.println("Generating interfaces for following Toolkit: " 
                    + chosenToolkit);
            getInterfaceDatas();
        }
        System.out.println("Creating interfaces finished.");
    }

    /**
     * @param toolkit toolkit
     */
    @SuppressWarnings("nls")
    private static void readPropertyFile(String toolkit) {
        Configuration conf;
        try {
            URL prop = ClassLoader.getSystemResource(PROPERTY_FILE);
            conf = new PropertiesConfiguration(prop);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
        generatableTKList = conf.getList("generatableToolkits");
        String[] possibleToolkits = new String[generatableTKList.size()];
        Boolean status = false;
        for (int i = 0; i < generatableTKList.size(); i++) {
            possibleToolkits[i] = generatableTKList.get(i).toLowerCase();
            if (toolkit.matches(possibleToolkits[i])) {
                chosenToolkit = conf.getString(possibleToolkits[i] + "Toolkit");
                packagestr = conf.getString(possibleToolkits[i] + "Package");
                location = conf.getString(possibleToolkits[i] + "Location");
                status = true;
            }
        }
        if (!status) {
            System.out.println("Error occured: invalid toolkit parameter!");
            System.exit(1);
        }
    }
    
    /**
     * @param args java application arguments
     */
    @SuppressWarnings("nls")
    private static void chooseToolkit(String[] args) {
        if (args.length != 0 && args.length <= 2) {
            if (args.length <= 2) {
                if (args[0].matches("-toolkit")) {
                    if (args.length == 2) {
                        String toolkit = args[1].toLowerCase();
                        readPropertyFile(toolkit);
                    } else {
                        System.out.println("the toolkit parameter is missing");
                        System.exit(1);
                    }
                } else if (args[0].matches("-all")) {
                    alltoolkits = true;
                    System.out.println("Generate Interfaces for all Toolkits");
                } else {
                    System.out.println("invalid arguments");
                    System.exit(1);
                }
            } else {
                System.out.println("too many arguments.");
                System.exit(1);
            }
        } else {
            System.out.println("no arguments." 
                    + " Please insert arguments for choosing the toolkit");
            System.exit(1);
        }
    }

    /**
     * 
     * @param toolkit toolkit
     * @throws IOException
     */
    private static void getInterfaces(String toolkit) throws IOException {
        readPropertyFile(toolkit);
        getInterfaceDatas();
    }
    
    /**
     * @throws IOException
     */
    @SuppressWarnings("nls")
    private static void getInterfaceDatas() throws IOException {
        CompSystem c = new BuildCompSystem(new LoadToolkitConfig().
                loadConfig()).getCompSystem();
        List<Component> clist = c.getComponents(chosenToolkit, true);
        for (Component cc0 : clist) {
            if (!cc0.isConcrete()) {
                continue;
            }
            ConcreteComponent cc = (ConcreteComponent)cc0;
            String cctype = cc.getTesterClass();
            String ccsingletype = cctype.substring(cctype.lastIndexOf("."));
            ccsingletype = ccsingletype.replace(".", StringConstants.EMPTY);
            String methodbegin = "    public ";
            StringBuilder finalline = new StringBuilder(10240);
            String methodtype = "void ";
            List<Action> alist = cc.getActions();
            for (Action aa : alist) {
                String method = aa.getMethod();
                if (aa.getPostExecutionCommand() != null
                        && method.contains("Value")) {
                    methodtype = "String ";
                } else {
                    methodtype = "void ";
                }
                String javadoc = StringConstants.EMPTY 
                    + "\n" + "\n" + "    /**";
                StringBuilder templine = new StringBuilder(1024);
                templine.append(methodbegin).append(methodtype).append(method).append("("); //$NON-NLS-1$
                List<Param> plist = aa.getParams();
                for (Param pp : plist) {
                    String paramtype = pp.getType();
                    String singleparamtype = paramtype.substring(paramtype
                            .lastIndexOf("."));
                    singleparamtype = singleparamtype.replace(".", 
                            StringConstants.EMPTY);
                    String paramname = pp.getName();
                    String singleparamname = paramname.substring(paramname
                            .lastIndexOf("."));
                    singleparamname = singleparamname.replace(".", 
                            StringConstants.EMPTY);
                    String firstletter = singleparamname.substring(0, 1);
                    firstletter = firstletter.toLowerCase();
                    String restparamname = singleparamname.substring(1);
                    singleparamname = firstletter + restparamname;
                    javadoc = javadoc + "\n     * @param " + singleparamname
                            + " " + singleparamname;
                    String params = "\n" + "        " + singleparamtype + " "
                            + singleparamname + ", ";
                    templine.append(params);
                }
                int pos = templine.lastIndexOf(",");
                if (pos != -1) {
                    templine.setLength(pos);
                }
                templine.append(");");
                String temp = templine.toString();
                if (temp.contains("Variable")) {
                    temp = temp.replaceAll("Variable", "String");
                }
                if (temp.contains("Integer")) {
                    temp = temp.replaceAll("Integer", "int");
                }
                if (temp.contains("Boolean")) {
                    temp = temp.replaceAll("Boolean", "boolean");
                }
                if (methodtype.matches("String ")) {
                    javadoc = javadoc + "\n     * @return value";
                }
                javadoc = javadoc + " */ \n";
                finalline.append(javadoc).append(temp);
            }
            createInterfaces(ccsingletype, packagestr, ccsingletype,
                    finalline.toString(), location);
        }
    }

    /**
     * @param componentType
     *            Type of Component
     * @param component
     *            component
     * @param packages
     *            package
     * @param line
     *            method
     * @param place
     *            location
     * @throws IOException
     */
    private static void createInterfaces(String componentType, String packages,
            String component, String line, String place) throws IOException {

        writeFile(createFile(componentType, place), packages, component, line);
    }

    /**
     * 
     * @param componentType
     *            Type of Component
     * @param place
     *            location
     * @return the created Interface-File
     * @throws IOException
     */
    @SuppressWarnings("nls")
    private static File createFile(String componentType, String place)
        throws IOException {
        File locationdir = new File(place);
        if (!locationdir.exists()) {
            locationdir.mkdir();
        }
        File iface = new File(place, "I" + componentType + ".java");
        return iface;
    }

    /**
     * 
     * @param output
     *            file
     * @param packages
     *            packagestruktur
     * @param component
     *            component
     * @param line
     *            method
     * @throws IOException
     */
    @SuppressWarnings("nls")
    private static void writeFile(File output, String packages,
            String component, String line) throws IOException {
        FileOutputStream fos = new FileOutputStream(output);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(bos);

        String javadocClass = "/** \n * @author "
                + System.getProperty("user.name") + "\n * @created "
                + new Date() + "\n */";
        dos.writeBytes("package " + packages + "\n" + "\n" + javadocClass
                + "\n" + "public interface " + "I" + component + " {");
        dos.writeBytes("\n" + line + "\n" + "\n");
        dos.writeBytes("}");
        closeOutputStream(fos, bos, dos);
    }

    /**
     * @param fos
     *            fos
     * @param bos
     *            bos
     * @param dos
     *            dos
     * @throws IOException
     */
    private static void closeOutputStream(FileOutputStream fos,
            BufferedOutputStream bos, DataOutputStream dos) throws IOException {
        dos.close();
        bos.close();
        fos.close();
    }

}
