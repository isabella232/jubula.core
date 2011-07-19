<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/TR/REC-html40">
	
	<xsl:template match="/">
		<html>
			<HEAD>
				<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>	
				<link rel="stylesheet" href="html/reportStyle.css"/>
				<style> body {font-family:Arial; }
				</style>
				<script type="text/javascript">
				    function toggle(element) {
				    var sibling = element.nextSibling;
				    if (sibling) {
					    var parent = element.parentNode;
					    if (parent.className =='htmlReportOpen') {
							parent.className = 'htmlReportClosed';
							sibling.style.display = 'none';
					    } else {
							parent.className = 'htmlReportOpen';
							sibling.style.display = 'block';
					    }
				    }
				   }
				</script>
				<title>
					Test Result Report
					<xsl:if test="report/@style">
						(<xsl:value-of select="report/@style"/>)
					</xsl:if>
				</title>
			</HEAD>
			<body bgcolor="#DDDDDD">
				<table border="0" width="95%" align="center"><tr><td>
					<h2>
						Test Result Report
						<xsl:if test="report/@style">
							(<xsl:value-of select="report/@style"/>)
						</xsl:if>
					
					</h2>
					<table border="0" width="50%">
						<tr bgcolor="#DDDDDD">
							<th colspan="2" align="left"> Project Information
							</th>
						</tr>
						<tr>
							<td align="left" width="175">Name</td>
							<td><xsl:value-of select="report/project/name"/>
							</td>
						</tr>
						<tr>
							<td align="left" width="175">Start Time</td>
							<td><xsl:value-of
									select="report/project/test-start"/>
							</td>
						</tr>
						<tr>
							<td align="left" width="175">End Time</td>
							<td><xsl:value-of select="report/project/test-end"/>
								
							</td>
						</tr>
						<tr>
							<td align="left" width="175">Duration</td>
							<td><xsl:value-of select="report/project/test-length"/>
								
							</td>
						</tr>
						<tr>
							<td align="left" width="175">Expected Test Steps</td>
							<td><xsl:value-of select="report/project/expectedNumSteps"/>
								
							</td>
						</tr>
						<tr>
							<td align="left" width="175">Executed Test Steps</td>
							<td><xsl:value-of select="report/project/numStepsTested"/>
								
							</td>
						</tr>
						<tr>
							<td align="left" width="175">Event Handler Test Steps</td>
							<td><xsl:value-of select="report/project/numEventHandlerSteps"/>
								
							</td>
						</tr>
						<tr>
							<td align="left" width="175">Failed Test Steps</td>
							<td><xsl:value-of select="report/project/numFailedSteps"/>
								
							</td>
						</tr>
						<tr>
							<td align="left" width="175">Language</td>
							<td><xsl:value-of select="report/project/language"/>
								
							</td>
						</tr>
						<xsl:if test="report/project/error-message != 0">
						
							<tr>
								<td align="left" width="175">Error Message</td>
								<td>
									<font color="red">
										<xsl:value-of select="report/project/error-message"/>
									</font>
								</td>
							</tr>
						
						</xsl:if>
					</table>
					<xsl:apply-templates select="report/project/testsuite"/>
					
					</td></tr></table>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="testsuite">
		<br></br>
		<table border="0" width="50%">
			<tr bgcolor="#DDDDDD">
				<th colspan="2" align="left"> Test Suite Information
				</th>
			</tr>
			<tr>
				<td align="left" width="175">Name</td>
				<td><xsl:value-of select="name"/>
				</td>
			</tr>
			<tr>
				<td align="left" width="175">Status</td>
				<td>
					<xsl:choose>
						<xsl:when test="status = 1">
							<font color="green">SUCCESSFUL</font>
						</xsl:when>
						<xsl:when test="status = 6">
							<font color="#000000">STOPPED</font>
						</xsl:when>
						<xsl:otherwise><font color="#DD0000">FAILED</font>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<xsl:apply-templates select="aut"/>
		</table>
		
		<br></br>
		<table border="0">
			<tr bgcolor="#DDDDDD">
				<th colspan="2" align="left"> Execution Stack
				</th>
			</tr>
			<tr>
				<td>
					<ul class="htmlReport">
						<xsl:choose>
							<xsl:when test="status != 2 and status != 3 and status != 5">
								<LI class="htmlReportClosed" nowrap="true">
								<A HREF="#testsuite" onclick="toggle(this)">
									<xsl:call-template name="writeColored">
										<!-- quoted String -->
										<xsl:with-param name="text"
											select="'Test Case'"/>
									</xsl:call-template>
									</A>
									<ul>
										<xsl:apply-templates select="test-run"/>
									</ul>
								</LI>
							</xsl:when>
							<xsl:otherwise>
								<LI class="htmlReportOpen" nowrap="true">
								<A HREF="#testsuite" onclick="toggle(this)">
									<xsl:call-template name="writeColored">
										<!-- quoted String -->
										<xsl:with-param name="text"
											select="'Test Case'"/>
									</xsl:call-template>
									</A>
									<ul>
										<xsl:apply-templates select="test-run"/>
									</ul>
								</LI>
							</xsl:otherwise>
						</xsl:choose>
					</ul>
				</td>
			</tr></table>
		
	</xsl:template>
	
	<xsl:template match="aut">
		<tr>
			<th colspan="2" align="left"><br></br>Application Under Test</th>
		</tr>
		<tr>
			<td>Name</td>
			<td><xsl:value-of select="name"/>
			</td>
		</tr>
		<tr>
			<td>Configuration</td>
			<td><xsl:value-of select="config"/>
			</td>
		</tr>
		<tr>
			<td>Server</td>
			<td><xsl:value-of select="server"/>
			</td>
		</tr>
		<tr>
			<td>CmdLine Param</td>
			<td><xsl:value-of select="cmdline-parameter"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="error-message">
		<tr>
			<th colspan="2" align="left"><br></br>Test could not be executed</th>
		</tr>
		<tr>
			<td>Message</td>
			<td><xsl:value-of select="asd"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="test-run">
		<xsl:apply-templates select="testcase|step|eventhandler"/>
	</xsl:template>
	
	<xsl:template match="testcase">
		<xsl:choose>
			<xsl:when test="status != 2 and status != 3 and status != 5">
				
				<LI class="htmlReportClosed" nowrap="true">
				<A HREF="#testcase" onclick="toggle(this)">
					<xsl:call-template name="writeColored">
						<!-- quoted String -->
						<xsl:with-param name="text" select="'Test Case'"/>
					</xsl:call-template>
					</A>
					<xsl:variable name="child_nodes" 
							      select="testcase|step|eventhandler"/>
					<xsl:if test="count($child_nodes) != 0">
						<ul>
							<xsl:apply-templates
								select="testcase|step|eventhandler"/>
						</ul>
					</xsl:if>
				</LI>
			</xsl:when>
			<xsl:otherwise>
				<LI class="htmlReportOpen" nowrap="true">
				<A HREF="#testcase" onclick="toggle(this)">
					<xsl:call-template name="writeColored">
						<!-- quoted String -->
						<xsl:with-param name="text" select="'Test Case'"/>
					</xsl:call-template>
					</A>
					<xsl:variable name="child_nodes" 
							      select="testcase|step|eventhandler"/>
					<xsl:if test="count($child_nodes) != 0">
						<ul>
							<xsl:apply-templates
								select="testcase|step|eventhandler"/>
						</ul>
					</xsl:if>
				</LI>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	
	<xsl:template match="step">
		<xsl:choose>
			<xsl:when test="status != 2 and status != 3 and status != 5 and status != 9">
				<li class="htmlReportClosed" nowrap="true">
				<A HREF="#step" onclick="toggle(this)">
					<xsl:call-template name="writeColored">
						<!-- quoted String -->
						<xsl:with-param name="text" select="''"/>
					</xsl:call-template>
					</A>
					<UL>
						<table bgcolor="#BBBBBB" border="0">
                            <tr> 
                                <td>Timestamp</td>
                                <td>:</td>
                                <td><xsl:value-of select="timestamp"/></td>
                            </tr>
							<tr>
								<td nowrap="true">Component Name</td>
								<td>:</td>
								<td><xsl:value-of select="component-name"/></td>
							</tr>
							<tr>
								<td>Component Type</td>
								<td>:</td>
								<td><xsl:value-of select="component-type"/></td>
							</tr>
							<tr><td colspan="3"><hr></hr></td></tr>
							<tr>
								<td>Action Type</td>
								<td>:</td>
								<td><xsl:value-of select="action-type"/></td>
							</tr>
							<tr><td colspan="3"><hr></hr></td></tr>
							<xsl:apply-templates select="parameter"/>
							<xsl:apply-templates select="error"/>
						</table>
					</UL>
				</li>
			</xsl:when>
			<xsl:otherwise>
				<li class="htmlReportOpen" nowrap="true">
				<A HREF="#step" onclick="toggle(this)">
					<xsl:call-template name="writeColored">
						<!-- quoted String -->
						<xsl:with-param name="text" select="''"/>
					</xsl:call-template>
					</A>
					<UL>
						<table bgcolor="#BBBBBB" border="0">
                            <tr> 
                                <td>Timestamp</td>
                                <td>:</td>
                                <td><xsl:value-of select="timestamp"/></td>
                            </tr>
                            <tr><td colspan="3"><hr></hr></td></tr>
                            <tr>
								<td nowrap="true">Component Name</td>
								<td>:</td>
								<td><xsl:value-of select="component-name"/></td>
							</tr>
							<tr>
								<td>Component Type</td>
								<td>:</td>
								<td><xsl:value-of select="component-type"/></td>
							</tr>
							<tr><td colspan="3"><hr></hr></td></tr>
							<tr>
								<td>Action Type</td>
								<td>:</td>
								<td><xsl:value-of select="action-type"/></td>
							</tr>
							<tr><td colspan="3"><hr></hr></td></tr>
							<xsl:apply-templates select="parameter"/>
							<xsl:apply-templates select="error"/>
						</table>
					</UL>
				</li>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

	<xsl:template match="parameter">
		<tr>
			<td>Parameter Name</td>
			<td>:</td>
			<td><xsl:value-of select="parameter-name"/></td>
		</tr>
		<tr>
			<td>Parameter Type</td>
			<td>:</td>
			<td><xsl:value-of select="parameter-type"/></td>
		</tr>
		<tr>
			<td>Parameter Value</td>
			<td>:</td>
			<td><xsl:value-of select="parameter-value"/></td>
		</tr>
		<tr><td colspan="3"><hr></hr></td></tr>
	</xsl:template>
				
		
	<xsl:template match="error">
		
		<tr><th colspan="3">Error Details</th></tr>
		<tr>
			<td>Error Type</td>
			<td>:</td>
			<td><xsl:value-of select="type"/></td>
		</tr>
		<xsl:choose>
			<xsl:when test="type = 'Check Failed'">
				<tr>
					<td>Expected Value</td>
					<td>:</td>
					<td><xsl:value-of select="guidancerPattern"/></td>
				</tr>
				<tr>
					<td>Actual Value</td>
					<td>:</td>
					<td><xsl:value-of select="guidancerActualValue"/></td>
				</tr>
			</xsl:when>
			<xsl:when test="type = 'Action Error'">
				<tr>
					<td>description</td>
					<td>:</td>
					<td><xsl:value-of select="description"/></td>
				</tr>
			</xsl:when>

		</xsl:choose>
		
	</xsl:template>
	
	<xsl:template match="eventhandler">
		<xsl:choose>
			<xsl:when test="status != 2 and status != 3 and status != 5">
				<li class="htmlReportClosed" nowrap="true">
				<A HREF="#eventhandler" onclick="toggle(this)">
					<xsl:call-template name="writeColored">
						<!-- quoted String -->
						<xsl:with-param name="text" select="'Event Handler'"/>
					</xsl:call-template>
					</A>
					<ul>
						<table border="0" bgcolor="#BBBBBB">
							<tr>
								<td nowrap="true">Error Type</td>
								<td>:</td>
								<td><xsl:value-of select="type"/></td>
							</tr>
							<tr>
								<td>Reentry Property</td>
								<td>:</td>
								<td><xsl:value-of select="reentry-property"/>
									</td>
							</tr>
						</table>
						<xsl:apply-templates
							select="testcase|step|eventhandler"/>
					</ul>
				</li>
			</xsl:when>
			<xsl:otherwise>
				<li class="htmlReportOpen" nowrap="true">
				<A HREF="#eventhandler" onclick="toggle(this)">
					<xsl:call-template name="writeColored">
						<!-- quoted String -->
						<xsl:with-param name="text" select="'Event Handler'"/>
					</xsl:call-template>
					</A>
					<ul>
						<table border="0" bgcolor="#BBBBBB">
							<tr>
								<td nowrap="true">Error Type</td>
								<td>:</td>
								<td><xsl:value-of select="type"/></td>
							</tr>
							<tr>
								<td>Reentry Property</td>
								<td>:</td>
								<td><xsl:value-of select="reentry-property"/>
									</td>
							</tr>
						</table>
						<xsl:apply-templates
							select="testcase|step|eventhandler"/>
					</ul>
				</li>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

	<!-- functional templates-->
	<xsl:template name="writeColored">
		<xsl:param name="text"/>
		<big>
			<xsl:choose>
				<xsl:when test="status = 0">
					<font color="#999999"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (not tested)</font>
				</xsl:when>
				<xsl:when test="status = 1">
					<font color="green"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (ok)</font>
				</xsl:when>
				<xsl:when test="status = 2">
					<font color="#DD0000"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (error occurred)</font>
				</xsl:when>
				<xsl:when test="status = 3">
					<font color="#DD0000"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (not verified)</font>
				</xsl:when>
				<xsl:when test="status = 4">
					<font color="#999999"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (not tested)</font>
				</xsl:when>
				<xsl:when test="status = 5">
					<font color="#DD0000"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (error occurred in
						child)</font>
				</xsl:when>
				<xsl:when test="status = 7">
					<font color="#DD0000"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (retrying)</font>
				</xsl:when>
				<xsl:when test="status = 8">
					<font color="green"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (ok after retry)</font>
				</xsl:when>
				<xsl:when test="status = 9">
					<font color="#DD0000"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (test aborted due to internal error)</font>
				</xsl:when>
				<xsl:otherwise>
					<font color="#000000"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (testing)</font>
				</xsl:otherwise>
			</xsl:choose>
			</big>
	</xsl:template>
	
</xsl:stylesheet>