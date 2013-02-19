package org.eclipse.jubula.rc.swt.interfaces;

/** 
 * @author marvin
 * @created Tue Feb 19 16:06:18 CET 2013
 */
public interface ITable {


    /**
     * @param text text
     * @param row row
     * @param rowOperator rowOperator
     * @param column column
     * @param columnOperator columnOperator */ 
    public void rcInputText(
        String text, 
        String row, 
        String rowOperator, 
        String column, 
        String columnOperator);

    /**
     * @param text text
     * @param row row
     * @param column column */ 
    public void rcInputText(
        String text, 
        int row, 
        int column);

    /**
     * @param text text
     * @param row row
     * @param rowOperator rowOperator
     * @param column column
     * @param columnOperator columnOperator */ 
    public void rcReplaceText(
        String text, 
        String row, 
        String rowOperator, 
        String column, 
        String columnOperator);

    /**
     * @param text text
     * @param row row
     * @param column column */ 
    public void rcReplaceText(
        String text, 
        int row, 
        int column);

    /**
     * @param row row
     * @param rowOperator rowOperator
     * @param column column
     * @param columnOperator columnOperator
     * @param clickCount clickCount
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param extendSelection extendSelection
     * @param mouseButton mouseButton */ 
    public void rcSelectCell(
        String row, 
        String rowOperator, 
        String column, 
        String columnOperator, 
        int clickCount, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String extendSelection, 
        int mouseButton);

    /**
     * @param row row
     * @param column column
     * @param clickCount clickCount
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param extendSelection extendSelection */ 
    public void rcSelectCell(
        int row, 
        int column, 
        int clickCount, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String extendSelection);

    /**
     * @param direction direction
     * @param cellCount cellCount
     * @param clickCount clickCount
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param extendSelection extendSelection */ 
    public void rcMove(
        String direction, 
        int cellCount, 
        int clickCount, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        String extendSelection);

    /**
     * @param row row
     * @param extendSelection extendSelection */ 
    public void rcSelectRow(
        int row, 
        String extendSelection);

    /**
     * @param isEditable isEditable
     * @param row row
     * @param rowOperator rowOperator
     * @param column column
     * @param columnOperator columnOperator */ 
    public void rcVerifyEditable(
        boolean isEditable, 
        String row, 
        String rowOperator, 
        String column, 
        String columnOperator);

    /**
     * @param isEditable isEditable
     * @param row row
     * @param column column */ 
    public void rcVerifyEditable(
        boolean isEditable, 
        int row, 
        int column);

    /**
     * @param isEditable isEditable */ 
    public void rcVerifyEditableSelected(
        boolean isEditable);

    /**
     * @param isEditable isEditable */ 
    public void rcVerifyEditableMousePosition(
        boolean isEditable);

    /**
     * @param text text
     * @param textOperator textOperator
     * @param row row
     * @param rowOperator rowOperator
     * @param column column
     * @param columnOperator columnOperator */ 
    public void rcVerifyText(
        String text, 
        String textOperator, 
        String row, 
        String rowOperator, 
        String column, 
        String columnOperator);

    /**
     * @param text text
     * @param operator operator
     * @param row row
     * @param column column */ 
    public void rcVerifyText(
        String text, 
        String operator, 
        int row, 
        int column);

    /**
     * @param text text
     * @param operator operator */ 
    public void rcVerifyTextAtMousePosition(
        String text, 
        String operator);

    /**
     * @param row row
     * @param rowOperator rowOperator
     * @param cellValue cellValue
     * @param valueOperator valueOperator
     * @param searchType searchType
     * @param exists exists */ 
    public void rcVerifyValueInRow(
        String row, 
        String rowOperator, 
        String cellValue, 
        String valueOperator, 
        String searchType, 
        boolean exists);

    /**
     * @param column column
     * @param columnOperator columnOperator
     * @param cellValue cellValue
     * @param valueOperator valueOperator
     * @param searchType searchType
     * @param exists exists */ 
    public void rcVerifyValueInColumn(
        String column, 
        String columnOperator, 
        String cellValue, 
        String valueOperator, 
        String searchType, 
        boolean exists);

    /**
     * @param column column
     * @param columnOperator columnOperator
     * @param cellValue cellValue
     * @param valueOperator valueOperator
     * @param clickCount clickCount
     * @param extendSelection extendSelection
     * @param searchType searchType
     * @param mouseButton mouseButton */ 
    public void rcSelectRowByValue(
        String column, 
        String columnOperator, 
        String cellValue, 
        String valueOperator, 
        int clickCount, 
        String extendSelection, 
        String searchType, 
        int mouseButton);

    /**
     * @param column column
     * @param cellValue cellValue
     * @param clickCount clickCount
     * @param operator operator
     * @param extendSelection extendSelection
     * @param searchType searchType */ 
    public void rcSelectRowByValue(
        int column, 
        String cellValue, 
        int clickCount, 
        String operator, 
        String extendSelection, 
        String searchType);

    /**
     * @param row row
     * @param rowOperator rowOperator
     * @param cellValue cellValue
     * @param valueOperator valueOperator
     * @param clickCount clickCount
     * @param extendSelection extendSelection
     * @param searchType searchType
     * @param mouseButton mouseButton */ 
    public void rcSelectCellByColValue(
        String row, 
        String rowOperator, 
        String cellValue, 
        String valueOperator, 
        int clickCount, 
        String extendSelection, 
        String searchType, 
        int mouseButton);

    /**
     * @param row row
     * @param cellValue cellValue
     * @param operator operator
     * @param clickCount clickCount
     * @param extendSelection extendSelection
     * @param searchType searchType */ 
    public void rcSelectCellByColValue(
        int row, 
        String cellValue, 
        String operator, 
        int clickCount, 
        String extendSelection, 
        String searchType);

    /**
     * @param column column
     * @param cellValue cellValue
     * @param useRegularExpression useRegularExpression */ 
    public void rcSelectCellByRowValue(
        int column, 
        String cellValue, 
        boolean useRegularExpression);

    /**
     * @param row row
     * @param column column
     * @param indexPath indexPath */ 
    public void rcPopupByIndexPathAtCell(
        int row, 
        int column, 
        String indexPath);

    /**
     * @param row row
     * @param column column
     * @param textPath textPath */ 
    public void rcPopupByTextPathAtCell(
        int row, 
        int column, 
        String textPath);

    /**
     * @param indexPath indexPath */ 
    public void rcPopupByIndexPathAtSelectedCell(
        String indexPath);

    /**
     * @param textPath textPath */ 
    public void rcPopupByTextPathAtSelectedCell(
        String textPath);

    /**
     * @param variable variable
     * @param row row
     * @param rowOperator rowOperator
     * @param column column
     * @param columnOperator columnOperator
     * @return value */ 
    public String rcReadValue(
        String variable, 
        String row, 
        String rowOperator, 
        String column, 
        String columnOperator);

    /**
     * @param variable variable
     * @param row row
     * @param column column
     * @return value */ 
    public String rcReadValue(
        String variable, 
        int row, 
        int column);

    /**
     * @param variable variable
     * @return value */ 
    public String rcReadValueAtMousePosition(
        String variable);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param row row
     * @param rowOperator rowOperator
     * @param column column
     * @param columnOperator columnOperator
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits */ 
    public void rcDragCell(
        int mouseButton, 
        String modifierSpecification, 
        String row, 
        String rowOperator, 
        String column, 
        String columnOperator, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param row row
     * @param column column
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits */ 
    public void rcDragCell(
        int mouseButton, 
        String modifierSpecification, 
        int row, 
        int column, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits);

    /**
     * @param row row
     * @param rowOperator rowOperator
     * @param column column
     * @param columnOperator columnOperator
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDropCell(
        String row, 
        String rowOperator, 
        String column, 
        String columnOperator, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        int delayBeforeDrop);

    /**
     * @param row row
     * @param column column
     * @param xPos xPos
     * @param xUnits xUnits
     * @param yPos yPos
     * @param yUnits yUnits
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDropCell(
        int row, 
        int column, 
        int xPos, 
        String xUnits, 
        int yPos, 
        String yUnits, 
        int delayBeforeDrop);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param column column
     * @param columnOperator columnOperator
     * @param cellValue cellValue
     * @param valueOperator valueOperator
     * @param searchType searchType */ 
    public void rcDragRowByValue(
        int mouseButton, 
        String modifierSpecification, 
        String column, 
        String columnOperator, 
        String cellValue, 
        String valueOperator, 
        String searchType);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param column column
     * @param cellValue cellValue
     * @param operator operator
     * @param searchType searchType */ 
    public void rcDragRowByValue(
        int mouseButton, 
        String modifierSpecification, 
        int column, 
        String cellValue, 
        String operator, 
        String searchType);

    /**
     * @param column column
     * @param columnOperator columnOperator
     * @param cellValue cellValue
     * @param valueOperator valueOperator
     * @param searchType searchType
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDropRowByValue(
        String column, 
        String columnOperator, 
        String cellValue, 
        String valueOperator, 
        String searchType, 
        int delayBeforeDrop);

    /**
     * @param column column
     * @param cellValue cellValue
     * @param operator operator
     * @param searchType searchType
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDropRowByValue(
        int column, 
        String cellValue, 
        String operator, 
        String searchType, 
        int delayBeforeDrop);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param row row
     * @param rowOperator rowOperator
     * @param cellValue cellValue
     * @param valueOperator valueOperator
     * @param searchType searchType */ 
    public void rcDragCellByColValue(
        int mouseButton, 
        String modifierSpecification, 
        String row, 
        String rowOperator, 
        String cellValue, 
        String valueOperator, 
        String searchType);

    /**
     * @param mouseButton mouseButton
     * @param modifierSpecification modifierSpecification
     * @param row row
     * @param cellValue cellValue
     * @param operator operator
     * @param searchType searchType */ 
    public void rcDragCellByColValue(
        int mouseButton, 
        String modifierSpecification, 
        int row, 
        String cellValue, 
        String operator, 
        String searchType);

    /**
     * @param row row
     * @param rowOperator rowOperator
     * @param cellValue cellValue
     * @param valueOperator valueOperator
     * @param searchType searchType
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDropCellByColValue(
        String row, 
        String rowOperator, 
        String cellValue, 
        String valueOperator, 
        String searchType, 
        int delayBeforeDrop);

    /**
     * @param row row
     * @param cellValue cellValue
     * @param operator operator
     * @param searchType searchType
     * @param delayBeforeDrop delayBeforeDrop */ 
    public void rcDropCellByColValue(
        int row, 
        String cellValue, 
        String operator, 
        String searchType, 
        int delayBeforeDrop);

    /**
     * @param isEditable isEditable */ 
    public void rcVerifyEditable(
        boolean isEditable);

    /**
     * @param text text */ 
    public void rcReplaceText(
        String text);

    /**
     * @param text text */ 
    public void rcInputText(
        String text);

    /**
     * @param text text
     * @param operator operator */ 
    public void rcVerifyText(
        String text, 
        String operator);

    /**
     * @param variable variable
     * @return value */ 
    public String rcReadValue(
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