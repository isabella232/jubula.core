/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.html.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.toolkit.base.config.AbstractOSAUTConfiguration;
import org.eclipse.jubula.toolkit.html.Browser;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
public class HTMLAUTConfiguration extends AbstractOSAUTConfiguration {
    /** the URL to open */
    private URL m_url;
    /** the browser to use */
    private Browser m_browser;
    /** the browser path to use */
    private String m_browserPath;
    /** the AUT window mode */
    private boolean m_singleWindow = true;
    /** the name of the attribute used to retrieve a unique identifier */
    private String m_idAttributeName;

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param workingDir
     *            the working directory
     * @param sURL
     *            the URL to open
     * @param browser
     *            the browser type to use
     * @param browserPath
     *            the path to the browser executable
     * @param singleWindow
     *            whether the AUT should be opened in single window mode
     * @param idAttributeName
     *            the name of the attribute used to retrieve a unique identifier
     * @throws MalformedURLException
     *             If the URL string specifies an unknown protocol.
     */
    public HTMLAUTConfiguration(
            @Nullable String name, 
            @NonNull String autID,
            @NonNull String workingDir,
            @NonNull String sURL,
            @NonNull Browser browser,
            @Nullable String browserPath,
            boolean singleWindow,
            @Nullable String idAttributeName) throws MalformedURLException {
        super(name, autID, workingDir);
        
        Validate.notNull(sURL, "The URL must not be null"); //$NON-NLS-1$
        setUrl(new URL(sURL));

        Validate.notNull(browser, "The Browser must not be null"); //$NON-NLS-1$
        setBrowser(browser);
        
        if (browserPath != null && Browser.InternetExplorer.equals(browser)) {
            throw new IllegalArgumentException("Setting of browser path is not supported for " + browser); //$NON-NLS-1$
        }
        
        setBrowserPath(browserPath);
        setSingleWindow(singleWindow);
        setIdAttributeName(idAttributeName);

        // Toolkit specific information
        add(AutConfigConstants.AUT_URL, sURL);
        add(AutConfigConstants.BROWSER, browser.toString());
        add(AutConfigConstants.BROWSER_PATH, browserPath);
        add(AutConfigConstants.SINGLE_WINDOW_MODE, 
                String.valueOf(singleWindow));
        add(AutConfigConstants.WEB_ID_TAG, idAttributeName);
        add(ToolkitConstants.ATTR_TOOLKITID, CommandConstants.HTML_TOOLKIT);
    }

    /**
     * @return the URL
     */
    public URL getUrl() {
        return m_url;
    }

    /**
     * @param url the URL to set
     */
    private void setUrl(URL url) {
        m_url = url;
    }

    /**
     * @return the browser
     */
    public Browser getBrowser() {
        return m_browser;
    }

    /**
     * @param browser the browser to set
     */
    private void setBrowser(Browser browser) {
        m_browser = browser;
    }

    /**
     * @return the browserPath
     */
    public String getBrowserPath() {
        return m_browserPath;
    }

    /**
     * @param browserPath the browserPath to set
     */
    private void setBrowserPath(String browserPath) {
        m_browserPath = browserPath;
    }

    /**
     * @return the singleWindow
     */
    public boolean isSingleWindow() {
        return m_singleWindow;
    }

    /**
     * @param singleWindow the singleWindow to set
     */
    private void setSingleWindow(boolean singleWindow) {
        m_singleWindow = singleWindow;
    }

    /**
     * @return the idAttributeName
     */
    public String getIdAttributeName() {
        return m_idAttributeName;
    }

    /**
     * @param idAttributeName the idAttributeName to set
     */
    private void setIdAttributeName(String idAttributeName) {
        m_idAttributeName = idAttributeName;
    }
}