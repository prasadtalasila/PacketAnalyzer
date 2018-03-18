#!/bin/bash

#print a shell command before its execution
set -xv

echo ">> REDEPLOYING APPLICATION"
echo ""

echo "] Shutdown tomcat and wait for the socket to become available"
sudo systemctl stop tomcat
sleep 5

echo "] Removing the war file ..."
sudo rm -rf /opt/tomcat/webapps/protocolanalyzer.war /opt/tomcat/webapps/protocolanalyzer
echo "] Remove complete."
echo ""

#reset permissions on data and log directories
sudo chmod -R 777 /opt/darshini-es
sudo chmod -R 777 /opt/darshini-logs

echo "] Packaging the app ..."
mvn package
echo "] Packaging complete."

echo "] Restarting tomcat ..."
sudo systemctl restart tomcat
sleep 5
echo "] Restart complete."
echo ""

echo "] Deploying Application ..."
# mvn tomcat:deploy                     #mvn tomcat plug in does not work for tomcat v8.5
curl --upload-file target/protocolanalyzer-1.0-SNAPSHOT.war "http://adminscript:passwordscript@localhost:8080/manager/text/deploy?path=/protocolanalyzer&update=true"

echo "] Deploy complete"
