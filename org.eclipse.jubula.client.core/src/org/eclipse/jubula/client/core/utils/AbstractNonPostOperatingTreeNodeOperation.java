package org.eclipse.jubula.client.core.utils;

/**
 * TreeNodeOperation which does not support the postOperating method
 * 
 * @param <T>
 *            The class of nodes handled by the operation.
 */
public abstract class AbstractNonPostOperatingTreeNodeOperation<T> implements
        ITreeNodeOperation<T> {
    /** {@inheritDoc} */
    public final void postOperate(ITreeTraverserContext<T> ctx, T parent,
            T node, boolean alreadyVisited) {
        // no post operation necessary
    }
}
