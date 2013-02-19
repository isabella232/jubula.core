package org.eclipse.jubula.rc.swt.interfaces;

/** 
 * @author marvin
 * @created Tue Feb 19 16:06:18 CET 2013
 */
public interface IMenu {


    /**
     * @param menuPath menuPath
     * @param operator operator */ 
    public void selectMenuItem(
        String menuPath, 
        String operator);

    /**
     * @param indexPath indexPath */ 
    public void selectMenuItemByIndexpath(
        String indexPath);

    /**
     * @param menuPath menuPath
     * @param operator operator
     * @param isEnabled isEnabled */ 
    public void verifyEnabled(
        String menuPath, 
        String operator, 
        boolean isEnabled);

    /**
     * @param indexPath indexPath
     * @param isEnabled isEnabled */ 
    public void verifyEnabledByIndexpath(
        String indexPath, 
        boolean isEnabled);

    /**
     * @param menuPath menuPath
     * @param operator operator
     * @param isExisting isExisting */ 
    public void verifyExists(
        String menuPath, 
        String operator, 
        boolean isExisting);

    /**
     * @param indexPath indexPath
     * @param isExisting isExisting */ 
    public void verifyExistsByIndexpath(
        String indexPath, 
        boolean isExisting);

    /**
     * @param menuPath menuPath
     * @param operator operator
     * @param isSelected isSelected */ 
    public void verifySelected(
        String menuPath, 
        String operator, 
        boolean isSelected);

    /**
     * @param indexPath indexPath
     * @param isSelected isSelected */ 
    public void verifySelectedByIndexpath(
        String indexPath, 
        boolean isSelected);

    /**
     * @param timeout timeout
     * @param delayAfterVisibility delayAfterVisibility */ 
    public void waitForComponent(
        int timeout, 
        int delayAfterVisibility);

}