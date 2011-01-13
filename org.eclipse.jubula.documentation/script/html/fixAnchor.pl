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

# searches for help id tags that have been placed immediately after document headers,
# and moves them in front of the header, to fix eclipse's context-sensitive help.
#

use strict;
use File::Copy;
use File::Find;
use HTML::Parser;
use IO::File;
use HTML::Tree;

require "common.pl";

my (@htmlFiles);

sub htmlFiles {
	if ( (/\.htm$/) || (/\.html$/) ) {
		push( @htmlFiles, "$_" );
	}
}

sub fillHtmlList {

	print "Searching HTML files....";
	find( \&htmlFiles, "." );
	print "done.\n";

}

sub reWriteHtml {

	my ( $htmlFile, $changed, $entities, $indent_char );

	print "Processing HTML files...";

	foreach $htmlFile (@htmlFiles) {

		# parse HTML into a tree
		my $tree = HTML::TreeBuilder->new_from_file($htmlFile);

		# process
		$changed = process($tree);

		# output HTML if we made some changes
		if ($changed) {

			# leave entities undefined for default behaviour
			# two spaces for indent
			$indent_char = '  ';
			open( OUT, ">" . $htmlFile );
			print OUT $tree->as_HTML( $entities, $indent_char );
			close OUT;
		}

		# delete tree
		$tree = $tree->delete();
	}

	print "done.\n";
}

# makes the desired changes if necessary
# returns true if changes were made to the file,
# and false otherwise
sub process {
	my $tree = shift;

	# get all header tags
	my @headers = $tree->look_down( '_tag', qr/h\d/ );
	foreach my $header (@headers) {
		my $nextTag = $header->right();

		# if there exists a context id to the right
		# while for multiple help id's
		while ( isContextId($nextTag) ) {

			# rename to id for clarity
			my $id = $nextTag;

			# move the context id tag to before the header tag
			$id->detach();
			$header->preinsert($id);
			
			$nextTag = $header->right();
		}
	}
	if ( undef @headers ) {
		return 0;
	}
	return 1;
}

sub isContextId {
	my $tag = shift;
	# right() seems to be returning objects that are neither
	# HTML::Element nor undef, which doesn't comply
	# to the given API. We'll ignore them for now.
	if ( defined($tag) && UNIVERSAL::isa($tag, 'HTML::Element') ) {
		my $tagname = $tag->tag();
		if (   $tagname eq 'a'
			&& $tag->attr('name') =~ /.*ContextId$/ )
		{
			return 1;
		}
	}
	return 0;
}

# main prog starts here

@htmlFiles = ();

fillHtmlList();
reWriteHtml();

