package org.eclipse.jubula.rc.swing.swing.interfaces;

/** 
 * @author markus
 * @created Thu Apr 28 17:34:36 CEST 2011
 */
public interface ISwingApplicationImplClass {


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
     * @param activationMethod activationMethod */ 
    public void gdActivate(
        String activationMethod);

    /**
     * @param text text */ 
    public void gdInputText(
        String text);

    /**
     * @param modifierSpecification modifierSpecification
     * @param keySpecification keySpecification */ 
    public void gdKeyStroke(
        String modifierSpecification, 
        String keySpecification);

    /**
     * @param toogleKey toogleKey
     * @param selectKey selectKey */ 
    public void gdToggle(
        int toogleKey, 
        boolean selectKey);

    /**
     * @param text text */ 
    public void gdReplaceText(
        String text);

    /**
     * @param timeMillSec timeMillSec */ 
    public void gdWait(
        int timeMillSec);

    /** */ 
    public void gdPause();

    /**
     * @param title title
     * @param operator operator
     * @param timeout timeout
     * @param delayAfterVisibility delayAfterVisibility */ 
    public void gdWaitForWindow(
        String title, 
        String operator, 
        int timeout, 
        int delayAfterVisibility);

    /**
     * @param title title
     * @param operator operator
     * @param timeout timeout
     * @param delayAfterVisibility delayAfterVisibility */ 
    public void gdWaitForWindowActivation(
        String title, 
        String operator, 
        int timeout, 
        int delayAfterVisibility);

    /**
     * @param title title
     * @param operator operator
     * @param timeout timeout
     * @param delayAfterClose delayAfterClose */ 
    public void gdWaitForWindowToClose(
        String title, 
        String operator, 
        int timeout, 
        int delayAfterClose);

    /** */ 
    public void gdRestart();

    /**
     * @param destination destination
     * @param delay delay
     * @param fileAccess fileAccess
     * @param scalingFactor scalingFactor
     * @param createDirs createDirs */ 
    public void gdTakeScreenshot(
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
    public void gdExecuteExternalCommand(
        String command, 
        int expectedExitCode, 
        boolean runLocal, 
        int timeout);

    /**
     * @param modifierSpecification modifierSpecification
     * @param keySpecification keySpecification */ 
    public void gdNativeKeyStroke(
        String modifierSpecification, 
        String keySpecification);

    /**
     * @param text text */ 
    public void gdNativeInputText(
        String text);

    /**
     * @param variable variable
     * @param value value
     * @return value */ 
    public String gdSetValue(
        String variable, 
        String value);

    /**
     * @param text text */ 
    public void gdCopyToClipboard(
        String text);

    /**
     * @param timerName timerName
     * @param variableToStoreAbsoluteStartTime variableToStoreAbsoluteStartTime */ 
    public void gdStartTimer(
        String timerName, 
        String variableToStoreAbsoluteStartTime);

    /**
     * @param timerName timerName
     * @param variableToStoreTimeDeltaSinceTimerStart variableToStoreTimeDeltaSinceTimerStart */ 
    public void gdReadTimer(
        String timerName, 
        String variableToStoreTimeDeltaSinceTimerStart);

    /**
     * @param value1 value1
     * @param comparisonMethod comparisonMethod
     * @param value2 value2 */ 
    public void gdCheckValues(
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
    public void gdTakeScreenshotOfActiveWindow(
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
    public void gdCheckStringValues(
        String value1, 
        String operatorValue2, 
        String value2);

    /**
     * @param title title
     * @param operator operator
     * @param isExisting isExisting */ 
    public void gdCheckExistenceOfWindow(
        String title, 
        String operator, 
        boolean isExisting);

    /**
     * @param actionToPerfom actionToPerfom
     * @param expectedBehavior expectedBehavior
     * @param timeout timeout */ 
    public void gdManualTestStep(
        String actionToPerfom, 
        String expectedBehavior, 
        int timeout);

}