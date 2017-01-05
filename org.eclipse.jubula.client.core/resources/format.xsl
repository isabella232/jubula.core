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
				
				/* Turn off wrapping for list items */
				li {
					white-space: nowrap;
				}
				
				/* Turn off list bullets */
				ul.report li {
					list-style: none;
				}
				
				/* CAP details table style */
				table.cap {
					border-collapse: collapse;
				}
				
				table.cap tr:nth-child(even) {
				    background-color: lightgray;
				}
				
				table.cap tr:nth-child(odd) {
				    background-color: lightsteelblue;
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
				
				/*********************************/
				
				/* not tested */
				big.not {
					color: #999999;
				}
				
				big.not::after {
					content: " (not tested)";
				}
				
				/* ok */
				big.ok {
					color: green;
				}
				
				big.ok::after {
					content: " (ok)";
				}
				
				/* not ok */
				big.nok {
					color: #DD0000;
				}
				
				big.nok::after {
					content: " (failed)";
				}
				
				/* not verified */
				big.nov {
					color: #DD0000;
				}
				
				big.nov::after {
					content: " (not verified)";
				}
				
				/* nested error */
				big.ner {
					color: #DD0000;
				}
				
				big.ner::after {
					content: " (nested error)";
				}
				
				/* retrying */
				big.ret {
					color: #DD0000;
				}
				
				big.ret::after {
					content: " (retrying)";
				}
				
				/* ok after retrying */
				big.okr {
					color: green;
				}
				
				big.okr::after {
					content: " (ok after retry)";
				}
				
				/* test aborted due to internal error */
				big.abo {
					color: #DD0000;
				}
				
				big.abo::after {
					content: " (test aborted due to internal error)";
				}
				
				big.inf {
                    color: darkgreen;
                }
                
                big.inf::after {
                    content: " (ok - possibly infinite loop)";
                }
                
                big.skp {
                    color: lightgray;
                }
                
                big.skp::after {
                    content: " (skipped)";
                }

                big.chs {
                    color: lightgray;
                }
                
                big.chs::after {
                    content: " (all child nodes were skipped)";
                }
				
				/* testing */
				big.tst {
					color: lightgray;
				}
				
				big.tst::after {
					content: " (testing)";
				}
				
				/*********************************/

				/* Show "bullets" in the links, depending on the class of the
			     li that the link is in */
				ul.report li.o big::before {
					content: "&#8863; ";
				}
				
				ul.report li.c big::before {
					content: "&#8862; ";
				}

				/* Actually show and hide sublists */
				ul.report li.o ul {
					display: block;
				}

				ul.report li.c ul {
					display: none;
				}
				</style>
				<script type="text/javascript">
					/* toggle expansion state */
				    function t(element) {
					    var sibling = element.nextSibling;
					    var parent = element.parentNode;
					    if (parent.className =='o') {
							parent.className = 'c';
							if (sibling) sibling.style.display = 'none';
					    } else {
							parent.className = 'o';
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
                        <xsl:when test="status = 21">
                            <font color="lightgray">SUCCESSFUL (ALL CHILD NODES WERE SKIPPED)</font>
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
						<li>
							<xsl:choose>
								<xsl:when test="status != 2 and status != 3 and status != 5">
									<xsl:attribute name="class">c</xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="class">o</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<A HREF="#ts" onclick="t(this)">
								<xsl:call-template name="writeColored">
									<!-- quoted String -->
									<xsl:with-param name="text" select="'TS'"/>
								</xsl:call-template>
							</A>
                            <xsl:apply-templates select="@duration" />
							<ul>
								<xsl:apply-templates select="test-run"/>
							</ul>
						</li>
					</ul>
				</td>
			</tr></table>
		
	</xsl:template>
	
	<xsl:template match="comment">
        <table border="0" width="100%">
            <tr>
                <td>
	                <xsl:call-template name="LFsToBRs_SpaceToNBSP">
	                    <xsl:with-param name="input" select="name"/>
                    </xsl:call-template>
                </td>
            </tr>
        </table>
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
			<th colspan="2" align="left"><br/>Test could not be executed</th>
		</tr>
		<tr>
			<td>Message</td>
			<td><xsl:value-of select="asd"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="test-run">
		<xsl:apply-templates select="comment|testcase|step|eventhandler|ifthenelse|dowhile|whiledo|repeat"/>
	</xsl:template>
	
	<xsl:template match="testcase">
		<li>
			<xsl:choose>
				<xsl:when test="status != 2 and status != 3 and status != 5">
					<xsl:attribute name="class">c</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">o</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<A HREF="#tc" onclick="t(this)">
			<xsl:call-template name="writeColored">
				<!-- quoted String -->
				<xsl:with-param name="text" select="'TC'"/>
			</xsl:call-template>
			</A>
            <xsl:apply-templates select="@duration" />
			<xsl:variable name="child_nodes" 
					      select="comment|testcase|step|eventhandler|ifthenelse|dowhile|whiledo|repeat"/>
			<xsl:if test="count($child_nodes) != 0">
				<ul>
					<xsl:apply-templates
						select="comment|testcase|step|eventhandler|ifthenelse|dowhile|whiledo|repeat"/>
				</ul>
			</xsl:if>
		</li>
	</xsl:template>
    
    <xsl:template match="ifthenelse">
        <li>
            <xsl:choose>
                <xsl:when test="status != 2 and status != 3 and status != 5">
                    <xsl:attribute name="class">c</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">o</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <A HREF="#cond" onclick="t(this)">
            <xsl:call-template name="writeColored">
                <!-- quoted String -->
                <xsl:with-param name="text" select="'If-Then-Else'"/>
            </xsl:call-template>
            </A>
            <xsl:apply-templates select="@duration" />
            <xsl:variable name="child_nodes" 
                          select="container"/>
            <xsl:if test="count($child_nodes) != 0">
                <ul>
                    <xsl:apply-templates
                        select="container"/>
                </ul>
            </xsl:if>
        </li>
    </xsl:template>
    
    <xsl:template match="container">
        <li>
            <xsl:choose>
                <xsl:when test="status != 2 and status != 3 and status != 5">
                    <xsl:attribute name="class">c</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">o</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <A HREF="#cont" onclick="t(this)">
            <xsl:call-template name="writeColored">
                <!-- quoted String -->
                <xsl:with-param name="text" select="'Container'"/>
            </xsl:call-template>
            </A>
            <xsl:apply-templates select="@duration" />
            <xsl:variable name="child_nodes" 
                          select="comment|testcase|step"/>
            <xsl:if test="count($child_nodes) != 0">
                <ul>
                    <xsl:apply-templates
                        select="comment|testcase|step"/>
                </ul>
            </xsl:if>
        </li>
    </xsl:template>
    
    <xsl:template match="dowhile">
        <li>
            <xsl:choose>
                <xsl:when test="status != 2 and status != 3 and status != 5">
                    <xsl:attribute name="class">c</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">o</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <A HREF="#cont" onclick="t(this)">
            <xsl:call-template name="writeColored">
                <!-- quoted String -->
                <xsl:with-param name="text" select="'Do-While'"/>
            </xsl:call-template>
            </A>
            <xsl:apply-templates select="@duration" />
            <xsl:variable name="child_nodes" 
                          select="container"/>
            <xsl:if test="count($child_nodes) != 0">
                <ul>
                    <xsl:apply-templates
                        select="container"/>
                </ul>
            </xsl:if>
        </li>
    </xsl:template>

    <xsl:template match="whiledo">
        <li>
            <xsl:choose>
                <xsl:when test="status != 2 and status != 3 and status != 5">
                    <xsl:attribute name="class">c</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">o</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <A HREF="#cont" onclick="t(this)">
            <xsl:call-template name="writeColored">
                <!-- quoted String -->
                <xsl:with-param name="text" select="'While-Do'"/>
            </xsl:call-template>
            </A>
            <xsl:apply-templates select="@duration" />
            <xsl:variable name="child_nodes" 
                          select="container"/>
            <xsl:if test="count($child_nodes) != 0">
                <ul>
                    <xsl:apply-templates
                        select="container"/>
                </ul>
            </xsl:if>
        </li>
    </xsl:template>
    
    <xsl:template match="repeat">
        <li>
            <xsl:choose>
                <xsl:when test="status != 2 and status != 3 and status != 5">
                    <xsl:attribute name="class">c</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">o</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <A HREF="#cont" onclick="t(this)">
            <xsl:call-template name="writeColored">
                <!-- quoted String -->
                <xsl:with-param name="text" select="'Repeat'"/>
            </xsl:call-template>
            </A>
            <xsl:apply-templates select="@duration" />
            <xsl:variable name="child_nodes" 
                          select="container"/>
            <xsl:if test="count($child_nodes) != 0">
                <ul>
                    <xsl:apply-templates
                        select="container"/>
                </ul>
            </xsl:if>
        </li>
    </xsl:template>
	
	<xsl:template match="step">
		<li>
			<xsl:choose>
				<xsl:when test="status != 2 and status != 3 and status != 5 and status != 9">
					<xsl:attribute name="class">c</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">o</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<a href="#cap" onclick="t(this)">
			<xsl:call-template name="writeColored">
				<!-- quoted String -->
				<xsl:with-param name="text" select="'CAP'"/>
			</xsl:call-template>
			</a>
            <xsl:apply-templates select="@duration" />
			<UL>
				<table class="cap">
					<tr><th colspan="2"><xsl:value-of select="component-type"/> - <xsl:value-of select="action-type"/></th></tr>
					<xsl:if test="component-name != ''">
						<tr><th colspan="2">
			                    <xsl:value-of select="component-name"/>
								<xsl:if test="component-heuristic-match != ''">
									(<xsl:value-of select='format-number(component-heuristic-match, "##%")' />)
								</xsl:if>
						</th></tr>
					</xsl:if>
					<xsl:apply-templates select="parameter"/>
                    <xsl:choose>
                        <xsl:when test="command-log != ''">
                            <tr>
                            <td>CommandLog</td>
                            <td>
                                <xsl:call-template name="LFsToBRs">
                                    <xsl:with-param name="input" select="command-log"/>
                                </xsl:call-template>
                            </td>
                            </tr>
                        </xsl:when>
                    </xsl:choose>
					<xsl:apply-templates select="error"/>
					<xsl:if test="timestamp != ''">
	                   <tr> 
	                       <td>Timestamp:</td>
	                       <td><xsl:value-of select="timestamp"/></td>
	                   </tr>
					</xsl:if>
				</table>
			</UL>
		</li>
	</xsl:template>

	<xsl:template match="parameter">
		<tr>
			<td><xsl:value-of select="parameter-name"/> [<xsl:value-of select="parameter-type"/>]:</td>
			<td><xsl:value-of select="parameter-value"/></td>
		</tr>
	</xsl:template>
		
	<xsl:template match="error">
		<tr><th colspan="2">Error Details</th></tr>
		<tr>
			<td>Type:</td>
			<td><xsl:value-of select="type"/></td>
		</tr>
		<xsl:choose>
			<xsl:when test="type = 'Check Failed'">
				<tr>
					<td>Expected Value:</td>
					<td><xsl:value-of select="guidancerPattern"/></td>
				</tr>
				<tr>
					<td>Actual Value:</td>
					<td><xsl:value-of select="guidancerActualValue"/></td>
				</tr>
			</xsl:when>
			<xsl:when test="type = 'Action Error'">
				<tr>
					<td>Description:</td>
					<td><xsl:value-of select="description"/></td>
				</tr>
			</xsl:when>
		</xsl:choose>
        <xsl:choose>
          <xsl:when test="screenshot != ''">
            <tr><th colspan="2">Screenshot</th></tr>
            <tr>
              <td colspan="2" align="center">
                <img width="400" onclick="toggleSize(this, 400)">
                  <xsl:attribute name="src">data:image/png;base64,<xsl:value-of select="screenshot"/></xsl:attribute>
                </img>
              </td>
            </tr>
          </xsl:when>
        </xsl:choose>
	</xsl:template>
	
	<xsl:template match="eventhandler">
		<li>
			<xsl:choose>
				<xsl:when test="status != 2 and status != 3 and status != 5">
					<xsl:attribute name="class">c</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">o</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<A HREF="#eh" onclick="t(this)">
			<xsl:call-template name="writeColored">
				<!-- quoted String -->
				<xsl:with-param name="text" select="'Event Handler'"/>
			</xsl:call-template>
			</A>
            <xsl:apply-templates select="@duration" />
			<ul>
				<table class="cap">
					<tr>
						<td>Error Type:</td>
						<td><xsl:value-of select="type"/></td>
					</tr>
					<tr>
						<td>Reentry Property:</td>
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
				<xsl:when test="status = 0 or status = 4">
					<xsl:attribute name="class">not</xsl:attribute>
				</xsl:when>
				<xsl:when test="status = 1">
					<xsl:attribute name="class">ok</xsl:attribute>
				</xsl:when>
				<xsl:when test="status = 2">
					<xsl:attribute name="class">nok</xsl:attribute>
				</xsl:when>
				<xsl:when test="status = 3">
					<xsl:attribute name="class">nov</xsl:attribute>
				</xsl:when>
				<xsl:when test="status = 5">
					<xsl:attribute name="class">ner</xsl:attribute>
				</xsl:when>
				<xsl:when test="status = 7">
					<xsl:attribute name="class">ret</xsl:attribute>
				</xsl:when>
				<xsl:when test="status = 8">
					<xsl:attribute name="class">okr</xsl:attribute>
				</xsl:when>
				<xsl:when test="status = 9">
					<xsl:attribute name="class">abo</xsl:attribute>
				</xsl:when>
				<xsl:when test="status = 11">
					<xsl:attribute name="class">inf</xsl:attribute>
				</xsl:when>
                <xsl:when test="status = 20">
                    <xsl:attribute name="class">skp</xsl:attribute>
                </xsl:when>
                <xsl:when test="status = 21">
                    <xsl:attribute name="class">chs</xsl:attribute>
                </xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">tst</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="name"/> - <xsl:value-of select="$text"/>
			<xsl:choose>
            	<xsl:when test="negated = 'true'"> [Negated]</xsl:when>
            </xsl:choose>
		</big>
		<xsl:if test="comment != ''">
			<abbr><xsl:attribute name="title"><xsl:value-of select="comment"/></xsl:attribute>*</abbr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="LFsToBRs">
		<xsl:param name="input" />
		<xsl:choose>
			<xsl:when test="contains($input, '&#10;')">
				<xsl:value-of select="substring-before($input, '&#10;')" />
				<br />
				<xsl:call-template name="LFsToBRs">
					<xsl:with-param name="input"
						select="substring-after($input, '&#10;')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$input" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="LFsToBRs_SpaceToNBSP">
        <xsl:param name="input" />
        <xsl:choose>
            <xsl:when test="contains($input, '&#10;')">
                <xsl:call-template name="LFsToBRs_SpaceToNBSP">
                    <xsl:with-param name="input"
                        select="substring-before($input, '&#10;')" />
                </xsl:call-template>
                <br />
                <xsl:call-template name="LFsToBRs_SpaceToNBSP">
                    <xsl:with-param name="input"
                        select="substring-after($input, '&#10;')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="contains($input, '&#32;')">
                <xsl:call-template name="LFsToBRs_SpaceToNBSP">
                    <xsl:with-param name="input"
                        select="substring-before($input, '&#32;')" />
                </xsl:call-template>&#160;<xsl:call-template name="LFsToBRs_SpaceToNBSP">
                    <xsl:with-param name="input"
                        select="substring-after($input, '&#32;')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="contains($input, '&#9;')">
                <xsl:call-template name="LFsToBRs_SpaceToNBSP">
                    <xsl:with-param name="input"
                        select="substring-before($input, '&#9;')" />
                </xsl:call-template>&#160;&#160;&#160;&#160;<xsl:call-template name="LFsToBRs_SpaceToNBSP">
                    <xsl:with-param name="input"
                        select="substring-after($input, '&#9;')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$input" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
	
</xsl:stylesheet>