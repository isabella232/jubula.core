#!/usr/bin/perl -w
#*******************************************************************************
# Copyright (c) 2004, 2010 BREDEX GmbH.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     BREDEX GmbH - initial API and implementation and/or initial documentation
#*******************************************************************************
#
# $Id: latex2eclipse.pl 4575 2007-02-16 14:49:58Z zeb $
# ---------------------------------------------------------------
# This script translates LaTeX Files into the Eclipse Help-Format
# 
# The idea is having one source for a PDF user manual and the
# Eclipse online help.
# ----------------------------------------------------------------

use strict;
use FileHandle;
use File::Basename;
use IO::File;
use XML::Writer;
use HTML::Parser;

my @nodeLinks;

#
# Here are the values which must be set, to suit your environment
# ----------------------------------------------------------------
#
# document name
my $DOC = shift;
# main document file
my $inputTocFile = $DOC . '.html';
# file containing the toc
my $inputFile = "index.html";      
# a cleaned up, easy to read toc is written to this file
my $tmpHtmlFile = "index_clean.html";
# the output file for use by eclipse
my $tocFilename   = "../toc" . $DOC . ".xml";    # Eclipse TOC file for output

# ----------------------------------------------------------------


require "common.pl";


sub getTocName {

    my $inFile = shift;
    my $found = 0;
    my $tocName="GUIdancer User Manual";

    open(INFILE, $inFile ) || die "Cannot open file " . $inFile;
 
    while ( ! $found && ! eof(INFILE)) {
	$_ = <INFILE>;
        if ( /<meta name=\"description\" content=\"(.+)\"\>/i ) {
	    $tocName = $1;
            $found = 1;
        }
    }
    
    close(INFILE);

    if (! $found) {
        print "No document name found. Using default: $tocName\n";
    }

    return $tocName;

}

sub getContentsFile {
    my $inFile = shift;
    my $contentsFile;

    open(INFILE, $inFile ) || die "Cannot open file " . $inFile;

    my $line = <INFILE>;
    # skip lines until we get to the contents link
    my $found = 0;
    do {
	$line = <INFILE>;
    if ( $line =~ /.*{href|HREF}=\"([^\"]*)\".*>\s*Contents\s*<\/[aA]>/ ) {
	    $contentsFile = $1;
	    $found = 1;
	}
    } until ( $found || eof(INFILE));

    close(INFILE);

    die "Cannot locate contents file.\n" if (not $found);

    return $contentsFile;
}

sub outputXML {
    print "Writing XML";

    my $xmlFile = new IO::File(">" . shift);
    my $xml = XML::Writer->new(OUTPUT => $xmlFile, 
                                 DATA_MODE => 1, 
                                 DATA_INDENT => 5, 
                                 UNSAFE => 1,
                                 ENCODING => "utf-8");
    $xml->xmlDecl();
    $xml->raw("<?NLS TYPE\=\"org.eclipse.help.toc\"?>\n");
    my $tocName = getTocName($inputTocFile);
    $xml->startTag("toc", "label" => html2utf8($tocName));

    my $preLevel = -1;
    my $curLevel;

    my $size = scalar (@nodeLinks);
    my $startLevel = $nodeLinks[0]{level} if ($size > 0);
    for (my $i=0; $i<$size; $i++) {
	print "." if (($i%5) == 0);
	my %link = %{shift(@nodeLinks)};
	$curLevel = $link{level};
	my $descr = $link{descr};
	my $href = $link{href};

	# close topics if necessary
	my $j;
	for ($j = $curLevel; $j<=$preLevel; $j++) {
	    $xml->endTag("topic");
	}
        $preLevel = $curLevel;

	my $htmldir = ($DOC eq "UserManual")? "html/manual" : "html/reference";
	# output current link
	$xml->startTag("topic", 
		       "label" => trim(html2utf8($descr)),
		       "href"  => $htmldir . "/" . $href);
    }
    
    if ($curLevel && $startLevel) {
	for (my $i = $curLevel; $i >= $startLevel; $i--) {
	    $xml->endTag("topic");
	}
    }

    $xml->endTag("toc");
    $xml->end();
    $xmlFile->close();
    print "done.\n\n";
}

sub startHandler{
    my ($tagname, $attr, $self) = @_;

    print "." if ( int(rand(100)) == 1 );

    if ($tagname eq "ul" ) {
	$main::level++;
	if (!$main::active && $attr->{class} eq "TofC") {
	    $main::active++;
	}
    } elsif ($tagname eq "a") {
	my $href = $attr->{href};
	my $text;
	if ( $href && $href =~ /^node[0-9]*\.html/ ) {
	    $self->handler(text => sub{$startHandler::text = shift;},"dtext");
	    $self->handler(end => sub{endAnchorHandler($href, $startHandler::text, $self)
					  if (shift eq "a");},"tagname");
        }
    }
}

sub endAnchorHandler {
    my $href = shift;
    my $descr = shift;
    my $self = shift;

    if (! $descr) {
	$descr = "";
    }

    my %newEntry = (href => $href, descr => $descr, level => $main::level);

    if ($main::active) {
	push(@nodeLinks, {%newEntry});
    }

    # done processing the anchor, restore end handler to default
    $self->handler(end => \&endHandler,"tagname");
}

sub endHandler{
    my ($tagname) = shift;
    $main::level-- if ($tagname eq "ul");
}


###############################################
# here starts the main program

my $contentsFile = getContentsFile($inputFile);
print "found file with contents: $contentsFile\n";

print "Parsing HTML";
my $p = HTML::Parser->new(api_version => 3 );
$p->handler(start => \&startHandler,"tagname,attr,self");
$p->handler(end => \&endHandler,"tagname");
# we want only the full descriptions between tags.
$p->unbroken_text(1);

my $level = -1;
my $active = 0;
$p->parse_file($contentsFile);

print "done\n\n";

outputXML($tocFilename);
