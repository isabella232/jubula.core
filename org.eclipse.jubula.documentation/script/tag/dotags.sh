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
#!/bin/sh
# $Id: dotags.sh 7819 2009-02-17 12:26:15Z markus $
# this script will write tags to documents in doc tree.
# -------------------------------------------------------
#
# this function will edit the file ($2) and will set the tag ($1)
makeBXVersion ()
{
    echo changing $2
    sed -i -e 's/\\bxversion.*$/\\bxversion{'$1'}/' $2
    sed -i -e 's/\\bxdocinfo.*$/\\bxdocinfo{RELEASE}{BREDEX GmbH}{\\today}{}/' $2
}
# -------------------------------------------------------
if [ "X$1" = "X" ] ; then
    echo "??? dotags.sh: No TAG defined, aborting"
    echo
    exit 1
fi
echo "\bxversion{$1}" > ./share/version.tex
#makeBXVersion $1 ./manual/en/tex/UserManual.tex
#makeBXVersion $1 ./install/en/tex/install.tex
#makeBXVersion $1 ./releasenotes/en/tex/releaseNotes.tex
#makeBXVersion $1 ./extensionapi/en/tex/EXTEND.tex
#makeBXVersion $1 ./reference/en/tex/ReferenceManual.tex