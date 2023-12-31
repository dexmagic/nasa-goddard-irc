<!-- a stylesheet to display changelogs ala netbeans -->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:param name="title"/>
  <xsl:param name="module"/>
  <xsl:param name="cvsweb"/>

  <xsl:output method="html" indent="yes"/>

  <!-- Copy standard document elements.  Elements that
       should be ignored must be filtered by apply-templates
       tags. -->
  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="attribute::*[. != '']"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="tagdiff">
    <HTML>
      <HEAD>
        <TITLE><xsl:value-of select="$title"/></TITLE>
      </HEAD>
      <BODY link="#000000" alink="#000000" vlink="#000000" text="#000000">
        <style type="text/css">
          body, p {
          font-family: verdana,arial,helvetica;
          font-size: 80%;
          color:#000000;
          }
	  .dateAndAuthor {
          font-family: verdana,arial,helvetica;
          font-size: 80%;
          font-weight: bold;
          text-align:left;
          background:#a6caf0;
	  }
          tr, td{
          font-family: verdana,arial,helvetica;
          font-size: 80%;
          background:#eeeee0;
          }	  
	  </style>        
          <h1>
            <a name="top"><xsl:value-of select="$title"/></a>
          </h1>
          Tagdiff between <xsl:value-of select="@startTag"/> <xsl:value-of select="@startDate"/> and
			<xsl:value-of select="@endTag"/> <xsl:value-of select="@endDate"/>
          <p align="right">Designed for use with <a href="http://jakarta.apache.org">Ant</a>.</p>
          <hr size="2"/>
	<a name="TOP"/>
	<table width="100%">
		<tr>
			<td align="right">
				<a href="#New">New Files</a> |
				<a href="#Modified">Modified Files</a> |
				<a href="#Removed">Removed Files</a>
			</td>
		</tr>
	</table>
        <TABLE BORDER="0" WIDTH="100%" CELLPADDING="3" CELLSPACING="1">
		<xsl:call-template name="show-entries">
			<xsl:with-param name="title">New Files</xsl:with-param>
			<xsl:with-param name="anchor">New</xsl:with-param>
			<xsl:with-param name="entries" select=".//entry[file/revision][not(file/prevrevision)]"/>
		</xsl:call-template>

		<xsl:call-template name="show-entries">
			<xsl:with-param name="title">Modified Files</xsl:with-param>
			<xsl:with-param name="anchor">Modified</xsl:with-param>
			<xsl:with-param name="entries" select=".//entry[file/revision][file/prevrevision]"/>
		</xsl:call-template>

		<xsl:call-template name="show-entries">
			<xsl:with-param name="title">Removed Files</xsl:with-param>
			<xsl:with-param name="anchor">Removed</xsl:with-param>
			<xsl:with-param name="entries" select=".//entry[not(file/revision)][not(file/prevrevision)]"/>
		</xsl:call-template>
        </TABLE>
        
      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template name="show-entries">
	<xsl:param name="title"/>
	<xsl:param name="anchor"/>
	<xsl:param name="entries"/>
	<TR>
		<TD colspan="2" class="dateAndAuthor">
			<a>
				<xsl:attribute name="name"><xsl:value-of select="$anchor"/></xsl:attribute>
				<xsl:value-of select="$title"/> - <xsl:value-of select="count($entries)"/> entries
			</a>
			<a href="#TOP">(back to top)</a>
		</TD>
	</TR>
	<TR>
		<TD width="20">
			<xsl:text>    </xsl:text>
		</TD>
		<TD>
		        <ul>
				<xsl:apply-templates select="$entries"/>
			</ul>
		</TD>
	</TR>
  </xsl:template>  

  <xsl:template match="entry">
	<xsl:apply-templates select="file"/>
  </xsl:template>

  <xsl:template match="date">
    <i><xsl:value-of select="."/></i>
  </xsl:template>

  <xsl:template match="time">
    <i><xsl:value-of select="."/></i>
  </xsl:template>

  <xsl:template match="author">
    <i>
      <a>
        <xsl:attribute name="href">mailto:<xsl:value-of select="."/></xsl:attribute>
        <xsl:value-of select="."/>
      </a>
    </i>
  </xsl:template>

  <xsl:template match="file">
    <li>
      <a target="_new">
        <xsl:attribute name="href"><xsl:value-of select="$cvsweb"/><xsl:value-of select="$module" />/<xsl:value-of select="name" /></xsl:attribute>
        <xsl:value-of select="name" />
      </a>
      <xsl:if test="string-length(prevrevision) > 0 or string-length(revision) > 0">
      <xsl:text> </xsl:text>
      <a target="_new">
        <xsl:choose>
          <xsl:when test="string-length(prevrevision) = 0 ">
            <xsl:attribute name="href"><xsl:value-of select="$cvsweb"/><xsl:value-of select="$module" />/<xsl:value-of select="name" />?rev=<xsl:value-of select="revision" />&amp;content-type=text/x-cvsweb-markup</xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="href"><xsl:value-of select="$cvsweb"/><xsl:value-of select="$module" />/<xsl:value-of select="name" />?r1=<xsl:value-of select="revision" />&amp;r2=<xsl:value-of select="prevrevision"/>&amp;diff_format=h</xsl:attribute>
          </xsl:otherwise>
        </xsl:choose> (<xsl:value-of select="revision"/>)
      </a>
      </xsl:if>
    </li>
  </xsl:template>

  <!-- Any elements within a msg are processed,
       so that we can preserve HTML tags. -->
  <xsl:template match="msg">
    <b><xsl:apply-templates/></b>
  </xsl:template>
  
</xsl:stylesheet>
