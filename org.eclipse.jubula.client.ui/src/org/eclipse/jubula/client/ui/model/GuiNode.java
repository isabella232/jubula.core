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
package org.eclipse.jubula.client.ui.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.swt.graphics.Image;


/**
 * Superclass of all GuiNodes.
 *
 * @author BREDEX GmbH
 * @created 06.01.2005
 */
public abstract class GuiNode implements Serializable, Comparable<GuiNode> {
    /** The name of this node */
    private String m_name;
    
    /** The "real" model */
    private INodePO m_content;
    
    /** The parent */
    private GuiNode m_parent;
    
    /** The children nodes of this node */
    private List < GuiNode > m_nodeList = new ArrayList < GuiNode > (0);
    
    /** indicates whether or not the GuiNode is editable */
    private boolean m_isEditable;
    
    /**
     * Default constructor
     */
    protected GuiNode() {
        //
    }
    
    /** only to use for invisible root node
     * Constructor
     * @param name the name
     */
    protected GuiNode(String name) {
        m_name = name;
        m_isEditable = true;
    }

    
    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     */
    protected GuiNode(String name, GuiNode parent, INodePO content) {
        this(name, parent, content, parent.getChildren().size());
    }
    
    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     * @param isEditable whether or not this GuiNode is editable
     */
    protected GuiNode(String name, GuiNode parent, INodePO content, 
        boolean isEditable) {
        this(name, parent, content, parent.getChildren().size(), isEditable);
    }

    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     * @param pos position to insert child in nodelist of parent
     */
    protected GuiNode(String name, GuiNode parent, INodePO content, 
        Integer pos) {

        this(name, parent, content, pos, true);
    }
    
    /**
     * Constructor
     * @param name the name
     * @param parent the parent
     * @param content the content
     * @param pos position to insert child in nodelist of parent
     * @param isEditable whether or not this GuiNode is editable
     */
    protected GuiNode(String name, GuiNode parent, INodePO content, 
        Integer pos, boolean isEditable) {
        
        m_name = name;
        m_parent = parent;
        m_content = content;
        if (m_parent != null) {
            if (pos != null) {
                m_parent.addNode(pos, this);                
            } else {
                m_parent.addNode(this); 
            }
        }
        m_isEditable = isEditable;
    }
    
    /**
     * @return Returns the name (name of the content).
     */
    public String getName() {
        if (m_content != null
                && m_content.getName() != null) {
            return m_content.getName();
        }
        return m_name;
    }
    
    
    
    /**
     * @return <code>true</code> if the GuiNode is editable. Otherwise 
     *         <code>false</code>.
     */
    public boolean isEditable() {
        return m_isEditable;
    }

    /**
     * @return the name of the GUINode.
     */
    protected String getInternalName() {
        return m_name;
    }
    
    
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        if (m_content != null) {
            m_content.setName(name);
        }
        m_name = name;
    }
    
    /**
     * @param editable Whether the node shold be editable.
     */
    public void setEditable(boolean editable) {
        m_isEditable = editable;
    }

    /**
     * Sets the name of the GUINode
     * @param name the name to set
     */
    protected void setInternalName(String name) {
        m_name = name;
    }
    
    
    /**
     * Returns a <code>List</code> of children of a <code>GuiNode</code>.
     * @return the children.
     */
    public List < GuiNode > getChildren() {
        return Collections.unmodifiableList(m_nodeList);
    }
    
    
    /**
     * Gets the content from a GuiNode
     * @return a <code>INodePO</code> value. The "real" Model.
     */
    public INodePO getContent() {
        return m_content;
    }
   
    /**
     * Adds a child to this node.
     * @param child the node to be set as children.
     */
    public void addNode(GuiNode child) {
        addNode(m_nodeList.size(), child);
    }   
    
    /**
     * Adds a child to this node without setting the childs parent.
     * @param child the child to add.
     */
    public void addReferencedNode(GuiNode child) {
        m_nodeList.add(child);
    }
    
    
    /**
     * Adds a new child to this node.
     * @param child the node to be set as children.
     * @param position where to insert
     */
    public void addNode(int position, GuiNode child) {
        if (position > m_nodeList.size()) {
            addNode(child);
            return;
        }
        m_nodeList.add(position, child);
        child.setParent(this);
    }

    /**
     * Moves the node of the given actual position to the given new position
     * @param actualPos the actual position.
     * @param newPos the new position.
     */
    public void moveNode(int actualPos, int newPos) {
        GuiNode guiNode = m_nodeList.get(actualPos);
        m_nodeList.remove(actualPos);
        m_nodeList.add(newPos, guiNode);
    }
    
    /**
     * Returns index of node.
     * @param node the node we want index from
     * @return int position
     */
    public int indexOf(GuiNode node) {
        return m_nodeList.indexOf(node);
        
    }
    
    /**
     * Removes the given node from the child list.
     * @param child the node to be removed.
     */
    public void removeNode(GuiNode child) {
        m_nodeList.remove(child);
    }
    
    /**
     * Removes all Nodes.
     */
    protected void removeAllNodes() {
        m_nodeList.clear();
    }
    

    /**
     * Gets the Image (Icon) for this type of Node.
     * @return an Image object.
     */
    public abstract Image getImage();

    /**
     * Gets the Image (Icon) for this type of Node when it has been marked as
     * "cut" (for cut and paste). The default implementation simply calls
     * <code>getImage()</code>.
     * 
     * @return an Image object.
     */
    public Image getCutImage() {
        return getImage();
    }
    
    /**
     * Gets the Image (Icon) for this type of Node when it has been marked as
     * "generated" . The default implementation simply calls
     * <code>getImage()</code>.
     * 
     * @return an Image object.
     */
    public Image getGeneratedImage() {
        return getImage();
    }
    
    /**
     * @return Returns the parent.
     */
    public GuiNode getParentNode() {
        return m_parent;
    }
    

    /**
     * @return Returns the parents node iterator.
     */
    public Iterator getRootNodeIterator() {
        GuiNode node = this;
        while (node.getParentNode() != null) {
            node = node.getParentNode();
        }
        return node.getChildren().iterator();
    }

    /**
     * @param parent The parent to set.
     */
    public void setParent(GuiNode parent) {
        m_parent = parent;
    }
    
    
    /**
     * @param content a <code>INodePO</code> The content to set.
     */
    public void setContent(INodePO content) {
        m_content = content;
    }
    
    
    /**
     * sorts the childrenList
     * @param c     Comparator
     */
    @SuppressWarnings("unchecked")
    public void sort(Comparator c) {
        Collections.sort(m_nodeList, c);
    }

    /**
     * Subclasses should override this method!
     * @param builder
     *      StringBuilder
     */
    public void getInfoString(StringBuilder builder) {
        // do nothing
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return super.toString() + StringConstants.SPACE 
            + StringConstants.LEFT_PARENTHESES + m_name 
            + StringConstants.RIGHT_PARENTHESES;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(GuiNode otherNode) {
        return m_name.compareTo(otherNode.getName());
    }
    
    /**
     * @return The index of this node in its parent's node list. <br>
     *         If this node has no parent the return value is -1.
     */
    public int getPositionInParent() {
        return m_parent.getChildren().indexOf(this);
    }
    /**
     * @return The Guid of the content.
     */
    public String getMylynId() {
        if (getContent() == null) {
            return null;
        }
        return getContent().getGuid();
    }
    
}
