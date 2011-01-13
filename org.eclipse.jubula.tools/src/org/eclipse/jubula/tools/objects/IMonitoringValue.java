package org.eclipse.jubula.tools.objects;
/**
 * Minimum functionality of a monitoring value
 * @author marc
 *
 */
public interface IMonitoringValue {
    /**
     * 
     * @return The value
     */
    public String getValue();
    /**
     * 
     * @return The type
     */
    public String getType();
    /**
     *     
     * @param value The value to set
     */
    public void setValue(String value); 
    /**
     * 
     * @param type The type to set
     */
    public void setType(String type);
    /**
     * @param category The name of the category    
     */
    public void setCategory(String category);
    /**
     * @return The name of the category which this monitoring value is set to
     */
    public String getCategory();
    /**
     * @return If true, this value will be displayed is TestResultSummaryView
     */
    public Boolean isSignificant();
    /**
     * @param isSignificant if true, this value will be displayed is TestResultSummaryView
     */
    public void setSignificant(Boolean isSignificant);
    
    
}
