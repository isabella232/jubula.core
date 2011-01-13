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
{
	use strict;

	package NodeParser;
	use base 'HTML::Parser';

	my @anchors = ();

	sub start {
		my ( $self, $tagname, $attr, $attrseq, $origtext ) = @_;
		if ( $tagname eq "a" && $attr->{name} ) {
			my $name = $attr->{name};
			if (   $name =~ /^SWING/
                                || $name =~ /^CONCRETE/
                                || $name =~ /^ABSTRACT/
                                || $name =~ /^WEB/
                                || $name =~ /^RCP/	
				|| $name =~ /ContextId$/ )
			{
				push( @anchors, $name );
			}
		}
	}

	sub getAnchors {
		return @anchors;
	}

	sub reset {
		@anchors = ();
	}
}
1;
