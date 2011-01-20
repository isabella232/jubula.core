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
package org.eclipse.jubula.client.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyClass;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.jubula.client.core.businessprocess.progress.ElementLoadedProgressListener;
import org.eclipse.jubula.client.core.businessprocess.progress.InsertProgressListener;
import org.eclipse.jubula.client.core.businessprocess.progress.RemoveProgressListener;
import org.eclipse.jubula.client.core.persistence.HibernateUtil;
import org.eclipse.jubula.client.core.utils.DependencyCheckerOp;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.tools.constants.StringConstants;


/**
 * base class for all kinds of nodes in test tree (with the exception
 * of testsuite nodes
 * 
 * @author BREDEX GmbH
 * @created 17.08.2004
 */
@SuppressWarnings("unchecked")
@Entity
@Table(name = "NODE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.CHAR, 
                     name = "CLASS_ID")
@DiscriminatorValue(value = "N")
@EntityListeners(value = { 
        ElementLoadedProgressListener.class, 
        InsertProgressListener.class, RemoveProgressListener.class })
abstract class NodePO implements INodePO {
    
    /** hibernate OID */
    private transient Long m_id = null;
    
    /** Globally Unique Identifier for recognizing nodes across databases */
    private transient String m_guid = null;

    /** hibernate version id */
    private transient Integer m_version = null;
    
    /** Flag if the parent at the children is set.
     * @see getNodeList() 
     */
    private transient boolean m_isParentNodeSet = false;
    
    /**
     * generated tag
     */
    private boolean m_isGenerated;
    
    /**
     * whether this element has been marked as "active" or "inactive"
     */
    private boolean m_isActive = true;
    
    /**
     * The current toolkit level of this node.
     * Not to persist!
     */
    private transient String m_toolkitLevel = StringConstants.EMPTY;
    
    /**
     * name of the real node, e.g. CapPO name or Testcase name
     */
    private String m_name;

    /**
     * describes, if the node is derived from another node
     */
    
    private INodePO m_parentNode = null;

    /** The ID of the parent project */
    private Long m_parentProjectId = null;
    
    /**
     * list of all child nodes, if existent
     */
    private List<INodePO> m_nodeList = new ArrayList<INodePO>();
    
    /**
     * contains the comment for a node
     */
    private String m_comment;  
    
    /** mapping from attribute descriptions to attributes */
    private Map<IDocAttributeDescriptionPO, IDocAttributePO> m_docAttributes = 
        new HashMap/*<IDocAttributeDescriptionPO, IDocAttributePO>*/();
    
    /**
     * not to persist!
     * <code>m_sumOmMap</code>manages the flags to summarize the information about  
     * completeness of object mapping for all child nodes related to the
     * associated AUT
     * only relevant for ExecTestCasePO and TestSuitePO
     * key: AUTMainPO, value: summarized OMFlag
     */
    private transient Map<IAUTMainPO, Boolean> m_sumOmMap = new HashMap();
    
    /** 
     * not to persist!
     * <code>m_sumTDMap</code>map to summarize the information about 
     * completeness of testdata for all child nodes and the actual node itself
     * for each supported language <br>
     * key: supported languages, Type: string (string presentation of Locale)
     * value: flag to summarize the completeness of testdata for the node 
     * itself and its children
     * only relevant for ExecTestCasePO and TestSuitePO
     */
    private transient Map<String, Boolean> m_sumTDMap = new HashMap();
    
    /** 
     * not to persist!
     * <code>m_sumSpecTcFlag</code>: Flag to summarize the information about 
     * availablity of SpecTestCases for all child nodes and the actual node 
     * itself <br>
     */
    private transient boolean m_sumSpecTcFlag = false;

    /** The timestamp */
    private long m_timestamp = 0;

    /**
     * constructor for a node with a pre-existing GUID
     * @param name of the node
     * @param guid of the node
     * @param isGenerated indicates whether this node has been generated
     */
    protected NodePO(String name, String guid, boolean isGenerated) {
        setName(name);
        setGuid(guid);
        setGenerated(isGenerated);
    }
    
    /**
     * constructor
     * @param name of the node
     * @param isGenerated indicates whether this node has been generated
     */
    protected NodePO(String name, boolean isGenerated) {
        this(name, HibernateUtil.generateGuid(), isGenerated);
    }

    /**
     * only for hibernate
     */
    NodePO() {
        // only for hibernate
    }

    
    /**
     * @param nodeList The nodeList to set.
     */
    void setHbmNodeList(List<INodePO> nodeList) {
        m_nodeList = nodeList;
        m_isParentNodeSet = false;
    }
    
    /**
     * 
     * {@inheritDoc}
     * @return The name of this node
     */
    @Transient
    public String getName() {
        return getHbmName();
    }
    
    /**
     * gets the value of the m_name property
     * 
     * @return the name of the node
     */
    @Basic
    @Column(name = "NAME", length = 4000)
    private String getHbmName() {
        return m_name;
    }
    /**
     * For Hibernate only
     * Sets the value of the m_name property.
     * 
     * @param name
     *            the new value of the m_name property
     */
    private void setHbmName(String name) {
        m_name = name;
    }

    /**
     * Sets the value of the m_name property.
     * @param name the name of this node
     */
    public void setName(String name) {
        setHbmName(name);
    }
    
    /**
     * @return the current value of the m_parentNode property or null
     */
    @Transient
    public INodePO getParentNode() {
        return m_parentNode;
    } 
    
    /**
     * @param parent parent to set
     */
    public void setParentNode(INodePO parent) {
        m_parentNode = parent;
    }

    /**
     * 
     * Access method for the m_nodeList property.
     * only to use for hibernate
     * 
     * @return the current value of the m_nodeList property
     */
    @ManyToMany(fetch = FetchType.EAGER, 
                cascade = CascadeType.ALL, 
                targetEntity = NodePO.class)
    @JoinTable(name = "NODE_LIST", 
               joinColumns = @JoinColumn(name = "PARENT"), 
               inverseJoinColumns = @JoinColumn(name = "CHILD"))
    @OrderColumn(name = "IDX")
    List<INodePO> getHbmNodeList() {
        return m_nodeList;
    }
    
    /**
     * @return The List of children nodes
     */
    @Transient
    List<INodePO> getNodeList() {
        if (!m_isParentNodeSet) {
            List<INodePO> nodeList = getHbmNodeList();
            for (Object o : nodeList) {
                INodePO node = (INodePO)o;
                node.setParentNode(this);
            }
            m_isParentNodeSet = true;
        }
        return getHbmNodeList();
    }
    
    /**
     * @return the unmodifiable node list.
     */
    @Transient
    public List getUnmodifiableNodeList() {
        return Collections.unmodifiableList(getNodeList());
    }
    
    /**
     * 
     * @return Returns the m_comment.
     */
    @Basic
    @Column(name = "COMM_TXT", length = 4000)
    private String getHbmComment() {
        return m_comment;
    }
    
    
    /**
     * @return Returns the m_comment.
     */
    @Transient
    public String getComment() {
        return getHbmComment();
    }
    
    /**
     * For Hibernate only
     * @param comment The m_comment to set.
     */
    private void setHbmComment(String comment) {
        m_comment = comment;
    }
    
    /**
     * @param comment The m_comment to set.
     */
    public void setComment(String comment) {
        setHbmComment(comment);
    }
    /**
     * adds a childnode to an existent node
     * creation of reference to the parent node
     * @param childNode
     *            reference to the childnode
     */
    public void addNode(INodePO childNode) {
        addNode(-1, childNode);
    }
    
    /**
     * adds a childnode to an existent node
     * creation of reference to the parent node
     * @param position the position to add the childnode.
     * @param childNode
     *            reference to the childnode
     */
    public void addNode(int position, INodePO childNode) {
        if (position < 0 || position > getNodeList().size()) {
            getNodeList().add(childNode);
        } else {
            getNodeList().add(position, childNode);            
        }
        childNode.setParentNode(this);
        setParentProjectIdForChildNode(childNode);
    }

    /**
     * Sets the child node's parentProjectId equal to this node's parentProjectId.
     * This is the default implementation. Subclasses may override.
     * 
     * @param childNode The node that will have its parentProjectId set.
     */
    protected void setParentProjectIdForChildNode(INodePO childNode) {
        childNode.setParentProjectId(getParentProjectId());
    }

    /**
     * deletes a node and resolves the
     * reference to the parent node
     * sign off as child node of the parent node
     * @param childNode reference to the childnode
     */
    public void removeNode(INodePO childNode) {
        ((NodePO)childNode).removeMe(this);   
    }
    
    /**
     * @param parent removes the node from childrenList or eventhandlerMap
     */
    protected void removeMe(INodePO parent) {
        ((NodePO)parent).getNodeList().remove(this);
        setParentNode(null);
    }

    /**
     * Removes all child nodes and sets the parent of the child nodes 
     * to <code>null</code>
     */
    public void removeAllNodes() {
        Iterator iter = getNodeList().iterator();
        while (iter.hasNext()) {
            INodePO childNode = (INodePO)iter.next();
            childNode.setParentNode(null);
            iter.remove();
        }
    }
    
    /**
     * Returns the index of the given node in the node list.
     * @param node the node whose index is want.
     * @return the index of the given node.
     */
    public int indexOf(INodePO node) {
        return getNodeList().indexOf(node);
    }
    
    /**
     * Returns the valis staus of the node.<br>
     * Normally all Nodes are valid. only CapPOs with an InvalidComponent
     * should return false.
     * @return true if the Node is valid, false otherwise. 
     */
    @Transient
    public boolean isValid() {
        return true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() { // NOPMD by al on 3/19/07 1:35 PM
        return getGuid().hashCode();
    }
    
    /**
     * use this method instead of getNodeList()
     * use add-/removeNode-method for modification of list 
     * @return iterator for unmodifiable NodeList
     */
    @Transient
    public Iterator<INodePO> getNodeListIterator() {
        List nodeList = Collections.unmodifiableList(getNodeList());
        return nodeList.iterator();
    }
       
    /**
     * @return size of nodeList
     */
    @Transient
    public int getNodeListSize() {
        return getNodeList().size();
    }

    
    /**
     * @param aut aut, for which to get the sumOMFlag
     * @return Returns the sumOMFlag or false, if the given AUT isn't contained
     * in map
     */
    public boolean getSumOMFlag(IAUTMainPO aut) {
        if (m_sumOmMap == null) {
            m_sumOmMap = new HashMap<IAUTMainPO, Boolean>();
        }
        if (aut != null) {
            Boolean value = m_sumOmMap.get(aut);
            return value != null ? value.booleanValue() : false;
        }
        return true;
    }
    /**
     * @param aut aut, for which to set the sumOMFlag
     * @param sumOMFlag The sumOMFlag to set.
     */
    public void setSumOMFlag(IAUTMainPO aut, boolean sumOMFlag) {
        if (m_sumOmMap == null) {
            m_sumOmMap = new HashMap<IAUTMainPO, Boolean>();
        }
        m_sumOmMap.put(aut, Boolean.valueOf(sumOMFlag));
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public boolean getSumSpecTcFlag() {
        return m_sumSpecTcFlag;
    }
    /**
     * 
     * {@inheritDoc}
     */
    public void setSumSpecTcFlag(boolean sumSpecTcFlag) {
        m_sumSpecTcFlag = sumSpecTcFlag;
    }

    /**
     * method to get the sumTdFlag for a given Locale
     * @param loc locale, for which to get the sumTdFlag
     * @return the state of sumTdFlag
     */
    public boolean getSumTdFlag(Locale loc) {
        Boolean value = getSumTDMap().get(loc.toString());
        return (value != null) ? value.booleanValue() : false;
    }
    
    /**
     * method to set the sumTdFlag for a given Locale
     * @param loc  locale, for which to set the sumTdFlag
     * @param flag the state of sumTdFlag to set
     */
    public void setSumTdFlag(Locale loc, boolean flag) {
        getSumTDMap().put(loc.toString(), Boolean.valueOf(flag));
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return super.toString() + StringConstants.SPACE 
            + StringConstants.LEFT_PARENTHESES + getName() 
            + StringConstants.RIGHT_PARENTHESES;
    }
    
    /**
     *  
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    /**
     * @param id The id to set.
     */
    void setId(Long id) {
        m_id = id;
    }
    /**
     * 
     * @return Long
     */
    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return m_version;
    }
    /**
     * @param version The version to set.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }
    
    /**
     *    
     * @return the GUID.
     */
    @Basic
    @Column(name = "GUID")
//    @Index(name = "PI_NODE_GUID")
    public String getGuid() {
        return m_guid;
    }
    /**
     * @param guid The GUID to set.
     */
    private void setGuid(String guid) {
        m_guid = guid;
    }

    /**
     * Checks for circular dependences with a potential parent.
     * @param parent the parent to check
     * @return true if there is a circular dependence, false otherwise.
     */
    public boolean hasCircularDependences(INodePO parent) {

        DependencyCheckerOp op = new DependencyCheckerOp(parent);
        TreeTraverser traverser = new TreeTraverser(this, op);
        traverser.traverse(true);
        return op.hasDependency();
    }
    
    /**
     * Checks the equality of the given Object with this Object.
     * {@inheritDoc}
     * @param obj the object to check
     * @return if there is a database ID it returns true if the ID is equal.
     * If there is no ID it will be compared to identity.
     */
    public boolean equals(Object obj) { // NOPMD by al on 3/19/07 1:35 PM
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NodePO  || obj instanceof INodePO)) {
            return false;
        }
        INodePO o = (INodePO)obj;
        return getGuid().equals(o.getGuid());
    }

    /**
     * @return Returns the sumTDMap.
     */
    @Transient
    private Map<String, Boolean> getSumTDMap() {
        if (m_sumTDMap == null) {
            m_sumTDMap = new HashMap<String, Boolean>();
        }
        return m_sumTDMap;
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }
    
    /**
     * @return if parameter of Node may be modified or not
     */
    @Transient
    public abstract Boolean isReused();

    /**
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        for (INodePO node : getHbmNodeList()) {
            node.setParentProjectId(projectId);
        }
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
//    @Index(name = "PI_NODE_PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * 
     * {@inheritDoc}
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * @return the current toolkit level of this node.
     */
    @Transient
    public String getToolkitLevel() {
        return m_toolkitLevel;
    }

    /**
     * Sets the current toolkit level of this node.
     * @param toolkitLevel the toolkit level.
     */
    public void setToolkitLevel(String toolkitLevel) {
        m_toolkitLevel = toolkitLevel;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Basic
    public long getTimestamp() {
        return m_timestamp;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setTimestamp(long timestamp) {
        m_timestamp = timestamp;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public Set<IDocAttributeDescriptionPO> getDocAttributeTypes() {
        return Collections.unmodifiableSet(getDocAttributeMap().keySet());
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public IDocAttributePO getDocAttribute(
            IDocAttributeDescriptionPO attributeType) {
        
        return getDocAttributeMap().get(attributeType);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setDocAttribute(IDocAttributeDescriptionPO attributeType, 
            IDocAttributePO attribute) {

        getDocAttributeMap().put(attributeType, attribute);
    }

    /**
     * 
     * @return The map of documentation attributes.
     */
    @ManyToMany(cascade = CascadeType.ALL, 
                fetch = FetchType.EAGER, 
                targetEntity = DocAttributePO.class)
    @JoinTable(name = "DOC_ATTR_ASSOC", 
               joinColumns = @JoinColumn(name = "PARENT"), 
               inverseJoinColumns = @JoinColumn(name = "CHILD"))
    @MapKeyClass(value = DocAttributeDescriptionPO.class)
    private Map<IDocAttributeDescriptionPO, IDocAttributePO> 
    getDocAttributeMap() {
    
        return m_docAttributes;
    }
    /**
     * 
     * @param map The new map.
     */
    @SuppressWarnings("unused")
    private void setDocAttributeMap(
            Map<IDocAttributeDescriptionPO, IDocAttributePO> map) {
        
        m_docAttributes = map;
    }

    /**
     *      
     * @return the isGenerated Attribute for all nodes
     */
    @Basic(optional = false)
    @Column(name = "IS_GENERATED")
    public boolean isGenerated() {
        return m_isGenerated;
    }

    /**
     * @param isGenerated the isGenerated to set
     */
    public void setGenerated(boolean isGenerated) {
        m_isGenerated = isGenerated;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setActive(boolean isActive) {
        m_isActive = isActive;
    }

    /**
     *      
     * @return the isActive Attribute for all nodes
     */
    @Basic(optional = false)
    @Column(name = "IS_ACTIVE")
    public boolean isActive() {
        return m_isActive;
    }    
}
