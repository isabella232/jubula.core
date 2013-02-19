package org.eclipse.jubula.rc.swing.interfaces;

/** 
 * @author marvin
 * @created Tue Feb 19 16:06:17 CET 2013
 */
public interface IJTextComponent {


    /**
     * @param text text
     * @param index index */ 
    public void rcInsertText(
        String text, 
        int index);

    /**
     * @param text text
     * @param pattern pattern
     * @param operator operator
     * @param afterPattern afterPattern */ 
    public void rcInsertText(
        String text, 
        String pattern, 
        String operator, 
        boolean afterPattern);

    /** */ 
    public void rcSelect();

    /**
     * @param pattern pattern
     * @param operator operator */ 
    public void rcSelect(
        String pattern, 
        String operator);

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