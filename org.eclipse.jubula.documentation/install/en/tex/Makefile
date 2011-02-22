# $Id: Makefile 5483 2007-07-18 09:05:03Z mike $
#
# Makefile for installation manual
DOC=install
OUTPUTBASE=../build
OUTPUTPDF=${OUTPUTBASE}/pdf
SCRIPTDIR=../../../script

.PHONY: default images pdf clean-pdf clean deprecated 

default:
	@echo
	@echo "    make pdf             - to generate the release notes pdf"
	@echo "    make clean           - to clean up"
	@echo


images:
	find . -type d -name "PS" -print -exec ${SCRIPTDIR}/image/psd2latex {} \;

pdf:	clean-pdf images
	if [ "X${TAG}" != "X" ] ;\
	then   echo "TexGen anwerfen und Version setzen" ;\
	fi ;\
	if [ ! -d ${OUTPUTBASE} ]; then mkdir ${OUTPUTBASE}; fi
	if [ ! -d ${OUTPUTPDF} ]; then mkdir ${OUTPUTPDF}; fi
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error "\def\productname{jubula}\input{${DOC}}"
	makeindex ${OUTPUTPDF}/${DOC} ;\
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error "\def\productname{jubula}\input{${DOC}}"
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error "\def\productname{jubula}\input{${DOC}}"

clean-pdf:
	@rm -Rf ${OUTPUTPDF}

clean:
	@rm -Rf ${OUTPUTBASE}

deprecated:
