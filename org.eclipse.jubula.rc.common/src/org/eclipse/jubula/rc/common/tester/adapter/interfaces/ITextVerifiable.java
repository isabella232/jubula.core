package org.eclipse.jubula.rc.common.tester.adapter.interfaces;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public interface ITextVerifiable extends IWidgetAdapter {

    /**
     * Gets the value of the component, or if there are more than the first
     * selected.
     * 
     * @return the value of the component
     */
    public String getText();
}
