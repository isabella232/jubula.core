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
package org.eclipse.jubula.client.core.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.tools.exception.GDException;
import org.eclipse.jubula.tools.exception.GDFatalException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;


/**
 * @author BREDEX GmbH
 * @created Aug 20, 2009
 */
public class DBSchemaPropertyCreator {
    /** standard logging */
    private static Log log = LogFactory.getLog(DBSchemaPropertyCreator.class);
    
    /**
     * location of the schema properties file
     */
    private static final String SCHEMA_PROPERTIES = "schema.properties"; //$NON-NLS-1$

    /**
     * location of the database properties file
     */
    private static final String DATABASE_PROPERTIES = "databases.properties"; //$NON-NLS-1$
    
    /**
     * file-name for our database
     */
    private static String embeddedName = "guidancer-db"; //$NON-NLS-1$
    
    /** schema properties */
    private static Properties schemaProp = null;
    
    /** database properties */
    private static Properties dbProp = null;
    
    /** map of all schemas (key=SchemaName, value=properties  */
    private static Map<String, Properties> schemaMap = 
        new HashMap<String, Properties>();
    
    /**
     * private constructor
     */
    private DBSchemaPropertyCreator() {
        //loadProperties();
    }
    
    /**
     * fills Map with all defined Schemas
     */
    public static void fillSchemaPropertiesMap() {
        loadProperties();
        
        Set keys = schemaProp.keySet();
        Iterator it = keys.iterator();
        Set<String> schemaNrSet = new HashSet<String>();
        //add different schema numbers to a set
        while (it.hasNext()) {
            String key = it.next().toString();
            String schemaNr = key.substring(0, key.indexOf("_")); //$NON-NLS-1$
            schemaNrSet.add(schemaNr);
        }
        
        Iterator<String> nrIt = schemaNrSet.iterator();
        while (nrIt.hasNext()) {
            Properties props = new Properties();            
            String number = nrIt.next();
            
            try {
                validateSchemaProperties(number);
                
                //add url to properties
                String url = schemaProp.getProperty(number + "_url"); //$NON-NLS-1$
                //use home directory if path is empty
                if (url.endsWith("file://")) { //$NON-NLS-1$
                    url = url + normalizePath(System.getProperty("user.home")) //$NON-NLS-1$
                        + "/" + embeddedName + "/" + embeddedName; //$NON-NLS-1$ //$NON-NLS-2$
                }
                props.setProperty("javax.persistence.jdbc.url", url); //$NON-NLS-1$
                
                //add schema to properties if it is set
                String schema = schemaProp.getProperty(number + "_schema"); //$NON-NLS-1$
                if (schema != null) {
                    props.setProperty("hibernate.default_schema", schema); //$NON-NLS-1$
                }
                
                //add database properties for current schema to properties
                String dbtype = schemaProp.getProperty(number + "_dbtype"); //$NON-NLS-1$
                String dbNr = dbtype.substring(0, dbtype.indexOf("_")); //$NON-NLS-1$
                Properties completeProps = getDbProps(props, dbNr);

                //add schemaname and properties to schemaMap
                String schemaName = schemaProp.getProperty(number + "_schemName"); //$NON-NLS-1$
                schemaMap.put(schemaName, completeProps);
            } catch (GDException e) {                
                if (e.getErrorId().equals(
                        MessageIDs.E_ERROR_IN_SCHEMA_CONFIG)) {
                    log.fatal(e.getMessage());
                    ProgressEventDispatcher.notifyListener(new ProgressEvent(
                            ProgressEvent.SHOW_MESSAGE,
                            MessageIDs.E_ERROR_IN_SCHEMA_CONFIG, null));
                }
                if (e.getErrorId().equals(
                        MessageIDs.E_ERROR_IN_DB_CONFIG)) {
                    log.fatal(e.getMessage());
                    ProgressEventDispatcher.notifyListener(new ProgressEvent(
                            ProgressEvent.SHOW_MESSAGE,
                            MessageIDs.E_ERROR_IN_DB_CONFIG, null));
                }
                schemaMap.clear();
                return;
            }
        }
    }

    /**
     * @param properties Properties
     * @param dbNr String of dbtype (1, 2, 3 ...)
     * @return the db properties
     */
    public static Properties getDbProps(Properties properties, String dbNr)
        throws GDException {
        Properties dbProps = properties;
        
        try {
            validateDatabaseProperties(dbNr);
            
            dbProps.setProperty("javax.persistence.jdbc.driver", //$NON-NLS-1$
                    dbProp.getProperty(dbNr + "_driverclass")); //$NON-NLS-1$
            Set dbKeys = dbProp.keySet();
            Iterator it = dbKeys.iterator();
            while (it.hasNext()) {
                String dbKey = it.next().toString();
                if (dbKey.indexOf(dbNr + "_prop.") != -1) { //$NON-NLS-1$
                    String propNr = dbKey.substring(dbKey.indexOf(".") + 1); //$NON-NLS-1$
                    dbProps.setProperty(dbProp.getProperty(dbKey),
                            dbProp.getProperty(dbNr + "_val." + propNr)); //$NON-NLS-1$
                }
            } 
        } catch (GDException e) {
            throw new GDException(e.getMessage(), e.getErrorId());
        }
               
        return dbProps;
    }
    
    /**
     * validate, if schema.properties contains necessary properties
     * @param number of scheme
     */
    private static void validateSchemaProperties(String number)
        throws GDException {
        if (schemaProp.getProperty(number + "_schemName") == null //$NON-NLS-1$
                || schemaProp.getProperty(number + "_dbtype") == null //$NON-NLS-1$
                || schemaProp.getProperty(number + "_url") == null) { //$NON-NLS-1$
            final String msg = "Your schema.properties is not correct."; //$NON-NLS-1$
            throw new GDException(msg,
                    MessageIDs.E_ERROR_IN_SCHEMA_CONFIG);
        }        
    }
    
    /**
     * validate, if databases.properties contains necessary properties
     * @param number of scheme
     */
    private static void validateDatabaseProperties(String number)
        throws GDException {
        if (dbProp.getProperty(number + "_db") == null //$NON-NLS-1$
                || dbProp.getProperty(number + "_driverclass") == null) { //$NON-NLS-1$
            final String msg = "Your databases.properties is not correct."; //$NON-NLS-1$
            throw new GDException(msg,
                    MessageIDs.E_ERROR_IN_DB_CONFIG);
        }
    }
    
    /**
     * Loads the properties files.
     */
    private static void loadProperties() throws GDFatalException {
        schemaProp = BundleUtils.loadProperties(Platform
                .getBundle(Activator.PLUGIN_ID), Activator.RESOURCES_DIR
                + SCHEMA_PROPERTIES);

        dbProp = BundleUtils.loadProperties(Platform
                .getBundle(Activator.PLUGIN_ID), Activator.RESOURCES_DIR
                + DATABASE_PROPERTIES);
    }
    
    /**
     * "normalize" path to ensure sane operation (write of configuration files).
     * re-places all back-slashes with forward-slashes.
     * @param inPath
     *            filesystem-path for file-access
     * @return "normaliszed" path
     */

    public static String normalizePath(final String inPath) {
        String pathStr = inPath.trim();
        /**
         * "normalize" the input and re-place all back-slashes with
         * forward-slashes. Do this on Windows only
         */
        if (isWindows()) {
            return pathStr.replace('\\', '/');
        }
        return pathStr;
    }
    
    /**
     * ..
     * @return true if we are running on Windows
     */
    public static boolean isWindows() {
        return System.
        getProperty("os.name").toLowerCase().indexOf("windows") != -1;  //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * @return Returns the schema.
     */
    public static Map<String, Properties> getSchemaMap() {
        fillSchemaPropertiesMap();
        
        return schemaMap;
    }

}
