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
# wrapper-script for latex2html to make lgrind work
#
# (c) BREDEX GmbH, 2002
#
# 1st version done by Torsten
#

use FileHandle;
use File::Basename;
$subdir="BXINPUTTMP" ;

sub main'copy_grind{ #'
    local($_, $in_file,$out_file,$num);
    
    # do some processing and get filenames
    $_ = $_[0];
    s/^\s*//; s/\s*$//;
    s/.*\\(bxgrind|bxgrindinsert)(\s*{|\s+)(.*?)}(.*)/$3/;
    s/}.*$//;
    s/^\s*//; s/\s*$//;
    $in_file=$_;
    $in_file=$_ . "\.tex" unless $in_file =~ /\.tex$/;

    print "*** Copy grind commands for file $in_file. ***\n";
    
    s/\.\./__/g;
    $out_file="$subdir/$_";
    system "mkdir -p " . dirname($out_file) ;
    $CMD = "cat $in_file  | grep -v LGbegin | grep -v LGend | sed -e 's/\\\\Proc{.*}\\\\L/\\\\L/;' > $out_file.tex";
    system($CMD);
}
    

sub main'extract_lgrind{ #'
    local($_, $in_file,$out_file,$num);
    
    my $OUT = FileHandle->new();
    my $IN = FileHandle->new();

    # do some processing and get filenames
    $_ = $_[0];
    s/^\s*//; s/\s*$//;
    s/.*\\bxinput(\s*{|\s+)(.*)/$2/;
    s/}.*$//;
    s/^\s*//; s/\s*$//;
    $in_file=$_;
    $in_file=$_ . "\.tex"unless $in_file =~ /\.tex$/;

    print "*** Start extract lgrind commands for file $in_file. ***\n";

    s/\.\./__/g;
    $out_file="$subdir/$_";
    system "mkdir -p " . dirname($out_file) ;
    
    # run lgrind
     
    system "lgrind -e -c -o $out_file $in_file";

    # extract all lgrind-blocks (LGbegin ... LGend)
    
    $num = 0;
    open($IN, $out_file) || die "Cannot open $out_file";
    open($OUT, ">$out_file" . ".tex") || die "Cannot open $out_file tex";

    while ($_ = <$IN>)
    {
	if ( /LGbegin/ ){
	    $num = $num + 1;
	    print $OUT "\\lgrindfile{" . $out_file . "__" . $num . "}\;\n";
	    close($OUT);
	    open($OUT, ">$out_file"  . "__" . $num . ".tex") || die "Cannot open $out_file$num";
	} elsif ( /LGend/ ){
	    close($OUT);
	    open($OUT, ">>$out_file" .".tex") || die "Cannot open $out_file tex";
	    } else {
		s/\\Proc{.*?}\\L/\\L/;
		print $OUT $_;
		if ( /\\input({|\s*)/ ){
		    &rewrite_lgrind($_);
		} elsif ( /\\bxinput/ ) {
		    &extract_lgrind($_);
		} elsif ( /\\(bxgrind|bxgrindinsert)/ ) {
		    &copy_grind($_);
		}
	    }
    }

    close($OUT);
    close($IN);
    unlink("$out_file");
}



sub main'rewrite_lgrind{ #'
    local($_, $in_file,$line);
    
    my $IN = FileHandle->new();

    # do some processing and get filenames
    $_ = $_[0];
    s/^\s*//; s/\s*$//;
    s/.*\\input(\s*{|\s+)(.*)/$2/;
    s/}.*$//;   
    s/^\s*//;s/\s*$//;
    $in_file=$_;
    $in_file .= ".tex" unless $in_file =~ /\.tex$/;
    print "*** Start re-writing lgrind commands for file $in_file ***\n";
    if ( -e $in_file) {
	open($IN, $in_file) || die "Cannot open $in_file";
	while ($line = <$IN>)
	{
	    if ( $line !~ /^\s*\%/ ) {
		if ( $line =~ /\\input({|\s+)/ ){
		    &rewrite_lgrind($line);  # translations for new input-file
		} elsif ( $line =~ /\\bxinput({|\s+)/ ){
		    &extract_lgrind($line); # extract lgrind-blocks to single files
		}  elsif ( $line =~ /\\(bxrind|bxgrindinsert)({|\s+)/ ){
		    &copy_grind($line); # extract lgrind-blocks to single files
		} 
	    }
	 }    
	 close($IN);
     }
}

sub main'copyAndRewrite { #'

    my $from = $_[0];
    my $to = $_[1] . "/" . $from;
#    my $foundbxstyle = 0;
    my $germanlang = 1;

    open(INF, $from) || die "Cannot open input file $from";
    open(OUTF, ">" . $to) || die "Cannot open output file $to";

    while ($_ = <INF>) {
	
	if ( (/\\documentclass(\s*?)\[(.*?)\](.*?)(bxarticle|bxreport)/) && 
	     (/\\documentclass(\s*?)\[(.*?en.*?)\](.*?)(bxarticle|bxreport)/) ) {
	    $germanlang = 0;
	}
	
	if (/\\documentclass(\s*?)\[(.*?guidancer.*?)\](.*?)(bxarticle|bxreport)/) {
	    s/\](\s*?)\{/\,generatehtml\]\{/;
	    s/documentclass(\s*?)\{/documentclass\[generatehtml\]\{/;
	    $foundbxstyle = 1;
	}
	
	if ( ($foundbxstyle) && 
	     (/\\begin(\s*?)\{(\s*?)document/) ) {	   
	    system "ln -s /usr/local/teTeX/share/texmf-local/tex/latex/bredex/guidancer.sty ___guidancer.tex";
	    $_ .= "\n\\input\{___guidancer.tex\} \n"; 
		 $foundbxstyle = 0;
	}

	if ( ($germanlang) && 
	     (/\\begin(\s*?)\{(\s*?)document/) ) {
	    $_ .=  "\n\\usepackage\{german\}\n";		
	    $germanlang = 0;
	}
	
	print OUTF $_;

    }
    
    close(INF);
    close(OUTF);
    
}

# main part starts here
{

    $newargs = "";
    $inputFileFound = 0;

# get last argument, the filename
    foreach $argument (@ARGV)
    {
	$in_file = $argument;
        $in_file .= ".tex" unless $in_file =~ /\.tex$/;
        if ( -e $in_file ) {
	    $inputFileFound = 1;
	    if (-e $subdir) {
		print "Removing old directory $subdir...\n";
		system "rm -rf $subdir";
	    }	    
	    system "mkdir $subdir";
	    &rewrite_lgrind("\\input{" . $in_file . "}\n");
	    copyAndRewrite($in_file, $subdir);
	    $in_file = $subdir . "/" . $in_file;
	    $newargs .= " " . $in_file;
        } else {
	    $newargs .= " " . $argument;
	}
    }

    if ( ($inputFileFound == 1 ) && 
	 ($newargs !~ /\-dir/) ) {
	$newargs = "\-dir html \-mkdir" . $newargs;
    }
    
# set the configuration file, in case it is not already set
    
    if ( ! $ENV{'L2HCONFIG'} ) {
	$ENV{'L2HCONFIG'} = 'bxl2hconf.pm';
    }

# l2h needs to be able to see our style files.
#     system('ln -s /usr/local/teTeX-3.0/share/texmf-local/tex/latex/bredex/guidancer.sty .');
    
#    print $newargs; exit;
    system("/usr/local/l2h/bin/latex2html $newargs");# == 0 or die "Executing of Latex2html failed.";
    my $status = $? >> 8;
    if ($status != 0) {
	print "latex2html exited with status $status\n";
    }

    system "rm -rf $subdir";
    system "rm -f ___guidancer.tex";
}

