# $Id: Makefile 5541 2007-07-26 15:48:57Z mike $
#
# Makefile for release notes
default:
	@echo
	@echo "    make pdf             - to generate the release notes pdf"
	@echo "    make clean           - to clean up"
	@echo

pdf:	clean
	mkdir ../build
	mkdir ../build/pdf
	pdflatex -output-directory ../build/pdf -shell-escape --halt-on-error releaseNotes
	pdflatex -output-directory ../build/pdf -shell-escape --halt-on-error releaseNotes

clean:
	@rm -Rf ../build
