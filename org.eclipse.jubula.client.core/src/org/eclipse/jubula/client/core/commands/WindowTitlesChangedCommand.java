package org.eclipse.jubula.client.core.commands;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.ICommand;
import org.eclipse.jubula.communication.message.Message;
import org.eclipse.jubula.communication.message.html.WindowTitlesMessage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The command object for WindowTitlesMessage. <br>
 * 
 * The <code>execute()</code> method calls the <code>fireWindowsChanged()</code>
 * of the DataEventDispatcher to notify changes in the count and titles of the windows
 * @author BREDEX GmbH
 *
 */
public class WindowTitlesChangedCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(WindowTitlesChangedCommand.class);

    
    /** */
    private WindowTitlesMessage m_message;
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }
    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (WindowTitlesMessage) message;
    }
    /**
     * {@inheritDoc}
     */
    public Message execute() {
        //do nothing since this is only for the data
        DataEventDispatcher.getInstance()
            .fireWindowsChanged(m_message.getWindowTitles());
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + StringConstants.DOT 
                + Messages.TimeoutCalled);
    }

}
