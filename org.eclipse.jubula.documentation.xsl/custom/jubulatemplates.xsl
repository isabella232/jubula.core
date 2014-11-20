<?xml version="1.0"?>

<!-- 
  Copyright (c) 2014 BREDEX GmbH.
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
                xmlns:d="http://docbook.org/ns/docbook"
                version="1.0"
                exclude-result-prefixes="exsl">

<xsl:import href="generatecontexts.xsl"/>

<!-- 
Generates a path navigation to see where you are in the document. For more information, see:

http://www.sagehill.net/docbookxsl/HTMLHeaders.html#BreadCrumbs
-->

<xsl:param name="breadcrumbs.separator" select="' > '"/>

<xsl:template name="generate.breadcrumbs">
  <xsl:param name="current.node" select="."/>
  <div class="breadcrumbs">
    <xsl:for-each select="$current.node/ancestor::*">
      <span class="breadcrumb-link">
        <a>
          <xsl:attribute name="href">
            <xsl:call-template name="href.target">
              <xsl:with-param name="object" select="."/>
              <xsl:with-param name="context" select="$current.node"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:apply-templates select="." mode="title.markup"/>
        </a>
      </span>
      <xsl:copy-of select="$breadcrumbs.separator"/>
    </xsl:for-each>
    <!-- Display the current node if it isn't the first node, but not as a link -->
    <xsl:if test="$current.node/ancestor::*">
      <span class="breadcrumb-node">
        <xsl:apply-templates select="$current.node" mode="title.markup"/>
      </span>
    </xsl:if>
  </div>
</xsl:template>

<xsl:template name="user.header.content">
  <xsl:call-template name="generate.breadcrumbs"/>
</xsl:template>

<xsl:template match="d:para[@role='warning']">
  <xsl:variable name="href">
    <xsl:call-template name="relative.path.link">
      <xsl:with-param name="target.pathname" select="'images/img1.jpg'"/>
    </xsl:call-template>
  </xsl:variable>
  
  <p>
    <table>
      <colgroup>
        <col/>
        <col/>
      </colgroup>
      <tbody>
        <tr>
          <td>
            <img align="bottom" width="102" height="89">
              <xsl:attribute name="src">
                <xsl:value-of select="$href"/>
              </xsl:attribute>
            </img>
          </td>
          <td class="topbotline"><span class="strong"><strong><xsl:apply-templates/></strong></span></td>
        </tr>
      </tbody>
    </table>
  </p>
</xsl:template>

<xsl:template match="d:para[@role='tip']">
  <xsl:variable name="href">
    <xsl:call-template name="relative.path.link">
      <xsl:with-param name="target.pathname" select="'images/img2.jpg'"/>
    </xsl:call-template>
  </xsl:variable>
  
  <p>
    <table>
      <colgroup>
        <col/>
        <col/>
      </colgroup>
      <tbody>
        <tr>
          <td>
            <img align="bottom" width="100" height="91">
              <xsl:attribute name="src">
                <xsl:value-of select="$href"/>
              </xsl:attribute>
            </img>
          </td>
          <td class="topbotline"><span class="strong"><strong><xsl:apply-templates/></strong></span></td>
        </tr>
      </tbody>
    </table>
  </p>
</xsl:template>

<xsl:template match="d:mediaobject[@role='icon']">
  <xsl:variable name="href">
    <xsl:call-template name="relative.path.link">
      <xsl:with-param name="target.pathname" select="@file"/>
    </xsl:call-template>
  </xsl:variable>
  
  <span class="inlinemediaobject">
    <img>
      <xsl:attribute name="src">
        <xsl:value-of select="$href"/>
      </xsl:attribute>
    </img>
  </span>
</xsl:template>

<xsl:template match="d:imagedata">
  <xsl:variable name="href">
    <xsl:call-template name="relative.path.link">
      <xsl:with-param name="target.pathname" select="@fileref"/>
    </xsl:call-template>
  </xsl:variable>
  
  <img>
    <xsl:attribute name="src">
      <xsl:value-of select="$href"/>
    </xsl:attribute>
  </img>
</xsl:template>

<xsl:template match="processing-instruction('linebreak')">
    <br/>
</xsl:template>

<xsl:template name="footer.navigation">
  <br/>
  <hr/>

  <address>Copyright BREDEX GmbH 2014. Made available under the Eclipse Public License v1.0.</address>
  <br/>
</xsl:template>

</xsl:stylesheet>
