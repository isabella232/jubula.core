package org.eclipse.jubula.rc.swt.interfaces;

/** 
 * @author markus
 * @created Thu Apr 28 17:34:37 CEST 2011
 */
public interface ITableTreeImplClass {


    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator
     * @param clickCount clickCount
     * @param column column
     * @param mouseButton mouseButton */ 
    public void gdSelect(
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
    public void gdSelectByIndices(
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
    public void gdVerifySelectedValue(
        String text, 
        String operator, 
        int column);

    /**
     * @param checked checked */ 
    public void gdVerifySelectedCheckbox(
        boolean checked);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator */ 
    public void gdToggleCheckbox(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath */ 
    public void gdToggleCheckboxByIndices(
        String pathType, 
        int preAscend, 
        String indexPath);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator
     * @param checked checked */ 
    public void gdVerifyCheckbox(
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
    public void gdVerifyCheckboxByIndices(
        String pathType, 
        int preAscend, 
        String indexPath, 
        boolean checked);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator */ 
    public void gdCollapse(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath */ 
    public void gdCollapseByIndices(
        String pathType, 
        int preAscend, 
        String indexPath);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator */ 
    public void gdExpand(
        String pathType, 
        int preAscend, 
        String textPath, 
        String operator);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param indexPath indexPath */ 
    public void gdExpandByIndices(
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
    public void gdSelect(
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
    public void gdSelectByIndices(
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
    public void gdMove(
        String direction, 
        int nodeCount, 
        int clickCount);

    /**
     * @param pathType pathType
     * @param preAscend preAscend
     * @param textPath textPath
     * @param operator operator
     * @param exists exists */ 
    public void gdVerifyPath(
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
    public void gdVerifyPathByIndices(
        String pathType, 
        int preAscend, 
        String indexPath, 
        boolean exists);

    /**
     * @param text text
     * @param operator operator */ 
    public void gdVerifySelectedValue(
        String text, 
        String operator);

    /**
     * @param text text
     * @param operator operator */ 
    public void gdVerifyTextAtMousePosition(
        String text, 
        String operator);

    /**
     * @param indexPath indexPath */ 
    public void gdPopupByIndexPathAtSelectedNode(
        String indexPath);

    /**
     * @param textPath textPath */ 
    public void gdPopupByTextPathAtSelectedNode(
        String textPath);

    /**
     * @param treeIndexPath treeIndexPath
     * @param popupIndexPath popupIndexPath */ 
    public void gdPopupByIndexPathAtIndexNode(
        String treeIndexPath, 
        String popupIndexPath);

    /**
     * @param treeIndexPath treeIndexPath
     * @param popupTextPath popupTextPath */ 
    public void gdPopupByTextPathAtIndexNode(
        String treeIndexPath, 
        String popupTextPath);

    /**
     * @param treeTextPath treeTextPath
     * @param operator operator
     * @param popupIndexPath popupIndexPath */ 
    public void gdPopupByIndexPathAtTextNode(
        String treeTextPath, 
        String operator, 
        String popupIndexPath);

    /**
     * @param treeTextPath treeTextPath
     * @param operator operator
     * @param popupTextPath popupTextPath */ 
    public void gdPopupByTextPathAtTextNode(
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
    public void gdDragByTextPath(
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
    public void gdDropByTextPath(
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
    public void gdDragByIndexPath(
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
    public void gdDropByIndexPath(
        String pathType, 
        int preAscend, 
        String treeIndexPath, 
        int delayBeforeDrop);

    /**
     * @param variable variable
     * @return value */ 
    public String gdStoreSelectedNodeValue(
        String variable);

    /**
     * @param variable variable
     * @return value */ 
    public String gdStoreValueAtMousePosition(
        String variable);

    /**
     * @param text text
     * @param textSize textSize
     * @param timePerWord timePerWord
     * @param windowWidth windowWidth */ 
    public void gdShowText(
        String text, 
        int textSize, 
        int timePerWord, 
        int windowWidth);

    /**
     * @param isExisting isExisting */ 
    public void gdVerifyExists(
        boolean isExisting);

    /**
     * @param isEnabled isEnabled */ 
    public void gdVerifyEnabled(
        boolean isEnabled);

    /**
     * @param propertyName propertyName
     * @param propertyValue propertyValue
     * @param operator operator */ 
    public void gdVerifyProperty(
        String propertyName, 
        String propertyValue, 
        String operator);

    /**
     * @param hasFocus hasFocus */ 
    public void gdVerifyFocus(
        boolean hasFocus);

    /**
     * @param timeout timeout
     * @param delayAfterVisibility delayAfterVisibility */ 
    public void gdWaitForComponent(
        int timeout, 
        int delayAfterVisibility);

    /**
     * @param clickCount clickCount
     * @param mouseButton mouseButton */ 
    public void gdClick(
        int clickCount, 
        int mouseButton);

    /**
     * @param clickCount clickCount
     * @param mouseButton mouseButton
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits */ 
    public void gdClickDirect(
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
    public void gdPopupSelectByTextPath(
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
    public void gdPopupSelectByTextPath(
        int xPos, 
        int yPos, 
        String units, 
        String menuPath, 
        String operator);

    /**
     * @param indexPath indexPath
     * @param mouseButton mouseButton */ 
    public void gdPopupSelectByIndexPath(
        String indexPath, 
        int mouseButton);

    /**
     * @param indexPath indexPath */ 
    public void gdPopupSelectByIndexPath(
        String indexPath);

    /**
     * @param textPath textPath
     * @param operator operator
     * @param mouseButton mouseButton */ 
    public void gdPopupSelectByTextPath(
        String textPath, 
        String operator, 
        int mouseButton);

    /**
     * @param textPath textPath
     * @param operator operator */ 
    public void gdPopupSelectByTextPath(
        String textPath, 
        String operator);

    /**
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param indexPath indexPath
     * @param mouseButton mouseButton */ 
    public void gdPopupSelectByIndexPath(
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
    public void gdPopupSelectByIndexPath(
        int xPos, 
        int yPos, 
        String units, 
        String indexPath);

    /**
     * @param indexPath indexPath
     * @param isEnabled isEnabled
     * @param mouseButton mouseButton */ 
    public void gdPopupVerifyEnabledByIndexPath(
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
    public void gdPopupVerifyEnabledByIndexPath(
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
    public void gdPopupVerifyEnabledByTextPath(
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
    public void gdPopupVerifyEnabledByTextPath(
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
    public void gdPopupVerifyExistsByIndexPath(
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
    public void gdPopupVerifyExistsByIndexPath(
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
    public void gdPopupVerifyExistsByTextPath(
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
    public void gdPopupVerifyExistsByTextPath(
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
    public void gdPopupVerifySelectedByIndexPath(
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
    public void gdPopupVerifySelectedByIndexPath(
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
    public void gdPopupVerifySelectedByTextPath(
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
    public void gdPopupVerifySelectedByTextPath(
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
    public void gdDrag(
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
    public void gdDrop(
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        int delayBeforeDrop);

}