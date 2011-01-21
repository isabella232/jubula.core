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
#!/bin/bash

#converts all eps files in current directory to jpg
#but only if:
#  there is no .psd and
#  there is
for file in `ls *.eps`
do
  baseName=`basename $file .eps`
  args="-density 600x600 -colorspace rgb -strip -quality 85 -resize 256x256"
  convert $args $file ${baseName}.jpg
done


