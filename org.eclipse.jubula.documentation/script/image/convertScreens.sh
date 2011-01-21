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
#! /bin/bash

echo "Converting files"

cd ../..

listOfFiles=`find . -name "*.png" | sed -e s/.png//`
for i in $listOfFiles; do convert -flatten -average $i.png $i.jpg; done

listOfFiles=`find . -name "*.png" | sed -e s/.png//`
for i in $listOfFiles; do convert -flatten -average $i.png $i.eps; done

cd ..