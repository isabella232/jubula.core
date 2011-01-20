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
#!/bin/perl -w
#
# $Id: generateContext.pl 4575 2007-02-16 14:49:58Z zeb $
# 
#
# This script is extracting the help IDs from tex
# --------------------------------------

use strict;
use IO::File;
use XML::Writer;
use Unicode::String qw(latin1 utf8);
require "NodeParser.pl";
require "common.pl";


my $DOC = shift;
my %ids;
my %fileref;
my $contFilename = "../context" . $DOC . ".xml";
my $exitCode;
my %anchors;
my @missingDescr;
my @htmlNotFound;


sub makeEntry {

    my $hfile = $_[0];
    my $id = $_[1];
    my $htmldir = ($DOC eq "UserManual")? "html/manual" : "html/reference";

    # get the label from the file.
    open(INF, "grep -i \"<TITLE>\" $hfile |");

    if ($_ = <INF>) {
	s/\<TITLE\>//i;
	s/\<\/TITLE\>//i;
    } else {
	$_ = "";
    }

    close(INF);

    $main::writer->startTag("topic",
			    "href" => $htmldir . "/" . $hfile . "#" . $id,
			    "label" => html2utf8($_));
    $main::writer->endTag("topic");
    
}

sub getAnchors {
 # pre: descriptions have been completely read in
 # post: %ids contains anchor locations
    # get all anchors who's names start with "SWING" or end with "ContextId"
    # search through all node*.html files
    # double escapes are needed here.
    print "Parsing HTML";
    
    my $p = NodeParser->new;
    my @files = `ls node*.html`;
    foreach my $file (@files) {
	print "." if (int(rand(20)) == 1);
	$file = trim($file);
	$file =~ /node([0-9]+)\.html/;
	my $currentNode = $1;
	$p->reset();
	$p->parse_file($file);
	my @anchors = $p->getAnchors();
	foreach my $anchor (@anchors) {
	    push(@{$ids{$anchor}{nodes}}, $currentNode);
	}
    }
    print "done.\n\n"

}

sub outputXML {

    print "Writing XML";

    $main::output = new IO::File(">" . $contFilename);
    $main::writer = XML::Writer->new(OUTPUT => $main::output, 
				     DATA_MODE => 1, 
				     DATA_INDENT => 5, 
				     UNSAFE => 1,
				     ENCODING => "utf-8");
    $main::writer->xmlDecl();
    $main::writer->startTag("contexts");

    foreach my $id (keys %ids) {
	print "." if (int(rand(10)) == 1);
	if ( not exists $ids{$id}{'descr'} ) {
	    push(@missingDescr, $id);
	    next;
	}
	if ( not $ids{$id}{'nodes'} ) {
	    push(@htmlNotFound, $id);
	    next;
	}

	$main::writer->startTag("context",
				"id" => $id);
	
	$main::writer->startTag("description");
	$main::writer->raw($ids{$id}{'descr'});
	$main::writer->endTag("description");
	
	foreach my $node (@{$ids{$id}{'nodes'}}) {
	    my $filename = "node" . $node . ".html";
	    makeEntry($filename, $id);
	}
	
	$main::writer->endTag("context");
    }
    
    $main::writer->endTag("contexts");
    $main::writer->end();
    $main::output->close();

    print "done.\n\n";
}

sub parseTeX {
    my $texdir =  shift(@ARGV); # read the next parameter from main
    my $logdir =  shift(@ARGV); # read the next parameter from main

    # read the helpids and their descriptions from the TeX source
    print "Parsing TeX source";
    open(IN, "cd $texdir && find \. \-name \"\*\.tex\" \| xargs grep \"\\gdhelpid\" |");

    while (<IN>) {
	# let 'em know we're working here
	print "." if (int(rand(25)) == 1);
	my $line = $_;
	my $key;
	my $descr;
	if ( $line =~ m/[^%]*\\gdhelpid\s*\{(.*)\}\s*\{(.*)\}/ ) {
	    $key = trim($1);
	    $descr = trim($2);
	    if ( not $ids{$key} ) {
		$ids{$key}{'descr'} = $descr;
	    }
	}
    }

    close(IN);

    print "done.\n\n";
}


# main
print "Context File Generator\n\n";

parseTeX();
getAnchors();
outputXML();

# output error info
print "The description for the following id's could not be found:\n";
foreach my $id (@missingDescr) {
    print "$id ";
}
print "\n\n";
print "The following id's could not be found in the html help: ";
foreach my $id (@htmlNotFound) {
    print "$id ";
}
print "\n\n";
