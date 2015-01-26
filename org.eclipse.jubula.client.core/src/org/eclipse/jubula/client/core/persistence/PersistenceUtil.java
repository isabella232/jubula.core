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

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.utils.ValueListIterator;


/**
 * Contains utility methods for interaction with Persistence (JPA / EclipseLink).
 *
 * @author BREDEX GmbH
 * @created May 19, 2010
 */
public class PersistenceUtil {

    /**
     * Private constructor to prevent instantiation of a utility class.
     */
    private PersistenceUtil() {
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
     * 
     * @return a Globally Unique Identifier that is a 32-character 
     *         hexadecimal string.
     */
    public static String generateGuid() {
        return UUID.randomUUID().toString().replaceAll(StringConstants.MINUS, 
                StringConstants.EMPTY);
    }
    
    /**
     * Recursively dissociates and deletes all children of the given node.
     * 
     * Workaround for: 
     * http://eclip.se/347010
     * 
     * Once the aforementioned bug is resolved, this method and all 
     * references to it should be removed. Note that this will also require a 
     * change to the required version of EclipseLink to the version in which 
     * the bug was fixed (i.e. if the bug is fixed in 2.3.1, then the minimum 
     * required version will be 2.3.1).
     * 
     * @param node The node for which to clear the child list.
     * @param sess The session in which remove/delete operations should be 
     *             performed. The session is otherwise not modified by this 
     *             method (i.e. this method does not close or flush the 
     *             session).
     */
    public static void removeChildNodes(INodePO node, EntityManager sess) {
        
        for (INodePO child : node.getUnmodifiableNodeList()) {
            removeChildNodes(child, sess);
            sess.remove(child);
        }
        node.removeAllNodes();

        if (node instanceof ITestCasePO) {
            ITestCasePO testCase = (ITestCasePO)node;
            for (IEventExecTestCasePO eventHandler 
                    : testCase.getAllEventEventExecTC()) {
                sess.remove(eventHandler);
            }
            
            testCase.getEventExecTcMap().clear();
        }
    }
}
