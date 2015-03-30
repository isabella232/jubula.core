# Intro

 - all documentation resources are contained within this repository 
 - documentation takes place within `o(rg).e(clipse).j(ubula).documentation.*` and `o.e.j.client.ua.*`
 - documentation is [DocBook 5][ref.docbook] based
 
# Pre-requisites
 - XML editor of choice with UTF-8 file encoding support
 - an ANT and mvn3 installation
 - a JDK installed with javac on PATH
 - commandline tools required: `xsltproc` and `xmllint` - known [limitations][ref.bug.docu.build.env] building the documentation

# howTo write DocBook for Jubula
`o.e.j.documentation/howTo/howTo.tracwiki` contains a howTo describing the general structure and way to write documentation via docbook for Jubula.

# XML characters and DocBook entities

If you're using [special characters][ref.specialCharacters] within the documentation source make sure to use the correct XML / DocBook entity.

# validating
Due to external library dependencies ([RelaxNG Schema][ref.docbook.relaxng] + [JING][ref.jing]) the validation is currently a manual step to invoke before committing new content. Additional information for validation options can be discussed [here][ref.bug.docu.validation].

To validate all documents invoke `o.e.j.documentation> ant -f buildScript.xml validateDocumentation`. The output is placed to `o.e.j.documentation/<manualType>/en/validate`.

As the documentation document structure makes use of [XInclude][ref.xinclude] the validation is happening on the completely inlined (via `xmllint`) document `<manualType>/en/validate/resolved.xml` keep in mind that the line number won't match the src-lines 

# building
To build all documents invoke `o.e.j.documentation> ant -f buildScript.xml  buildDocumentation`. The output is placed to `o.e.j.documentation/<manualType>/en/build`. The documentation won't necessarily fail if there are validation problems so make sure to validate beforehand.
 
[ref.docbook]: http://www.docbook.org/tdg5/en/html/docbook.html
[ref.xinclude]: http://en.wikipedia.org/wiki/XInclude
[ref.jing]: http://www.thaiopensource.com/relaxng/jing.html
[ref.docbook.relaxng]: http://relaxng.org/
[ref.bug.docu.build.env]: http://eclip.se/457238
[ref.bug.docu.validation]: http://eclip.se/456903
[ref.specialCharacters]: http://www.sagehill.net/docbookxsl/SpecialChars.html