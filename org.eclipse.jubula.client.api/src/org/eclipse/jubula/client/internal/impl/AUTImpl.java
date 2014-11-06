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

import java.net.ConnectException;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.AUT;
import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.client.exceptions.ActionException;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.exceptions.CommunicationException;
import org.eclipse.jubula.client.exceptions.ComponentNotFoundException;
import org.eclipse.jubula.client.exceptions.ConfigurationException;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.client.internal.AUTConnection;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.internal.Synchronizer;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.communication.internal.message.CAPTestMessage;
import org.eclipse.jubula.communication.internal.message.CAPTestMessageFactory;
import org.eclipse.jubula.communication.internal.message.CAPTestResponseMessage;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.UnknownMessageException;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.toolkit.internal.AbstractToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;
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
    @NonNull private AutIdentifier m_autID;
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
     *            the toolkit information
     */
    public AUTImpl(
        @NonNull AutIdentifier autID, 
        @NonNull ToolkitInfo information) {
        Validate.notNull(autID, "The AUT-Identifier must not be null."); //$NON-NLS-1$
        Validate.notNull(information, "The toolkit information must not be null."); //$NON-NLS-1$
        
        m_autID = autID;
        setToolkitInformation((AbstractToolkitInfo)information);
    }

    /** {@inheritDoc} */
    public void connect() throws CommunicationException {
        if (!isConnected()) {
            final Map<ComponentClass, String> typeMapping = 
                getInformation().getTypeMapping();
            try {
                m_instance = AUTConnection.getInstance();
                m_instance.connectToAut(m_autID, typeMapping);
                if (!isConnected()) {
                    throw new CommunicationException(
                        new ConnectException(
                            "Could not connect to AUT: " //$NON-NLS-1$
                                + m_autID.getID() + ".")); //$NON-NLS-1$
                }
            } catch (ConnectionException e) {
                throw new CommunicationException(e);
            }
        } else {
            throw new IllegalStateException("AUT connection is already made"); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public void disconnect() {
        if (isConnected()) {
            m_instance.close();
        } else {
            throw new IllegalStateException("AUT connection is already disconnected"); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    public boolean isConnected() {
        return m_instance != null ? m_instance.isConnected() : false;
    }

    /** {@inheritDoc} */
    @NonNull 
    public AUTIdentifier getIdentifier() {
        return m_autID;
    }
    
    /** @param information the toolkit information to set */
    private void setToolkitInformation(AbstractToolkitInfo information) {
        m_information = information;
    }

    /**
     * @return the information
     */
    public AbstractToolkitInfo getInformation() {
        return m_information;
    }
    
    /** {@inheritDoc} */
    @NonNull
    public <T> Result<T> execute(@NonNull CAP cap, @Nullable T payload)
        throws ExecutionException, CommunicationException {
        Validate.notNull(cap, "The CAP must not be null."); //$NON-NLS-1$
        AUTAgentImpl.checkConnected(this);
        
        final ResultImpl<T> result = new ResultImpl<T>(cap, payload);
        try {
            CAPTestMessage capTestMessage = CAPTestMessageFactory
                .getCAPTestMessage((MessageCap) cap, getInformation()
                    .getToolkitID());

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
        } catch (NotConnectedException e) {
            throw new CommunicationException(e);
        } catch (UnknownMessageException e) {
            throw new CommunicationException(e);
        } catch (org.eclipse.jubula.tools.internal.
                exception.CommunicationException e) {
            throw new CommunicationException(e);
        } catch (CommunicationException e) {
            throw new CommunicationException(e);
        } catch (InterruptedException e) {
            throw new CommunicationException(e);
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
        @NonNull final Result result)
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
                Object actualValueObject = event.getProps().get(
                    TestErrorEvent.Property.ACTUAL_VALUE_KEY);
                @NonNull String actualValue = "n/a"; //$NON-NLS-1$
                if (actualValueObject instanceof String) {
                    actualValue = (String)actualValueObject;
                }
                throw new CheckFailedException(
                    result, description, actualValue);
            }
        }
    }
}