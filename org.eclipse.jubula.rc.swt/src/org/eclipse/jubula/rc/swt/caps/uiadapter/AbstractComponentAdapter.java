package org.eclipse.jubula.rc.swt.caps.uiadapter;

import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.uiadapter.interfaces.IComponentAdapter;
import org.eclipse.jubula.rc.swt.driver.RobotFactoryConfig;
/**
 * 
 * @author BREDEX GmBH
 *
 */
public abstract class AbstractComponentAdapter implements IComponentAdapter {

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

}
