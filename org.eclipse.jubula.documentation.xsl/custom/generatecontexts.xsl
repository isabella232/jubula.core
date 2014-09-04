<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:d="http://docbook.org/ns/docbook">
    
    <xsl:import href="../docbook-xsl_reduced/eclipse/eclipse.xsl"/>
    
    <xsl:template match="/">
        <!-- Call original code from the imported stylesheet -->
        <xsl:apply-imports/>
        
        <!-- Call custom templates for the contexts.xml -->
        <xsl:call-template name="contexts.xml"/>
    </xsl:template>
    
    <!-- Template for creating auxiliary contexts.xml file -->
    <xsl:template name="contexts.xml">
        <xsl:call-template name="write.chunk">
            <xsl:with-param name="filename" select="'contexts.xml'"/>
            <xsl:with-param name="method" select="'xml'"/>
            <xsl:with-param name="encoding" select="'utf-8'"/>
            <xsl:with-param name="indent" select="'yes'"/>
            <xsl:with-param name="content">

                <contexts>
                
                <!-- Get context for all children of the root element -->
                <xsl:apply-templates select="/*/*" mode="contexts.xml"/>
                    
                </contexts>
                
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <!-- Template which converts all anchors with the role 
         'helpid' into one entry in the contexts file -->
    <xsl:template
        match="d:anchor[@role='helpid']"
        mode="contexts.xml">
        
        <xsl:if test="(@id)">
            
            <!-- Get the title of the current element -->
            <xsl:variable name="title">
                <xsl:apply-templates select=".." mode="title.markup"/>
            </xsl:variable>
            
            <!-- Get HTML filename for the current element -->
            <xsl:variable name="dir">
                <xsl:call-template name="dbhtml-dir"/>
            </xsl:variable>
            
            <xsl:variable name="filename">
                <xsl:apply-templates select="." mode="recursive-chunk-filename"/>
            </xsl:variable>
            
            <!-- Create ToC entry for the current node and process its 
                 container-type children further -->
            <context id="{@id}">
                <description><xsl:value-of select="."/></description>
                <topic label="{$title}" href="{$base.dir}/{$dir}{$filename}"/>
            </context>
        </xsl:if>
        
        <xsl:apply-templates
            select="d:anchor[@role='helpid']"
            mode="contexts.xml"/>
        
    </xsl:template>
    
    <!-- Default processing in the contexts.xml mode is no processing -->
    <xsl:template match="text()" mode="contexts.xml"/>
    
</xsl:stylesheet>