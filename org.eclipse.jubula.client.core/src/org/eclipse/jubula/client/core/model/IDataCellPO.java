package org.eclipse.jubula.client.core.model;

/**
 * Representing data values, see http://bugs.eclipse.org/488218
 */
public interface IDataCellPO extends IPersistentObject {

    /**
     * @return the value
     */
    public abstract String getDataValue();

}