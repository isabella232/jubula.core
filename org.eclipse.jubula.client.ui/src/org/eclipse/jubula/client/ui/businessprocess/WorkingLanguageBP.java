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
package org.eclipse.jubula.client.ui.businessprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectPropertiesModifyListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.Plugin;


/**
 * @author BREDEX GmbH
 * @created 30.03.2006
 */
public class WorkingLanguageBP extends AbstractActionBP 
                               implements IProjectLoadedListener {   
    
    /** <code>instance</code> single WorkingLanguageBP instance */
    private static WorkingLanguageBP instance = null;
    
    /** <code>m_currentLanguage</code> current language */
    private Locale m_currentLanguage = null;
    
    /**
     * <code>m_projectPropertiesModifyListener</code> listener for modification 
     * of project properties (in this case escpecially for modification of 
     * project languages
     */
    private IProjectPropertiesModifyListener m_projectPropertiesModifyListener =
        new IProjectPropertiesModifyListener() {
        
            public void handleProjectPropsChanged() {
                List <Locale> langs = GeneralStorage.getInstance().getProject()
                    .getLangHelper().getLanguageList();
                if (m_currentLanguage != null 
                    && !langs.contains(m_currentLanguage)) {
                    m_currentLanguage = null;
                    setCurrentLanguage(getWorkingLanguage());
                }
            }
        
        };

    /**
     * <code>m_dataChangedListener</code> listener for when project data 
     * changes; specifically, we are listening in case the current project 
     * is deleted
     */
    private IDataChangedListener m_dataChangedListener = 
        new IDataChangedListener() {
            /**
             * {@inheritDoc}
             */
            @SuppressWarnings("synthetic-access") 
            public void handleDataChanged(IPersistentObject po, 
                               DataState dataState, 
                               UpdateState updateState) {
                Plugin.showLangInfo();
                setEnabledStatus();
            }
        };

    /**
     * private constructor
     */
    private WorkingLanguageBP() {
        DataEventDispatcher dispatcher = DataEventDispatcher.getInstance();
        dispatcher.addProjectLoadedListener(this, true);
        dispatcher.addProjectPropertiesModifyListener(
                m_projectPropertiesModifyListener, true);
        dispatcher.addDataChangedListener(m_dataChangedListener, true);
    }
    
    /**
     * @return single WorkingLanguageBP instance
     */
    public static WorkingLanguageBP getInstance() {
        if (instance == null) {
            instance = new WorkingLanguageBP();
            
        }
        return instance;
    }
    
    /**
     * Checks if the AUT in the given ITestSuitePO supports the given Locale
     * @param locale the locale to check
     * @param testSuite the ITestSuitePO to check
     * @return true if the AUT in the given TestSuite support the Locale, false otherwise
     */
    public boolean isTestSuiteLanguage(Locale locale, ITestSuitePO testSuite) {
        if (testSuite.getAut() == null) {
            return false;
        }
        return testSuite.getAut().getLangHelper().getLanguageList()
            .contains(locale);
    }
    
    /**
     * @return current WorkingLanguage or null!
     */
    public Locale getWorkingLanguage() {
        Locale workingLang = null;
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            workingLang = (m_currentLanguage != null) 
                ? m_currentLanguage : project.getDefaultLanguage();
        }
        return workingLang;
    }

    /**
     * Returns all supported languages of the given IAUTMainPO
     * @param aut the aut whose languages to get
     * @return a List of locales
     */
    public List<Locale> getLanguages(IAUTMainPO aut) {
        return aut != null ? aut.getLangHelper().getLanguageList() 
            : new ArrayList<Locale>(0);
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        resetWorkingLanguageBP();
        Plugin.showLangInfo();
    }

    /**
     * reset members of WorkingLanguageBP
     */
    private void resetWorkingLanguageBP() {
        m_currentLanguage = null;
        setEnabledStatus();
    }

    /**
     * @param currentLanguage The currentLanguage to set.
     */
    public void setCurrentLanguage(Locale currentLanguage) {
        m_currentLanguage = currentLanguage;
        Plugin.showLangInfo();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return !isWorkingLanguageNull();
    }

    /**
     * @return true if the current project is null
     */    
    private boolean isWorkingLanguageNull() {
        return (getWorkingLanguage() == null);
    }

}