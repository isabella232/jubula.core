# $Id: Makefile 5541 2007-07-26 15:48:57Z mike $
#
# Makefile for release notes

OUTPUTBASE=../build
OUTPUTPDF=${OUTPUTBASE}/pdf
OUTPUTHTML=${OUTPUTBASE}/html

default:
	@echo
	@echo "    make pdf             - to generate the release notes pdf"
	@echo "    make clean-pdf       - to clean up the pdf artifacts"
	@echo "    make html            - to generate the release notes HTML"
	@echo "    make clean-html       - to clean up the HTML artifacts"
	@echo

pdf:	clean-pdf
	if [ ! -d ${OUTPUTBASE} ]; then mkdir -p ${OUTPUTBASE}; fi
	if [ ! -d ${OUTPUTPDF} ]; then mkdir ${OUTPUTPDF}; fi
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error releaseNotes
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error releaseNotes

html:	clean-html
	if [ ! -d ${OUTPUTBASE} ]; then mkdir -p ${OUTPUTBASE}; fi
	if [ ! -d ${OUTPUTHTML}/tex ]; then mkdir -p ${OUTPUTHTML}/tex; fi
	if [ ! -d ${OUTPUTHTML}/html ]; then mkdir ${OUTPUTHTML}/html; fi
	latex -output-directory ${OUTPUTHTML}/tex -shell-escape --halt-on-error releaseNotes
	makeindex ${OUTPUTHTML}/tex/${DOC} ;\
	latex -output-directory ${OUTPUTHTML}/tex -shell-escape --halt-on-error releaseNotes
# l2h uses the aux file for numbering, etc.
	cp ${OUTPUTHTML}/tex/releaseNotes.aux .
	latex2html -dir ${OUTPUTHTML}/html -mkdir -address "'Copyright BREDEX GmbH ${MYDATE}'" -nonavigation -noinfo -split 0 -noshow_section_numbers -html_version 4.0 -verbosity 0 releaseNotes

clean-pdf:
	@rm -Rf ../build/pdf

clean-html:
	@rm -Rf ../build/html
