package org.eclipse.jubula.rc.rcp.swt.aut;

import java.util.Properties;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.tester.adapter.factory.GUIAdapterFactoryRegistry;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.tester.adapter.factory.SWTAdapterFactory;
import org.eclipse.jubula.tools.constants.AutConfigConstants;
import org.eclipse.jubula.tools.utils.EnvironmentUtils;
import org.eclipse.swt.widgets.Display;

/**
 * This class extends the SwtAUTServer to avoid buddy class loading,
 * which does not work, if the SWT library is in it's own bundle.
 */
public class SwtRemoteControlService extends SwtAUTServer {

    /** An instance of this class. */
    private static SwtRemoteControlService instance;

    /** True, if AUTServer is running, otherwise false. */
    private boolean m_hasRemoteControlServiceStarted = false;

    /**
     * Private constructor for Singleton pattern.
     */
    private SwtRemoteControlService() {
        super();
        // set also static instance in parent AUTServer
        setInstance(this);
        AdapterFactoryRegistry.initRegistration();
    }

    /**
     * @return The instance of this remote control service.
     */
    public static AUTServer getInstance() {
        if (instance == null) {
            instance = new SwtRemoteControlService();
        }
        return instance;
    }

    /**
     * Check that the remote control service has been started
     * and start it, if necessary. It can only be started, if
     * there exists an active shell, which contains the
     * needed display.
     * @param display The model element with existing widget
     */
    public void checkRemoteControlService(Display display) {
        if (!m_hasRemoteControlServiceStarted) {
            if (startRemoteControlService(display)) {
                prepareRemoteControlService();
            }
        }
    }

    /**
     * Start the SwtAUTServer by connecting with the AUT agent.
     * @param display The SWT display.
     * @return True, if the AUTServer is already running, otherwise false.
     */
    private boolean startRemoteControlService(final Display display) {
        final Properties envVars =
                EnvironmentUtils.getProcessEnvironment();
        if (getValue(AutConfigConstants.AUT_AGENT_HOST, envVars) != null) {
            try {
                setAutAgentHost(getValue(
                        AutConfigConstants.AUT_AGENT_HOST, envVars));
                setAutAgentPort(getValue(
                        AutConfigConstants.AUT_AGENT_PORT, envVars));
                setAutID(getValue(
                        AutConfigConstants.AUT_NAME, envVars));
                setDisplay(display);
                start(true); // true = start an RCP accessor
                m_hasRemoteControlServiceStarted = true;
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        return m_hasRemoteControlServiceStarted;
    }

    /**
     * Prepare the SwtAUTServer for SWT components.
     */
    private static void prepareRemoteControlService() {
        // Registering the AdapterFactory for SWT at the registry
        GUIAdapterFactoryRegistry.getInstance()
            .registerFactory(new SWTAdapterFactory());
        // add listener to AUT
        AUTServer.getInstance().addToolKitEventListenerToAUT();
    }

    /**
     * Returns the value for a given property. First, <code>envVars</code>
     * is checked for the given property. If this property cannot be found
     * there, the Java System Properties will be checked. If the property
     * is not found there, <code>null</code> will be returned.
     *
     * @param envVars The first source to check for the given property.
     * @param propName The name of the property for which to find the value.
     * @return The value for the given property name, or <code>null</code> if
     *         the given property name cannot be found.
     */
    private static String getValue(String propName, Properties envVars) {
        String value = envVars.getProperty(propName);
        if (value == null) {
            value = System.getProperty(propName);
        }
        return value;
    }

}
