# Jubula Client API Course - Table of Contents

## to check in advance
 - IDE: Eclipse, intelliJ, ...
 - UI-toolkit: JavaFX, Swing, RCP, ...
 - flavor-of-dependencies: JARs, maven, OSGi
 - test-framework: JUnit, ... 
 
 - Java knowledge present

## day 1 - Jubula its concepts

### morning
 - Introduction to concepts
 - ITE, AUT-Agent, AUT introduction
 - **Hands-on:** Start ITE, connect to AUT-Agent, create project, Start AUT 
 - JavaFX SimpleAdder specification with ITE
 - **Hands-on:** Write and execute first simple adder test with 17 + 4
 - Highlight ITE capabilities

### afternoon
 - Transfer concepts to client API
 - General setup information
 - **Hands-on:** setup an *IDE* project using *UI-toolkit* with the preferred *flavor-of-dependencies* using *test-framework*
 - Introduction to API for AUT start
 - **Hands-on:** start AUT via API
 - Introduction to API for addressing UI components (with and without OM)
 - **Hands-on:** export OM from ITE 
 - Introduction to API for test specification (including toolkit abstraction)
 - **Hands-on:** write and execute SimpleAdder 1+1 test within JavaAPI 

## day 2 - advanced topics

### morning
 - short retrospection
 - **Hands-on:** alter test specification to 17 + 4
 - Introduction to API exception types and handling
 - **Hands-on:** alter test specification to expect failure
 - **Hands-on:** add execution event tracker
 - Introduction to result processing
 - **Hands-on:** write a small result collector

### afternoon
 - best practices / patterns
 - **Hands-on:** transform SimpleAdder test to use page-object pattern
 - **Hands-on:** connect to AUT instead of start it every time
 - sketch possible CI environment setups
