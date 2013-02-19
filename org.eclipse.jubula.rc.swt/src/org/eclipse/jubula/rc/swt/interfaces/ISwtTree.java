package org.eclipse.jubula.rc.swt.interfaces;

/** 
 * @author marvin
 * @created Tue Feb 19 16:06:18 CET 2013
 */
public interface ISwtTree {


    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator
     * @param clickCount clickCount
     * @param column column
     * @param mouseButton mouseButton */ 
    public void rcSelect(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator, 
        int clickCount, 
        int column, 
        int mouseButton);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath
     * @param clickCount clickCount
     * @param column column
     * @param mouseButton mouseButton */ 
    public void rcSelectByIndices(
        String pathType, 
        int preAscend, 
        String indexPath, 
        int clickCount, 
        int column, 
        int mouseButton);

    /**
     * @param text text
     * @param operator operator
     * @param column column */ 
    public void rcVerifySelectedValue(
        String text, 
        String operator, 
        int column);

    /**
     * @param checked checked */ 
    public void rcVerifySelectedCheckbox(
        boolean checked);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator */ 
    public void rcToggleCheckbox(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath */ 
    public void rcToggleCheckboxByIndices(
        String pathType, 
        int preAscend, 
        String indexPath);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator
     * @param checked checked */ 
    public void rcVerifyCheckbox(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator, 
        boolean checked);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath
     * @param checked checked */ 
    public void rcVerifyCheckboxByIndices(
        String pathType, 
        int preAscend, 
        String indexPath, 
        boolean checked);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator */ 
    public void rcCollapse(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath */ 
    public void rcCollapseByIndices(
        String pathType, 
        int preAscend, 
        String indexPath);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator */ 
    public void rcExpand(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath */ 
    public void rcExpandByIndices(
        String pathType, 
        int preAscend, 
        String indexPath);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator
     * @param clickCount clickCount
     * @param mouseButton mouseButton
     * @param extendSelection extendSelection */ 
    public void rcSelect(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator, 
        int clickCount, 
        int mouseButton, 
        String extendSelection);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath
     * @param clickCount clickCount
     * @param mouseButton mouseButton
     * @param extendSelection extendSelection */ 
    public void rcSelectByIndices(
        String pathType, 
        int preAscend, 
        String indexPath, 
        int clickCount, 
        int mouseButton, 
        String extendSelection);

    /**
     * @param direction direction
     * @param nodeCount nodeCount
     * @param clickCount clickCount */ 
    public void rcMove(
        String direction, 
        int nodeCount, 
        int clickCount);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator
     * @param exists exists */ 
    public void rcVerifyPath(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator, 
        boolean exists);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath
     * @param exists exists */ 
    public void rcVerifyPathByIndices(
        String pathType, 
        int preAscend, 
        String indexPath, 
        boolean exists);

    /**
     * @param text text
     * @param operator operator */ 
    public void rcVerifySelectedValue(
        String text, 
        String operator);

    /**
     * @param text text
     * @param operator operator */ 
    public void rcVerifyTextAtMousePosition(
        String text, 
        String operator);

    /**
     * @param indexPath indexPath */ 
    public void rcPopupByIndexPathAtSelectedNode(
        String indexPath);

    /**
     * @param textPath textPath */ 
    public void rcPopupByTextPathAtSelectedNode(
        String textPath);

    /**
     * @param treeIndexPath treeIndexPath
     * @param popupIndexPath popupIndexPath */ 
    public void rcPopupByIndexPathAtIndexNode(
        String treeIndexPath, 
        String popupIndexPath);

    /**
     * @param treeIndexPath treeIndexPath
     * @param popupTextPath popupTextPath */ 
    public void rcPopupByTextPathAtIndexNode(
        String treeIndexPath, 
        String popupTextPath);

    /**
     * @param treeTextPath treeTextPath
     * @param operator operator
     * @param popupIndexPath popupIndexPath */ 
    public void rcPopupByIndexPathAtTextNode(
        String treeTextPath, 
        String operator, 
        String popupIndexPath);

    /**
     * @param treeTextPath treeTextPath
     * @param operator operator
     * @param popupTextPath popupTextPath */ 
    public void rcPopupByTextPathAtTextNode(
        String treeTextPath, 
        String operator, 
        String popupTextPath);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param pathType pathType
     * @param preAscend preAscend
     * @param treeTextPath treeTextPath
     * @param operator operator */ 
    public void rcDragByTextPath(
        int mouseButton, 
        String modifierSpecification, 
        String pathType, 
        int preAscend, 
        String treeTextPath, 
        String operator);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param treeTextPath treeTextPath
     * @param operator operator
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDropByTextPath(
        String pathType, 
        int preAscend, 
        String treeTextPath, 
        String operator, 
        int delayBeforeDrop);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param pathType pathType
     * @param preAscend preAscend
     * @param treeIndexPath treeIndexPath */ 
    public void rcDragByIndexPath(
        int mouseButton, 
        String modifierSpecification, 
        String pathType, 
        int preAscend, 
        String treeIndexPath);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param treeIndexPath treeIndexPath
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDropByIndexPath(
        String pathType, 
        int preAscend, 
        String treeIndexPath, 
        int delayBeforeDrop);

    /**
     * @param variable variable
     * @return value */ 
    public String rcStoreSelectedNodeValue(
        String variable);

    /**
     * @param variable variable
     * @return value */ 
    public String rcStoreValueAtMousePosition(
        String variable);

    /**
     * @param text text
     * @param textSize textSize
     * @param timePerWord timePerWord
     * @param windowWidth windowWidth */ 
    public void rcShowText(
        String text, 
        int textSize, 
        int timePerWord, 
        int windowWidth);

    /**
     * @param isExisting isExisting */ 
    public void rcVerifyExists(
        boolean isExisting);

    /**
     * @param isEnabled isEnabled */ 
    public void rcVerifyEnabled(
        boolean isEnabled);

    /**
     * @param propertyName propertyName
     * @param propertyValue propertyValue
     * @param operator operator */ 
    public void rcVerifyProperty(
        String propertyName, 
        String propertyValue, 
        String operator);

    /**
     * @param variable variable
     * @param propertyName propertyName
     * @return value */ 
    public String rcStorePropertyValue(
        String variable, 
        String propertyName);

    /**
     * @param hasFocus hasFocus */ 
    public void rcVerifyFocus(
        boolean hasFocus);

    /**
     * @param timeout timeout
     * @param delayAfterVisibility delayAfterVisibility */ 
    public void rcWaitForComponent(
        int timeout, 
        int delayAfterVisibility);

    /**
     * @param clickCount clickCount
     * @param mouseButton mouseButton */ 
    public void rcClick(
        int clickCount, 
        int mouseButton);

    /**
     * @param clickCount clickCount
     * @param mouseButton mouseButton
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits */ 
    public void rcClickDirect(
        int clickCount, 
        int mouseButton, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param menuPath menuPath
     * @param operator operator
     * @param mouseButton mouseButton */ 
    public void rcPopupSelectByTextPath(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String menuPath, 
        String operator, 
        int mouseButton);

    /**
     * @param xPos xPos
     * @param yPos yPos
     * @param units units
     * @param menuPath menuPath
     * @param operator operator */ 
    public void rcPopupSelectByTextPath(
        int xPos, 
        int yPos, 
        String units, 
        String menuPath, 
        String operator);

    /**
     * @param indexPath indexPath
     * @param mouseButton mouseButton */ 
    public void rcPopupSelectByIndexPath(
        String indexPath, 
        int mouseButton);

    /**
     * @param indexPath indexPath */ 
    public void rcPopupSelectByIndexPath(
        String indexPath);

    /**
     * @param textPath textPath
     * @param operator operator
     * @param mouseButton mouseButton */ 
    public void rcPopupSelectByTextPath(
        String textPath, 
        String operator, 
        int mouseButton);

    /**
     * @param textPath textPath
     * @param operator operator */ 
    public void rcPopupSelectByTextPath(
        String textPath, 
        String operator);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param indexPath indexPath
     * @param mouseButton mouseButton */ 
    public void rcPopupSelectByIndexPath(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String indexPath, 
        int mouseButton);

    /**
     * @param xPos xPos
     * @param yPos yPos
     * @param units units
     * @param indexPath indexPath */ 
    public void rcPopupSelectByIndexPath(
        int xPos, 
        int yPos, 
        String units, 
        String indexPath);

    /**
     * @param indexPath indexPath
     * @param isEnabled isEnabled
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifyEnabledByIndexPath(
        String indexPath, 
        boolean isEnabled, 
        int mouseButton);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param indexPath indexPath
     * @param isEnabled isEnabled
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifyEnabledByIndexPath(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String indexPath, 
        boolean isEnabled, 
        int mouseButton);

    /**
     * @param textPath textPath
     * @param operator operator
     * @param isEnabled isEnabled
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifyEnabledByTextPath(
        String textPath, 
        String operator, 
        boolean isEnabled, 
        int mouseButton);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param textPath textPath
     * @param operator operator
     * @param isEnabled isEnabled
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifyEnabledByTextPath(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String textPath, 
        String operator, 
        boolean isEnabled, 
        int mouseButton);

    /**
     * @param indexPath indexPath
     * @param isExisting isExisting
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifyExistsByIndexPath(
        String indexPath, 
        boolean isExisting, 
        int mouseButton);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param indexPath indexPath
     * @param isExisting isExisting
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifyExistsByIndexPath(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String indexPath, 
        boolean isExisting, 
        int mouseButton);

    /**
     * @param textPath textPath
     * @param operator operator
     * @param isExisting isExisting
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifyExistsByTextPath(
        String textPath, 
        String operator, 
        boolean isExisting, 
        int mouseButton);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param textPath textPath
     * @param operator operator
     * @param isExisting isExisting
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifyExistsByTextPath(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String textPath, 
        String operator, 
        boolean isExisting, 
        int mouseButton);

    /**
     * @param indexPath indexPath
     * @param isSelected isSelected
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifySelectedByIndexPath(
        String indexPath, 
        boolean isSelected, 
        int mouseButton);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param indexPath indexPath
     * @param isSelected isSelected
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifySelectedByIndexPath(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String indexPath, 
        boolean isSelected, 
        int mouseButton);

    /**
     * @param textPath textPath
     * @param operator operator
     * @param isSelected isSelected
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifySelectedByTextPath(
        String textPath, 
        String operator, 
        boolean isSelected, 
        int mouseButton);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param textPath textPath
     * @param operator operator
     * @param isSelected isSelected
     * @param mouseButton mouseButton */ 
    public void rcPopupVerifySelectedByTextPath(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String textPath, 
        String operator, 
        boolean isSelected, 
        int mouseButton);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits */ 
    public void rcDrag(
        int mouseButton, 
        String modifierSpecification, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDrop(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        int delayBeforeDrop);

}