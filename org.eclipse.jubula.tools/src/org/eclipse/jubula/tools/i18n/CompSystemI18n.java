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
package org.eclipse.jubula.tools.i18n;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jubula.tools.constants.StringConstants;

/**
 * Class to internationalize all compSystem strings
 * @author BREDEX GmbH
 * @created 02.01.2007
 */
public class CompSystemI18n {  
    /** the logger */
    private static Log log = LogFactory.getLog(CompSystemI18n.class);
    
    /** List of ResourceBundles */
    private static final List PLUGIN_BUNDLES = new LinkedList();
    
    
    /**
     * Constructor
     */
    private CompSystemI18n() {
        // private constructor to prevent instantiation of class utitlity
    }
    
    /**
     * Adds the given {@link ResourceBundle}
     * @param bundle a {@link ResourceBundle}
     */
    public static void addResourceBundle(ResourceBundle bundle) {
        if (bundle == null) {
            log.error("ResourceBundle is null!"); //$NON-NLS-1$
            return;
        }
        PLUGIN_BUNDLES.add(bundle);
    }
    
    
    
    /**
     * Gets the internationalized String by a given key.
     * @param key the key for the internationalized String.
     * @return a internationalized <code>String</code>.
     */
    public static String getString(String key) {
        if (StringConstants.EMPTY.equals(key)) {
            return key;
        }
        String str = key;
        try {
            str = getStringInternal(key);
        } catch (MissingResourceException mre) {
            logError(key, mre);
        }
        return str;
    }

    /**
     * Logs in error log
     * @param key the I18n-key
     * @param throwable the Throwable
     */
    private static void logError(String key, Throwable throwable) {
        log.error("Cannot find I18N-key in resource bundles: " + key, throwable); //$NON-NLS-1$
    }
    
    /**
     * Searches for th evalue of th egiven key in all bundles.<br>
     * throws MissingResourceException if the key was not found. 
     * @param key the key
     * @return the value for the given key
     */
    private static String getStringInternal(String key) {
        Iterator bundleIter = PLUGIN_BUNDLES.iterator();
        while (bundleIter.hasNext()) {
            ResourceBundle bundle = (ResourceBundle)bundleIter.next();
            try {
                return bundle.getString(key);
            } catch (MissingResourceException ex) {
                // ok here, we search in multiple bundles
            }
        }
        throw new MissingResourceException("No entry found for key: " + key, //$NON-NLS-1$
            CompSystemI18n.class.getName(), key);
    }
    
    /**
     * Gets the internationalized String by a given key.
     * @param key the key for the internationalized String.
     * @param fallBack returns the key if no value found 
     * @return a internationalized <code>String</code>.
     */
    public static String getString(String key, boolean fallBack) {
        if (key == null) {
            return StringConstants.EMPTY;
        }
        if (StringConstants.EMPTY.equals(key)) {
            return key;
        }
        String str = StringConstants.EMPTY;
        try {
            str = getStringInternal(key);
        } catch (MissingResourceException mre) {
            if (fallBack) {
                return key;
            }
            logError(key, mre);
        }
        return str;
    }
    
    /**
     * returns an internationalized string for the given key
     * @param key the key
     * @param args the arguments needed to generate the string
     * @return the internationalized string
     */
    public static String getString(String key, Object[] args) {
        if (StringConstants.EMPTY.equals(key)) {
            return key;
        }
        try {
            MessageFormat formatter =
                new MessageFormat(getStringInternal(key));
            return formatter.format(args);
        } catch (MissingResourceException e) {
            logError(key, e);
            StringBuffer buf = new StringBuffer(key);
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    buf.append(" "); //$NON-NLS-1$
                    buf.append(args[i]);
                }
            }
            return buf.toString();
        }
    }
    

    
    /**
     * Sets an new language for the I18N.
     * @param lang a <code>String</code> value. The new language.
     */
    public static void setLanguage(String lang) {
        Locale.setDefault(new Locale(lang, StringConstants.EMPTY));
    }
    
    /**
     * @return the Bundles.
     */
    public static List getPluginBundles() {
        return new ArrayList(PLUGIN_BUNDLES);
    }
    
    /**
     * 
     * @return a String repesentation of the ResourceBundles to use for
     * fromString(String string)
     * @see fromString(String string)
     */
    public static String bundlesToString() {
        final String keyValueSeparator = "="; //$NON-NLS-1$
        final String lineBreak = "\n"; //$NON-NLS-1$
        final StringBuffer entries = new StringBuffer();
        for (Iterator bundlesIt = PLUGIN_BUNDLES.iterator(); 
            bundlesIt.hasNext();) {
            
            final ResourceBundle bundle = (ResourceBundle)bundlesIt.next();
            for (Enumeration keys = bundle.getKeys(); keys.hasMoreElements();) {
                final String key = String.valueOf(keys.nextElement());
                final String value = bundle.getString(key);
                entries.append(key)
                        .append(keyValueSeparator)
                        .append(value)
                        .append(lineBreak);
            }
        }
        return entries.toString();
    }
    
    /**
     * Creates a ResourceBundle from the given String.<br>
     * The given String must have the specification of a Propeties-File:<br>
     * key=value<br>
     * key=value<br>
     * ...<br>
     * with a line break (\n) after every value.
     * @param string a String from bundleToString
     */
    public static void fromString(String string) {
        final ByteArrayInputStream stream = new ByteArrayInputStream(
            string.getBytes());
        try {
            final PropertyResourceBundle bundle = new PropertyResourceBundle(
                stream);
            PLUGIN_BUNDLES.clear();
            addResourceBundle(bundle);
        } catch (IOException e) {
            log.error(e);
        }
    }

}