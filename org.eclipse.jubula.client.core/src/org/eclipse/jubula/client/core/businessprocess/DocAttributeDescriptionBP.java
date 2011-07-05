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
package org.eclipse.jubula.client.core.businessprocess;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.eclipse.jubula.client.core.model.IDocAttributeDescriptionPO;
import org.eclipse.jubula.client.core.model.PoMaker;


/**
 * @author BREDEX GmbH
 * @created 20.05.2008
 */
public final class DocAttributeDescriptionBP {
    
    /** string property for HQL queries */
    private static final String LABEL_KEY = "labelKey"; //$NON-NLS-1$

    /** HQL query to get an attribute description from the db by label key */
    private static final String QUERY = 
        "select desc from DocAttributeDescriptionPO as desc where labelKey = :" + LABEL_KEY; //$NON-NLS-1$
    
    /**
     * Private constructor for utility class.
     */
    private DocAttributeDescriptionBP() {
        // Nothing to initialize
    }

    /**
     * 
     * @param labelKey The label key for which to find the attribute 
     *                 description.
     * @param session The Persistence (JPA / EclipseLink) session in which the query should be 
     *                performed.
     * @return the attribute description with the given label key, or 
     *         <code>null</code> if no such attribute description is found.
     */
    public static IDocAttributeDescriptionPO getDescription(String labelKey, 
            EntityManager session) {
        
        Query query = session.createQuery(QUERY);
        query.setParameter(LABEL_KEY, labelKey);
        try {
            IDocAttributeDescriptionPO desc = 
                (IDocAttributeDescriptionPO)query.getSingleResult();
            return desc;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns a group of attribute descriptions that should be used for every
     * Project. This method will use existing attribute descriptions from the
     * database if possible. If any given attribute description does not already
     * exist in the database, it will be created.
     * 
     * @param session The session to use in order to check whether the 
     *                descriptions already exist in the database and load them
     *                if necessary.
     * @return the group of attribute descriptions that should be used for every
     *         Project.
     */
    public static IDocAttributeDescriptionPO[] initProjectAttributeDescriptions(
            EntityManager session) {
        
        // FIXME zeb implement
        return new IDocAttributeDescriptionPO [0];
    }

    /**
     * Returns a group of attribute descriptions that should be used for every
     * TestCase. This method will use existing attribute descriptions from the
     * database if possible. If any given attribute description does not already
     * exist in the database, it will be created.
     * 
     * @param session The session to use in order to check whether the 
     *                descriptions already exist in the database and load them
     *                if necessary.
     * @return the group of attribute descriptions that should be used for every
     *         TestCase.
     */
    public static IDocAttributeDescriptionPO[] initTcAttributeDescriptions(
            EntityManager session) {
        
        // FIXME zeb implement
        return new IDocAttributeDescriptionPO[0];
    }

    /**
     * Updates an existing attribute description in the database, or creates
     * a new one if no such attribute description exists.
     * 
     * @param session The session to use for database queries.
     * @param labelKey The i18n key used to fetch the label for this attribute
     *                 type. This also serves as a unique id for the attribute 
     *                 type.
     * @param displayClass The name of a class capable of loading and 
     *                         storing objects of this type.
     * @param initializerClass The name of a class capable of initializing 
     *                             objects of this type.
     * @return the updated or newly created attribute description.
     */
    private static IDocAttributeDescriptionPO updateAttributeDescription(
            EntityManager session, String labelKey, String displayClass, 
            String initializerClass) {
        
        IDocAttributeDescriptionPO desc = 
            DocAttributeDescriptionBP.getDescription(labelKey, session); 
        if (desc == null) {
            desc = PoMaker.createDocAttributeDescription(
                    labelKey, displayClass, initializerClass);
        } else {
            desc.setInitializerClassName(initializerClass);
            desc.setDisplayClassName(displayClass);
        }
        
        return desc;
    }

}
