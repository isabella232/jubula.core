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
package org.eclipse.jubula.client.internal.impl;

import java.util.Map;

import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.client.exceptions.ActionException;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.exceptions.ComponentNotFoundException;
import org.eclipse.jubula.client.exceptions.ConfigurationException;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.client.internal.AUTConnection;
import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.communication.internal.message.CAPTestMessage;
import org.eclipse.jubula.communication.internal.message.CAPTestMessageFactory;
import org.eclipse.jubula.communication.internal.message.CAPTestResponseMessage;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.UnknownMessageException;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.toolkit.internal.AbstractToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author BREDEX GmbH */
public class AUTImpl implements AUT {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(AUTImpl.class);
    /** the AUT identifier */
    private AutIdentifier m_autID;
    /** the instance */
    private AUTConnection m_instance;
    /** the toolkit specific information */
    private AbstractToolkitInfo m_information;

    /**
     * Constructor
     * 
     * @param autID
     *            the identifier to use for connection
     * @param information 
     */
    public AUTImpl(AutIdentifier autID, ToolkitInfo information) {
        m_autID = autID;
        setToolkitInformation((AbstractToolkitInfo)information);
    }

    /** {@inheritDoc} */
    public void connect() throws Exception {
        final Map<ComponentClass, String> typeMapping = 
            getInformation().getTypeMapping();
        Assert.verify(typeMapping != null);
        m_instance = AUTConnection.getInstance();
        m_instance.connectToAut(m_autID, typeMapping);
    }

    /** {@inheritDoc} */
    public void disconnect() throws Exception {
        m_instance.close();
    }

    /** {@inheritDoc} */
    public boolean isConnected() {
        return m_instance != null ? m_instance.isConnected() : false;
    }

    /** {@inheritDoc} */
    public AUTIdentifier getIdentifier() {
        return m_autID;
    }
    
    /** {@inheritDoc} */
    public void setToolkitInformation(AbstractToolkitInfo information) {
        m_information = information;
    }

    /**
     * @return the information
     */
    public AbstractToolkitInfo getInformation() {
        return m_information;
    }
    
    /** {@inheritDoc} */
    public Result execute(CAP cap) throws ExecutionException {
        return execute(cap, null);
    }

    /** {@inheritDoc} */
    public <T> Result<T> execute(CAP cap, T payload) throws ExecutionException {
        final ResultImpl<T> result = new ResultImpl<T>(cap, payload);
        try {
            CAPTestMessage capTestMessage = CAPTestMessageFactory
                .getCAPTestMessage((MessageCap)cap,
                    getInformation().getToolkitID());

            m_instance.send(capTestMessage);

            Object exchange = Synchronizer.instance().exchange(null);
            if (exchange instanceof CAPTestResponseMessage) {
                CAPTestResponseMessage response = 
                    (CAPTestResponseMessage) exchange;
                processResponse(response, result);
                result.setOK(true);
            } else {
                log.error("Unexpected response received: " //$NON-NLS-1$
                    + String.valueOf(exchange));
            }

        } catch (UnknownMessageException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (CommunicationException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return result;
    }

    /**
     * @param result
     *            the result
     * @param response
     *            the response to process
     */
    private void processResponse(CAPTestResponseMessage response,
        final Result result)
        throws ExecutionException {
        if (response.hasTestErrorEvent()) {
            final TestErrorEvent event = response.getTestErrorEvent();
            final String eventId = event.getId();
            Map<Object, Object> eventProps = event.getProps();
            String description = null;
            if (eventProps.containsKey(
                TestErrorEvent.Property.DESCRIPTION_KEY)) {
                String key = (String) eventProps
                    .get(TestErrorEvent.Property.DESCRIPTION_KEY);
                Object[] args = (Object[]) eventProps
                    .get(TestErrorEvent.Property.PARAMETER_KEY);
                args = args != null ? args : new Object[0];
                description = I18n.getString(key, args);
            }
            if (TestErrorEvent.ID.ACTION_ERROR.equals(eventId)) {
                throw new ActionException(result, description);
            } else if (TestErrorEvent.ID.COMPONENT_NOT_FOUND.equals(eventId)) {
                throw new ComponentNotFoundException(result, description);
            } else if (TestErrorEvent.ID.CONFIGURATION_ERROR.equals(eventId)) {
                throw new ConfigurationException(result, description);
            } else if (TestErrorEvent.ID.VERIFY_FAILED.equals(eventId)) {
                Object actualValue = event.getProps().get(
                    TestErrorEvent.Property.ACTUAL_VALUE_KEY);
                throw new CheckFailedException(result, description,
                    String.valueOf(actualValue));
            }
        }
    }
}