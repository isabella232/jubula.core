# Makefile for reference manual
DOC=ReferenceManual
OUTPUTBASE=../build
OUTPUTPDF=${OUTPUTBASE}/pdf
OUTPUTHTML=${OUTPUTBASE}/onlinehelp
SCRIPTDIR=../../../script
TEXGEN=../../../../org.eclipse.jubula.documentation.gen
MYDATE:=$(shell date '+%Y')

default:
	@echo
	@echo "    make pdf             - to generate the reference manual pdf"
	@echo "    make pdf-manual      - same as pdf but with full output"
	@echo "    make htmlandpdf      - to generate the reference manual html and pdf"
	@echo "    make clean           - to clean up"
	@echo


images:
	chmod 775 ${SCRIPTDIR}/image/psd2latex
	chmod 775 ${SCRIPTDIR}/html/*.pl
	find . -type d -name "PS" -print -exec ${SCRIPTDIR}/image/psd2latex {} \;

pdf:	clean-pdf pdf-nocirc
# prevent circular calls due to joint dependence
pdf-nocirc: images
	if [ ! -d ${OUTPUTBASE} ]; then mkdir ${OUTPUTBASE}; fi
	if [ ! -d ${OUTPUTPDF} ]; then mkdir ${OUTPUTPDF}; fi
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error ${DOC}
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error ${DOC}
	makeindex ${OUTPUTPDF}/${DOC} ;\
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape --halt-on-error ${DOC}
pdf-manual: clean-pdf images UserManual.aux
	if [ ! -d ${OUTPUTBASE} ]; then mkdir ${OUTPUTBASE}; fi
	if [ ! -d ${OUTPUTPDF} ]; then mkdir ${OUTPUTPDF}; fi
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape ${DOC}
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape ${DOC}
	makeindex ${OUTPUTPDF}/${DOC} ;\
	pdflatex -output-directory ${OUTPUTPDF} -shell-escape ${DOC}
# just the raw html please
html-simple: 
	if [ ! -d ${OUTPUTHTML}/tmp ]; then mkdir -p ${OUTPUTHTML}/tmp; fi
	latex2html -dir ${OUTPUTHTML}/tmp -address "'Copyright BREDEX GmbH ${MYDATE}'" -nonavigation -noinfo -html_version 4.0 -verbosity 0 ${DOC} > ${OUTPUTHTML}/l2h.log

htmlandpdf: clean-html pdf
	if [ ! -d ${OUTPUTHTML}/tex ]; then mkdir -p ${OUTPUTHTML}/tex; fi
	if [ ! -d ${OUTPUTHTML}/html ]; then mkdir ${OUTPUTHTML}/html; fi
	latex -output-directory ${OUTPUTHTML}/tex -shell-escape --halt-on-error ${DOC} ;\
	latex -output-directory ${OUTPUTHTML}/tex -shell-escape --halt-on-error ${DOC} ;\
	makeindex ${OUTPUTHTML}/tex/${DOC} ;\
	latex -output-directory ${OUTPUTHTML}/tex -shell-escape --halt-on-error ${DOC} ;\
# l2h uses the aux file for numbering, etc.
	cp ${OUTPUTHTML}/tex/${DOC}.aux .
	latex2html -dir ${OUTPUTHTML}/html -mkdir -address "'Copyright BREDEX GmbH ${MYDATE}'" -nonavigation -noinfo -html_version 4.0 -verbosity 0 ${DOC}
# remove all instances of regsitered trademark symbol
# otherwise the eclipse help system will mangle the encoding
	cd ${OUTPUTHTML}/html && perl -pi -e "s/&reg;//g;" *
	cd ${OUTPUTHTML}/html && perl -I../../${SCRIPTDIR}/html ../../${SCRIPTDIR}/html/latex2eclipse.pl ${DOC}
	cd ${OUTPUTHTML}/html && perl -I../../${SCRIPTDIR}/html ../../${SCRIPTDIR}/html/generateContext.pl ${DOC} ../../../tex ../tex
	cd ${OUTPUTHTML}/html && ../../${SCRIPTDIR}/html/fiximages.pl
	cd ${OUTPUTHTML}/html && perl -I../../${SCRIPTDIR}/html ../../${SCRIPTDIR}/html/fixAnchor.pl
	rm -f ${OUTPUTHTML}/html/images.* ${OUTPUTHTML}/html/*.log ${OUTPUTHTML}/html/WARNINGS

texgen:
	if [ ! -d Action/Generated ]; then mkdir Action/Generated; fi
	cd ${TEXGEN}/script; sh texgen.sh ../../org.eclipse.jubula.documentation/reference/en/tex/Action/Generated en_US actions
clean-pdf:
	@rm -Rf ${OUTPUTPDF}
	@if [ -f UserManual.aux ] ; then rm UserManual.aux ; fi

clean-html:
	@rm -Rf ${OUTPUTHTML}

clean:	clean-pdf
	@rm -Rf ${OUTPUTBASE}
