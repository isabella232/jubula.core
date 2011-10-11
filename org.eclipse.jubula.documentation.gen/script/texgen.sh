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

#set -x

if [ $# -ne 3 ]
then
    echo 
    echo "usage: $0 <directory> <language> <type>"
    echo 
    echo "the generated .tex files will be put into <directory>"
    echo "<type> is either actions or errors"
    echo 
    exit 1
fi

# The following line is probably the only one that needs changed.
GDWS_DIR=../..

# Location of texgen.jar
TEXGEN_DIR="$GDWS_DIR/org.eclipse.jubula.documentation.gen"

# Output directory
OUT_DIR="$1"

# Desired language
# This will determine which Strings file to open
#LANG="en_US"
LANG="$2"

# Location of TeX templates
TEMPL_DIR="$GDWS_DIR/org.eclipse.jubula.documentation.gen/templates"

# classpath
CL_PATH="$TEXGEN_DIR/target/org.eclipse.jubula.documentation.gen-1.1.0-SNAPSHOT-jar-with-dependencies.jar"

for i in `ls $GDWS_DIR/org.eclipse.jubula.tools/lib/*.jar`
do
  CL_PATH="${CL_PATH}:$i"
done
CL_PATH="${CL_PATH}:${TEXGEN_DIR}/resources:${GDWS_DIR}/org.eclipse.jubula.tools/resources"


if [ "$3" = "actions" ]; then
    java -classpath $CL_PATH org.eclipse.jubula.documentation.gen.TexGen -gt actions -td $TEMPL_DIR -nl $LANG -od $OUT_DIR
fi
