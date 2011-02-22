# $Id: Makefile 5573 2007-08-09 09:41:04Z mike $
#
# Makefile for extension API
DOC=EXTEND
OUTPUTBASE=../build
OUTPUTPDF=${OUTPUTBASE}/pdf
SCRIPTDIR=../../../script


# declare all targets as "phony targets"
.PHONY: default all images pdf clean-pdf clean deprecated

default:
	@echo
	@echo "    make pdf             - to generate the extension API pdf"
	@echo "    make clean           - to clean up"
	@echo

images:
	find . -type d -name "PS" -print -exec ${SCRIPTDIR}/image/psd2latex {} \;

pdf:    clean-pdf images
	if [ ! -d ${OUTPUTBASE} ]; then mkdir ${OUTPUTBASE}; fi
	if [ ! -d ${OUTPUTPDF} ]; then mkdir ${OUTPUTPDF}; fi
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error "\def\productname{jubula}\input{${DOC}}"
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error "\def\productname{jubula}\input{${DOC}}"
	makeindex ${OUTPUTPDF}/${DOC} ;\
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error "\def\productname{jubula}\input{${DOC}}"

clean-pdf:
	@rm -Rf ${OUTPUTPDF}

clean:
	@rm -Rf ${OUTPUTBASE}

deprecated:
