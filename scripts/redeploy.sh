#!/bin/bash

#print a shell command before its execution
set -xv

echo ">> REDEPLOYING APPLICATION"
echo ""
echo "] Removing the war file ..."
sudo rm -rf /opt/tomcat/webapps/protocolanalyzer.war /opt/tomcat/webapps/protocolanalyzer
echo "] Remove complete."
echo ""
echo "] Restarting tomcat ..."
<<<<<<< HEAD
sudo service tomcat restart
=======
sudo bash /opt/tomcat/bin/shutdown.sh 
sudo bash /opt/tomcat/bin/startup.sh
sleep 5
>>>>>>> upstream/dev
echo "] Restart complete."
echo ""
echo "] Packaging the app ..."
mvn package
echo "] Packaging complete."
echo "] Deploying Application ..."
<<<<<<< HEAD
mvn tomcat:deploy
=======
# mvn tomcat:deploy                     #mvn tomcat plug in does not work for tomcat v8.5
curl --upload-file target/protocolanalyzer-1.0-SNAPSHOT.war "http://adminscript:passwordscript@localhost:8080/manager/text/deploy?path=/protocolanalyzer&update=true"
>>>>>>> upstream/dev
echo "] Deploy complete"
