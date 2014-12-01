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

<xsl:template name="section.heading">
  <xsl:param name="section" select="."/>
  <xsl:param name="level" select="1"/>
  <xsl:param name="allow-anchors" select="1"/>
  <xsl:param name="title"/>
  <xsl:param name="class" select="'title'"/>

  <xsl:variable name="id">
    <xsl:choose>
      <!-- Make sure the subtitle doesn't get the same id as the title -->
      <xsl:when test="self::d:subtitle">
        <xsl:call-template name="object.id">
          <xsl:with-param name="object" select="."/>
        </xsl:call-template>
      </xsl:when>
      <!-- if title is in an *info wrapper, get the grandparent -->
      <xsl:when test="contains(local-name(..), 'info')">
        <xsl:call-template name="object.id">
          <xsl:with-param name="object" select="../.."/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="object.id">
          <xsl:with-param name="object" select=".."/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <!-- HTML H level is one higher than section level -->
  <xsl:variable name="hlevel">
    <xsl:choose>
      <!-- Anything nested deeper than 3 levels down becomes H4 -->
      <xsl:when test="$level &gt; 3">4</xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$level + 1"/>   
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:element name="h{$hlevel}">
    <xsl:attribute name="class"><xsl:value-of select="$class"/></xsl:attribute>
    <xsl:if test="$css.decoration != '0'">
      <xsl:if test="$hlevel&lt;3">
        <xsl:attribute name="style">clear: both</xsl:attribute>
      </xsl:if>
    </xsl:if>
    <xsl:if test="$allow-anchors != 0 and $generate.id.attributes = 0">
      <xsl:call-template name="anchor">
        <xsl:with-param name="node" select="$section"/>
        <xsl:with-param name="conditional" select="0"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="$generate.id.attributes != 0 and not(local-name(.) = 'appendix')">
      <xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
    </xsl:if>
    <xsl:copy-of select="$title"/>
  </xsl:element>
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
