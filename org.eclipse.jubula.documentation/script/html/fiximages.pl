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

use strict;
use File::Copy;
use File::Find;

use vars qw(%images @htmlFiles);


sub trim {              # (string) -> trimmed string 
    my $s=shift;
    if ($s) {
        chomp($s);      # get rid of \n
        $s =~ s/^\s+//; # remove leading spaces
        $s =~ s/\s+$//; # remove trailing spaces
    }
    return $s;
} 


sub htmlFiles {
    if ( (/\.htm$/) || (/\.html$/) ) {
	push(@$main::htmlFiles, "$_");
    }
}


sub fillHtmlList {
    
    print "Searching HTML files....";
    find(\&htmlFiles, ".");
    print "done.\n"

}


sub fillImagesArray {

    print "Reading image definitions....";

    open(FILE, "<images.pl") || die "Cannot read image definitions file";

    while(<FILE>) {
	$_ = trim ($_);
	if ( /SRC\=\".*?(img[0-9]*)\.gif\"$/ ) {
	    my $dest = $1 . ".jpg";
	    my $old = $1 . ".gif";
	    $_ = <FILE>;
	    if ( /ALT\=\".*\{(.*?)\}\"/ ) {
		$_ = $1;
		my $src = "";
		if ( /.*\/.*/ ) {
		    $src =  "../tex/" .$_ . ".jpg";
		} else {
		    open(KPSEWHICH, "kpsewhich " . $_ . ".jpg|");
		    $src = trim(<KPSEWHICH>);
		    close(KPSEWHICH);
		    if ( ! "$src" ) {
			$src = $_ . ".jpg";
		    }
		}
		if ( -e $src ) {
		    $main::images{$dest} = $src;
		} else {
		    print "\nMissing: " . $src . " --> " . $dest ;
		}
	    }
	}
    }


    close(FILE);
    
    print "done.\n";
}


sub reWriteHtml {

    my $oldimg = shift;
    my $newimg = shift;
    my $htmlFile;
    my @lines = ();
    my $line = "";
    
    foreach $htmlFile (@$main::htmlFiles) {
	if ( -e $htmlFile ) {
	    open(FILE, "<" . $htmlFile) || die "Cannot open file : " . $htmlFile;
	    @lines = <FILE>;
	    close(FILE);

	open(FILE, ">" . $htmlFile) || die "Cannot open file : " . $htmlFile;
	    foreach $line (@lines) {
		$_ = $line;
		s/\ alt\=\".*?\"//g;
		if ( /$oldimg/ ) {
		    s/$oldimg/$newimg/g;		
		    s/width\=\".*?\"//g;
		    s/height\=\".*?\"//g;
		}

		if (/(^.*\<span\ id\=\"cbox[0-9]*\"\>)to\ (\<big\ class\=\"LARGE\"\>\<span class\=\"textbf\"\>.*$)/) {
		    $_ =  $1 . $2 ; 
		}

		print FILE $_ ;
	    }
	    close(FILE);
	}
    } 
    
}


sub updateImages {

    my $dest = "";
    my $src = "";
    my $oldimg = "";
    my $newimg = "";

    print "Update images....";

    foreach $dest (keys %main::images) {
	copy($main::images{$dest}, $dest) || die "Cannot copy file " . $main::images{$dest} ;

	$newimg = $dest;
	$_ = $newimg;
	s/jpg$/gif/;
	$oldimg = $_;
	reWriteHtml($oldimg, $newimg);
	if ( -e $oldimg ) {
	    unlink($oldimg);
	}
    }

    print "\n";
}




# main prog starts here

@$main::htmlFiles = "";
%$main::images = ();

fillHtmlList();
fillImagesArray();
updateImages();

