package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponentAdapter;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.driver.RobotFactoryConfig;
/**
 * 
 * @author BREDEX GmBH
 *
 */
public abstract class AbstractComponentAdapter implements IComponentAdapter {
   
    /** constants for communication */
    protected static final String POS_UNIT_PIXEL = "Pixel"; //$NON-NLS-1$
    /** constants for communication */
    protected static final String POS_UNI_PERCENT = "Percent"; //$NON-NLS-1$

    /** the RobotFactory from the AUT */
    private IRobotFactory m_robotFactory;     
    

    /**
     * Gets the Robot factory. The factory is created once per instance.
     *
     * @return The Robot factory.
     */
    public IRobotFactory getRobotFactory() {
        if (m_robotFactory == null) {
            m_robotFactory = new RobotFactoryConfig().getRobotFactory();
        }
        return m_robotFactory;
    }
    
    /**
     * @return The event thread queuer.
     */
    public IEventThreadQueuer getEventThreadQueuer() {
        return getRobotFactory().getEventThreadQueuer();
    }
    
    /**
     * Gets the Robot. 
     * @return The Robot
     * @throws RobotException If the Robot cannot be created.
     */
    protected IRobot getRobot() throws RobotException {
        return AUTServer.getInstance().getRobot();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getKeyCode(String mod) {
        return KeyCodeConverter.getKeyCode(mod);
    }

    
}
