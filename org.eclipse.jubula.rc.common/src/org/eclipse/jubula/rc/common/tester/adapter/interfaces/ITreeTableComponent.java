package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeTableOperationContext;

/**
 * 
 * @author BREDEX GmbH
 *
 * @param <T>
 */
public interface ITreeTableComponent<T>
    extends ITreeComponent<T>, ITableComponent<T> {

    /**
     * Gets the TreeTableOperationContext which is created through an toolkit
     * specific implementation.
     * 
     * @param column
     *            the column
     * @return the TreeTablesOperationContext for the tree
     */
    public AbstractTreeTableOperationContext getContext(int column);

}
