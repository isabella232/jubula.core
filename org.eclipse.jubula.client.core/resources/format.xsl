<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/TR/REC-html40">
	
	<xsl:template match="/">
		<html>
			<HEAD>
				<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>	
				<style> 
				body {
					font-family: monospace;
				}
				
				/* Turn off list bullets */
				ul.report li {
					list-style: none;
				}

				ul.report, ul.report ul, ul.report li {
					margin: 0;
					padding: 0;
				}

				/* This controls the indent for each sublist */
				ul.report ul {
					padding-left: 1em;
				}
				
				ul.report li a {
					text-decoration: none;
				}

				/* Show "bullets" in the links, depending on the class of the
			     LI that the link's in */
				ul.report li.open big :before {
					content: "&#8863; ";
				}
				
				ul.report li.closed big :before {
					content: "&#8862; ";
				}

				/* Actually show and hide sublists */
				ul.report li.open ul {
					display: block;
				}

				ul.report li.closed ul {
					display: none;
				}
				</style>
				<script type="text/javascript">
				    function toggle(element) {
					    var sibling = element.nextSibling;
					    var parent = element.parentNode;
					    if (parent.className =='open') {
							parent.className = 'closed';
							if (sibling) sibling.style.display = 'none';
					    } else {
							parent.className = 'open';
							if (sibling) sibling.style.display = 'block';
					    }
				   }
				   
				   function toggleSize(element, defaultSize) {
				       if (element.width == defaultSize) {
				           element.removeAttribute("width");
				           element.style.maxWidth = "100%";
				       } else {
				           element.removeAttribute("style");
				           element.width = defaultSize;
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
			<body>
				<table border="0" width="95%" align="center"><tr><td>
					<h1>Test Result Report
						<xsl:if test="report/@style">
							(<xsl:value-of select="report/@style"/>)
						</xsl:if>
					</h1>
					<table border="0" width="100%">
						<tr bgcolor="#DDDDDD">
							<th colspan="2" align="left"><h2>Project Information</h2></th>
						</tr>
						<tr>
							<td align="left" width="200">Name</td>
							<td><xsl:value-of select="report/project/name"/></td>
						</tr>
						<tr>
							<td align="left" width="200">Version</td>
							<td><xsl:value-of select="report/project/version"/></td>
						</tr>
						<tr bgcolor="#DDDDDD">
							<th colspan="2" align="left"><h2>Execution Information</h2></th>
						</tr>
						<tr>
							<td align="left" width="200">&#8614; Start Time</td>
							<td><xsl:value-of select="report/project/test-start"/></td>
						</tr>
						<tr>
							<td align="left" width="200">&#8677; End Time</td>
							<td><xsl:value-of select="report/project/test-end"/></td>
						</tr>
						<tr>
							<td align="left" width="200">&#8633; Duration (hh:mm:ss)</td>
							<td><xsl:value-of select="report/project/test-length"/></td>
						</tr>
						<tr>
							<td align="left" width="200">&#8721; expected CAPs</td>
							<td><xsl:value-of select="report/project/expectedNumSteps"/></td>
						</tr>
						<tr>
							<td align="left" width="200">&#8721; executed CAPs</td>
							<td><xsl:value-of select="report/project/numStepsTested"/></td>
						</tr>
						<tr>
							<td align="left" width="200">&#8709; CAP execution (millis)</td>
							<td><xsl:value-of select="report/project/average-cap-duration"/></td>
						</tr>
						<tr>
							<td align="left" width="200">&#10547; Event Handler CAPs</td>
							<td><xsl:value-of select="report/project/numEventHandlerSteps"/></td>
						</tr>
						<tr>
							<td align="left" width="200">&#8623; failed CAPs</td>
							<td><xsl:value-of select="report/project/numFailedSteps"/></td>
						</tr>
						<tr>
							<td align="left" width="200">Language</td>
							<td><xsl:value-of select="report/project/language"/></td>
						</tr>
						<xsl:if test="report/project/error-message != 0">
						
							<tr>
								<td align="left" width="200">Error Message</td>
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
		<table border="0" width="100%">
			<xsl:choose>
				<xsl:when test="status = 1">
					<tr bgcolor="lightgreen">
						<th colspan="2" align="left"><h2>Test Suite Information</h2></th>
					</tr>
				</xsl:when>
				<xsl:when test="status = 6">
					<tr bgcolor="lightgray">
						<th colspan="2" align="left"><h2>Test Suite Information</h2></th>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr bgcolor="#DD0000">
						<th colspan="2" align="left"><h2>Test Suite Information</h2></th>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
			<tr>
				<td align="left" width="200">Name</td>
				<td><xsl:value-of select="name"/>
				</td>
			</tr>
			<tr>
				<td align="left" width="200">Status</td>
				<td>
					<xsl:choose>
						<xsl:when test="status = 1">
							<font color="green">SUCCESSFUL</font>
						</xsl:when>
						<xsl:when test="status = 6">
							<font color="lightgray">STOPPED</font>
						</xsl:when>
						<xsl:otherwise><font color="#DD0000">FAILED</font>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<xsl:apply-templates select="aut"/>
		</table>
		
		<table border="0">
			<tr bgcolor="#DDDDDD">
				<th colspan="2" align="left"><h2>Execution Stack</h2></th>
			</tr>
			<tr>
				<td>
					<ul class="report">
						<LI nowrap="true">
							<xsl:choose>
								<xsl:when test="status != 2 and status != 3 and status != 5">
									<xsl:attribute name="class">closed</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="class">open</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<A HREF="#testsuite" onclick="toggle(this)">
								<xsl:call-template name="writeColored">
									<!-- quoted String -->
									<xsl:with-param name="text" select="'TS'"/>
								</xsl:call-template>
							</A>
                            <xsl:apply-templates select="@duration" />
							<ul>
								<xsl:apply-templates select="test-run"/>
							</ul>
						</LI>
					</ul>
				</td>
			</tr></table>
		
	</xsl:template>
	
	<xsl:template match="aut">
		<tr bgcolor="#DDDDDD">
			<th colspan="2" align="left"><h2>Application Under Test (AUT) Information</h2></th>
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
			<td>Hostname</td>
			<td><xsl:value-of select="server"/>
			</td>
		</tr>
		<tr>
			<td>AUT Arguments</td>
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
		<LI nowrap="true">
			<xsl:choose>
				<xsl:when test="status != 2 and status != 3 and status != 5">
					<xsl:attribute name="class">closed</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">open</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<A HREF="#testcase" onclick="toggle(this)">
			<xsl:call-template name="writeColored">
				<!-- quoted String -->
				<xsl:with-param name="text" select="'TC'"/>
			</xsl:call-template>
			</A>
            <xsl:apply-templates select="@duration" />
			<xsl:variable name="child_nodes" 
					      select="testcase|step|eventhandler"/>
			<xsl:if test="count($child_nodes) != 0">
				<ul>
					<xsl:apply-templates
						select="testcase|step|eventhandler"/>
				</ul>
			</xsl:if>
		</LI>
	</xsl:template>
	
	<xsl:template match="step">
		<li nowrap="true">
			<xsl:choose>
				<xsl:when test="status != 2 and status != 3 and status != 5 and status != 9">
					<xsl:attribute name="class">closed</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">open</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<A HREF="#step" onclick="toggle(this)">
			<xsl:call-template name="writeColored">
				<!-- quoted String -->
				<xsl:with-param name="text" select="'CAP'"/>
			</xsl:call-template>
			</A>
            <xsl:apply-templates select="@duration" />
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
					<xsl:if test="component-heuristic-match != ''">
						<tr>
							<td>Heuristic Match</td>
							<td>:</td>
							<td><xsl:value-of select='format-number(component-heuristic-match, "##%")' /></td>
						</tr>
					</xsl:if>
					<tr><td colspan="3"><hr/></td></tr>
					<tr>
						<td>Action</td>
						<td>:</td>
						<td><xsl:value-of select="action-type"/></td>
					</tr>
                    <tr><td colspan="3"><hr/></td></tr>
					<xsl:apply-templates select="parameter"/>
					<xsl:apply-templates select="error"/>
				</table>
			</UL>
		</li>
	</xsl:template>

	<xsl:template match="parameter">
		<tr>
			<td><xsl:value-of select="parameter-name"/> [<xsl:value-of select="parameter-type"/>]</td>
			<td>:</td>
			<td><xsl:value-of select="parameter-value"/></td>
		</tr>
	</xsl:template>
		
	<xsl:template match="error">
		<tr><td colspan="3"><hr/></td></tr>
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
        <xsl:choose>
          <xsl:when test="screenshot != ''">
            <tr><td colspan="3"><hr/></td></tr>
            <tr><th colspan="3">Screenshot</th></tr>
            <tr>
              <td colspan="3" align="center">
                <img width="400" onclick="toggleSize(this, 400)">
                  <xsl:attribute name="src">data:image/png;base64,<xsl:value-of select="screenshot"/></xsl:attribute>
                </img>
              </td>
            </tr>
          </xsl:when>
        </xsl:choose>
	</xsl:template>
	
	<xsl:template match="eventhandler">
		<li nowrap="true">
			<xsl:choose>
				<xsl:when test="status != 2 and status != 3 and status != 5">
					<xsl:attribute name="class">closed</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">open</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<A HREF="#eventhandler" onclick="toggle(this)">
			<xsl:call-template name="writeColored">
				<!-- quoted String -->
				<xsl:with-param name="text" select="'Event Handler'"/>
			</xsl:call-template>
			</A>
            <xsl:apply-templates select="@duration" />
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
						<td><xsl:value-of select="reentry-property"/></td>
					</tr>
				</table>
				<xsl:apply-templates select="testcase|step|eventhandler"/>
			</ul>
		</li>
	</xsl:template>

    <xsl:template match="@duration">
    	<!-- prepends non-breaking space to avoid cluttered appearance -->
		&#160;- <xsl:value-of select="."/>
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
						<xsl:value-of select="$text"/> (failed)</font>
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
						<xsl:value-of select="$text"/> (nested error)</font>
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
					<font color="lightgray"><xsl:value-of select="name"/> -
						<xsl:value-of select="$text"/> (testing)</font>
				</xsl:otherwise>
			</xsl:choose>
			</big>
	</xsl:template>
	
</xsl:stylesheet>