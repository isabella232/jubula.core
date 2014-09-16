/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.desktop;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.autagent.agent.AutAgent;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 11.06.2010
 *
 */
public class DesktopIntegration implements PropertyChangeListener {
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(DesktopIntegration.class);
    
    /** is the system tryy supported on this platform */
    private boolean m_isSystraySupported;
    
    /** access to system tray */
    private TrayIcon m_trayIcon;
    
    /** status: port number */
    private int m_port = 0;
    
    /** status: connected AUTs */
    private List<String> m_auts = new ArrayList<String>();

    /**
     * create the necessary environment
     * 
     * @param autAgent The AUT Agent monitored by the created object.
     */
    public DesktopIntegration(final AutAgent autAgent) {
        m_isSystraySupported = SystemTray.isSupported();
        if (m_isSystraySupported) {

            SystemTray tray = SystemTray.getSystemTray();
            URL imageURL;
            final ClassLoader classLoader = this.getClass().getClassLoader();
            if (EnvironmentUtils.isMacOS()) {
                imageURL = classLoader.getResource("resources/gdagent_osx.png"); //$NON-NLS-1$
                
            } else {
                imageURL = classLoader.getResource("resources/gdagent.png"); //$NON-NLS-1$
            }
            Image image = Toolkit.getDefaultToolkit().getImage(imageURL);

            ActionListener exitListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };

            PopupMenu popup = new PopupMenu();
            final CheckboxMenuItem strictModeItem = new CheckboxMenuItem("Strict AUT Management"); //$NON-NLS-1$

            autAgent.addPropertyChangeListener(
                    AutAgent.PROP_KILL_DUPLICATE_AUTS, 
                    new PropertyChangeListener() {
                        @SuppressWarnings("synthetic-access")
                        public void propertyChange(PropertyChangeEvent evt) {
                            Object newValue = evt.getNewValue();
                            if (newValue instanceof Boolean) {
                                boolean isKillDuplicateAuts = 
                                    ((Boolean)newValue).booleanValue();
                                strictModeItem.setState(isKillDuplicateAuts);
                            } else {
                                LOG.error("Expected new value for property to be of type " + Boolean.class.getName()); //$NON-NLS-1$
                            }
                        }
                    });
            boolean isKillDuplicateAuts = autAgent.isKillDuplicateAuts();
            strictModeItem.setState(isKillDuplicateAuts);

            strictModeItem.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    autAgent.setKillDuplicateAuts(
                            e.getStateChange() == ItemEvent.SELECTED);
                }
            });
            
            MenuItem defaultItem = new MenuItem("Exit"); //$NON-NLS-1$
            defaultItem.addActionListener(exitListener);
            
            popup.add(strictModeItem);
            popup.addSeparator();
            popup.add(defaultItem);

            m_trayIcon = new TrayIcon(image, "AUT Agent", popup); //$NON-NLS-1$

            m_trayIcon.setImageAutoSize(true);

            try {
                tray.add(m_trayIcon);
            } catch (AWTException e) {
                m_isSystraySupported = false; // strange but ignorable
            }

        }
    }

    /**
     * update the tray icon when the status changes
     */
    private void updateStatus() {
        if (m_isSystraySupported) {
            m_trayIcon.setToolTip(buildToolTip());
        }        
    }
    /**
     * @return info according to status fields
     */
    private String buildToolTip() {
        StringBuilder tt = new StringBuilder("AUT Agent\n"); //$NON-NLS-1$
        tt.append(" Port used: "); //$NON-NLS-1$
        tt.append(m_port);
        if (!m_auts.isEmpty()) {
            tt.append('\n');
            tt.append(' ');
            tt.append(m_auts.size());
            tt.append(" running AUT"); //$NON-NLS-1$
            if (m_auts.size() == 1) {
                tt.append(':');
            } else {
                tt.append("s:"); //$NON-NLS-1$
            }
            for (String aut : m_auts) {
                tt.append('\n');
                tt.append(' ');
                tt.append(aut);
            }
        }
        
        return tt.toString();
    }
    
    /**
     * @param port info
     */
    public void setPort(int port) {
        m_port = port;
        updateStatus();
    }
    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AutAgent.PROP_NAME_AUTS)) {
            if (evt.getNewValue() instanceof AutIdentifier) {
                AutIdentifier aut = (AutIdentifier)evt.getNewValue();
                m_auts.add(aut.getExecutableName());
            }
            if (evt.getOldValue() instanceof AutIdentifier) {
                AutIdentifier aut = (AutIdentifier)evt.getOldValue();
                m_auts.remove(aut.getExecutableName());
            }
        }

        updateStatus();
    }

}
