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
package org.eclipse.jubula.client.core.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.utils.ValueListIterator;


/**
 * Contains utility methods for interaction with Hibernate.
 *
 * @author BREDEX GmbH
 * @created May 19, 2010
 */
public class HibernateUtil {

    /**
     * Private constructor to prevent instantiation of a utility class.
     */
    private HibernateUtil() {
        // Nothing to initialize
    }

    /**
     * Creates and returns an "in" disjunction (i.e. an "or statement") using 
     * the arguments provided. The returned disjunction is semantically similar 
     * to: "<code>propertyName</code> in <code>expressionList</code>", but 
     * works around the Oracle expression list size limit.
     * </br></br>
     * See: "ORA-01795: maximum number of expressions in a list is 1000"
     * 
     * @param expressionList The expression list for the statement.
     * @param property The property to check with the statement.
     * @param criteriaBuilder The builder to use to construct the disjunction.
     * @return the created disjunction.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Predicate getExpressionDisjunction(
            Collection expressionList, Path property, 
            CriteriaBuilder criteriaBuilder) {

        List<Predicate> expressionCollections = new ArrayList<Predicate>();
        In currentSet = criteriaBuilder.in(property);
        int count = ValueListIterator.MAX_DB_VALUE_LIST;
        for (Object expression : expressionList) {
            if (count >= ValueListIterator.MAX_DB_VALUE_LIST) {
                currentSet = criteriaBuilder.in(property);
                expressionCollections.add(currentSet);
                count = 0;
            }

            currentSet.value(expression);
            count++;
        }

        return criteriaBuilder.or(expressionCollections.toArray(
                new Predicate[expressionCollections.size()]));
    }
 
    /**
     * Indirection layer for acquiring the underlying class for a proxied
     * object.
     * 
     * @param persistenceProxy The object for which to get the class.
     * @return the true underlying class for the given object.
     * @see Hibernate#getClass()
     */
    @SuppressWarnings("rawtypes")
    public static Class getClass(Object persistenceProxy) {
        return persistenceProxy.getClass();
    }

    /**
     * 
     * @return a Globally Unique Identifier that is a 32-character 
     *         hexadecimal string.
     */
    public static String generateGuid() {
        return UUID.randomUUID().toString().replaceAll(StringConstants.MINUS, 
                StringConstants.EMPTY);
    }
}
