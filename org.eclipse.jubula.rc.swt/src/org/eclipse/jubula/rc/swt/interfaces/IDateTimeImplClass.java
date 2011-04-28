package org.eclipse.jubula.rc.swt.interfaces;

/** 
 * @author markus
 * @created Mon Feb 14 16:33:06 CET 2011
 */
public interface IDateTimeImplClass {


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