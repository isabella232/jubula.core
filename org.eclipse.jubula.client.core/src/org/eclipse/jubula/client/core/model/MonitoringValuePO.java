package org.eclipse.jubula.client.core.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.eclipse.jubula.tools.objects.MonitoringValue;
/**
 * 
 * @author marc
 *
 */
@Embeddable
public class MonitoringValuePO extends MonitoringValue {
      
    /** default */
    public MonitoringValuePO() {
        super();
    }
    
    /** 
     * @param value the value to set
     * @param type the type to set
     */
    public MonitoringValuePO(String value, String type) {
        super(value, type);
       
    }
    /**
     * 
     * @param value ads
     * @param type asd
     * @param category asd
     */
    public MonitoringValuePO(String value, 
            String type, String category) {
        super(value, type, category);
       
    } 
    /**
     * 
     * @param value asd
     * @param type asd
     * @param category asd
     * @param isSignificant asd
     */
    public MonitoringValuePO(String value, String 
            type, String category, boolean isSignificant) {
        super(value, type, category, isSignificant);
       
    }   
    
    /**
     * @return the value
     */
    @Basic
    public String getValue() {
        return super.getValue();
    }
    /**
     * @return the typ
     */
    @Basic
    public String getType() {
        return super.getType();
    }
    /**
     * {@inheritDoc}
     */
    @Basic
    public String getCategory() {
        return super.getCategory();
    }
    /**
     * {@inheritDoc}
     */
    public void setCategory(String category) {
        super.setCategory(category);
    }    
    /**
     *  @param value the value to set
     */
    
    public void setValue(String value) {
        super.setValue(value);
    }
    /**
     * @param type the type to set
     */       
    public void setType(String type) {
        super.setType(type);
    }
    /**
     * {@inheritDoc}
     */
    
    public void setSignificant(Boolean significant) {
        super.setSignificant(significant);
        
    }
    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "isSignificant")
    public Boolean isSignificant() {        
        return super.isSignificant();
        
    }
    
    
    
}
