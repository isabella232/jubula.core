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
package org.eclipse.jubula.client.api.converter.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.eclipse.jubula.client.api.converter.exceptions.InvalidNodeNameException;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.toolkit.api.gen.internal.genmodel.CommonGenInfo;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @created 28.10.2014
 */
public class Utils {
    
    /** specific path for executables */
    public static final String EXEC_PATH = "testsuites"; //$NON-NLS-1$
    
    /** specific path for specifications */
    public static final String SPEC_PATH = "testcases"; //$NON-NLS-1$

    /** class name pattern */
    private static final Pattern CLASS_NAME_PATTERN =
            Pattern.compile("^[A-Z][\\w]*$"); //$NON-NLS-1$

    /** package name pattern */
    private static final Pattern PACKAGE_NAME_PATTERN =
            Pattern.compile("^[a-z0-9_]*$"); //$NON-NLS-1$
    
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(Utils.class);
    
    /**
     * private constructor
     */
    private Utils() {
        // private
    }

    /** 
     * Returns the fully qualified name of a node involving
     * all of its super category names
     * @param node the node
     * @return the fully qualified name
     */
    public static String getFullyQualifiedName(INodePO node) {
        String name = node.getName();
        INodePO parentNode = node.getParentNode();
        if (parentNode != null) {
            name = getFullyQualifiedName(parentNode)
                    + StringConstants.SLASH + name;
        }
        return name;
    }

    /** 
     * Returns the fully qualified name of a node without package/project prefix
     * involving all of its super category names
     * @param node the node
     * @return the fully qualified name
     */
    private static String getFullyQualifiedTranslatedNameWithoutPrefix(
            INodePO node) {
        String name = StringConstants.EMPTY;
        try {
            if (node instanceof ICategoryPO) {
                name = translateToPackageName(node);
            } else {
                name = determineClassName(node);
            }
        } catch (InvalidNodeNameException e) {
            LOG.error(e.getLocalizedMessage());
        }
        INodePO parentNode = node.getParentNode();
        if (parentNode != null && parentNode.getParentNode() != null) {
            name = getFullyQualifiedTranslatedNameWithoutPrefix(parentNode)
                    + StringConstants.DOT + name;
        }
        return name;
    }
    
    /** 
     * Returns the fully qualified name of a node involving
     * all of its super category names
     * @param node the node
     * @param packageBasePath the package base path
     * @param projectName the name of the project
     * @return the fully qualified name
     */
    public static String getFullyQualifiedTranslatedName(
            INodePO node, String packageBasePath, String projectName) {
        String name = packageBasePath + StringConstants.DOT
                + projectName + StringConstants.DOT;
        if (node instanceof ISpecTestCasePO) {
            name = name + SPEC_PATH;
        } else {
            name = name + EXEC_PATH;
        }
        return name + StringConstants.DOT
                + getFullyQualifiedTranslatedNameWithoutPrefix(node);
    }

    /**
     * Translates a node to a valid java package name
     * @param node the category
     * @return the translated name
     */
    public static String translateToPackageName(INodePO node)
        throws InvalidNodeNameException {
        String name = node.getName();
        name = removeInvalidCharacters(name);
        name = name.toLowerCase();
        name = StringUtils.deleteWhitespace(name);
        name = name.replaceAll("^[0-9]*", StringConstants.EMPTY); //$NON-NLS-1$
        if (name.isEmpty()) {
            name = "invalid_package_name";
        }
        if (!PACKAGE_NAME_PATTERN.matcher(name).matches()) {
            throw new InvalidNodeNameException();
        }
        return name;
    }

    /**
     * Determines a valid Java class name for a given node
     * @param node the node
     * @return the class name
     * @throws InvalidNodeNameException if a node name cannot be translated to a Java class name
     */
    public static String determineClassName(INodePO node)
        throws InvalidNodeNameException {
        String name = node.getName();
        name = removeInvalidCharacters(name);
        name = WordUtils.capitalize(name);
        name = StringUtils.deleteWhitespace(name);
        name = name.replaceAll("^[0-9]*", StringConstants.EMPTY); //$NON-NLS-1$
        if (name.isEmpty()) {
            name = "InvalidClassName";
        }
        if (!CLASS_NAME_PATTERN.matcher(name).matches()) {
            throw new InvalidNodeNameException();
        }
        return name;
    }

    /**
     * Removes characters which are invalid in java qualifiers from a name
     * @param name the name
     * @return the adjusted name
     */
    private static String removeInvalidCharacters(String name) {
        String adjustedName = name;
        String [] invalidChars = new String [] {
            StringConstants.AMPERSAND,
            StringConstants.APOSTROPHE,
            StringConstants.BACKSLASH,
            StringConstants.COLON,
            StringConstants.COMMA,
            StringConstants.DOT,
            StringConstants.EQUALS_SIGN,
            StringConstants.EXCLAMATION_MARK,
            StringConstants.LEFT_BRACKET,
            StringConstants.LEFT_INEQUALITY_SING,
            StringConstants.LEFT_PARENTHESES,
            StringConstants.MINUS,
            StringConstants.PIPE,
            StringConstants.PLUS,
            StringConstants.QUESTION_MARK,
            StringConstants.QUOTE,
            StringConstants.RIGHT_BRACKET,
            StringConstants.RIGHT_INEQUALITY_SING,
            StringConstants.RIGHT_PARENTHESES,
            StringConstants.SEMICOLON,
            StringConstants.SLASH,
            StringConstants.STAR
        };
        for (String c : invalidChars) {
            adjustedName = adjustedName.replace(c, StringConstants.SPACE);
        }
        return adjustedName;
    }

    /**
     * Returns the factory name for a toolkit
     * @param toolkit the toolkit
     * @return the name of the factory
     */
    public static String getFactoryName(String toolkit) {
        String name;
        ToolkitDescriptor toolkitDescriptor =
                ComponentBuilder.getInstance().getCompSystem()
                .getToolkitDescriptor(toolkit);
        CommonGenInfo defaultFactoryInfo =
                new CommonGenInfo(toolkitDescriptor, false);
        name = defaultFactoryInfo.getFqClassName();
        return name;
    }
}
