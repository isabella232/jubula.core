package org.eclipse.jubula.rc.swing.interfaces;

/** 
 * @author marvin
 * @created Tue Feb 19 16:06:17 CET 2013
 */
public interface IGraphicApplication {


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
     * @param activationMethod activationMethod */ 
    public void rcActivate(
        String activationMethod);

    /**
     * @param text text */ 
    public void rcInputText(
        String text);

    /**
     * @param modifierSpecification modifierSpecification
     * @param keySpecification keySpecification */ 
    public void rcKeyStroke(
        String modifierSpecification, 
        String keySpecification);

    /**
     * @param toogleKey toogleKey
     * @param selectKey selectKey */ 
    public void rcToggle(
        int toogleKey, 
        boolean selectKey);

    /**
     * @param timeMillSec timeMillSec */ 
    public void rcWait(
        int timeMillSec);

    /** */ 
    public void rcPause();

    /**
     * @param title title
     * @param operator operator
     * @param timeout timeout
     * @param delayAfterVisibility delayAfterVisibility */ 
    public void rcWaitForWindow(
        String title, 
        String operator, 
        int timeout, 
        int delayAfterVisibility);

    /**
     * @param title title
     * @param operator operator
     * @param timeout timeout
     * @param delayAfterVisibility delayAfterVisibility */ 
    public void rcWaitForWindowActivation(
        String title, 
        String operator, 
        int timeout, 
        int delayAfterVisibility);

    /**
     * @param title title
     * @param operator operator
     * @param timeout timeout
     * @param delayAfterClose delayAfterClose */ 
    public void rcWaitForWindowToClose(
        String title, 
        String operator, 
        int timeout, 
        int delayAfterClose);

    /** */ 
    public void rcRestart();

    /**
     * @param destination destination
     * @param delay delay
     * @param fileAccess fileAccess
     * @param scalingFactor scalingFactor
     * @param createDirs createDirs */ 
    public void rcTakeScreenshot(
        String destination, 
        int delay, 
        String fileAccess, 
        int scalingFactor, 
        boolean createDirs);

    /**
     * @param command command
     * @param expectedExitCode expectedExitCode
     * @param runLocal runLocal
     * @param timeout timeout */ 
    public void rcExecuteExternalCommand(
        String command, 
        int expectedExitCode, 
        boolean runLocal, 
        int timeout);

    /**
     * @param modifierSpecification modifierSpecification
     * @param keySpecification keySpecification */ 
    public void rcNativeKeyStroke(
        String modifierSpecification, 
        String keySpecification);

    /**
     * @param text text */ 
    public void rcNativeInputText(
        String text);

    /**
     * @param variable variable
     * @param value value
     * @return value */ 
    public String rcSetValue(
        String variable, 
        String value);

    /**
     * @param text text */ 
    public void rcCopyToClipboard(
        String text);

    /**
     * @param timerName timerName
     * @param variableToStoreAbsoluteStartTime variableToStoreAbsoluteStartTime */ 
    public void rcStartTimer(
        String timerName, 
        String variableToStoreAbsoluteStartTime);

    /**
     * @param timerName timerName
     * @param variableToStoreTimeDeltaSinceTimerStart variableToStoreTimeDeltaSinceTimerStart */ 
    public void rcReadTimer(
        String timerName, 
        String variableToStoreTimeDeltaSinceTimerStart);

    /**
     * @param value1 value1
     * @param comparisonMethod comparisonMethod
     * @param value2 value2 */ 
    public void rcCheckValues(
        String value1, 
        String comparisonMethod, 
        String value2);

    /**
     * @param destination destination
     * @param delay delay
     * @param fileAccess fileAccess
     * @param scalingFactor scalingFactor
     * @param createDirs createDirs
     * @param marginTop marginTop
     * @param marginRight marginRight
     * @param marginBottom marginBottom
     * @param marginLeft marginLeft */ 
    public void rcTakeScreenshotOfActiveWindow(
        String destination, 
        int delay, 
        String fileAccess, 
        int scalingFactor, 
        boolean createDirs, 
        int marginTop, 
        int marginRight, 
        int marginBottom, 
        int marginLeft);

    /**
     * @param value1 value1
     * @param operatorValue2 operatorValue2
     * @param value2 value2 */ 
    public void rcCheckStringValues(
        String value1, 
        String operatorValue2, 
        String value2);

    /**
     * @param title title
     * @param operator operator
     * @param isExisting isExisting */ 
    public void rcCheckExistenceOfWindow(
        String title, 
        String operator, 
        boolean isExisting);

    /**
     * @param actionToPerfom actionToPerfom
     * @param expectedBehavior expectedBehavior
     * @param timeout timeout */ 
    public void rcManualTestStep(
        String actionToPerfom, 
        String expectedBehavior, 
        int timeout);

}