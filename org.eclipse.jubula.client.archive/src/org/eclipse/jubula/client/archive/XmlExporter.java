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
package org.eclipse.jubula.client.archive;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jubula.client.archive.i18n.Messages;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICapParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.ICheckConfContPO;
import org.eclipse.jubula.client.core.model.ICheckConfPO;
import org.eclipse.jubula.client.core.model.ICompIdentifierPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IDocAttributeDescriptionPO;
import org.eclipse.jubula.client.core.model.IDocAttributeListPO;
import org.eclipse.jubula.client.core.model.IDocAttributePO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.II18NStringPO;
import org.eclipse.jubula.client.core.model.IListWrapperPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IObjectMappingProfilePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManagerPO;
import org.eclipse.jubula.client.core.model.ITestDataPO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestResultSummary;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.IUsedToolkitPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ISpecPersistable;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.persistence.TestResultSummaryPM;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.jubula.tools.exception.GDException;
import org.eclipse.jubula.tools.exception.GDProjectDeletedException;
import org.eclipse.jubula.tools.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.objects.IMonitoringValue;
import org.eclipse.jubula.tools.objects.MonitoringValue;

import com.bredexsw.guidancer.client.importer.gdschema.ActivationMethodEnum;
import com.bredexsw.guidancer.client.importer.gdschema.Attribute;
import com.bredexsw.guidancer.client.importer.gdschema.Aut;
import com.bredexsw.guidancer.client.importer.gdschema.AutConfig;
import com.bredexsw.guidancer.client.importer.gdschema.Cap;
import com.bredexsw.guidancer.client.importer.gdschema.Category;
import com.bredexsw.guidancer.client.importer.gdschema.CheckActivatedContext;
import com.bredexsw.guidancer.client.importer.gdschema.CheckAttribute;
import com.bredexsw.guidancer.client.importer.gdschema.CheckConfiguration;
import com.bredexsw.guidancer.client.importer.gdschema.CompNames;
import com.bredexsw.guidancer.client.importer.gdschema.ComponentName;
import com.bredexsw.guidancer.client.importer.gdschema.EventHandler;
import com.bredexsw.guidancer.client.importer.gdschema.EventTestCase;
import com.bredexsw.guidancer.client.importer.gdschema.I18NString;
import com.bredexsw.guidancer.client.importer.gdschema.MapEntry;
import com.bredexsw.guidancer.client.importer.gdschema.MonitoringValues;
import com.bredexsw.guidancer.client.importer.gdschema.NamedTestData;
import com.bredexsw.guidancer.client.importer.gdschema.Node;
import com.bredexsw.guidancer.client.importer.gdschema.ObjectMapping;
import com.bredexsw.guidancer.client.importer.gdschema.ObjectMappingProfile;
import com.bredexsw.guidancer.client.importer.gdschema.OmCategory;
import com.bredexsw.guidancer.client.importer.gdschema.OmEntry;
import com.bredexsw.guidancer.client.importer.gdschema.ParamDescription;
import com.bredexsw.guidancer.client.importer.gdschema.Project;
import com.bredexsw.guidancer.client.importer.gdschema.ReentryProperty;
import com.bredexsw.guidancer.client.importer.gdschema.ReentryProperty.Enum;
import com.bredexsw.guidancer.client.importer.gdschema.RefTestCase;
import com.bredexsw.guidancer.client.importer.gdschema.RefTestSuite;
import com.bredexsw.guidancer.client.importer.gdschema.ReusedProject;
import com.bredexsw.guidancer.client.importer.gdschema.SummaryAttribute;
import com.bredexsw.guidancer.client.importer.gdschema.TechnicalName;
import com.bredexsw.guidancer.client.importer.gdschema.TestCase;
import com.bredexsw.guidancer.client.importer.gdschema.TestCase.Teststep;
import com.bredexsw.guidancer.client.importer.gdschema.TestData;
import com.bredexsw.guidancer.client.importer.gdschema.TestDataCell;
import com.bredexsw.guidancer.client.importer.gdschema.TestDataRow;
import com.bredexsw.guidancer.client.importer.gdschema.TestJobs;
import com.bredexsw.guidancer.client.importer.gdschema.TestSuite;
import com.bredexsw.guidancer.client.importer.gdschema.TestresultSummaries;
import com.bredexsw.guidancer.client.importer.gdschema.TestresultSummary;
import com.bredexsw.guidancer.client.importer.gdschema.UsedToolkit;

/**
 * @author BREDEX GmbH
 * @created 11.01.2006
 */
class XmlExporter {

    /** compares locales alphabetically according to code */
    private static final Comparator<Locale> LANG_CODE_ALPHA_COMPARATOR =
        new Comparator<Locale>() {
                public int compare(Locale locale1, Locale locale2) {
                    return ObjectUtils.toString(locale1).compareTo(
                            ObjectUtils.toString(locale2));
                }
            };
    
    /** Table identifier to make the ID disjunct */
    private static final char TABLE_AUT = 'A';

    /** Table identifier to make the ID disjunct */
    private static final char TABLE_AUT_CONFIG = 'C';

    /** standard logging */
    private static Log log = LogFactory.getLog(XmlExporter.class);

    /** The progress monitor for the operation. */
    private IProgressMonitor m_monitor;

    /**
     * Constructor
     * 
     * @param monitor
     *            The progress monitor for the export operation.
     */
    public XmlExporter(IProgressMonitor monitor) {
        m_monitor = monitor;
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillAUTConfig(AutConfig xml, IAUTConfigPO po) {
        xml.setId(i2id(TABLE_AUT_CONFIG, po.getId()));
        xml.setName(po.getName());

        // FIXME BEGIN : only for compatibility reasons. Remove in version > 2.0
        xml.setClassname(StringConstants.EMPTY);
        xml.setClasspath(StringConstants.EMPTY);
        xml.setJarfile(StringConstants.EMPTY);
        xml.setParameter(StringConstants.EMPTY);
        xml.setWorkingDir(StringConstants.EMPTY);
        xml.setJreDir(StringConstants.EMPTY);
        xml.setJreParameter(StringConstants.EMPTY);
        xml.setServer(StringConstants.EMPTY);
        xml.setEnvironment(StringConstants.EMPTY);
        xml.setActivateApp(false);
        xml.setActivationMethod(ActivationMethodEnum.NONE);
        // FIXME END
        
        // Sort the list of configuration entries by key
        final List<String> sortedConfigKeys = 
            new ArrayList<String>(po.getAutConfigKeys());
        Collections.sort(sortedConfigKeys);

        for (String key : sortedConfigKeys) {
            final MapEntry entry = xml.addNewConfAttrMapEntry();
            entry.setKey(key);
            entry.setValue(po.getValue(key, StringConstants.EMPTY));
        }
        
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillAUT(Aut xml, IAUTMainPO po) {
        xml.setId(i2id(TABLE_AUT, po.getId()));
        xml.setName(po.getName());
        xml.setAutToolkit(po.getToolkit());
        xml.setGenerateNames(po.isGenerateNames());

        ObjectMapping om = xml.addNewObjectMapping();
        fillObjectMapping(om, po.getObjMap());

        for (Locale l : po.getLangHelper().getLanguageList()) {
            xml.addLanguage(l.toString());
        }

        // Sort the list of AUT Configurations alphabetically by name
        List<IAUTConfigPO> sortedAutConfigs = 
            new ArrayList<IAUTConfigPO>(po.getAutConfigSet());
        Collections.sort(sortedAutConfigs, new Comparator<IAUTConfigPO>() {
            public int compare(IAUTConfigPO autConfig1, 
                    IAUTConfigPO autConfig2) {
                
                return autConfig1.getName().compareTo(autConfig2.getName());
            }
        });
        for (IAUTConfigPO conf : sortedAutConfigs) {
            AutConfig xmlConf = xml.addNewConfig();
            fillAUTConfig(xmlConf, conf);
        }

        for (String autId : po.getAutIds()) {
            xml.addAutId(autId);
        }

    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillObjectMapping(ObjectMapping xml, IObjectMappingPO po) {

        IObjectMappingProfilePO profilePo = po.getProfile();
        ObjectMappingProfile profileXml = xml.addNewProfile();
        profileXml.setName(profilePo.getName());
        profileXml.setContextFactor(profilePo.getContextFactor());
        profileXml.setNameFactor(profilePo.getNameFactor());
        profileXml.setPathFactor(profilePo.getPathFactor());
        profileXml.setThreshold(profilePo.getThreshold());

        fillObjectMappingCategory(xml.addNewMapped(), po.getMappedCategory());
        fillObjectMappingCategory(xml.addNewUnmappedComponent(), po
                .getUnmappedLogicalCategory());
        fillObjectMappingCategory(xml.addNewUnmappedTechnical(), po
                .getUnmappedTechnicalCategory());
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param categoryXml
     *            The XML element to be filled.
     * @param category
     *            The persistent object which contains the information
     */
    private void fillObjectMappingCategory(OmCategory categoryXml,
            IObjectMappingCategoryPO category) {

        categoryXml.setName(category.getName());
        for (IObjectMappingCategoryPO subcategory : category
                .getUnmodifiableCategoryList()) {

            OmCategory subcategoryXml = categoryXml.addNewCategory();
            fillObjectMappingCategory(subcategoryXml, subcategory);
        }
        for (IObjectMappingAssoziationPO assoc : category
                .getUnmodifiableAssociationList()) {

            OmEntry assocXml = categoryXml.addNewAssociation();
            fillObjectMappingAssociation(assocXml, assoc);
        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param assocXml
     *            The XML element to be filled.
     * @param assoc
     *            The persistent object which contains the information
     */
    private void fillObjectMappingAssociation(OmEntry assocXml,
            IObjectMappingAssoziationPO assoc) {

        final ICompIdentifierPO technicalName = assoc.getTechnicalName();
        // tecName == null means not mapped

        if (technicalName != null) {
            TechnicalName xmlTecName = assocXml.addNewTechnicalName();
            fillTechnicalName(xmlTecName, technicalName);
        }
        assocXml.setType(assoc.getType());

        for (String logicalName : assoc.getLogicalNames()) {
            assocXml.addLogicalName(logicalName);
        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillTechnicalName(TechnicalName xml, ICompIdentifierPO po) {
        xml.setComponentClassName(po.getComponentClassName());
        xml.setSupportedClassName(po.getSupportedClassName());
        xml.setAlternativeDisplayName(po.getAlternativeDisplayName());

        for (Object n : po.getNeighbours()) {
            String neighbour = (String)n;
            xml.addNeighbour(neighbour);
        }
        for (Object h : po.getHierarchyNames()) {
            String hierarchy = (String)h;
            xml.addHierarchyName(hierarchy);
        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     * @param includeTestResultSummaries 
     *                 Whether to save the Test Result Summaries as well.
     * 
     * @throws OperationCanceledException if the operation was canceled.
     */
    public void fillProject(Project xml, IProjectPO po, 
            boolean includeTestResultSummaries)
        throws GDProjectDeletedException, PMException, 
            OperationCanceledException {
        fillNode(xml, po);
        fillAttributes(xml, po, po.getProjectAttributeDescriptions());
        checkForCancel();
        fillCheckConfiguration(
                xml, po.getProjectProperties().getCheckConfCont());
        
        // used toolkits
        fillUsedToolkits(xml, po);
        // All project toolkit info finished
        m_monitor.worked(1);
        fillComponentNames(xml, po);
        // aut toolkit
        final String autToolKit = po.getToolkit();
        xml.setAutToolKit(autToolKit);
        // AUT toolkit finished
        m_monitor.worked(1);
        // project languages
        xml.setDefaultLanguage(po.getDefaultLanguage().toString());
        for (Locale l : po.getLangHelper().getLanguageList()) {
            xml.addProjectLanguage(l.toString());
            // Project language finished
            m_monitor.worked(1);
        }
        // Test Data Cubes
        for (IParameterInterfacePO testDataCube 
                : po.getTestDataCubeCont().getTestDataCubeList()) {
            NamedTestData xmlTestDataCube = xml.addNewNamedTestData();
            fillNamedTestData(xmlTestDataCube, testDataCube);
        }
        // AUTs
        for (IAUTMainPO aut : getSortedAutList(po)) {
            Aut xmlAut = xml.addNewAut();
            fillAUT(xmlAut, aut);
            // AUT finished
            m_monitor.worked(1);
        }
        // test cases and categories (visible in test case view)
        List<IDocAttributeDescriptionPO> tcDescList = po
                .getTestCaseAttributeDescriptions();
        for (ISpecPersistable tcOrCat : po.getSpecObjCont().getSpecObjList()) {
            checkForCancel();
            if (tcOrCat instanceof ICategoryPO) {
                Category cat = xml.addNewCategory();
                fillCategory(cat, (ICategoryPO)tcOrCat);
            } else {
                TestCase tc = xml.addNewTestcase();
                fillTestCase(tc, (ISpecTestCasePO)tcOrCat);
                fillAttributes(tc, tcOrCat, tcDescList);
            }
        }
        // test suites
        handleTestSuites(xml, po);
        checkForCancel();
        // test jobs
        handleTestJobs(xml, po);
        checkForCancel();
        // reused projects
        handleReusedProjects(xml, po);
        checkForCancel();
        
        // testresult summaries
        if (includeTestResultSummaries) {
            fillTestResultSummary(xml, po);
        }
        checkForCancel();
        xml.setMetaDataVersion(po.getClientMetaDataVersion());
        xml.setMajorProjectVersion(po.getMajorProjectVersion());
        xml.setMinorProjectVersion(po.getMinorProjectVersion());
        xml.setIsReusable(po.getIsReusable());
        xml.setIsProtected(po.getIsProtected());
        xml.setTestResultDetailsCleanupInterval(
                po.getTestResultCleanupInterval());
        m_monitor.worked(1);
    }

    /**
     * @param xml
     *            The project node of the xml which will be filled with the
     *            check configuration.
     * @param checkConfCont
     *            The check configuration object as the source of the stuff
     *            which will be put in the xml.
     */
    private void fillCheckConfiguration(Project xml,
            ICheckConfContPO checkConfCont) { 
        for (String chkId : checkConfCont.getConfMap().keySet()) {
            checkForCancel();
            ICheckConfPO chkConf = checkConfCont.getConfMap().get(chkId);
            CheckConfiguration xmlChkConf = xml.addNewCheckConfiguration();
            
            xmlChkConf.setCheckId(chkId);
            xmlChkConf.setActivated(chkConf.isActive());
            xmlChkConf.setSeverity(chkConf.getSeverity());
            fillCheckAttribute(xmlChkConf, chkConf.getAttr());
            fillCheckContext(xmlChkConf, chkConf.getContexts());
        }
    }

    /**
     * @param xmlChkConf
     *            The xml configuration where the attributes should be saved.
     * @param contexts
     *            The map where the context activation will be saved.
     */
    private void fillCheckContext(CheckConfiguration xmlChkConf,
            Map<String, Boolean> contexts) {
        for (Entry<String, Boolean> e : contexts.entrySet()) {
            checkForCancel();
            CheckActivatedContext c = xmlChkConf.addNewActiveContext();
            c.setClass1(e.getKey());
            Object obj = e.getValue();
            if (obj instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal)obj;
                c.setActive(bd.equals(BigDecimal.ONE) ? true : false);
            } else {
                c.setActive(e.getValue());
            }
        }
    }

    /**
     * @param xmlChkConf
     *            The xml configuration where the attributes should be saved.
     * @param attr
     *            The map where the attributes will be saved.
     */
    private void fillCheckAttribute(CheckConfiguration xmlChkConf,
            Map<String, String> attr) {
        for (Entry<String, String> e : attr.entrySet()) {
            checkForCancel();
            CheckAttribute chkAttr = xmlChkConf.addNewCheckAttribute();
            chkAttr.setName(e.getKey());
            chkAttr.setValue(e.getValue());
        }
    }

    /**
     * Sorts (by GUID) a copy of the AUT list of the given Project and returns 
     * that sorted copy.
     * 
     * @param po The Project containing the list of AUTs to sort.
     * @return the sorted copy.
     */
    private List<IAUTMainPO> getSortedAutList(IProjectPO po) {
        List<IAUTMainPO> sortedAuts = 
            new ArrayList<IAUTMainPO>(po.getAutMainList());
        Collections.sort(sortedAuts, new Comparator<IAUTMainPO>() {
            public int compare(IAUTMainPO aut1, IAUTMainPO aut2) {
                return aut1.getGuid().compareTo(aut2.getGuid());
            }
        });
        return sortedAuts;
    }

    /**
     * @param xml
     *            The XML element representation of the project.
     * @param po
     *            The PO representation of the project.
     */
    private void fillTestResultSummary(Project xml, IProjectPO po)
        throws PMException {
        
        PropertyDescriptor [] properties = 
            XmlImporter.BEAN_UTILS.getPropertyUtils()
                .getPropertyDescriptors(ITestResultSummary.class);
        List<ITestResultSummaryPO> poSummaryList = 
                TestResultSummaryPM.getAllTestResultSummaries(po, null);
        TestresultSummaries xmlSummaryList = xml.addNewTestresultSummaries();
        for (ITestResultSummaryPO poSummary : poSummaryList) {
            checkForCancel();
            if (!poSummary.isTestsuiteRelevant()) {
                continue;
            }
            TestresultSummary xmlSummary = 
                xmlSummaryList.addNewTestresultSummary();
            for (PropertyDescriptor p : properties) {
                String pName = p.getName();                
                try {                                        
                    String pValue = 
                        XmlImporter.BEAN_UTILS.getProperty(poSummary, pName);
                    Class<? extends Object> pType = p.getPropertyType();
                    SummaryAttribute xmlSummaryAttribute = 
                        xmlSummary.addNewAttribute();
                    xmlSummaryAttribute.setKey(pName);
                    if (pValue != null) {
                        xmlSummaryAttribute.setValue(pValue);
                    } else {
                        xmlSummaryAttribute.setNilValue();
                    }
                    xmlSummaryAttribute.setType(pType.getName());
                } catch (NoSuchMethodException e) {
                    log.warn(e);
                } catch (IllegalAccessException e) {
                    log.warn(e);
                } catch (InvocationTargetException e) {
                    log.warn(e);
                }
            }           
            Map<String, IMonitoringValue> 
                    tmpMap = poSummary.getMonitoringValues();
            Iterator it = tmpMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                MonitoringValue tmp = (MonitoringValue)pairs.getValue();
                MonitoringValues monTmp = xmlSummary.addNewMonitoringValue(); 
                monTmp.setKey((String)pairs.getKey());
                monTmp.setCategory(tmp.getCategory());
                monTmp.setIsSignificant(tmp.isSignificant());
                monTmp.setType(tmp.getType());
                monTmp.setValue(tmp.getValue());
              
            }
            
        }
    }

    /**
     * Writes all of the given project's test suite information to XML.
     * 
     * @param xml
     *            The XML element representation of the project.
     * @param po
     *            The PO representation of the project.
     */
    private void handleTestSuites(Project xml, IProjectPO po) {
        List<IDocAttributeDescriptionPO> tsDescs = po
                .getTestSuiteAttributeDescriptions();
        for (ITestSuitePO ts : po.getTestSuiteCont().getTestSuiteList()) {
            checkForCancel();
            TestSuite xmlTs = xml.addNewTestsuite();
            fillTestsuite(xmlTs, ts);
            fillAttributes(xmlTs, ts, tsDescs);
        }
    }

    /**
     * Writes all of the given project's test job information to XML.
     * 
     * @param xml
     *            The XML element representation of the project.
     * @param po
     *            The PO representation of the project.
     */
    private void handleTestJobs(Project xml, IProjectPO po) {
        for (ITestJobPO tj : po.getTestJobCont().getTestJobList()) {
            checkForCancel();
            TestJobs xmlTj = xml.addNewTestJobs();
            fillNode(xmlTj, tj);
            fillTestJob(xmlTj, tj);
        }
    }

    /**
     * @param xmlTj xml storage
     * @param tj test jobs to write
     */
    private void fillTestJob(TestJobs xmlTj, ITestJobPO tj) {
        for (Object child : tj.getUnmodifiableNodeList()) {
            if (child instanceof IRefTestSuitePO) {
                IRefTestSuitePO rts = (IRefTestSuitePO)child;
                RefTestSuite xmlRts = xmlTj.addNewRefTestSuite();
                fillNode(xmlRts, rts);
                xmlRts.setName(rts.getName());
                xmlRts.setGUID(rts.getGuid());
                xmlRts.setTsGuid(rts.getTestSuiteGuid());
                xmlRts.setAutId(rts.getTestSuiteAutID());
            }
        }
    }

    /**
     * Writes all of the given project's reused project information to XML.
     * 
     * @param xml
     *            The XML element representation of the project.
     * @param po
     *            The PO representation of the project.
     */
    private void handleReusedProjects(Project xml, IProjectPO po) {
        for (IReusedProjectPO reusedProject : po.getUsedProjects()) {
            checkForCancel();
            ReusedProject xmlReused = xml.addNewReusedProjects();
            fillReusedProject(xmlReused, reusedProject);
            m_monitor.worked(1);
        }
    }

    /**
     * Write the information from the ComponentNamePOs to the corresponding XML
     * element.
     * 
     * @param projXml
     *            the XML-Project
     * @param projPo
     *            The IProjectPO.
     */
    private void fillComponentNames(Project projXml, IProjectPO projPo)
        throws PMException {
        final Collection<IComponentNamePO> allCompNamePOs = ComponentNamesBP
                .getInstance().getAllComponentNamePOs(projPo.getId());
        for (IComponentNamePO compName : allCompNamePOs) {
            final ComponentName newXmlCompName = projXml.addNewComponentNames();
            newXmlCompName.setGUID(compName.getGuid());
            newXmlCompName.setCompType(compName.getComponentType());
            newXmlCompName.setCompName(compName.getName());
            newXmlCompName.setCreationContext(compName.getCreationContext()
                    .toString());
            newXmlCompName.setRefGuid(compName.getReferencedGuid());
            m_monitor.worked(1);
        }
    }

    /**
     * Adds the toolkits used by the given project to the given XML element.
     * 
     * @param xml
     *            The XML element to which the toolkits will be added.
     * @param po
     *            The project from which the toolkits will be read.
     * @throws GDProjectDeletedException
     *             if the project was deleted while toolkits were being read.
     * @throws PMSaveException
     *             if a database error occurs while reading the project
     *             toolkits.
     */
    private void fillUsedToolkits(Project xml, IProjectPO po)
        throws GDProjectDeletedException, PMSaveException {

        UsedToolkitBP toolkitBP = UsedToolkitBP.getInstance();
        try {
            toolkitBP.refreshToolkitInfo(po);
        } catch (PMException e) {
            throw new PMSaveException(
                    Messages.DataBaseErrorUpdatingToolkits
                            + String.valueOf(po.getName())
                            + Messages.OriginalException + e.toString()
                    , MessageIDs.E_FILE_IO);
        }
        final Set<IUsedToolkitPO> toolkits = toolkitBP.getUsedToolkits();
        for (IUsedToolkitPO toolkit : toolkits) {
            final UsedToolkit usedToolkit = xml.addNewUsedToolkit();
            fillUsedToolkit(usedToolkit, toolkit);
        }
    }

    /**
     * Adds the attributes for the given node to the given XML element.
     * 
     * @param xml
     *            The XML element to which the attributes will be added.
     * @param po
     *            The node from which the attributes will be read.
     * @param descList
     *            List describing which types of attributes should be exported
     *            and in what order they should be exported.
     */
    private void fillAttributes(Node xml, INodePO po,
            List<IDocAttributeDescriptionPO> descList) {
        for (IDocAttributeDescriptionPO desc : descList) {
            IDocAttributePO attrPo = po.getDocAttribute(desc);
            if (attrPo != null) {
                Attribute attrXml = xml.addNewAttribute();
                fillAttribute(attrXml, attrPo, desc);
            }
        }
    }

    /**
     * Adds the subattributes for the given attribute to the given XML element.
     * 
     * @param xml
     *            The XML element to which the attributes will be added.
     * @param po
     *            The attribute from which the subattributes will be read.
     * @param description
     *            The subattribute's type.
     */
    private void fillAttribute(Attribute xml, IDocAttributePO po,
            IDocAttributeDescriptionPO description) {

        xml.setValue(po.getValue());
        xml.setType(description.getLabelKey());
        for (IDocAttributeDescriptionPO subDesc : po.getDocAttributeTypes()) {
            IDocAttributeListPO attrList = po.getDocAttributeList(subDesc);
            if (attrList != null) {
                for (IDocAttributePO subAttrPo : attrList.getAttributes()) {
                    Attribute subAttrXml = xml.addNewSubAttribute();
                    fillAttribute(subAttrXml, subAttrPo, subDesc);
                }
            }
        }
    }

    /**
     * @param xml
     *            The XML element to be filled
     * @param usedToolkit
     *            The persistent object which contains the information
     */
    private void fillUsedToolkit(UsedToolkit xml, IUsedToolkitPO usedToolkit) {
        xml.setName(usedToolkit.getToolkitId());
        xml.setMajorVersion(usedToolkit.getMajorVersion());
        xml.setMinorVersion(usedToolkit.getMinorVersion());
    }

    /**
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillReusedProject(ReusedProject xml, IReusedProjectPO po) {
        xml.setProjectName(ProjectNameBP.getInstance().getName(
                po.getProjectGuid()));
        xml.setProjectGUID(po.getProjectGuid());
        xml.setMajorProjectVersion(po.getMajorNumber());
        xml.setMinorProjectVersion(po.getMinorNumber());
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillNode(Node xml, INodePO po) {
        checkForCancel();
        String name = po.getName();
        if (po instanceof IExecTestCasePO) {
            name = ((IExecTestCasePO)po).getRealName();
            if (name == null) {
                name = StringConstants.EMPTY;
            }
        }
        xml.setName(po.getName());
        xml.setComment(po.getComment());
        xml.setGUID(po.getGuid());
        xml.setGenerated(po.isGenerated());
        xml.setActive(po.isActive());

        // Finished a node
        m_monitor.worked(1);
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillTestsuite(TestSuite xml, ITestSuitePO po) {
        fillNode(xml, po);

        checkForCancel();

        if (po.getAut() != null) {
            xml.setSelectedAut(i2id(TABLE_AUT, po.getAut().getId()));
        } else {
            xml.setSelectedAut(null);
        }
        xml.setStepDelay(po.getStepDelay());
        xml.setCommandLineParameter(po.getCmdLineParameter());

        for (Object o : po.getUnmodifiableNodeList()) {
            if (o instanceof IExecTestCasePO) {
                IExecTestCasePO tc = (IExecTestCasePO)o;
                RefTestCase xmlTc = xml.addNewUsedTestcase();
                fillRefTestCase(xmlTc, tc);
            }
        }
        for (Object o : po.getDefaultEventHandler().keySet()) {
            String eventType = (String)o;
            Integer evProp = po.getDefaultEventHandler().get(eventType);
            Enum reentryProperty = ReentryProperty.Enum.forInt(evProp);
            EventHandler xmlEvHandler = xml.addNewEventHandler();
            xmlEvHandler.setEvent(eventType);
            xmlEvHandler.setReentryProperty(reentryProperty);

            // Trac#1908
            // since EventHandler on TestSuites are fakes, we can not
            // use the real data. The default for this is set to 1.
            if (reentryProperty == ReentryProperty.RETRY) {
                xmlEvHandler.setMaxRetries(1);
            }

        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillRefTestCase(RefTestCase xml, IExecTestCasePO po) {
        fillNode(xml, po);

        checkForCancel();

        String execName = po.getRealName();
        if (execName == null) {
            execName = StringConstants.EMPTY;
        }

        if (po.getSpecTestCase() != null) {
            String specName = po.getSpecTestCase().getName();
            if (execName.equals(specName)) {
                xml.setName(null);
            } else {
                xml.setName(execName);
            }
        } else {
            xml.setName(execName);
        }
        xml.setTestcaseGuid(po.getSpecTestCaseGuid());
        if (po.getProjectGuid() != null) {
            xml.setProjectGuid(po.getProjectGuid());
        } else if (po.getSpecTestCase() != null) {
            // FIXME zeb This is only necessary now because project guid was not
            // created when the test case was created. Once all current
            // projects are converted, we won't need this if/else anymore.
            // This will not work for testcases that don't exist in the
            // exporting DB.
            try {
                xml.setProjectGuid(ProjectPM.loadProjectById(
                        po.getSpecTestCase().getParentProjectId()).getGuid());
            } catch (GDException e) {
                // FIXME zeb Could not load project for spec testcase
            }
        }
        xml.setHasOwnTestdata(!po.getHasReferencedTD());
        xml.setDatafile(po.getDataFile());
        if (po.getReferencedDataCube() != null) {
            xml.setReferencedTestData(po.getReferencedDataCube().getName());
        }
        
        if (!po.getHasReferencedTD()) {
            // ExecTestCasePO doesn't have an own parameter list.
            // It uses generally the parameter from the associated
            // SpecTestCase.
            final ITDManagerPO dataManager = po.getDataManager();
            if (dataManager != null) {
                TestData xmlTD = xml.addNewTestdata();
                if (po.getReferencedDataCube() == null) {
                    fillTestData(xmlTD, dataManager);
                }
            }
        }

        for (ICompNamesPairPO name : po.getCompNamesPairs()) {
            CompNames xmlNames = xml.addNewOverriddenNames();
            xmlNames.setOriginalName(name.getFirstName());
            xmlNames.setNewName(name.getSecondName());
            xmlNames.setPropagated(name.isPropagated());
            xmlNames.setType((name.getType() == null) 
                    ? StringConstants.EMPTY : name.getType());
        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillEventTestCase(EventTestCase xml, IEventExecTestCasePO po) {
        fillRefTestCase(xml, po);
        xml.setEventType(po.getEventType());
        Enum reentryProperty = ReentryProperty.Enum.forInt(po.getReentryProp()
                .getValue());
        xml.setReentryProperty(reentryProperty);
        if (reentryProperty == ReentryProperty.RETRY) {
            Integer maxRetries = po.getMaxRetries();
            if (maxRetries != null) {
                xml.setMaxRetries(maxRetries);
            }
        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillTestCase(TestCase xml, ISpecTestCasePO po) {
        fillNode(xml, po);

        checkForCancel();

        for (Object o : po.getUnmodifiableNodeList()) {
            Teststep stepXml = xml.addNewTeststep();
            if (o instanceof ICapPO) {
                ICapPO capPO = (ICapPO)o;
                Cap xmlCap = stepXml.addNewCap();
                fillCap(xmlCap, capPO);
            } else if (o instanceof IExecTestCasePO) {
                IExecTestCasePO tcPO = (IExecTestCasePO)o;
                RefTestCase xmlTC = stepXml.addNewUsedTestcase();
                fillRefTestCase(xmlTC, tcPO);
            }
        }

        for (IParamDescriptionPO paramPO : po.getParameterList()) {
            ParamDescription xmlDescription = xml.addNewParameterDescription();
            fillParamDescription(xmlDescription, paramPO);
        }

        xml.setInterfaceLocked(po.isInterfaceLocked());

        xml.setDatafile(po.getDataFile());
        if (po.getReferencedDataCube() != null) {
            xml.setReferencedTestData(po.getReferencedDataCube().getName());
        }
        final ITDManagerPO dataManager = po.getDataManager();
        if (dataManager != null) {
            TestData xmlTD = xml.addNewTestdata();
            if (po.getReferencedDataCube() == null) {
                fillTestData(xmlTD, dataManager);
            }
        }

        for (Object o : po.getEventExecTcMap().keySet()) {
            String eventType = (String)o;
            IEventExecTestCasePO evTc = po.getEventExecTC(eventType);
            EventTestCase xmlEvTc = xml.addNewEventTestcase();
            fillEventTestCase(xmlEvTc, evTc);
        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillCap(Cap xml, ICapPO po) {
        fillNode(xml, po);

        xml.setActionName(po.getActionName());
        xml.setComponentName(po.getComponentName());
        xml.setComponentType(po.getComponentType());
        xml.setDatafile(po.getDataFile());

        TestData xmlData = xml.addNewTestdata();
        fillTestData(xmlData, po.getDataManager());
        for (IParamDescriptionPO desc : po.getParameterList()) {
            ParamDescription xmlDescription = xml.addNewParameterDescription();
            fillParamDescription(xmlDescription, desc);
        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillParamDescription(ParamDescription xml,
            IParamDescriptionPO po) {
        if (po instanceof ICapParamDescriptionPO) {
            xml.setName(po.getUniqueId());
        } else {
            xml.setName(po.getName());
        }
        xml.setType(po.getType());
        xml.setUniqueId(po.getUniqueId());
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillNamedTestData(NamedTestData xml, 
            IParameterInterfacePO po) {
        xml.setName(po.getName());
        for (IParamDescriptionPO paramDesc : po.getParameterList()) {
            ParamDescription xmlParamDesc = xml.addNewParameterDescription();
            fillParamDescription(xmlParamDesc, paramDesc);
        }
        TestData tdXml = xml.addNewTestData();
        if (po.getReferencedDataCube() == null) {
            fillTestData(tdXml, po.getDataManager());
        }
        xml.setTestData(tdXml);
    }
    
    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillTestData(TestData xml, ITDManagerPO po) {

        for (String uniqueId : po.getUniqueIds()) {
            xml.addUniqueIds(uniqueId);
        }

        int rowCnt = 1;
        for (IListWrapperPO row : po.getDataSets()) {
            TestDataRow xmlRow = xml.addNewRow();
            xmlRow.setRowCount(rowCnt++);
            int colCnt = 1;
            for (ITestDataPO td : row.getList()) {
                TestDataCell xmlCell = xmlRow.addNewData();
                xmlCell.setColumnCount(colCnt++);
                II18NStringPO i18n = td.getValue();
                if (i18n != null) {
                    List<Locale> sortedLanguageList = 
                        new ArrayList<Locale>(i18n.getLanguages());
                    Collections.sort(sortedLanguageList, 
                            LANG_CODE_ALPHA_COMPARATOR);
                    for (Locale lang : sortedLanguageList) {
                        String val = i18n.getValue(lang);
                        I18NString xmlI18n = xmlCell.addNewData();
                        xmlI18n.setLanguage(lang.toString());
                        xmlI18n.setValue(val);
                    }
                } else {
                    I18NString xmlI18n = xmlCell.addNewData();
                    xmlI18n.setNil();
                }
            }
        }
    }

    /**
     * Write the information from the Object to its corresponding XML element.
     * 
     * @param xml
     *            The XML element to be filled
     * @param po
     *            The persistent object which contains the information
     */
    private void fillCategory(Category xml, ICategoryPO po) {
        fillNode(xml, po);
        for (Object o : po.getUnmodifiableNodeList()) {
            checkForCancel();
            if (o instanceof ISpecPersistable) {
                ISpecPersistable tcOrCat = (ISpecPersistable)o;
                if (tcOrCat instanceof ICategoryPO) {
                    Category cat = xml.addNewCategory();
                    fillCategory(cat, (ICategoryPO)tcOrCat);
                } else {
                    TestCase tc = xml.addNewTestcase();
                    fillTestCase(tc, (ISpecTestCasePO)tcOrCat);
                }
            }
        }
    }

    /**
     * Converts a Long to a String
     * 
     * @param l
     *            Long
     * @return null if l==null, String represntation of l otherwise
     */
    private String i2str(Long l) {
        if (l == null) {
            return null;
        }
        return l.toString();
    }

    /**
     * 
     * @throws OperationCanceledException if the operation was canceled.
     */
    private void checkForCancel() throws OperationCanceledException {
        if (m_monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
    }

    /**
     * Converts a Long (an OID) to a ID suitable as an XML ID.
     * 
     * @param table
     *            Identifier for the table used.
     * @param l
     *            an OID
     * @return The String representation of l prefixed by "ID_"
     */
    private String i2id(char table, Long l) {
        return "ID" + StringConstants.UNDERLINE + table  //$NON-NLS-1$
            + StringConstants.UNDERLINE + i2str(l);
    }

    /**
     * 
     * @param project
     *            The project for which the work is predicted.
     * @return The predicted amount of work required to save a project.
     */
    public int getPredictedWork(IProjectPO project) throws PMException {
        int work = 0;

        // (Project=1)
        work++;

        // (all project toolkit info=1)
        work++;

        // (all aut toolkit info=1)
        work++;

        // (language=1)
        work += project.getLangHelper().getLanguageList().size();

        // (component names = 1)
        try {
            final Collection<IComponentNamePO> allComponentNamePOs = 
                ComponentNamesBP.getInstance()
                    .getAllComponentNamePOs(project.getId());
            work += allComponentNamePOs.size();
        } catch (PMException e) {
            // nothing here
        }

        // (AUT=1)
        work += project.getAutMainList().size();

        // (Node [TS, ExecTc, SpecTc, Category, CAP] = 1)
        work += NodePM.getNumNodes(project.getId(), GeneralStorage
                .getInstance().getMasterSession());

        // (reused project=1)
        work += project.getUsedProjects().size();

        // (final XML work=1))
        work++;

        return work;
    }
}
