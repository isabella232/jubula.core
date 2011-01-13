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
# Some functions which may be used by multiple scripts
# Authors: Torsten Kalix, Mike Hurst
# ------------------------------------------------------------------------------

use strict;
use HTML::Entities;


sub trim {              # (string) -> trimmed string 
    my $s=shift;
    if ($s) {
        chomp($s);      # get rid of \n
        $s =~ s/^\s+//; # remove leading spaces
        $s =~ s/\s+$//; # remove trailing spaces
    }
    return $s;
} 


sub html2utf8 {
    return decode_entities(shift);
}
1;
