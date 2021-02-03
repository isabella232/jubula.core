# Jubula API Junit 5 example

This is an example for a Junit 5 test. The JubulaJunitExtension.java is an simple version to have a workflow for starting, restarting and taking a screenshot during errors.
There is also a Junit 4 Runnter located in "./junit4Resources


The StartAgentAndAUT is starting the jubula agent and the AUT. This class can be used to start the embedded Agent and the AUT to do object mappings.
Please keep in mind to close the AUT-Agent after you have closed the application.

 
## Setup

 * SimpleAdder.jar must be copied into this folder, use the file from the Jubula installation ({JUBULA}/examples/AUTs/SimpleAdder/swing/)
 * all development jars for the API({JUBULA}/development/api/JARs/) must be copied into the "libs" folder
 * import the project into eclipse
 * run the SimpleAdder Unit Test

