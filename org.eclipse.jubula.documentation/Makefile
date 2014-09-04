# $Id: Makefile 11163 2010-05-20 15:01:12Z berndk $
#
# Makefile for all manuals

# declare all targets as "phony targets"
.PHONY: default all setversion releaseNotes licenseDoc reference reference-html checksplash

default:
	@echo
	@echo "    make reference        - to generate the reference manual pdf"
	@echo "    make reference-html   - to generate the reference manual html and pdf"
	@echo "    make releaseNotes     - to generate the release notes pdf"
	@echo "    make licenseDoc       - to generate the license doc. pdf"
	@echo "    make setversion TAG=x - to set version in documentation"
	@echo "    make checksplash      - to make sure doc-banner is up-to-date"
	@echo

all: setversion convert user-manual-html installDoc releaseNotes extensionapi \
     licenseDoc reference-html

setversion:
	@if [ "X${TAG}" != "X" ] ; then sh script/tag/dotags.sh $$TAG ; fi

convert:
	cd ./script/image; ant -buildfile ./buildScript.xml makeScreens

releaseNotes:
	cd ./releasenotes/en/tex ; make htmlandpdf

licenseDoc:
	cd ./licensedoc/en/tex ; make pdf

reference:
	cd ./reference/en/tex ; make pdf

reference-html:
	cd ./reference/en/tex ; make htmlandpdf

checksplash:
	cd ./script/image; bash checksplash
	
remove:
	cd ./script/image; ant -buildfile ./buildScript.xml deleteScreens
