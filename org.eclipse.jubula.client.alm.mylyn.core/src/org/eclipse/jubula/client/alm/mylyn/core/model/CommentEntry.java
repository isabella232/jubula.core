package org.eclipse.jubula.client.alm.mylyn.core.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.alm.mylyn.core.i18n.Messages;
import org.eclipse.jubula.client.core.constants.Constants;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.tools.constants.StringConstants;
import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 */
public class CommentEntry {
    /** the max length for data */
    private static final int MAX_DATA_STRING_LENGTH = 200;
    /** the timestamp */
    private String m_timestamp;
    /** the node type */
    private String m_nodeType;
    /** the node name and parameter description */
    private String m_nodeNameAndParams;
    /** the m_status */
    private String m_status;
    /** the dashboard base URL */
    private String m_dashboardURL;
    /** the summary ID */
    private String m_summaryId;
    /** the node count */
    private Long m_nodeCount;

    /**
     * Constructor
     * @param resultNode the node
     * @param dashboardURL the URL
     * @param summaryId the id
     * @param nodeCount the node count
     */
    public CommentEntry(TestResultNode resultNode, String dashboardURL,
            String summaryId, Long nodeCount) {
        m_dashboardURL = dashboardURL;
        m_summaryId = summaryId;
        m_nodeCount = nodeCount;
        
        Date executionTime = resultNode.getTimeStamp();
        // e.g. when not executed
        if (executionTime != null) {
            m_timestamp = executionTime.toString();
        } else {
            m_timestamp = Messages.NotAvailable;
        }

        m_nodeType = resultNode.getTypeOfNode();
        String paramDescription = StringUtils.abbreviate(
                resultNode.getParameterDescription(), MAX_DATA_STRING_LENGTH);
        
        m_nodeNameAndParams = getName(resultNode);
        
        if (!StringUtils.isBlank(paramDescription)) {
            m_nodeNameAndParams += StringConstants.SPACE + paramDescription;
        }
        
        if (hasPassed(resultNode.getStatus())) {
            m_status = Messages.StatusPassed;
        } else {
            m_status = Messages.StatusFailed;
        }
    }
    
    /**
     * @return the dashboard URL for this comment entry
     */
    public String getDashboardURL() {
        return m_dashboardURL + StringConstants.QUESTION_MARK
                + Constants.DASHBOARD_SUMMARY_PARAM
                + StringConstants.EQUALS_SIGN + m_summaryId
                + StringConstants.AMPERSAND
                + Constants.DASHBOARD_RESULT_NODE_PARAM
                + StringConstants.EQUALS_SIGN + String.valueOf(m_nodeCount);
    }
    
    @Override
    public String toString() {
        return NLS.bind(Messages.NodeComment, new String[] { m_timestamp,
            m_nodeType, m_nodeNameAndParams, m_status });
    }
    
    /**
     * @param resultNode
     *            the node
     * @return the name of the node
     */
    private String getName(TestResultNode resultNode) {
        StringBuilder nameBuilder = new StringBuilder();
        INodePO node = resultNode.getNode();
        if (node != null) {
            if (node instanceof IExecTestCasePO) {
                IExecTestCasePO testCaseRef = (IExecTestCasePO) node;
                String realName = testCaseRef.getRealName();
                ISpecTestCasePO testCase = testCaseRef.getSpecTestCase();
                String testCaseName = testCase != null ? testCase.getName()
                        : StringUtils.EMPTY;
                if (!StringUtils.isBlank(realName)) {
                    nameBuilder.append(realName);
                    nameBuilder.append(StringConstants.SPACE)
                            .append(StringConstants.LEFT_PARENTHESES)
                            .append(testCaseName)
                            .append(StringConstants.RIGHT_PARENTHESES);

                } else {
                    nameBuilder.append(testCaseName);
                }
            } else {
                nameBuilder.append(node.getName());
            }
        }
        return nameBuilder.toString();
    }
    
    /**
     * @param statusCode
     *            the m_status code
     * @return failure m_status
     */
    public static boolean hasPassed(int statusCode) {
        return statusCode == TestResultNode.SUCCESS
                || statusCode == TestResultNode.SUCCESS_RETRY;
    }
}