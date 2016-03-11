package org.eclipse.jubula.client.core.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Representing data values, see http://bugs.eclipse.org/488218
 */
@Entity
@Table(name = "TEST_DATA_CELL")
class DataCellPO implements IDataCellPO {

    /** the value */
    private String m_value;
    
    /** id */
    private Long m_id;
    
    /**
     * <code>m_parentProjectId</code>id of associated project
     */
    private Long m_parentProjectId = null;

    /**
     * Constructor for JPA
     */
    DataCellPO() {
        
    }
    
    /**
     * @param value the value
     * @param projectId the parent project id
     */
    DataCellPO(String value, Long projectId) {
        setDataValue(value);
        setParentProjectId(projectId);
    }

    /** {@inheritDoc} */
    @Column(name = "DATA_VALUE",
            length = MAX_STRING_LENGTH)
    public String getDataValue() {
        return m_value;
    }

    /**
     * @param value the value
     */
    public void setDataValue(String value) {
        this.m_value = value;
    }

    /**
     * @return the id
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }

    /**
     * @param id the id
     */
    public void setId(Long id) {
        this.m_id = id;
    }
    
    /**
     * Version is not used for optimistic locking for this class.
     * @return <code>0</code>
     */
    @Transient
    public Integer getVersion() {
        return 0;
    }

    /** {@inheritDoc} */
    public String getName() {
        // not needed
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }
}
