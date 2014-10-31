cd /projects/p21306/Workspace/users/MarvinMueller/repos/org.eclipse.jubula.core/org.eclipse.jubula.feature.api
pwd
mvn clean verify -P jubula-remote-fetch

cd /projects/p21306/Workspace/users/MarvinMueller/repos/org.eclipse.jubula.core/org.eclipse.jubula.qa.api

javac -d generated -cp "../org.eclipse.jubula.feature.api/lib/*" src/main/java/org/eclipse/jubula/qa/api/*.java   

cp -r src/main/resources generated/resources

java -cp "generated:generated/resources/:../org.eclipse.jubula.feature.api/lib/*" org.junit.runner.JUnitCore org.eclipse.jubula.qa.api.TestSimpleAdderRCPAUT


